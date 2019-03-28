package ru.bread.services.internal

import java.util.Date

trait TimeProvider {
  def currentTime: Date
}

class SystemTimeProvider(timeZoneNumber: Int) extends TimeProvider {

  private val millsInHour = 3600000
  private val timeZoneInMills = millsInHour * timeZoneNumber

  def currentTime = new Date(timeZoneInMills + System.currentTimeMillis())
}

class FixedTimeProvider(time: Date) extends TimeProvider {
  def currentTime = time
}

