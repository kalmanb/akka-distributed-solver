package com.kalmanb.solver

import akka.actor._

object ControllerApp extends App {
  println("Starting ...")

  val system = ActorSystem("master")

  val controller = system.actorOf(Props(new Controller), name = "controller")

  readLine()
  system.shutdown
  System.exit(0)
}

object Controller {
  case class ReadyForWork(batchSize: Int)
  case class Work(problems: Seq[Problem])
  case class Problem(data: Int, required: Int)
  case class Solution(work: Work, problem: Problem)
  case class Processed(work: Work, batchSize: Int)
}

class Controller extends Actor with ActorLogging {
  import Controller._

  def receive = {
    case ReadyForWork(batchSize) ⇒ {
      log.info(s"got new worker $sender")
      more(batchSize)
    }
    case Processed(work, batchSize) ⇒ {
      //log.info(s"processed batch $work from $sender")
      more(batchSize)
    }
    case Solution(work, problem) ⇒
      println("YAY we won! " + problem)
    // todo
  }

  def more(batchSize: Int) =
    if (Generator.hasNext) {
      log.info("sending work")
      val next = Generator.getNext(batchSize)
      sender ! Work(next)
    } else {
      log.info("No more work")
    }

}

// Gives us all permutations
object Generator {
  import Controller._
  val target = 10000
  var current = 0

  val solutionsFound = Seq.empty[Work]

  def getNext(batchSize: Int): Seq[Problem] = {
    println(s"Next batch at $current")
    (1 to batchSize) map { n ⇒
      current = current + n
      Problem(current, target)
    }
  }
  def hasNext = true
}
