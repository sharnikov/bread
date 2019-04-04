package ru.bread.settings.schedulers

import com.typesafe.scalalogging.LazyLogging
import ru.bread.services.internal.TaskFactory
import ru.bread.settings.config.Settings

trait ScheduledTaskManager {
  def start(): Unit
}

class SimpleScheduledTaskManager(taskFactory: TaskFactory,
                                 settings: Settings) extends ScheduledTaskManager with LazyLogging {

  private val scheduledExecutor = java.util.concurrent.Executors.newScheduledThreadPool(
    settings.schedulerSettings().treadsAmount()
  )

  override def start(): Unit = {
    taskFactory.getTasks().map(task =>
      scheduledExecutor.scheduleAtFixedRate(
        task.task,
        task.firstDelayTime,
        task.repeateRate,
        task.timeUnit
      )
    )
    logger.info("Scheduled manager was started")
  }
}
