/* SimplePCA.scala */
import scala.io.Source
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.util.MLUtils

// http://spark.apache.org/docs/1.2.1/mllib-dimensionality-reduction.html
// https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/mllib/TallSkinnySVD.scala

object SimplePCA {
  def main(args: Array[String]) {
    if (args.length != 1) {
      System.err.println("Usage: SimplePCA <inputfile>")
      System.exit(1)
    }

    val conf = new SparkConf().setAppName("Simple PCA")
    val sc = new SparkContext(conf)

    // load and parse the data
    val rows = sc.textFile(args(0)).map { line =>
      val values = line.split('\t').map(_.toDouble)
      Vectors.dense(values)
    }

    // create a RowMatrix from an RDD[Vector]
    val mat: RowMatrix = new RowMatrix(rows)

    val m = mat.numRows()
    val n = mat.numCols()
    println("Number of rows in RowMatrix: " + m)
    println("Number of columns in RowMatrix: " + n)

    // compute the top 10 principal components
    // principal components are stored in a local dense matrix.
    val pc: Matrix = mat.computePrincipalComponents(10)

    // project the rows to the linear space spanned by the top 10 principal components
    val projected: RowMatrix = mat.multiply(pc)

    println("Result: " + projected.toString())

    sc.stop()

  }
}
