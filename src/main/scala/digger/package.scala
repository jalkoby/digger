package object digger {
  def readLine(message: String): String = {
    Console.readLine(s"$message : ")
  }

  def readLine(message: String, default: Any): String = {
    readLine(s"$message (default value is $default)")
  }
}
