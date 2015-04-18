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
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter

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


    //val data: RDD[Vector] = ... // note that each Vector is a row and not a column
    val data = rows
    // compute the correlation using Pearson's method
    // note: correlationMatrix is org.apache.spark.mllib.linalg.Matrix
    val correlationMatrix = Statistics.corr(data, "pearson")
    println("Just printing out the correlationMatrix using println")
    println(correlationMatrix)

    // writing to a file
    val resultFile = new java.io.File("/opt/data/correlation-results.txt")
    val output = new BufferedWriter(new FileWriter(resultFile))
    // converting to an array
    val resultArray = correlationMatrix.toArray
    println("Printing each value of the correlationMatrix.toArray")
    // print all the array elements
    for ( x <- resultArray) {
       println(x)
       output.write(x.toString())
    }

    //output.write(correlationMatrix.toString())
    output.close

    sc.stop()

  }
}
