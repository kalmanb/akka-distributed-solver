package com.kalmanb.solver

import akka.actor._

class Worker(controller: ActorRef) extends Actor with ActorLogging {
  import Controller._
  import Solver._

  val batchSize = 100

  // Tell controller I'm ready
  controller ! ReadyForWork(batchSize)

  def receive = {
    // Controller responds with work for me
    case work: Seq[Work] ⇒ {
      if (goodSolution(work)) {
        log.info(s"YES!  $work")
      } else {
        log.info(s"NO!  $work")
        // Tell Controller I'm ready again
        controller ! ReadyForWork(batchSize)
      }
    }
    case _ => log.info("oh no")
  }

}

object Solver {
  import Controller._
  def goodSolution(work: Seq[Work]) = false
}
