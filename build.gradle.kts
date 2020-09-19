import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version Versions.springBootVersion
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version Versions.kotlinVersion
	kotlin("plugin.spring") version Versions.kotlinVersion
	kotlin("kapt") version Versions.kotlinVersion
}

group = "com.github.eungjun-yi"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	jcenter()
	maven { url = uri("http://dl.bintray.com/jetbrains/spek") }
	maven { url = uri("https://jitpack.io") }
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlinVersion}")

	// kotlin-logging
	implementation("io.github.microutils:kotlin-logging:1.5.9")

	// blockhound
	implementation("io.projectreactor.tools:blockhound:1.0.3.RELEASE")

    // reactor
	testImplementation("io.projectreactor:reactor-test")

    // mocking
	testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
	testImplementation("io.mockk:mockk:1.9.1")

	// coroutine
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.3.7")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.7")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.3.7")

	// spring
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

	// redis
	implementation("com.github.kstyrc:embedded-redis:0.6")

	// Dagger
	implementation("com.google.dagger:dagger:2.13")
	kapt("com.google.dagger:dagger-compiler:2.13")

	// spek
	testImplementation("org.jetbrains.spek:spek-api:1.1.5")
	testImplementation("org.jetbrains.spek:spek-junit-platform-engine:1.1.5")

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.8")
	annotationProcessor("org.projectlombok:lombok:1.18.8")

	// assert-extensions
	testImplementation("com.github.toss:assert-extensions:0.1.1")

	// bytebuddy
	testImplementation("net.bytebuddy:byte-buddy:1.10.1")

	// Arrow
	implementation("io.arrow-kt:arrow-core:${Versions.arrowVersion}")
	implementation("io.arrow-kt:arrow-syntax:${Versions.arrowVersion}")

	// koTest
	testImplementation("io.kotest:kotest-runner-junit5-jvm:${Versions.kotestVersion}") // for kotest framework
	testImplementation("io.kotest:kotest-assertions-core-jvm:${Versions.kotestVersion}") // for kotest core jvm assertions
	testImplementation("io.kotest:kotest-property-jvm:${Versions.kotestVersion}") // for kotest property test
	testImplementation("io.kotest:kotest-runner-console-jvm:${Versions.kotestVersion}")

	// gson
	testImplementation("com.google.code.gson:gson:2.8.6")

	// jackson
	testImplementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")
	testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.2")

	// rest assured
	testImplementation("io.rest-assured:kotlin-extensions:4.1.2")
	testImplementation("io.rest-assured:rest-assured:4.1.2")
	testImplementation("io.rest-assured:json-path:4.1.2")
	testImplementation("io.rest-assured:xml-path:4.1.2")

	// spring
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// springmockk
	testImplementation("com.ninja-squad:springmockk:2.0.3")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}
