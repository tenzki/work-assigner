package modules

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.services.{AuthInfoService, AuthenticatorService}
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.daos.{CacheAuthenticatorDAO, DelegableAuthInfoDAO}
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.providers.oauth1.TwitterProvider
import com.mohiva.play.silhouette.impl.providers.oauth1.secrets.{CookieSecretProvider, CookieSecretSettings}
import com.mohiva.play.silhouette.impl.providers.oauth1.services.PlayOAuth1Service
import com.mohiva.play.silhouette.impl.providers.oauth2.state.DummyStateProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.{FacebookProvider, GoogleProvider}
import com.mohiva.play.silhouette.impl.services.DelegableAuthInfoService
import com.mohiva.play.silhouette.impl.util.{BCryptPasswordHasher, DefaultFingerprintGenerator, PlayCacheLayer, SecureRandomIDGenerator}
import models.User
import net.codingwell.scalaguice.ScalaModule
import play.api.Play
import play.api.Play.current
import services.{SimpleMailService, MailService, LoginServiceDB, LoginService}

import scala.collection.immutable.ListMap

class SilhouetteModule extends AbstractModule with ScalaModule{

  /**
   * Configures the module.
   */
  def configure() {
    bind[LoginService].to[LoginServiceDB]
    bind[MailService[User]].to[SimpleMailService]
//    bind[AccountDAO].to[AccountDAODB]
//    bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDAO]
//    bind[DelegableAuthInfoDAO[OAuth1Info]].to[OAuth1InfoDAO]
//    bind[DelegableAuthInfoDAO[OAuth2Info]].to[OAuth2InfoDAO]
//    bind[TimezoneDAO].to[TimezoneDAODB]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[HTTPLayer].to[PlayHTTPLayer]
    bind[OAuth2StateProvider].to[DummyStateProvider]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
  }

  /**
   * Provides the Silhouette environment.
   *
   * @param loginService The user service implementation.
   * @param authenticatorService The authentication service implementation.
   * @param eventBus The event bus instance.
   * @param credentialsProvider The credentials provider implementation.
   * @param facebookProvider The Facebook provider implementation.
   * @param googleProvider The Google provider implementation.
   * @param twitterProvider The Twitter provider implementation.
   * @return The Silhouette environment.
   */
  @Provides
  def provideEnvironment(
                          loginService: LoginService,
                          authenticatorService: AuthenticatorService[JWTAuthenticator],
                          eventBus: EventBus,
                          credentialsProvider: CredentialsProvider,
                          facebookProvider: FacebookProvider,
                          googleProvider: GoogleProvider,
                          twitterProvider: TwitterProvider): Environment[User, JWTAuthenticator] = {

    Environment[User, JWTAuthenticator](
      loginService,
      authenticatorService,
      ListMap(
        credentialsProvider.id -> credentialsProvider,
        googleProvider.id -> googleProvider,
        facebookProvider.id -> facebookProvider,
        twitterProvider.id -> twitterProvider
      ),
      eventBus
    )
  }

  /**
   * Provides the authenticator service.
   *
   * @param cacheLayer The cache layer implementation.
   * @param idGenerator The ID generator used to create the authenticator ID.
   * @return The authenticator service.
   */
  @Provides
  def provideAuthenticatorService(
                                   cacheLayer: CacheLayer,
                                   idGenerator: IDGenerator,
                                   fingerprintGenerator: FingerprintGenerator): AuthenticatorService[JWTAuthenticator] = {

    new JWTAuthenticatorService(JWTAuthenticatorSettings(
      headerName = Play.configuration.getString("silhouette.authenticator.headerName").getOrElse { "X-Auth-Token" },
      issuerClaim = Play.configuration.getString("silhouette.authenticator.issueClaim").getOrElse { "play-silhouette" },
      encryptSubject = Play.configuration.getBoolean("silhouette.authenticator.encryptSubject").getOrElse { true },
      authenticatorIdleTimeout = Play.configuration.getInt("silhouette.authenticator.authenticatorIdleTimeout"),
      authenticatorExpiry = Play.configuration.getInt("silhouette.authenticator.authenticatorExpiry").getOrElse { 12 * 60 * 60 },
      sharedSecret = Play.configuration.getString("application.secret").get
    ), Some(new CacheAuthenticatorDAO[JWTAuthenticator](cacheLayer)), idGenerator, Clock())
  }

