plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "com.hunmin.domain"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // 스프링 관련 라이브러리
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // 코틀린 관련 라이브러리
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // DTO -> Entity 바꾸는거 자동화 하게 해주는 model mapper
    implementation("org.modelmapper:modelmapper:3.1.1")

    // Database and QueryDSL
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("mysql:mysql-connector-java:8.0.33")
    runtimeOnly("com.h2database:h2")

    // 실시간 채팅을 위한 필요 라이브러리들
    testImplementation("org.springframework.boot:spring-boot-starter-websocket")
    testImplementation("org.webjars:sockjs-client:1.1.2")
    testImplementation("org.webjars:stomp-websocket:2.3.3-1")

    // Redis 내장, 외장 라이브러리들
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("it.ozimov:embedded-redis:0.7.2")

    // 쿼리 dsl 라이브러리
    testImplementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")

    // 테스트 관련 라이브러리
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Swagger - springdoc
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // 이미지 처리 라이브러리
    implementation("net.coobird:thumbnailator:0.4.20")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
