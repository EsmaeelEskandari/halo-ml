import scala.io.Source
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.commons.io.FileUtils
import org.apache.spark.mllib.tree._
import org.apache.spark.mllib.tree.model._
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
object decision_tree{
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("Decision Tree Feature Importance")
    val sc = new SparkContext(conf)

    val rdd  = sc.textFile("file:///opt/spark/no_header.list")
    /// Maps file to Doubles vector
    val data = rdd.map{ line =>
        // if val can't be cast to Double, put 0 in its place
        def doubleOrZero(string: String) = try { string.toDouble } catch { case _ : Throwable => 0 }

        val values = line.split(" ").map(_.toDouble)
        
        // create a vector out of ranges of the columns we want to trim
        // from the data set
        val toTrim = ((1 to 5) ++ (34 to 43))

        // only keep columns not in toTrim vector (uses for comprehension)
        val trimmedVals = for ((value, index) <- values.zipWithIndex if (!toTrim.contains(index))) yield value

        // construct LabeledPoint wiht label from column 35 of
        // original 'values' vector, and a dense vector from the trimmed-out
        // columns vector
        LabeledPoint(values(35), Vectors.dense(trimmedVals))
    }
  // hyper paramters of decision tree
  val categoricalFeaturesInfo = Map[Int, Int]()
  val impurity = "variance"
  val maxDepth = 10
  val maxBins = 32
  // Train regression tree 
  val model = DecisionTree.trainRegressor(data, categoricalFeaturesInfo, impurity,
      maxDepth, maxBins)
  /*To FIX: Now that we have trained the model we need to get the information gain info from every node
   * Below I print the info for the root node model.topNode, and we can see the info for its children with
   * model.topNode.rightNode and model.topNode.leftNode. But I am unsure how to capture the information of any of the
   * children because it turns into an Option objects from a Node object
   * For each node we would like to return the split information which includes the column of the feature it split on
   * as well as the stats information which is the gain and impurity of the node as well as information about its
   * children. We can calculate the feature importance using the impurity of the node, the impurity of it's children, and the feature that node split on 
   * (the column of the feature)
   * */

  println("Model Depth")
  kprintln(model.depth)
  println("Number of Nodes")
  println(model.numNodes)


  var arr = Array.fill[Double](47)(0)  // how many features do we have? 46
  def recPrint(node: Node): Unit = {
    println(node.id)
    if (!node.isLeaf) {
      var gain = node.stats.get.gain
      var feature = node.split.get.feature
      arr(feature) += gain
      node.leftNode match {
        case Some(value) => recPrint(value)
        case None => null
      }
      node.rightNode match{
        case Some(value) => recPrint(value)
        case None => null
      }
    }
  }
  recPrint(model.topNode);
  println("Feature Importance")
  println(arr.deep);

  //println(model.toDebugString)

  //val numTrees = 5
  //val featureSubsetStrategy = "auto"

  //val rfModel = RandomForest.trainRegressor(data, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
  //println("Random Forest")
  //println(rfModel.toString) 
  //println(rfModel.totalNumNodes)
  //println(rfModel.toDebugString)
  }
}
