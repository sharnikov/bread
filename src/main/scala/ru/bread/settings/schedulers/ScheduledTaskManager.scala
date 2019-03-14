package ru.bread.settings.schedulers

import java.util.Date
import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.LazyLogging
import ru.bread.modules.AuthorizationModule.SessionStorage
import ru.bread.services.internal.TimeProvider
import ru.bread.settings.config.Settings

trait ScheduledTaskManager {
  def start(): Unit
}

class SimpleScheduledTaskManager(sessions: SessionStorage, timeProvider: TimeProvider, settings: Settings)
  extends ScheduledTaskManager with LazyLogging {

  private val scheduledExecutor = java.util.concurrent.Executors.newScheduledThreadPool(
    settings.schedulerSettings().treadsAmount()
  )

  private val sessionsCleanUp = new Runnable {

    def isTooOld(lastSessionTouch: Date, currentDate: Date) = {
      val difference = currentDate.getTime - lastSessionTouch.getTime
      TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS) > settings.sessionSettings().lifetimePeriod().toSeconds
    }

    def cleanElements(sessionIds: java.util.Enumeration[String], currentDate: Date): Unit = {
      if (sessionIds.hasMoreElements) {
        val sessionId = sessionIds.nextElement()
        val session = sessions.get(sessionId)
        if (isTooOld(session.expireDate, currentDate)) sessions.remove(sessionId)
        cleanElements(sessionIds, currentDate)
      }
    }

    override def run(): Unit = {
      cleanElements(sessions.keys(), timeProvider.currentTime)
    }
  }

  override def start(): Unit = {
    scheduledExecutor.scheduleAtFixedRate(
      sessionsCleanUp,
      settings.sessionSettings().lifetimePeriod().toMillis,
      settings.schedulerSettings().repeatRate().toMillis,
      TimeUnit.MILLISECONDS
    )
    logger.info("ScheduledExecutor was started")
  }
}
