package analyzer

import org.apache.spark.SparkConf
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.SparkContext;

object DTree {

  val featureIndices = Array(0, 1, 2, 3, 4, 23, 33, 35, 36, 37, 38, 40, 41, 42)
  val labelIndex = 39

  def run(master: String, file: String) {
    val appName = "halo-dtree"
    println("Starting app..." + appName)
    val conf = new SparkConf().setAppName(appName).setMaster(master)

    val sc = new SparkContext(conf)
    val lines = sc.textFile(file)
    //    println(lines.first())
    val filtered = lines.map(_.trim).filter(line => !(line.isEmpty || line.startsWith("#")))
    val trainingData = filtered.map(doRow)

    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "variance"
    val maxDepth = 5
    val maxBins = 32
    val model = DecisionTree.trainRegressor(trainingData, categoricalFeaturesInfo,
      impurity, maxDepth, maxBins)

    println("Learned classification tree model:\n" + model.toDebugString)

  }

  def doRow(row: String): LabeledPoint = {
    val allFields = row.split(" ")
    val features = new Array[Double](featureIndices.length)
    for ((index, i) <- featureIndices.zipWithIndex) {
      features(i) = allFields(index).toDouble
    }
    return LabeledPoint(allFields(labelIndex).toDouble, Vectors.dense(features))
  }

}

