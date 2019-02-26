package ru.bread.modules

import ru.bread.services.internal.{SystemTimeProvider, TimeProvider}

class CommonModule extends Module {
  override def name(): String = "Common"

  val timeProvider: TimeProvider = new SystemTimeProvider()
}
