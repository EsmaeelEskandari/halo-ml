/* ckCorrelations.scala */
import scala.io.Source
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.stat.Statistics

// http://spark.apache.org/docs/latest/api/python/pyspark.mllib.html#module-pyspark.mllib.stat
// https://spark.apache.org/docs/latest/mllib-statistics.html

object ckCorrelations {
  def main(args: Array[String]) {
    if (args.length != 1) {
      System.err.println("Usage: ckCorrelations <inputfile>")
      System.exit(1)
    }

    val conf = new SparkConf().setAppName("Check Correlations")
    val sc = new SparkContext(conf)

    // load and parse the data
    val rows = sc.textFile(args(0)).map { line =>
      val values = line.split('\t').map(_.toDouble)
      Vectors.dense(values)
    }


    val data = rows
    val correlationMatrix = Statistics.corr(data, "pearson")
    print(correlationMatrix.toString())


    //val seriesX: RDD[Double] = ... // a series
    //val seriesY: RDD[Double] = ... // must have the same number of partitions and cardinality as seriesX

    // compute the correlation using Pearson's method. Enter "spearman" for Spearman's method. If a
    // method is not specified, Pearson's method will be used by default.
    //val correlation: Double = Statistics.corr(seriesX, seriesY, "pearson")

    //val data: RDD[Vector] = ... // note that each Vector is a row and not a column

    // calculate the correlation matrix using Pearson's method. Use "spearman" for Spearman's method.
    // If a method is not specified, Pearson's method will be used by default.
    //val correlMatrix: Matrix = Statistics.corr(data, "pearson")

    sc.stop()

  }
}
