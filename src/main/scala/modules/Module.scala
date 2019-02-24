package modules

import com.typesafe.scalalogging.LazyLogging

trait Module extends LazyLogging {
  def name(): String
}
