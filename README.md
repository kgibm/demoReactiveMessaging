# demoReactiveMessaging

## Development

1. Create Kafka container network if it doesn't exist:
   ```
   podman network create kafka
   ```
1. Start Kafka:
   ```
   podman run --rm -p 9092:9092 -e ALLOW_PLAINTEXT_LISTENER=yes --name kafka --network kafka docker.io/bitnami/kafka
   ```
1. Create the Kafka `prices1` topic:
   ```
   podman run --rm --network kafka -it docker.io/bitnami/kafka kafka-topics.sh --create --topic prices1 --bootstrap-server kafka:9092
   ```
1. Create the Kafka `prices2` topic:
   ```
   podman run --rm --network kafka -it docker.io/bitnami/kafka kafka-topics.sh --create --topic prices2 --bootstrap-server kafka:9092
   ```
1. Build:
   ```
   mvn -Dimage.builder.arguments="--platform linux/amd64" -Dimage.checkpoint.arguments="--network kafka --user root" clean deploy
   ```
1. Run `reactive-service-b`:
   ```
   podman run --privileged --rm --network kafka -e kafka.bootstrap.servers=kafka:9092 --env "CRIU_EXTRA_ARGS=--tcp-close" -it localhost/reactive-service-b:latest
   ```
1. Run `reactive-service-a`:
   ```
   podman run --privileged --rm --network kafka -p 9080:9080 -e kafka.bootstrap.servers=kafka:9092 --env "CRIU_EXTRA_ARGS=--tcp-close" -it localhost/reactive-service-a:latest
   ```
1. Every 30 seconds, it should be visible in respective container logs that `reactive-service-a` is creating a message and `reactive-service-b` is receiving it.
1. A specific message may also be produced by accessing <http://localhost:9080/kafka/produce?price=999>

## Resources

1. <https://developer.ibm.com/articles/develop-reactive-microservices-with-microprofile/>
1. <https://openliberty.io/guides/microprofile-reactive-messaging.html>
1. <https://smallrye.io/smallrye-reactive-messaging/latest/concepts/concepts/>
1. <https://github.com/OpenLiberty/open-liberty/issues/19889>
