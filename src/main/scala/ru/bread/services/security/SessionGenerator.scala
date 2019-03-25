package ru.bread.services.security

import java.security.SecureRandom

import ru.bread.settings.config.Settings

import scala.util.Random

trait SessionGenerator {
  def generateSession(): String
}

class SessionGeneratorImpl(settings: Settings) extends SessionGenerator {

  private val random: ThreadLocal[Random] = ThreadLocal.withInitial(() => new Random(new SecureRandom()))

  override def generateSession(): String = random.get().alphanumeric.take(settings.sessionSettings().size()).mkString
}
