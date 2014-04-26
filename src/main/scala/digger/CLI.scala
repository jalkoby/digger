package digger

object CLI {
  def main(args: Array[String]) {
    val client = new RedmineClient
    val reports = ReportManager.generate
    client.uploadReports(reports)
  }
}
