import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
  kotlin ("jvm") version "2.2.20"
  application
  id("com.gradleup.shadow") version "9.2.2"
}

group = "app.betr"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://packages.confluent.io/maven/")
}

val vertxVersion = "5.0.7"
val junitJupiterVersion = "5.9.1"

val mainVerticleName = "app.betr.kafka_challenge.MainVerticle"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-launcher-application")
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-config")
  implementation("io.vertx:vertx-health-check")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-opentelemetry")
  implementation("io.vertx:vertx-mysql-client")
  implementation("io.vertx:vertx-circuit-breaker")
  implementation("io.vertx:vertx-rabbitmq-client")
  implementation("io.vertx:vertx-kafka-client")
  implementation("io.vertx:vertx-lang-kotlin")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  implementation("io.confluent:kafka-avro-serializer:8.0.0")
  implementation("com.google.code.gson:gson:2.11.0")

  implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
  implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.fromTarget("17")
    languageVersion = KotlinVersion.fromVersion("2.0")
    apiVersion = KotlinVersion.fromVersion("2.0")
  }
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf(mainVerticleName)
}
