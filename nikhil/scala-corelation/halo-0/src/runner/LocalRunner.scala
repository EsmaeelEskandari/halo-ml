package runner

import analyzer.CorrelationMatrix

object LocalRunner {
  def main(args: Array[String]) {
    val cluster = "local"
    val file = "/home/nikhil/ws/mids/bd/halows/halo-0/100.file"

    CorrelationMatrix.run(cluster, file)

  }
}

