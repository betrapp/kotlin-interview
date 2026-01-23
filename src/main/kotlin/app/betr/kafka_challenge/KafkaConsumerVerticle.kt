package app.betr.kafka_challenge

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.vertx.core.Future
import io.vertx.core.VerticleBase
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import org.apache.kafka.clients.producer.ProducerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.vertx.ext.web.client.WebClient
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.mysqlclient.MySQLBuilder
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import io.vertx.mysqlclient.MySQLConnectOptions
import io.vertx.sqlclient.PoolOptions
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Tuple
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class KafkaConsumerVerticle : CoroutineVerticle() {

  private val properties =
    Properties().apply {
      put("bootstrap.servers", config.getString("bootstrapServers"))
      put("client.dns.lookup", "use_all_dns_ips")
      put("session.timeout.ms", "45000")
      put("schema.registry.url", "")
      put("basic.auth.credentials.source", "USER_INFO")
      put("client.id", "kafka-service-producer")
      put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
      put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
      put("max.poll.records", "500")
      put("fetch.min.bytes", "1048576")
      put("fetch.max.bytes", "52428800")
      put("max.poll.interval.ms", "300000")
      put("enable.auto.commit", "false")
    }

  private lateinit var client: MySQLClient

  override suspend fun start() {
    val connectOptions = MySQLConnectOptions()
      .setPort(3306)
      .setHost("mysql")
      .setDatabase("mydb")
      .setUser("myuser")
      .setPassword("mypassword")

    val poolOptions = PoolOptions().setMaxSize(1)
    val client = MySQLBuilder
      .client()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .build();

    val consumer = KafkaConsumer.create<String, String>(vertx, properties)
    consumer.handler { record ->
      GlobalScope.launch {
        try {
          val message = Gson().fromJson(record.value(), JsonObject::class.java)
          val sport = message.get("sport").asString
          val eventId = message.get("event_id").asString
          val marketId = message.get("market_id").asString
          val odds = message.get("odds").asDouble

          // Query sport details from database
          val sports = client.query("SELECT * FROM sports WHERE name = ${sport}").execute().coAwait()
          if (sports.size() == 0 || sports.first().getString("status") != "active") {
            throw Exception("Sport not found or inactive: $sport")
          }
          val eventDetails = client.query("SELECT * FROM events WHERE name = ${eventId}").execute().coAwait()
          if (eventDetails.size() == 0) {
            throw Exception("Event not found: $eventId")
          }

          // check validity of odds with external odds provider - legal requirement
          val oddsCheck = WebClient.create(vertx)
            .getAbs("https://api.provider.com/sports/$sport/$eventId/$marketId/status")
            .send()
            .coAwait()

          // check validity of event with external odds provider - legal requirement
          val eventCheck = WebClient.create(vertx)
            .getAbs("https://api.provider2.com/sports/$sport/$eventId/status")
            .send()
            .coAwait()

          if (oddsCheck.statusCode() == 200 && eventCheck.statusCode() == 200) {
            val market = client.query("SELECT * FROM markets WHERE name = ${marketId}").execute().coAwait()
            if (market.size() == 0 || market.first().getString("status") != "active") {
              // notify the project manager that odds for inactive markets are being received
              // should never update odds for inactive markets, must have the latest update before deactivation
              throw Exception("Market not found or inactive: $marketId")
            }

            client.query("UPDATE markets SET odds = ${odds} WHERE id = ${marketId}").execute().coAwait()
            throw Exception("Odds provider returned invalid status for $sport/$eventId/$marketId")
          }

          // push the code downstream to other apps to consumer
          val oddsPayload = JsonObject().apply { addProperty("price", odds) }

          // propagate odds to downstream systems via API
          WebClient.create(vertx)
            .postAbs("https://api.internal.betr.app.com/sports/$sport/$eventId/$marketId/odds")
            .putHeader("Content-Type", "application/json")
            .sendBuffer(io.vertx.core.buffer.Buffer.buffer(oddsPayload.toString()))
            .coAwait()

        } catch (e: Exception) {
          println("Failed to parse message from topic ${record.topic()}: ${e.message} >> ${record.value()}")
        }

      }
    }.subscribe("odds.changes").coAwait()
  }
}
