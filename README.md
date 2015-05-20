# scala-pocs
A collection of POC's written in Scala &amp; running in Docker for Kafka, Hadoop and PostgreSQL

## Setting up Kafka

Copy the docker/kafka-up.sh file into your host machine and run ```sh kafka-up.sh```

*NOTE: the following processes run interactively, so you will need to open multiple terminals.*

Generate some data for a race with 5,000 participants and 2 checkpoints (start/finish) and stream it into Kafka on the 'reads' topic with the following command:
```shell
docker run --rm -i anaerobic/generate-race 5000 2 | docker run -i --rm --net host anaerobic/fsharp-kafka-producer reads http://kafka.lacolhost.com:9092
```

Now stream the race out of Kafka, into a microservice that bracketizes each participant and back into Kafka on the 'results' topic with the following command:
```shell
docker run --rm --net host anaerobic/fsharp-kafka-consumer reads http://kafka.lacolhost.com:9092 | docker run -i --rm anaerobic/streaming-fsharp | docker run -i --rm --net host anaerobic/fsharp-kafka-producer results http://kafka.lacolhost.com:9092
```

You can verify the data is on the 'results' topic using:
```shell
docker run -i --rm --net host anaerobic/fsharp-kafka-consumer results http://kafka.lacolhost.com:9092
```

## Setting up PostgreSQL

Copy the docker/postgres-up.sh file into your host machine and run ```sh postgres-up.sh```

Add a foo table to the postgres db (I used pgAdmin III) with the following schema:
```sql
CREATE TABLE public.foo
(
   id serial, 
   some_json json
) 
WITH (
  OIDS = FALSE
)
;
```

##Usage for kafka-to-postgres:

0. Build the kafka-to-postgres/target/kafka-to-postgres-1.0.jar with ```cd kafka-to-postgres``` and ```mvn package``` in a command prompt.
0. Copy the kafka-to-postgres/target/kafka-to-postgres-1.0.jar to the docker/kafka/share directory
0. Copy the docker/kafka directory to ~/kafka on your host machine
0. Run ```sh ~/kafka/docker-up.sh``` on your host machine
0. Run ```sh share/kafka-to-postgres.sh``` from inside the kafka container
0. Verify your data is in PostgreSQL
0. (╯°□°）╯︵ ┻━┻
0. ᕕ( ᐛ )ᕗ

##Usage for hdfs-to-postgres:

*NOTE: I've got a forked Kafka to HDFS library working at https://github.com/anaerobic/kafka-hadoop-consumer but it is in Java and it doesn't use consumer groups, so the next best thing was to just copy the output of that into a file and copy it from local into hdfs instead...*

0. Build the hdfs-to-postgres/target/hdfs-to-postgres-1.0.jar with ```cd hdfs-to-postgres``` and ```mvn package``` in a command prompt.
0. Copy the hdfs-to-postgres/target/hdfs-to-postgres-1.0.jar to the docker/hadoop/share directory
0. Copy the docker/hadoop directory to ~/hadoop on your host machine
0. Run ```sh ~/hadoop/docker-up.sh``` on your host machine
0. Run ```sh share/hdfs-to-postgres.sh``` from inside the Hadoop container
0. Verify your data is in PostgreSQL
0. (╯°□°）╯︵ ┻━┻
0. ᕕ( ᐛ )ᕗ
