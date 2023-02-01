#!/bin/bash
rm -f /etc/init.d/datasophon-worker
ps -ef | grep java | grep -v  grep | awk '{print $2}' | xargs kill -9
ps -ef | grep grafana | grep -v  grep | awk '{print $2}' | xargs kill -9
ps -ef | grep prometheus | grep -v  grep | grep prometheus.yml | awk '{print $2}' | xargs kill -9
rm -rf /opt/datasophon
rm -rf /data/dfs
rm -rf /data/log/version-2
rm -rf /data/tmp/hadoop
rm -rf /data/zookeeper