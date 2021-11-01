# Spring Logger With gRPC Server

This application developed for integrate gRPC protocol to Spring Boot application. 
Basically there exist one entrypoint. Get a LogRequest object and writes it to Mysql database
<br/>
With installation information below, application can run locally.

# Technologies

The project is built on `Spring Boot` and `io.grpc` architecture. Maven used as build automation tool.

# Maven Dependency

You need to add below dependencies for auto generate proto classes.

* Properties
````xml
	<properties>
		<protobuf.version>3.14.0</protobuf.version>
		<protobuf-plugin.version>0.6.1</protobuf-plugin.version>
		<grpc.version>1.35.0</grpc.version>
	</properties>

````

* Dependencies
* 
````xml
<dependencies>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-stub</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-protobuf</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <!-- Java 9+ compatibility - Do NOT update to 2.0.0 -->
        <groupId>jakarta.annotation</groupId>
        <artifactId>jakarta.annotation-api</artifactId>
        <version>1.3.5</version>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>net.devh</groupId>
        <artifactId>grpc-server-spring-boot-autoconfigure</artifactId>
        <version>2.5.1.RELEASE</version>
    </dependency>
</dependencies>
````

* Build

````xml
<build>
    <extensions>
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.6.2</version>
        </extension>
    </extensions>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>${protobuf-plugin.version}</version>
            <configuration>
                <protocArtifact>com.google.protobuf:protoc:${protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>
                        <goal>compile-custom</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
````

# Create Proto File

Path : [LoggerService.proto](src/main/proto/LoggerService.proto)

````protobuf
syntax = "proto3";
package com.workshop.springlogger;

option java_multiple_files = true;
option java_package = "com.workshop.springlogger.proto";
option java_outer_classname = "LoggerServiceProto";

message LogRequest {
    string message = 1;
    string applicationName = 2;
    string level = 3;
}
message LogResponse {
    bool success = 1;
    string responseMessage = 2;
}
service LoggerService {
    rpc log(LogRequest) returns (LogResponse);
}
````

# Project Package

After complete .proto file, you need to package application for create auto generated classess.

```shell
./mvnw clean package -Dspring-boot.run.profiles=tst -DskipTests
```

After package completed successfully, you can start implementation of GrpcService.

# Implement Grpc Service

Path : [LoggerServiceImpl.java](src/main/java/com/workshop/springlogger/grpc/LoggerServiceImpl.java)

````java
package com.workshop.springlogger.grpc;


import com.workshop.springlogger.data.model.LogEntity;
import com.workshop.springlogger.proto.*;
import com.workshop.springlogger.proto.LoggerServiceGrpc;
import com.workshop.springlogger.service.LogService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;


@GrpcService
@RequiredArgsConstructor
public class LoggerServiceImpl extends LoggerServiceGrpc.LoggerServiceImplBase {

    private final LogService service;

    @Override
    public void log(LogRequest request, StreamObserver<LogResponse> responseObserver) {
        try {
            service.log(LogEntity.builder()
                    .logDate(LocalDateTime.now())
                    .logLevel(request.getLevel())
                    .logMessage(request.getMessage())
                    .applicationName(request.getApplicationName())
                    .build());
            LogResponse response = LogResponse.newBuilder()
                    .setResponseMessage(request.getMessage())
                    .setSuccess(true)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception ex) {

            LogResponse response = LogResponse.newBuilder()
                    .setResponseMessage(ex.getMessage())
                    .setSuccess(false)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onError(ex);
        }


    }
}

````

# Application Configuration

Path : [application.yml](src/main/resources/application.yml)

```yaml
server:
  port: 9998
grpc:
  server:
    port: 9999
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/logger-database
    username: root
    password: pass
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      #creates repository at target database when application started
      ddl-auto: create
````

# Application Run

After creating database `logger-database` you can install and run application.
With `ddl-auto: create` property no need to create log table. Hibernate automatically creates table(s).

* First Install Application 
```shell
./mvnw clean install
```

* Second Run Application

```
./mvnw spring-boot:run
```
![Application Started](/assets/grpc_started.jpg)


# Application Send Request

You can try application with [BloomRPC](https://github.com/uw-labs/bloomrpc).

![BloomRPC](/assets/bloomrpc_demo.jpg)

After all you can see row at Mysql database

![Mysql Result](/assets/mysql_result.jpg)