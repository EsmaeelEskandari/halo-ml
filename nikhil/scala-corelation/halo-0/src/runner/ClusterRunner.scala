package runner

import analyzer.CorrelationMatrix

object ClusterRunner {
  def main(args: Array[String]) {

    val cluster = "spark://master:7077"
    val file1 = "hdfs://master/data/1000.file"
    val file2 = "hdfs://master/test2.list"
    val file3 = "hdfs://master/data/2G.file"
    val file4 = "hdfs://master/dat"

    val file = file4
    //    DTree.run(cluster, file)
    CorrelationMatrix.run(cluster, file)

  }
}