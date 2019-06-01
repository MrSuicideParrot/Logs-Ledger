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
#
##### Work done by:
###### [André Cirne](https://sigarra.up.pt/fcup/pt/fest_geral.cursos_list?pv_num_unico=201505860)
###### [José Rocha](https://sigarra.up.pt/fcup/pt/fest_geral.cursos_list?pv_num_unico=201503229)