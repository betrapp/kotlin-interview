package app.betr.kafka_challenge

import io.vertx.core.Future
import io.vertx.core.VerticleBase
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

class KafkaProducerVerticle : CoroutineVerticle() {

  private val properties =
    Properties().apply {
      put("bootstrap.servers", config.getString("bootstrapServers"))
      put("client.dns.lookup", "use_all_dns_ips")
      put("session.timeout.ms", "45000")
      put("acks", "all")
      put("schema.registry.url", "")
      put("basic.auth.credentials.source", "USER_INFO")
      put("client.id", "kafka-service-producer")
      put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
      put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    }

  override suspend fun start() {
    // create topics
    val adminClient = AdminClient.create(properties)
    val topic = NewTopic(
      "odds.changes", // name
      1, // numPartitions
      3 // replicationFactor
      )
    adminClient.createTopics(listOf(topic))
    adminClient.close()
    val producer = KafkaProducer<String, String>(properties)
    while(true) {
      val payload = """
        {
          "change_id": "abc123",
          "sport": "football",
          "event_id": "Tigers vs Raptors",
          "market_id": "moneyline",
          "odds": 2.5
        }
        """.trimIndent()
      val record = ProducerRecord("odds.changes", "abc123", payload)
      producer.send(record)
    }
  }
}
