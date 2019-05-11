# Logs-Ledger


### Docker
Compile:

``
$ mvn clean package
$ docker compile . -t node
``

Run:

`$ docker run -e "TRACKER_IP=172.17.0.1" --rm -it log-node`