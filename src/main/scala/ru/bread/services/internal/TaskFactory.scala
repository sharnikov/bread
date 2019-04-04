package ru.bread.services.internal

import java.util.Date
import java.util.concurrent.TimeUnit

import ru.bread.modules.AuthorizationModule.SessionStorage
import ru.bread.services.internal.TaskFactory.ScheduledTask
import ru.bread.settings.config.UpdatableAppSettings

trait TaskFactory {
  def getTasks(): Seq[ScheduledTask]
}

class TaskFactoryImpl(sessions: SessionStorage,
                      timeProvider: TimeProvider,
                      updatableSettings: UpdatableAppSettings) extends TaskFactory {

  override def getTasks(): Seq[ScheduledTask] = Seq(
    ScheduledTask(
      task = sessionsCleanUp(),
      firstDelayTime = updatableSettings.sessionSettings().lifetimePeriod().toMillis,
      repeateRate = updatableSettings.schedulerSettings().repeatRate().toMillis,
      timeUnit = TimeUnit.MILLISECONDS
    ),
    ScheduledTask(
      task = configUpdate(),
      firstDelayTime = updatableSettings.schedulerSettings().configRepeatRate().toMillis,
      repeateRate = updatableSettings.schedulerSettings().configRepeatRate().toMillis,
      timeUnit = TimeUnit.MILLISECONDS
    )
  )

  private def sessionsCleanUp() = new Runnable {

    def isTooOld(lastSessionTouch: Date, currentDate: Date) = {
      val difference = currentDate.getTime - lastSessionTouch.getTime
      TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS) > updatableSettings.sessionSettings().lifetimePeriod().toSeconds
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

  private def configUpdate() = new Runnable {
    override def run(): Unit = {
      updatableSettings.update()
    }
  }

}

object TaskFactory {
  case class ScheduledTask(task: Runnable,
                           firstDelayTime: Long,
                           repeateRate: Long,
                           timeUnit: TimeUnit)
}
