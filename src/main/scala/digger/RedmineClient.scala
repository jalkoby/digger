package digger

import scalaj.http.{Http, HttpOptions}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import java.text.SimpleDateFormat

class RedmineClient {
  case class RedmineConnectionException extends Exception
  case class Project(id: Int, name: String)
  case class ProjectList(projects: List[Project])

  implicit val formats = DefaultFormats
  private val url = "http://redmine.intersog.com"
  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  def uploadReports(list: Array[ReportItem]) {
    val apiKey = readLine(s"Write your api key(Find it on `$url/my/account`)")
    val project = selectProject(apiKey)
    for(reportItem <- list) {
      val params = compact(render(
        ("time_entry" ->
          ("project_id" -> project.id) ~
          ("spent_on" -> dateFormat.format(reportItem.date)) ~
          ("hours" -> reportItem.hours) ~
          ("comments" -> reportItem.message)
        )
      ))
      prepareRequest(Http.postData(s"$url/time_entries", params), apiKey).responseCode
    }
  }

  private def selectProject(apiKey: String): Project = {
    val (responseCode, _, jsonProjects) = prepareRequest(Http(s"$url/projects.json"), apiKey).asHeadersAndParse(Http.readString)
    if(responseCode != 200) throw new RedmineConnectionException
    val projects = parse(jsonProjects).extract[ProjectList].projects
    println("Please select project")
    for(project <- projects) println(s"${project.name} -- ${project.id}")
    while(true) {
      val id = readInt
      val selected = projects.find((project: Project) => project.id == id)
      if(selected.isEmpty)
        println("Invalid project id. Try again")
      else
        return selected.get
    }
    null
  }

  private def prepareRequest(request: Http.Request, apiKey: String): Http.Request = {
    request.header("content-type", "application/json").header("X-Redmine-API-Key", apiKey).option(HttpOptions.connTimeout(2000)).option(HttpOptions.readTimeout(4000))
  }
}
