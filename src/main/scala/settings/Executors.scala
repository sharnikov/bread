package settings

import java.util.concurrent.ExecutorService

import scala.concurrent.ExecutionContext

trait Context[+E <: ExecutorService] {
  implicit val executor: E
  implicit val context = ExecutionContext.fromExecutor(executor)
}

trait MainContext extends Context[ExecutorService] {
  override implicit val executor = java.util.concurrent.Executors.newWorkStealingPool()
}

trait DBContext extends Context[ExecutorService] {
  override implicit val executor = java.util.concurrent.Executors.newWorkStealingPool()
}

