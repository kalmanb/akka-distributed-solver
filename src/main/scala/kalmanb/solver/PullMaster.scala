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
  case class ReadyForWork(num: Int)
  case class Work(data: Int, required: Int)
  case class Solution(work: Work)
}

class Controller extends Actor with ActorLogging {
  import Controller._

  def receive = {
    case ReadyForWork(num) ⇒ {
      log.info(s"got new worker $sender")
      if (Generator.hasNext){
        log.info("sending work")
        sender ! Generator.getNext(num)
      } else {
        log.info("No more work")
      }
    }
    case Solution(work) => // todo
  }
}

// Gives us all permutations
object Generator {
  import Controller._
  val target = 10000
  var current = 0

  val solutionsFound = Seq.empty[Work]

  def getNext(num: Int):Seq[Work] = (current to num) map {n ⇒
    current = n + 1
    Work(n, target)
  }
  def hasNext = true
}
