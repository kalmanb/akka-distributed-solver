package com.kalmanb.solver

import akka.actor._

class Worker(controller: ActorRef) extends Actor with ActorLogging {
  import Controller._
  import Solver._

  val batchSize = 10

  // Tell controller I'm ready
  controller ! ReadyForWork(batchSize)

  def receive = {
    // Controller responds with work for me
    case work: Work ⇒ {
      goodSolution(work.problems) match {
        case Some(problem) ⇒
          log.info(s"YES! $problem")
          controller ! Solution(work, problem)
        case None ⇒
          log.info(s"NO!")
          // Tell Controller I'm ready again
          controller ! Processed(work, batchSize)
      }
    }
    case e ⇒ log.info(s"oh no $e")
  }

}

object Solver {
  import Controller._
  def goodSolution(problems: Seq[Problem]) = {
    Thread sleep 100
    problems.find { p ⇒
      p.data > p.required
    }
  }
}
