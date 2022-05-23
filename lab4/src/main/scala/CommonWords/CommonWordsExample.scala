package CommonWords

import TextAnalyzer.TextReader
import org.scalameter.{Measurer, Warmer, withWarmer}

import scala.collection.mutable
import scala.collection.parallel.CollectionConverters.MutableHashSetIsParallelizable
import scala.collection.parallel.mutable.ParSet

object CommonWordsExample extends App {

  val tr = new TextReader

  def readOneFile(path: String): Set[String] = {
    tr.read(path).toSet
  }

  def seqRead(paths: Array[String], start: Int, end: Int): ParSet[String] = {
    paths.slice(start, end).foldLeft(new mutable.HashSet[String]())((acc, path) => acc.addAll(tr.read(path))).par
  }

  def parRead(paths: Array[String], start: Int, end: Int, threshold: Int): ParSet[String] = {
    if (end - start <= threshold) seqRead(paths, start, end)
    else {
      val mid = (start + end) / 2
      val (l, r) = parallel(parRead(paths, start, mid, threshold), parRead(paths, mid, end, threshold))
      l ++ r
    }
  }

  val paths = Array("/run/media/gazinaft/d/prog/parallel/lab4/src/main/res/text.txt",
    "/run/media/gazinaft/d/prog/parallel/lab4/src/main/res/text2.txt",
    "/run/media/gazinaft/d/prog/parallel/lab4/src/main/res/text3.txt",
    "/run/media/gazinaft/d/prog/parallel/lab4/src/main/res/text4.txt")
  val threshold = 2
  val seq = withWarmer(new Warmer.Default) withMeasurer {
    new Measurer.IgnoringGC
  } measure {
    seqRead(paths, 0, paths.length)
  }
  val par = withWarmer(new Warmer.Default) withMeasurer {
    new Measurer.IgnoringGC
  } measure {
    parRead(paths, 0, paths.length, threshold)
  }

  println(seqRead(paths, 0, paths.length).toSeq)
  println(seq)
  println(parRead(paths, 0, paths.length, threshold).toSeq)
  println(par)
}
