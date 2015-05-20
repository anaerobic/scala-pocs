#!/bin/bash

cd $HADOOP_PREFIX

#bin/hdfs dfsadmin -safemode leave

bin/hdfs dfs -mkdir /hdfs

bin/hdfs dfs -mkdir /hdfs/race

bin/hdfs dfs -copyFromLocal /share/data/results /hdfs/race/results

bin/hdfs dfs -rm -r /hdfs-output/race/to-postgres

bin/hadoop jar /share/hdfs-to-postgres-1.0.jar app.HdfsToPostgres /hdfs/race /hdfs-output/race/to-postgres

bin/hdfs dfs -cat /hdfs-output/race/to-postgres/*