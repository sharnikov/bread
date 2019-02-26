package ru.bread.settings.schedulers

import java.util.concurrent.ExecutorService

import scala.concurrent.ExecutionContext

trait Context[+E <: ExecutorService] {
  val executor: E
  implicit val context = ExecutionContext.fromExecutor(executor)
}

trait MainContext extends Context[ExecutorService] {
  override implicit val executor = java.util.concurrent.Executors.newWorkStealingPool()
}

trait DatabaseContext extends Context[ExecutorService] {
  override implicit val executor = java.util.concurrent.Executors.newWorkStealingPool()
}

trait ServiceContext extends Context[ExecutorService] {
  override implicit val executor = java.util.concurrent.Executors.newWorkStealingPool()
}

object MainContext extends MainContext

object DatabaseContext extends DatabaseContext

object ServiceContext extends ServiceContext