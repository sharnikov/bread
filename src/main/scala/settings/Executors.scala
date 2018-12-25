package settings

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

trait Executors {

  def mainServiceContext(): ExecutionContextExecutor = {
    val pool = java.util.concurrent.Executors.newFixedThreadPool(4)
    ExecutionContext.fromExecutor(pool)
  }

}
