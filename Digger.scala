import java.util.{Calendar, GregorianCalendar}
import scala.collection.mutable.HashMap

object Digger {
  def main(args: Array[String]) {
    //val url = readLine("Write Redmine endpoint. For example, `redmine.intersog.com`")
    //val apiKey = readLine(s"Write your api key(Find it on `$url/my/account`)")

    val month = getStartDate
    val endDay = getEndDay
    val excludeDays = getExcludeDays
    val startDay = month.get(Calendar.DAY_OF_MONTH)
    val weekends = Array(Calendar.SATURDAY, Calendar.SUNDAY)
    var days = (startDay to endDay).filter((day: Int) => {
      month.set(Calendar.DAY_OF_MONTH, day)
      if(excludeDays.contains(day)) false
      else !weekends.contains(month.get(Calendar.DAY_OF_WEEK))
    })

    val report = new HashMap[Int, String]
    var message = ""
    var usePrev = false
    var skipAsk = false
    for(day <- days) {
      if(!usePrev) {
        message = readLine(s"Enter report message for $day")
        if(!skipAsk) {
          val autoComplete = readLine("Apply this message for next days([yes]/no/skip)?").toLowerCase
          if(autoComplete.contains("y"))
            usePrev = true
          else if(autoComplete.contains("sk"))
            skipAsk = true
        }
      }

      report.update(day, message)
    }

    report.foreach { case(day: Int, message: String) => println(s"#$day - $message") }
  }

  def getStartDate(): Calendar = {
    val rawInput = readLine("Enter start day(default value is 1)")
    val startDay = if(rawInput.isEmpty) 1 else rawInput.toInt
    val date = new GregorianCalendar
    date.set(Calendar.DAY_OF_MONTH, startDay)
    date
  }

  def getExcludeDays(): Array[Int] = {
    "\\d+".r.findAllIn(readLine("Write exclude days separated by comma or spaces")).map((x: String) => x.toInt).toArray
  }

  def getEndDay: Int = {
    val maxDay = (new GregorianCalendar).getMaximum(Calendar.DAY_OF_MONTH)
    val rawDate = readLine(s"Write end date(default is $maxDay)")
    if(rawDate.isEmpty) maxDay else rawDate.toInt
  }

  def readLine(message: String): String = {
    Console.readLine(s"$message : ")
  }
}
