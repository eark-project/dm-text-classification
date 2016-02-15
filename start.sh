#!/bin/bash

export HADOOP_CLASSPATH=/usr/lib/hbase/hbase-0.94.2-cdh4.2.2-security.jar:/srv/lily-2.4/lib/org/lilyproject/lily-client/2.4/lily-client-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-repository-remote-impl/2.4/lily-repository-remote-impl-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-repository-api/2.4/lily-repository-api-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-bytes/2.4/lily-bytes-2.4.jar:/srv/lily-2.4/lib/joda-time/joda-time/1.6/joda-time-1.6.jar:/srv/lily-2.4/lib/org/lilyproject/lily-avro-api/2.4/lily-avro-api-2.4.jar:/srv/lily-2.4/lib/org/apache/avro/avro/1.7.1-813-1154/avro-1.7.1-813-1154.jar:/srv/lily-2.4/lib/com/thoughtworks/paranamer/paranamer/2.3/paranamer-2.3.jar:/srv/lily-2.4/lib/org/xerial/snappy/snappy-java/1.0.4.1/snappy-java-1.0.4.1.jar:/srv/lily-2.4/lib/org/apache/avro/avro-ipc/1.7.1-813-1154/avro-ipc-1.7.1-813-1154.jar:/srv/lily-2.4/lib/org/mortbay/jetty/jetty/6.1.26.cloudera.2/jetty-6.1.26.cloudera.2.jar:/srv/lily-2.4/lib/org/mortbay/jetty/jetty-util/6.1.26.cloudera.2/jetty-util-6.1.26.cloudera.2.jar:/srv/lily-2.4/lib/io/netty/netty/3.4.0.Final/netty-3.4.0.Final.jar:/srv/lily-2.4/lib/org/apache/velocity/velocity/1.7/velocity-1.7.jar:/srv/lily-2.4/lib/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:/srv/lily-2.4/lib/commons-lang/commons-lang/2.5/commons-lang-2.5.jar:/srv/lily-2.4/lib/org/mortbay/jetty/servlet-api/2.5-20081211/servlet-api-2.5-20081211.jar:/srv/lily-2.4/lib/org/lilyproject/lily-repository-impl/2.4/lily-repository-impl-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-hbase-client/2.4/lily-hbase-client-2.4.jar:/srv/lily-2.4/lib/commons-configuration/commons-configuration/1.6/commons-configuration-1.6.jar:/srv/lily-2.4/lib/com/github/stephenc/high-scale-lib/high-scale-lib/1.1.1/high-scale-lib-1.1.1.jar:/srv/lily-2.4/lib/com/google/protobuf/protobuf-java/2.4.0a/protobuf-java-2.4.0a.jar:/srv/lily-2.4/lib/org/apache/hadoop/hadoop-common/2.0.0-cdh4.2.0/hadoop-common-2.0.0-cdh4.2.0.jar:/srv/lily-2.4/lib/org/apache/hadoop/hadoop-auth/2.0.0-cdh4.2.0/hadoop-auth-2.0.0-cdh4.2.0.jar:/srv/lily-2.4/lib/org/apache/hadoop/hadoop-core/2.0.0-mr1-cdh4.2.0/hadoop-core-2.0.0-mr1-cdh4.2.0.jar:/srv/lily-2.4/lib/org/apache/hadoop/hadoop-hdfs/2.0.0-cdh4.2.0/hadoop-hdfs-2.0.0-cdh4.2.0.jar:/srv/lily-2.4/lib/org/lilyproject/lily-repository-model/2.4/lily-repository-model-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-json-util/2.4/lily-json-util-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-repository-master/2.4/lily-repository-master-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-pluginregistry-api/2.4/lily-pluginregistry-api-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-repository-id-impl/2.4/lily-repository-id-impl-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-repository-spi/2.4/lily-repository-spi-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-repo-util/2.4/lily-repo-util-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-hbase-util/2.4/lily-hbase-util-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-indexer-sep-filter/2.4/lily-indexer-sep-filter-2.4.jar:/srv/lily-2.4/lib/com/ngdata/hbase-sep-api/1.1/hbase-sep-api-1.1.jar:/srv/lily-2.4/lib/commons-codec/commons-codec/1.4/commons-codec-1.4.jar:/srv/lily-2.4/lib/com/google/guava/guava/14.0.1/guava-14.0.1.jar:/srv/lily-2.4/lib/org/lilyproject/lily-indexer-remote-impl/2.4/lily-indexer-remote-impl-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-indexer-api/2.4/lily-indexer-api-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-zk-util/2.4/lily-zk-util-2.4.jar:/srv/lily-2.4/lib/org/apache/zookeeper/zookeeper/3.4.5-cdh4.2.0/zookeeper-3.4.5-cdh4.2.0.jar:/srv/lily-2.4/lib/jline/jline/0.9.94/jline-0.9.94.jar:/srv/lily-2.4/lib/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:/srv/lily-2.4/lib/org/lilyproject/lily-util/2.4/lily-util-2.4.jar:/srv/lily-2.4/lib/log4j/log4j/1.2.16/log4j-1.2.16.jar:/srv/lily-2.4/lib/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar:/srv/lily-2.4/lib/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar:/srv/lily-2.4/lib/org/lilyproject/lily-import/2.4/lily-import-2.4.jar:/srv/lily-2.4/lib/org/lilyproject/lily-cli-fw/2.4/lily-cli-fw-2.4.jar:/srv/lily-2.4/lib/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:/srv/lily-2.4/lib/commons-io/commons-io/2.1/commons-io-2.1.jar:/srv/lily-2.4/lib/net/iharder/base64/2.3.8/base64-2.3.8.jar:/srv/lily-2.4/lib/org/codehaus/jackson/jackson-core-asl/1.9.2/jackson-core-asl-1.9.2.jar:/srv/lily-2.4/lib/org/codehaus/jackson/jackson-mapper-asl/1.9.2/jackson-mapper-asl-1.9.2.jar:/srv/lily-2.4/lib/org/lilyproject/lily-mapreduce/2.4/lily-mapreduce-2.4.jar
export LIBJARS=`echo ${HADOOP_CLASSPATH} | sed s/:/,/g`
export HADOOP_USER_CLASSPATH_FIRST=true

hadoop jar dm-textclassification-1.0.0-SNAPSHOT-mapreduce-job.jar -libjars ${LIBJARS} -t eark1 -z localhost > text-clf.out 2>&1 &
