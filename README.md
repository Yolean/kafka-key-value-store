# Queryable Kafka Topics with Kafka Streams

This repository provides the source code for our blog post on [Medium](https://medium.com/bakdata/queryable-kafka-topics-with-kafka-streams-8d2cca9de33f).

It includes an example implementation of a Kafka Streams application that provides a key-value query interface to the messages of a key-partitioned Kafka topic.

Additionally, a Dockerfile and a Kubernetes deployment specification demonstrate how the application can be easily deployed to a Kubernetes Cluster.

## Quick Start

Compile the project using Maven: 
```
mvn package
```

Start one or more instances of the Kafka Streams application:

```
./streams-processor                         \
    --topic messages                        \
    --streams-props                         \
        bootstrap.servers=localhost:9092    \
        num.standby.replicas=1              \
    --application-id my-streams-processor   \
    --hostname localhost
```

Get the latest value for any of your keys:

```
curl http://localhost:8080/messages/[key]
```

## Development

The [build-contract](https://github.com/Yolean/build-contract/) can be used as dev stack.

```
alias compose='docker-compose -f build-contracts/docker-compose.yml'
compose up -d kafka pixy
compose up -d --build cache1
compose up --build example-nodejs-client
compose down
```

Note: Running `build-contract` without warmup doesn't work ATM, due to timing issues at start.

During development of the cache itself or the example nodejs client
it's more convenient to start only `kafka` and `pixy` through docker.

Run the cache service from your IDE with args like: `--port 18081 --streams-props bootstrap.servers=localhost:19092 num.standby.replicas=0 --hostname localhost --topic topic1 --application-id kv-test1-local-001 --onupdate http://127.0.0.1:8081/updated`

Run the nodejs locally using: `cd example-nodejs-client; npm ci; ./node_modules/.bin/jest --watch`
(Note that the mock server for unupdate calls only exists during Jest runs)
