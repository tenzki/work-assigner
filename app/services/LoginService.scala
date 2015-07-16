package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User

import scala.concurrent.Future

trait LoginService extends IdentityService[User] {

}

class LoginServiceDB extends LoginService {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = ???
}