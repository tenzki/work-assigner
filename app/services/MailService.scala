package services

import com.mohiva.play.silhouette.api._
import models.User
import play.api.Play.current
import play.api.i18n.Lang
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.RequestHeader
import play.twirl.api.{Html, Txt}

import scala.concurrent.duration._

/**
 * Trait for mail sending.
 */
trait MailService[I <: Identity] {

  val fromAddress = current.configuration.getString("smtp.from").get

  def sendWelcomeEmail(user: I)(implicit request: RequestHeader, lang: Lang)

  //  def sendPasswordResetEmail(user: I, token: String)(implicit request: RequestHeader, lang: Lang)
  //
  //  def sendPasswordChangedNotice(user: I)(implicit request: RequestHeader, lang: Lang)

  /**
   * @param subject of the email
   * @param recipient of the email
   * @param body pair with Text and Html email
   */
  def sendEmail(subject: String, recipient: String, body: (Option[Txt], Option[Html])) = {

    play.Logger.debug(s"[silhouette] sending email to $recipient")
    play.Logger.debug(s"[silhouette] mail = [$body]")

    Akka.system.scheduler.scheduleOnce(1 seconds) {
//      val mail = use[MailerPlugin].email
//      mail.setSubject(subject)
//      mail.setRecipient(recipient)
//      mail.setFrom(fromAddress)
//       the mailer plugin handles null / empty string gracefully
//      mail.send(body._1.map(_.body).getOrElse(""), body._2.map(_.body).getOrElse(""))
    }
  }

}

class SimpleMailService extends MailService[User] {

  def sendWelcomeEmail(account: User)(implicit request: RequestHeader, lang: Lang) = {
//    val html = views.html.authentication.mails.welcomeEmail(account)(request, lang)
//    val txtAndHtml = (None, Some(html))
//    sendEmail("Welcome", account.email.get, txtAndHtml)
  }

}