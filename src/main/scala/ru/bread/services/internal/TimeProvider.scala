package ru.bread.services.internal

import java.util.Date

import ru.bread.settings.config.Settings

trait TimeProvider {
  def currentTime: Date
}

class SystemTimeProvider(settings: Settings) extends TimeProvider {

  private val millsInHour = 3600000
  private val timeZoneInMills = millsInHour * settings.commonSettings().timeZone()

  def currentTime = new Date(timeZoneInMills + System.currentTimeMillis())
}

class FixedTimeProvider(time: Date) extends TimeProvider {
  def currentTime = time
}

