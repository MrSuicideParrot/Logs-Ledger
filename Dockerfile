FROM openjdk:11-jre

COPY target/log-ledger-Node-jar-with-dependencies.jar /root

ENV TRACKER_IP 127.0.0.1
WORKDIR /root
CMD java -jar log-ledger-Node-jar-with-dependencies.jar $TRACKER_IP