  /**
   * Provides the auth info service.
   *
   * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
   * @param oauth1InfoDAO The implementation of the delegable OAuth1 auth info DAO.
   * @param oauth2InfoDAO The implementation of the delegable OAuth2 auth info DAO.
   * @return The auth info service instance.
   */
  @Provides
  def provideAuthInfoService(
                              passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo],
                              oauth1InfoDAO: DelegableAuthInfoDAO[OAuth1Info],
                              oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info]): AuthInfoService = {

    new DelegableAuthInfoService(passwordInfoDAO, oauth1InfoDAO, oauth2InfoDAO)
  }

  /**
   * Provides the OAuth1 token secret provider.
   *
   * @return The OAuth1 token secret provider implementation.
   */
  @Provides
  def provideOAuth1TokenSecretProvider: OAuth1TokenSecretProvider = {
    new CookieSecretProvider(CookieSecretSettings(
      cookieName = Play.configuration.getString("silhouette.oauth1TokenSecretProvider.cookieName").get,
      cookiePath = Play.configuration.getString("silhouette.oauth1TokenSecretProvider.cookiePath").get,
      cookieDomain = Play.configuration.getString("silhouette.oauth1TokenSecretProvider.cookieDomain"),
      secureCookie = Play.configuration.getBoolean("silhouette.oauth1TokenSecretProvider.secureCookie").get,
      httpOnlyCookie = Play.configuration.getBoolean("silhouette.oauth1TokenSecretProvider.httpOnlyCookie").get,
      expirationTime = Play.configuration.getInt("silhouette.oauth1TokenSecretProvider.expirationTime").get
    ), Clock())
  }

  /**
   * Provides the credentials provider.
   *
   * @param authInfoService The auth info service implemenetation.
   * @param passwordHasher The default password hasher implementation.
   * @return The credentials provider.
   */
  @Provides
  def provideCredentialsProvider(
                                  authInfoService: AuthInfoService,
                                  passwordHasher: PasswordHasher): CredentialsProvider = {

    new CredentialsProvider(authInfoService, passwordHasher, Seq(passwordHasher))
  }

  /**
   * Provides the Facebook provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param stateProvider The OAuth2 state provider implementation.
   * @return The Facebook provider.
   */
  @Provides
  def provideFacebookProvider(httpLayer: HTTPLayer, stateProvider: OAuth2StateProvider): FacebookProvider = {
    FacebookProvider(httpLayer, stateProvider, OAuth2Settings(
      accessTokenURL = Play.configuration.getString("silhouette.facebook.accessTokenURL").get,
      redirectURL = Play.configuration.getString("silhouette.facebook.redirectURL").get,
      clientID = Play.configuration.getString("silhouette.facebook.clientID").getOrElse(""),
      clientSecret = Play.configuration.getString("silhouette.facebook.clientSecret").getOrElse(""),
      scope = Play.configuration.getString("silhouette.facebook.scope")))
  }

  /**
   * Provides the Google provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param stateProvider The OAuth2 state provider implementation.
   * @return The Google provider.
   */
  @Provides
  def provideGoogleProvider(httpLayer: HTTPLayer, stateProvider: OAuth2StateProvider): GoogleProvider = {
    GoogleProvider(httpLayer, stateProvider, OAuth2Settings(
      accessTokenURL = Play.configuration.getString("silhouette.google.accessTokenURL").get,
      redirectURL = Play.configuration.getString("silhouette.google.redirectURL").get,
      clientID = Play.configuration.getString("silhouette.google.clientID").getOrElse(""),
      clientSecret = Play.configuration.getString("silhouette.google.clientSecret").getOrElse(""),
      scope = Play.configuration.getString("silhouette.google.scope")))
  }

  /**
   * Provides the Twitter provider.
   *
   * @param httpLayer The HTTP layer implementation.
   * @param tokenSecretProvider The token secret provider implementation.
   * @return The Twitter provider.
   */
  @Provides
  def provideTwitterProvider(httpLayer: HTTPLayer, tokenSecretProvider: OAuth1TokenSecretProvider): TwitterProvider = {
    val settings = OAuth1Settings(
      requestTokenURL = Play.configuration.getString("silhouette.twitter.requestTokenURL").get,
      accessTokenURL = Play.configuration.getString("silhouette.twitter.accessTokenURL").get,
      authorizationURL = Play.configuration.getString("silhouette.twitter.authorizationURL").get,
      callbackURL = Play.configuration.getString("silhouette.twitter.callbackURL").get,
      consumerKey = Play.configuration.getString("silhouette.twitter.consumerKey").getOrElse(""),
      consumerSecret = Play.configuration.getString("silhouette.twitter.consumerSecret").getOrElse(""))

    TwitterProvider(httpLayer, new PlayOAuth1Service(settings), tokenSecretProvider, settings)
  }

}
