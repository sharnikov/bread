package modules

import services.{SystemTimeProvider, TimeProvider}

class CommonModule extends Module {
  override def name(): String = "Common"

  val timeProvider: TimeProvider = new SystemTimeProvider()
}
