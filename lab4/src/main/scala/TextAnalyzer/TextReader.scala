package TextAnalyzer

import scala.io.Source

class TextReader {
  def read(path: String) = {
    Source.fromFile(path).getLines().toArray.flatMap(_.split(" ")).map(_.filter(ch => ch != '.' && ch != ','))
  }
}
