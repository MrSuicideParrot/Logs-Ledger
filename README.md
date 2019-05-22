# Logs-Ledger


### Docker
Compile:

``
$ mvn clean package
$ docker compile . -t node
``

Run:

Consensus could be:
* PoS - Proof of Stake
* PoW - Proof of Work

`$ docker run -e "TRACKER_IP=172.17.0.1" -e "CONSENSUS=PoS" --rm -it log-node`