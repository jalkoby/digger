package digger

object CLI {
  def main(args: Array[String]) {
    //val url = readLine("Write Redmine endpoint. For example, `redmine.intersog.com`")
    //val apiKey = readLine(s"Write your api key(Find it on `$url/my/account`)")

    val reports = ReportManager.generate

    reports.foreach {
      (item: ReportItem) => println(s"#${item.date} - ${item.message} - ${item.hours}")
    }
  }
}
