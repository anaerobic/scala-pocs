docker run -d -p 2181:2181 -h zookeeper.lacolhost.com --name zookeeper confluent/zookeeper

docker run -d -p 9092:9092 -h kafka.lacolhost.com --name kafka --link zookeeper:zookeeper confluent/kafka