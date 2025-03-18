# EmpMongo

### Redis Commands:

docker exec -it <<redis_container_name>> redis-cli
docker exec -it guide-redis redis-cli
FLUSHALL   --> Clear all entries
HGETALL STD --> Get the all entries using STD key
HGET STD 105  --> Get the only 105 transaction


### Start MongoDb:

mongod --port 27017 --dbpath C:\data\db

### Change Data Capture Implementation:

Important topic below
Change Data Capture
we have to implement cache eviction for update , delete flow ...
Also I think we need to implement CDC ... if any data changes for accountToken or accountNumber we need to evict cache..
https://docs.spring.io/spring-data/mongodb/reference/mongodb/change-streams.html
https://medium.com/@marekchodak/change-data-capture-with-mongodb-change-streams-539a02cf401d


http://localhost:8080/banking-identifiers?accountToken=NQ1Vw53pmP5EK257GYHwoOuw7rGdyNs-5cBj1Yh4YKQ

http://localhost:8080/banking-identifiers?accountToken=Sample123


source="http:stud-mongo" (index="stud_api_mongo")

# Splunk Docker Command:

docker run --name splunk --hostname splunk -p 8000:8000 -e "SPLUNK_PASSWORD=Murthy1." -e "SPLUNK_START_ARGS=--accept-license" -it splunk/splunk:latest



docker run --name splunk --hostname splunk -p 8000:8000 -p 8088:8088 -p 8089:8089 -p 9997:9997 -e "SPLUNK_PASSWORD=Murthy1." -e "SPLUNK_START_ARGS=--accept-license" -it splunk/splunk:latest

Student Project Splunk Token:
4ff277b0-f1da-48f0-a47b-c2a4299610ce
ba2b2d0c-e3e3-4784-b9b6-d5bb6a6a80bb

Created the new toke:
source="http:studentapidev" (index="stud_api_dev") sourcetype="log4j"

/opt/splunk/etc/system/local/inputs.conf
[splunktcp://9997]
disabled = 0

After adding below
[http]
disabled = 0
enableSSL = true
port = 8088

Sri Samulu Naidu
7036310098

curl -k http://localhost:8088/services/collector \
-H "Authorization: Splunk 4ff277b0-f1da-48f0-a47b-c2a4299610ce" \
-H "Content-Type: application/json" \
-d '{"event": "Test log from cURL", "index": "stud_api_mongo"}'


    <appender name="SPLUNK" class="com.splunk.logging.HttpEventCollectorLogbackAppender">
        <url>https://localhost:8088/services/collector/event</url>  <!-- Update with your Splunk HEC URL -->
        <token>ba2b2d0c-e3e3-4784-b9b6-d5bb6a6a80bb</token>  <!-- Replace with your Splunk token -->
        <source>http_event_logs</source>
        <type>raw</type>
        <sourcetype>log4j</sourcetype>
        <host>localhost</host>
        <index>stud_api_mongo</index>
        <messageFormat>text</messageFormat>
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </layout>
    </appender>
	
	
	curl -k https://localhost:8088/services/collector \
-H "Authorization: Splunk ba2b2d0c-e3e3-4784-b9b6-d5bb6a6a80bb" \
-H "Content-Type: application/json" \
-d '{"event": "Hello Splunk from Logback!", "index": "stud_api_mongo", "sourcetype": "slf4j"}'



curl -k https://localhost:8088/services/collector \
-H "Authorization: Splunk 98583f12-68d9-4d06-96be-386745fa103b" \
-H "Content-Type: application/json" \
-d '{"event": "Hello Splunk from stud_api_dev index Logback!", "index": "stud_api_dev", "sourcetype": "slf4j"}'

# **Kafka:**

docker run -d \
-p 8080:8080 \
-v /tmp/application.yml:/app/application.yml \
tchiotludo/akhq

docker run -d -p 8080:8080 -v /tmp/application.yml:/app/application.yml tchiotludo/akhq

D:\Softwares\Kafka\akhq
java -Dmicronaut.config.files=application.yml -jar akhq.jar

Start the Zooker Server:
D\:Kafka\
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

Start Kafka Server:
D\:Kafka\
.\bin\windows\kafka-server-start.bat .\config\server.properties

Kafka Topic Creation:
D\:Kafka\bin\windows\
kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic emp_topic

Producer send message:
kafka-console-producer.bat --bootstrap-server localhost:9092 --topic emp_topic --group emp_group_id

Consumer consumes the producer messages:
kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic emp_topic --group emp_group_id

AKHQ server Start Command:
D:\Softwares\Kafka\akhq>java -Dmicronaut.config.files=application.yml -jar akhq-0.24.0-all.jar

application.properties
micronaut:
server:
port: 8855
cors:
enabled: true
configurations:
all:
allowedOrigins:
- http://localhost:8855

akhq:
connections:
local:
properties:
bootstrap.servers: "localhost:9092"

AKHQ url:
http://localhost:8855