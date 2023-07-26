//https://central.sonatype.com/
//search for mavencentral plugins here

plugins {
    java
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

//    jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.2")

//    this is the plain mybatis -> no spring boot integration
//    implementation("org.mybatis:mybatis:3.5.13")

//    myBatis Spring configured
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.2")

    //jdbctemplate
//    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    //h2 database integration
    runtimeOnly("com.h2database:h2")

    //테스트에서 lombok 사용
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}