package ru.bread.modules

import ru.bread.Starter.{authorizationModule, commonModule}
import ru.bread.modules.AuthorizationModule.SessionStorage
import ru.bread.services.internal.{TaskFactoryImpl, TimeProvider}
import ru.bread.settings.config.UpdatableAppSettings
import ru.bread.settings.schedulers.SimpleScheduledTaskManager

class SchedulerModule(sessions: SessionStorage,
                      timeProvider: TimeProvider,
                      updatableSettings: UpdatableAppSettings) extends Module {

  override def name(): String = "Scheduler"

  val taskFactory = new TaskFactoryImpl(authorizationModule.sessions, commonModule.timeProvider, updatableSettings)
  val schedule = new SimpleScheduledTaskManager(taskFactory, updatableSettings)
  logger.info("Scheduled manager was initialized")
  schedule.start()

}
