package services

import java.util.Date

trait TimeProvider {
  def currentTime: Date
}

class SystemTimeProvider extends TimeProvider {
  def currentTime = new Date()
}

class FixedTimeProvider(var time: Date) extends TimeProvider {
  def currentTime = time
}

