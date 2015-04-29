package parser

import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD

object BolshoiParser {

  val featureIndices = Array(10, 11, 12, 13, 16, 23, 24, 25, 26, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 48, 49, 50, 51, 52, 53, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 68, 69, 70, 71)
  val labelIndex = 39

  def getFeaturesRDD(sc: SparkContext, file: String): RDD[Array[Double]] = {
    val lines = sc.textFile(file)
    val filtered = lines.map(_.trim).filter(line => !(line.isEmpty || line.startsWith("#")))
    return filtered.map(parseRow)
  }

  def parseRow(row: String): Array[Double] = {
    val allFields = row.split(" +")
    val features = new Array[Double](featureIndices.length)
    for ((index, i) <- featureIndices.zipWithIndex) {
      try {
        features(i) = allFields(index).toDouble
      }catch{
        case e: Exception => {
          println(s"Non fatal exception ${e}")
          e.printStackTrace()
          features(i) = 0
         }
      }
    }
    return features
  }

  def printHeader(sc: SparkContext, file: String) {
    val lines = sc.textFile(file)
    val header = lines.first()
    val headerNames = header.split(" +")
    for (index <- featureIndices) {
      print(headerNames(index) + "!")
    }
  }

}