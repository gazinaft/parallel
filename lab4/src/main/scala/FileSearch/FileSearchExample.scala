package FileSearch

import TextAnalyzer.TextReader
import org.scalameter.{Measurer, Warmer, withWarmer}

import java.io.File
import scala.collection.immutable.HashSet


object FileSearchExample extends App {

  val searchDir = "/run/media/gazinaft/d/prog/parallel/lab4/src/main/res"
  val tr = new TextReader

  def getListOfFiles(dir: String): List[String] = {
    val file = new File(dir)
    file.listFiles.filter(_.isFile)
      .map(_.getPath).toList
  }

  def fileFitsKeywords(path: String, keywords: Set[String]) = {
    tr.read(path).exists(keywords.contains)
  }

  def seqSearch(paths: List[String], keywords: Set[String]): List[String] = {
    paths.filter(p => fileFitsKeywords(p, keywords))
  }


  def parSearch(paths: List[String], keywords: Set[String]): List[String] = {
    paths match {
      case Nil => Nil
      case x :: Nil => {
        if (fileFitsKeywords(x, keywords)) paths else Nil
      }
      case x :: xs => {
        val t = task(fileFitsKeywords(x, keywords))
        val l = parSearch(xs, keywords)
        if (t.join) x :: l
        else l
      }
    }
  }

  val keywords = new HashSet[String]() + "computer"
  val paths = getListOfFiles(searchDir)

  val seq = withWarmer(new Warmer.Default) withMeasurer {
    new Measurer.IgnoringGC
  } measure {
    seqSearch(paths, keywords)
  }
  val par = withWarmer(new Warmer.Default) withMeasurer {
    new Measurer.IgnoringGC
  } measure {
    parSearch(paths, keywords)
  }

  println(seqSearch(paths, keywords))
  println(seq)
  println(seqSearch(paths, keywords))
  println(par)
}
