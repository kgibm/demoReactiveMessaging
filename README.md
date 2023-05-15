# demoReactiveMessaging

## Development

1. Create Kafka container network if it doesn't exist:
   ```
   podman network create kafka
   ```
1. Start Kafka:
   ```
   podman run --rm -p 9092:9092 -e "ALLOW_PLAINTEXT_LISTENER=yes" -e "KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka-0:9092" --name kafka-0 --network kafka docker.io/bitnami/kafka
   ```
1. Create the Kafka `prices1` topic:
   ```
   podman run --rm --network kafka -it docker.io/bitnami/kafka kafka-topics.sh --create --topic prices1 --bootstrap-server kafka-0:9092
   ```
1. Create the Kafka `prices2` topic:
   ```
   podman run --rm --network kafka -it docker.io/bitnami/kafka kafka-topics.sh --create --topic prices2 --bootstrap-server kafka-0:9092
   ```
1. Build:
   ```
   mvn -Dimage.builder.arguments="--platform linux/amd64" -Dimage.checkpoint.arguments="--network kafka --user root" clean deploy
   ```
1. Run `reactive-service-b`:
   ```
   podman run --privileged --rm --network kafka --env "CRIU_EXTRA_ARGS=--tcp-close" -it localhost/reactive-service-b:latest
   ```
1. Run `reactive-service-a`:
   ```
   podman run --privileged --rm --network kafka -p 9080:9080 --env "CRIU_EXTRA_ARGS=--tcp-close" -it localhost/reactive-service-a:latest
   ```
1. Every 30 seconds, it should be visible in respective container logs that `reactive-service-a` is creating a message and `reactive-service-b` is receiving it.
1. A specific message may also be produced by accessing <http://localhost:9080/kafka/produce?price=999>

## OpenShift

### Pushing Images

```
CLUSTER_DOMAIN_NAME=...
echo $(oc whoami --show-token) | podman login --tls-verify=false -u $(oc whoami) --password-stdin default-route-openshift-image-registry.${CLUSTER_DOMAIN_NAME}
REGISTRY="$(oc get routes -n openshift-image-registry -o jsonpath='{.items[0].spec.host}')"
podman tag localhost/reactive-service-b $REGISTRY/default/reactive-service-b
podman tag localhost/reactive-service-a $REGISTRY/default/reactive-service-a
podman push --tls-verify=false $REGISTRY/default/reactive-service-b
podman push --tls-verify=false $REGISTRY/default/reactive-service-a
oc create serviceaccount privilegedserviceaccount
oc adm policy add-scc-to-user privileged -z privilegedserviceaccount
```

### Example YAMLs

#### kafka.yaml

```
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: kafka
spec:
  selector:
    matchLabels:
      app: kafka
  serviceName: "kafka"
  replicas: 1
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
      - name: kafka
        image: docker.io/bitnami/kafka
        ports:
        - containerPort: 9092
        env:
        - name: ALLOW_PLAINTEXT_LISTENER
          value: "yes"
        - name: KAFKA_CFG_ADVERTISED_LISTENERS
          value: "PLAINTEXT://kafka-0:9092"
---
apiVersion: v1
kind: Service
metadata:
  name: kafka
  labels:
    app: kafka
spec:
  ports:
  - port: 9092
    name: kafka
  clusterIP: None
  selector:
    app: kafka
```

#### reactive-service-a.yaml

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: reactive-service-a
spec:
  replicas: 1
  selector:
    matchLabels:
      name: reactive-service-a
  template:
    metadata:
      labels:
        name: reactive-service-a
    spec:
      serviceAccountName: privilegedserviceaccount
      containers:
        - name: reactive-service-a
          image: image-registry.openshift-image-registry.svc:5000/default/reactive-service-a
          imagePullPolicy: Always
          securityContext:
            privileged: true
          env:
          - name: CRIU_EXTRA_ARGS
            value: "--tcp-close"
```

#### reactive-service-b.yaml

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: reactive-service-b
spec:
  replicas: 1
  selector:
    matchLabels:
      name: reactive-service-b
  template:
    metadata:
      labels:
        name: reactive-service-b
    spec:
      serviceAccountName: privilegedserviceaccount
      containers:
        - name: reactive-service-b
          image: image-registry.openshift-image-registry.svc:5000/default/reactive-service-b
          imagePullPolicy: Always
          securityContext:
            privileged: true
          env:
          - name: CRIU_EXTRA_ARGS
            value: "--tcp-close"
```

## Resources

1. <https://developer.ibm.com/articles/develop-reactive-microservices-with-microprofile/>
1. <https://openliberty.io/guides/microprofile-reactive-messaging.html>
1. <https://smallrye.io/smallrye-reactive-messaging/latest/concepts/concepts/>
1. <https://github.com/OpenLiberty/open-liberty/issues/19889>
