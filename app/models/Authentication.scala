package models

import com.mohiva.play.silhouette.api.util.{Credentials, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.{OAuth1Info, OAuth2Info}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Case class for signUp element
 */
case class SignUp(
                   identifier: String,
                   password: String,
                   name: Option[String])


object SignUp{

  implicit val format: Format[SignUp] = (
    (__ \ "identifier").format[String](Reads.email.map(_.toLowerCase)) ~
    (__ \ "password").format[String] ~
    (__ \ "name").formatNullable[String])(SignUp.apply, unlift(SignUp.unapply))

}

/**
 * Generic class for Rest Social authentication
 */
case class SocialAuth(
                       token: String,
                       expiresIn: Option[Int],
                       secret: Option[String])

/**
 * Companion object
 */
object SocialAuth {

  implicit val format = (
    (__ \ "accessToken").format[String] ~
    (__ \ "expiresIn").formatNullable[Int] ~
    (__ \ "secret").formatNullable[String])(SocialAuth.apply, unlift(SocialAuth.unapply))

}

/**
 * This class represent token
 *
 * @param token Id of token
 * @param expiresOn The expiration time
 */
case class Token(token: String, expiresOn: DateTime)

/**
 * Companion object, contain format for Json
 */
object Token {

  implicit val jodaDateWrites: Writes[org.joda.time.DateTime] = new Writes[org.joda.time.DateTime] {
    def writes(d: org.joda.time.DateTime): JsValue = JsString(d.toString)
  }

  implicit val jodaDateReads: Reads[org.joda.time.DateTime] = new Reads[org.joda.time.DateTime] {

    def reads(json: JsValue): JsResult[DateTime] =
      JsSuccess(ISODateTimeFormat.dateTimeParser().parseDateTime(json.as[String]))
  }

  implicit val format = Json.format[Token]

}

/**
 * Contain all format for com.mohiva.play.silhouette.api.providers.Credentials type
 */
object CredentialFormat {

  implicit val format = (
    (__ \ "identifier").format[String](Reads.email.map(_.toLowerCase)) ~
    (__ \ "password").format[String])(Credentials.apply, unlift(Credentials.unapply))
}

object OAuth1InfoFormats {

  implicit val format = (
    (__ \ "token").format[String] ~
    (__ \ "secret").format[String])(OAuth1Info.apply, unlift(OAuth1Info.unapply))
}

object OAuth2InfoFormats {

  implicit val format = (
    (__ \ "accessToken").format[String] ~
    (__ \ "tokenType").formatNullable[String] ~
    (__ \ "expiresIn").formatNullable[Int] ~
    (__ \ "refreshToken").formatNullable[String] ~
    (__ \ "params").formatNullable[Map[String, String]])(OAuth2Info.apply, unlift(OAuth2Info.unapply))
}

object PasswordInfoFormats {

  implicit val format = (
    (__ \ "hasher").format[String] ~
    (__ \ "password").format[String] ~
    (__ \ "salt").formatNullable[String])(PasswordInfo.apply, unlift(PasswordInfo.unapply))
}
