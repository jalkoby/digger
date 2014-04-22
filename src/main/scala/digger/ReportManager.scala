package digger

import java.util.{Calendar, GregorianCalendar, Date}
import scala.collection.mutable.ArrayBuffer

case class ReportItem(message: String, hours: Int, date: Date)

object ReportManager {
  class ReportForm {
    import java.text.SimpleDateFormat

    private var message = ""
    private var hours = 0
    private val defaultHours = 8
    private var usePrev = false
    private var skipAsk = false
    private val dateFormater = new SimpleDateFormat("dd/MM/yyyy")

    def next(date: Date): ReportItem = {
      val day = dateFormater.format(date)
      if(!usePrev) {
        message = readLine(s"Enter report message for $day")
        hours = getDayHours(day)
        if(!skipAsk) {
          val autoComplete = readLine("Apply this message for next days([yes]/no/skip)?").toLowerCase
          if(autoComplete.contains("y"))
            usePrev = true
          else if(autoComplete.contains("sk"))
            skipAsk = true
        }
      }

      new ReportItem(message, hours, date)
    }

    private def getDayHours(day: String): Int = {
      val rawHours = readLine(s"Enter time for $day", defaultHours)
      if(rawHours.isEmpty) defaultHours else rawHours.toInt
    }
  }

  private val currentMonth = new GregorianCalendar
  private val weekends = Array(Calendar.SATURDAY, Calendar.SUNDAY)

  def generate(): Array[ReportItem] = {
    val startDay = readDate("Specify start")
    val endDay = readDate("Specify end")
    val excludeDays = readDates("Specify exclude dates")
    val report = new ArrayBuffer[ReportItem]
    val form = new ReportForm
    val useDay = (day: Calendar) => {
      if(excludeDays.contains(day)) false
      else !weekends.contains(day.get(Calendar.DAY_OF_WEEK))
    }

    while(!startDay.after(endDay)) {
      if(useDay(startDay)) report += form.next(startDay.getTime)
      startDay.add(Calendar.DAY_OF_MONTH, 1)
    }

    report.toArray
  }

  private def readDate(message: String): Calendar = {
    parseDate(readLine(s"$message (dd[/mm[/yyyy]])"))
  }

  private def readDates(message: String): Array[Calendar] = {
    val input = readLine(s"$message (#date1 #date1 #date3)")
    for(date <- ("\\S+".r findAllIn input).toArray) yield parseDate(date)
  }

  private def parseDate(input: String): Calendar = {
    val parts = ("\\d+".r findAllIn input).toArray
    val or = (i: Int, section: Int) =>
      if(i < parts.length) parts(i).toInt else currentMonth.get(section)
    new GregorianCalendar(or(2, Calendar.YEAR), or(1, Calendar.MONTH), or(0, Calendar.DAY_OF_MONTH), 0, 0 , 0)
  }
}
