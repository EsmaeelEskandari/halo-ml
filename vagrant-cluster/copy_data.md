To load data to hdfs cluster created using the vagrant script:

- First ssh to the machine where you have the preprocessed data and go to the directory where you have the file that needs to be put into hdfs. Let the name of the file be `test_data_processed.txt`
- From the softlayer manager get the ip and the password for your master node.
- Execute the following command to copy the file to hdfs

     `cat test_data_processed.txt | ssh root@108.168.160.50 "hadoop fs -put - /test_data_processed.txt"`


Now after you login to the master node using either `vagrant ssh master` or by directly sshing to the master node, you can see the newly uploaded file with the following command

     hadoop fs -ls /



