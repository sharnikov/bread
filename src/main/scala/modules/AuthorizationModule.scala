package modules

import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import database.UserDAOImpl
import services.SimpleAuthorizationService

class AuthorizationModule(dbModule: DatabaseModule) extends Module {
  override def name(): String = "Authorization"

  val sessions: ConcurrentHashMap[String, Date] = new ConcurrentHashMap[String, Date]()

  val userDao = new UserDAOImpl(dbModule.dbSchema)
  val authorizationService = new SimpleAuthorizationService(userDao, sessions)
}
