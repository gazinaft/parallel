package TextAnalyzer

import scala.collection.concurrent
import org.scalameter._

import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.concurrent.TrieMap
import scala.collection.parallel.mutable.{ParHashMap, ParMap}


object AnalyzerExample extends App {
  var tr = new TextReader();

  val standardConfig = config(
    Key.exec.minWarmupRuns := 20,
    Key.exec.maxWarmupRuns := 40,
    Key.exec.benchRuns := 80,
    Key.verbose := false
  ) withWarmer (new Warmer.Default)


  var threshold = 120

  def parallelSum(array: Array[String], start: Int, end: Int, s: ParMap[Int, Int]): (Int, Int, Int) = {
    if (end - start < threshold) sequentialSum(array, start, end, s)
    else {
      val mid = start + ((end - start) / 2)
      val (l, r) = parallel(parallelSum(array, start, mid, s), parallelSum(array, mid, end, s))
      (l._1 + r._1, l._2 + r._2, array.length)
    }
  }

  def parallelMetrics(array: Array[String], tr: TextReader, start: Int, end: Int, threshold: Int,
                      f: (Array[String], Int, Int, ParMap[Int, Int]) => (Int, Int, Int)): ((Int, Int, Int), ParMap[Int, Int]) = {
    if (end - start <= threshold) sequentialMetrics(array: Array[String], tr, start, end, f)
    else {
      val mid = start + ((end - start) / 2)
      val (l, r) = parallel(parallelMetrics(array, tr, start, mid, threshold, f),
        parallelMetrics(array, tr, mid, end, threshold, f))
      val (sum1, sqsum1, len1) = l._1
      val (sum2, sqsum2, len2) = r._1
      val resmap: ParMap[Int, Int] = new ParHashMap[Int, Int]() ++ (l._2.toParArray ++ r._2.toParArray).groupBy(_._1).map { case (k, v) => k -> v.map(_._2).sum }
      ((sum1 + sum2, sqsum1 + sqsum2, len1 + len2), resmap)
    }
  }

  def sequentialMetrics(strings: Array[String], reader: TextReader, start: Int, end: Int,
                        f: (Array[String], Int, Int, ParMap[Int, Int]) => (Int, Int, Int)): ((Int, Int, Int), ParMap[Int, Int]) = {
    val hm = new ParHashMap[Int, Int]()
    var res = (0, 0, 0)
    for (i <- start until end) {
      val text = reader.read(strings(i))
      val (sum, sqsum, len) = f(text, 0, text.length, hm)
      res = (res._1 + sum, res._2 + sqsum, res._3 + len)
    }
    (res, hm)
  }

  def sequentialSum(array: Array[String], start: Int, end: Int, parHashMap: ParMap[Int, Int]): (Int, Int, Int) = {
    var res = 0
    var ressq = 0
    for (i <- (start until end)) {
      val l = array(i).length;
      parHashMap.synchronized {
        if (!parHashMap.contains(l)) parHashMap.put(l, 0)
        parHashMap.put(l, parHashMap.apply(l) + 1);
      }
      res += l
      ressq += (l * l)
    }
    (res, ressq, array.length)
  }

  def resFromMetrics(metrics: ((Int, Int, Int), ParMap[Int, Int])) = {
    val (temp, hm) = metrics
    val (sum, sqsum, size) = temp
    val mean = sum.toDouble / size
    val dispersion = sqsum.toDouble / size
    val mode = hm.reduce((acc, x) => if (acc._2 > x._2) acc else x)._1
    (mean, dispersion, mode)
  }
  //  def analyze(array: Array[String], f: (Array[String], Int, Int, ParHashMap[Int, Int]) => (Int, Int, Int)): (Double, Double, Int) = {
  //    val hm = new ParHashMap[Int, Int]()
  //    val (sum, sqsum) = f(array, 0, array.length, hm)
  //    val mean = sum.toDouble / array.length
  //    val dispersion = sqsum.toDouble / array.length
  //    val mode = hm.reduce((acc, x) => if (acc._2 > x._2) acc else x)._1
  //    (mean, dispersion, mode)
  //  }

  val searchDir = "/run/media/gazinaft/d/prog/parallel/lab4/src/main/res"

  def getListOfFiles(dir: String): Array[String] = {
    val file = new File(dir)
    file.listFiles.filter(_.isFile)
      .map(_.getPath)
  }

  val files = getListOfFiles(searchDir)
  var seqSum: ((Int, Int, Int), ParMap[Int, Int]) = null
  val seq = standardConfig measure {
    seqSum = sequentialMetrics(files, tr, 0, files.length, sequentialSum)
  }
  println(seqSum)
  println("sequential: " + seq)
  var best = (100.0, 100)
  for (i <- 1 to 2) {
    threshold = i
    var parSum: ((Int, Int, Int), ParMap[Int, Int]) = null
    val par = standardConfig measure {
      parSum = parallelMetrics(files, tr, 0, files.length, i, sequentialSum)
    }
    if (par.value < best._1) {
      best = (par.value, threshold)
    }
    println(parSum)
    println("|" + par.value + "|" + threshold + "|")
  }
  println("==============================================")
  var best2 = (100.0, 100)
  for (i <- 2 to 100) {
    threshold = i
    var parSum: ((Int, Int, Int), ParMap[Int, Int]) = null
    val par = standardConfig measure {
      parSum = parallelMetrics(files, tr, 0, files.length, 1, parallelSum)
    }
    if (par.value < best2._1) {
      best = (par.value, threshold)
    }
//    println(parSum)
    println("|" + par.value + "|" + threshold + "|")
  }
    println("Best parallel performance at" + best)
}
