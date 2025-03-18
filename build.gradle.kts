plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.empmongo"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://splunk.jfrog.io/splunk/ext-releases-local/")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")



	//implementation("com.splunk.logging:splunk-library-logback:1.11.2")
	implementation("com.splunk.logging:splunk-library-javalogging:1.11.2")
	implementation("ch.qos.logback:logback-core:1.5.6")
	implementation("ch.qos.logback:logback-classic:1.5.6")
	//implementation("org.slf4j:slf4j-api:2.0.16")
	implementation("org.codehaus.janino:janino:3.1.9")

	/*implementation("com.splunk.logging:splunk-library-javalogging:1.11.2")  {
    exclude("org.apache.logging.log4j", "log4j-slf4j2-impl")
    exclude("org.apache.logging.log4j", "log4j-core")
    exclude("org.apache.logging.log4j", "log4j-jul")
    exclude("com.google.code.gson", "gson")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib")
}
	implementation("org.apache.logging.log4j:log4j-api:2.21.0")
	implementation("org.apache.logging.log4j:log4j-core:2.21.0")*/

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-redis-reactive:3.4.2")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	configurations.all {
		//exclude(group = "ch.qos.logback", module = "logback-classic")
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
		//exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}
