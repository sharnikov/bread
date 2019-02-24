package settings

import java.util.Date
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.typesafe.scalalogging.LazyLogging
import settings.config.Settings

trait ScheduledTaskManager {
  def start(): Unit
}

class SimpleScheduledTaskManager(settings: Settings, sessions: ConcurrentHashMap[String, Date])
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
        val value = sessions.get(sessionId)
        if (isTooOld(value, currentDate)) sessions.remove(sessionId)
        cleanElements(sessionIds, currentDate)
      }
    }

    override def run(): Unit = {
      val currentDate = new Date(System.currentTimeMillis())
      cleanElements(sessions.keys(), currentDate)
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
