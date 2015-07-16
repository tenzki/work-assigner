package controllers

import javax.inject.Inject

import _root_.services.LoginService
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.{ConfigurationException, ProviderException}
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import models._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

class SecurityController @Inject() (
  implicit val env: Environment[User, JWTAuthenticator],
  val loginService: LoginService)
  extends Silhouette[User, JWTAuthenticator] {


  implicit val credentialFormat = models.CredentialFormat.format

  /**
   * Authenticates a user against the credentials provider.
   *
   * receive json like this:
   * {
   * 	"identifier": "...",
   *  "password": "..."
   * }
   *
   * @return The result to display.
   */
  def authenticate = Action.async(parse.json) { implicit request =>
    request.body.validate[Credentials].map { credentials =>
      (env.providers.get(CredentialsProvider.ID) match {
        case Some(p: CredentialsProvider) => p.authenticate(credentials)
        case _ => Future.failed(new ConfigurationException("Cannot find credentials provider"))
      }).flatMap { loginInfo =>
        loginService.retrieve(loginInfo).flatMap {
          case Some(user) => env.authenticatorService.create(user.loginInfo).flatMap { authenticator =>
            env.eventBus.publish(LoginEvent(user, request, request2lang))
            env.authenticatorService.init(authenticator).flatMap { token =>
              env.authenticatorService.embed(token, Future.successful {
                Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDate)))
              })
            }
          }
          case None => Future.failed(new IdentityNotFoundException("Couldn't find account."))
        }
      }.recover {
        case e: ProviderException => Unauthorized(Json.toJson(Bad(message = "Invalid credentials!")))
      }
    }.recoverTotal {
      case error =>
        Future.successful(BadRequest(Json.toJson(Bad(message = JsError.toFlatJson(error)))))
    }
  }

  /**
   * Registers a new user.
   *
   * receive call with json like this:
   * 	{
   * 		"password": "",
   * 		"identifier": "",
   *    "name": ""
   * 	}
   *
   * @return The result to display.
   */
  def signUp = Action.async(parse.json) { implicit request =>
    null
//    request.body.validate[SignUp].map { signUp =>
//      val loginInfo = LoginInfo(CredentialsProvider.ID, signUp.identifier)
//      loginService.retrieve(loginInfo).flatMap {
//        case None => /* user not already exists */
//          val authInfo = passwordHasher.hash(signUp.password)
//          for {
//            accountToSave <- loginService.create(loginInfo, signUp)
//            account <- loginService.save(accountToSave)
//            authInfo <- authInfoService.save(loginInfo, authInfo)
//            authenticator <- env.authenticatorService.create(loginInfo)
//            token <- env.authenticatorService.init(authenticator)
//            result <- env.authenticatorService.embed(token, Future.successful {
//              Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDate)))
//            })
//          } yield {
//            env.eventBus.publish(SignUpEvent(account, request, request2lang))
//            env.eventBus.publish(LoginEvent(account, request, request2lang))
//            mailService.sendWelcomeEmail(account)
//            result
//          }
//        case Some(u) => /* user already exists! */
//          Future.successful(Conflict(Json.toJson(Bad(message = "User already exists."))))
//      }
//    }.recoverTotal {
//      case error =>
//        Future.successful(BadRequest(Json.toJson(Bad(message = JsError.toFlatJson(error)))))
//    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = SecuredAction.async { implicit request =>
    env.eventBus.publish(LogoutEvent(request.identity, request, request2lang))
    request.authenticator.discard(Future.successful(Ok))
  }

  /**
   * Authenticates a user against a social provider.
   *
   * @param provider The ID of the provider to authenticate against.
   * @return The result to display.
   */
  def authenticateSocial(provider: String) = Action.async(parse.json) { implicit request =>
    null
//    (env.providers.get(provider) match {
//      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
//        p.authenticate().flatMap {
//          case Left(result) => Future.successful(result)
//          case Right(authInfo) => for {
//            profile <- p.retrieveProfile(authInfo)
//            user <- loginService.save(profile)
//            authInfo <- authInfoService.save(profile.loginInfo, authInfo)
//            authenticator <- env.authenticatorService.create(user.loginInfo)
//            token <- env.authenticatorService.init(authenticator)
//            result <- env.authenticatorService.embed(token, Future.successful {
//              Ok(Json.toJson(Token(token = token, expiresOn = authenticator.expirationDate)))
//            })
//          } yield {
//              env.eventBus.publish(LoginEvent(user, request, request2lang))
//              result
//            }
//        }
//      case _ => Future.failed(new ConfigurationException(s"Cannot authenticate with unexpected social provider $provider"))
//    }).recoverWith(exceptionHandler)
  }

  /**
   * Link social with a existing user.
   *
   * receive json like this:
   * {
   * 	"accessToken": "...",
   *  	"expiresIn": 0000, //optional
   *  	"secret": "..."  //this is for OAuth1, for OAuth2 isn't request
   * }
   *
   * @param provider The ID of the provider to authenticate against.
   * @return The result to display.
   */
  def link(provider: String) = SecuredAction.async(parse.json) { implicit request =>
    null
//    request.body.validate[SocialAuth].map { socialAuth =>
//      profileAndAuthInfo(provider, socialAuth).flatMap {
//        case (profile: CommonSocialProfile, authInfo: AuthInfo) =>
//          for {
//            account <- loginService.link(request.identity, profile)
//            authInfo <- authInfoService.save(profile.loginInfo, authInfo)
//          } yield {
//            Ok(Json.toJson(Good(message = "link with social completed!")))
//          }
//      }.recoverWith(exceptionHandler)
//    }.recoverTotal {
//      case error => Future.successful(BadRequest(Json.obj("message" -> JsError.toFlatJson(error))))
//    }
  }

  /**
   * Util method to use for retrieve information from authInfo
   *
   * @param provider where retrieve information
   * @param socialAuth object where get auth information
   * @return a pair with CommonSocialProfile and AuthInfo
   */
  protected def profileAndAuthInfo(provider: String, socialAuth: SocialAuth) = {
    null
//    env.providers.get(provider) match {
//      case Some(p: OAuth1Provider with CommonSocialProfileBuilder) => //for OAuth1 provider type
//        val authInfo = OAuth1Info(token = socialAuth.token, socialAuth.secret.get)
//        p.retrieveProfile(authInfo).map(profile => (profile, authInfo))
//      case Some(p: OAuth2Provider with CommonSocialProfileBuilder) => //for OAuth2 provider type
//        val authInfo = OAuth2Info(accessToken = socialAuth.token, expiresIn = socialAuth.expiresIn)
//        p.retrieveProfile(authInfo).map(profile => (profile, authInfo))
//      case _ => Future.successful(new ConfigurationException(s"Cannot retrive information with unexpected social provider $provider"))
//    }
  }

}
