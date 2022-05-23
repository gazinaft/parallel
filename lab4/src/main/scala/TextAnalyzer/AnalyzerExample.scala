package TextAnalyzer

import scala.collection.concurrent
import org.scalameter._

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.concurrent.TrieMap
import scala.collection.parallel.mutable.ParHashSet


object AnalyzerExample extends App {
  var tr = new TextReader();

  var threshold = 120

  def parallelAnalyze(array: Array[String], start: Int, end: Int, s: ParHashSet[Int]): Int = {
    if (end - start < threshold) sequentialAnalyze(array, start, end, s)
    else {
      val mid = start + ((end - start) / 2)
      val (l, r) = parallel(parallelAnalyze(array, start, mid, s), parallelAnalyze(array, mid, end, s))
      l + r
    }
  }

  def sequentialAnalyze(array: Array[String], start: Int, end: Int, parHashSet: ParHashSet[Int]): Int = {
    var res = 0
    for (i <- (start until end)) {
      val l = array(i).length
      if (!parHashSet.contains(l)) {
        parHashSet.addOne(l)
      }
      res += array(i).length
    }
    res
  }

  def getResult(s: ParHashSet[Int], sum: Int): Double = {
    try {
      sum / (s.sum.toDouble)
    } catch {
      case _: Throwable => 0
    }
  }
  val textArr = tr.read("/run/media/gazinaft/d/prog/parallel/lab4/src/main/res/text.txt")
  val resSet = new ParHashSet[Int]()
  var seqSum = 0
  val seq = withWarmer(new Warmer.Default) withMeasurer {
    new Measurer.IgnoringGC
  } measure {
    seqSum = sequentialAnalyze(textArr, 0, textArr.length, resSet)
  }
  println(getResult(resSet, seqSum))
  println(seq)
  var best = (100.0, 100)
  for (i <- 10 to 100 by 4) {
    threshold = i
    val resSet2 = new ParHashSet[Int]()
    var parsum = 0
    val par = withWarmer(new Warmer.Default) withMeasurer {
      new Measurer.IgnoringGC
    } measure {
      parsum = parallelAnalyze(textArr, 0, textArr.length, resSet2)
    }
    if (par.value < best._1) {
      best = (par.value, threshold)
    }
    println(getResult(resSet, parsum))
    println(par + " at threshold " + threshold)
  }
  println("Best parallel performance at" + best)
}
