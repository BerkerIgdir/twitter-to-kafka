#!/bin/bash
# check-elastic-cluster-up.sh
apt-get update -y

yes | apt-get install curl
sleep 10

curlResult=$(curl -X GET  http://elastic-1:9200/_cluster/health)

echo "result status code:" "$curlResult"

while [[ ! "$curlResult" == *"green"* ]]; do
  >&2 echo "Elastic cluster is not up yet!"
  sleep 10
  curlResult=$(curl -X GET  http://elastic-1:9200/_cluster/health)
done
echo "Elastic cluster is up!"
echo "Service is being started"
/cnb/process/web