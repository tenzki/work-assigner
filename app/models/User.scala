package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{LoginInfo, Identity}

case class User(
  id: UUID,
  loginInfo: LoginInfo,
  roles: Set[Role] = Set(SimpleAccount)) extends Identity
