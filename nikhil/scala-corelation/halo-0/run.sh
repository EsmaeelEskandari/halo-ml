cd bin
rm /tmp/hal.jar
zip -r /tmp/hal.jar *
cd ..
cat /tmp/hal.jar | ssh root@master "hdfs dfs -rm /hal.jar"
cat /tmp/hal.jar | ssh root@master "hdfs dfs -put - /hal.jar"
../spark-lib/bin/spark-submit --class runner.ClusterRunner  --deploy-mode cluster --master spark://master:7077 hdfs://master/hal.jar
