package analyzer

import org.apache.spark.SparkConf
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.SparkContext;
import parser.BolshoiParser

object CorrelationMatrix {

  def run(master: String, file: String) {

    val conf = new SparkConf().setAppName("CorrelationMatrix").setMaster(master)
    val sc = new SparkContext(conf)
    println("About to start CorrelationMatrix data load")

    val features = BolshoiParser.getFeaturesRDD(sc, file);

    val numRows = features.count();
    val numFeatures = features.first.length
    println(s"Number of rows ${numRows}")
    println(s"Number of features ${numFeatures}")

    val correlations = Array.ofDim[Double](numFeatures, numFeatures)

    0 to numFeatures - 1 map { i =>
      i+1 to numFeatures - 1 map { j =>
        println(s"Starting...${i}, ${j} - ${curtime()}")
        val startTime = java.lang.System.currentTimeMillis()
        val labelRDD = features.map(_(i))
        val featureRDD = features.map(_(j))
        val corr = Statistics.corr(labelRDD, featureRDD)
        correlations(i)(j) = corr
        correlations(j)(i) = corr;
        val endTime = java.lang.System.currentTimeMillis()
        val timeElapsed = endTime - startTime
        println(s"Completed...${i}, ${j} - ${curtime()} in ${timeElapsed}ms")
      }
    }
    println(correlations.map(_.mkString(" ")).mkString("\n"))
    BolshoiParser.printHeader(sc, file)
    sc.stop()
  }

  def curtime() = {
    val today = java.util.Calendar.getInstance().getTime()
    val format = new java.text.SimpleDateFormat("hh-mm-ss")
    val currentMinuteAsString = format.format(today)
    currentMinuteAsString
  }
}

