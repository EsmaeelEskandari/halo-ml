name := "Simple PCA"
version := "1.0"
scalaVersion := "2.10.4"
libraryDependencies ++= Seq(
   "org.apache.spark" %% "spark-core" % "1.2.0" % "provided",
   "org.apache.spark" % "spark-mllib_2.10" % "1.2.0"
)
resolvers += "Akka Repository" at "http://repo.akka.io/releases/"
