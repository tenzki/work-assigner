package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * An util class, represent a good response, it's all right
 *
 * @param message
 */
class Good(val message: JsValue) {
  def status = "ok"
}

/**
 * Companion object for Good class
 */
object Good {

  def apply(message: String) = new Good(JsString(message))
  def apply(message: JsValue) = new Good(message)
  def unapply(good: Good) = Some((good.status, good.message))

  /**
   * Rest format
   */
  implicit val restFormat: Format[Good] = {
    /** because of single value of read, i have to do map, it's a bug of play's json library, but don't worry ;)*/
    val reads: Reads[Good] = (
      (__ \ "message").read[JsValue]).map(Good.apply)

    import play.api.libs.json.Writes._
    val writes: Writes[Good] = (
      (__ \ "status").write[String] ~
        (__ \ "message").write[JsValue])(unlift(Good.unapply))

    Format(reads, writes)
  }

}

/**
 * An util class, represent a bad response, not good
 *
 * @param code of error
 * @param message an object that expose the errors
 */
class Bad(val code: Option[Int], val message: JsValue, val description: Option[JsValue]) {
  def status = "ko"
}

/**
 * Companion object for Good class
 */
object Bad {

  def apply(code: Option[Int], message: JsValue, description: Option[JsValue]) = new Bad(code, message, description)
  def apply(code: Option[Int] = None, message: String, description: Option[String] = None) = new Bad(code, JsString(message), description.map(JsString))
  def apply(message: JsValue) = new Bad(None, message, None)
  def unapply(bad: Bad) = Some((bad.status, bad.code, bad.message, bad.description))

  /**
   * Rest format
   */
  implicit val restFormat: Format[Bad] = {
    val reader: Reads[Bad] = (
      (__ \ "code").readNullable[Int] ~
        (__ \ "message").read[JsValue] ~
        (__ \ "description").readNullable[JsValue])(Bad.apply(_, _, _))

    val writer: Writes[Bad] = (
      (__ \ "status").write[String] ~
        (__ \ "code").writeNullable[Int] ~
        (__ \ "message").write[JsValue] ~
        (__ \ "description").writeNullable[JsValue])(unlift(Bad.unapply))

    Format(reader, writer)
  }

}
