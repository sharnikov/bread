package ru.bread.settings.config

import org.scalamock.scalatest.MockFactory

trait TestSettings extends MockFactory {

  val scheduler = stub[Scheduler]
  val session = stub[Session]
  val akka = stub[Akka]

  val settings = stub[Settings]

  (settings.akkaSettings _).when().returns(akka)
  (settings.schedulerSettings _).when().returns(scheduler)
  (settings.sessionSettings _).when().returns(session)

}
