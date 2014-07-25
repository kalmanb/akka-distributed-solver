package com.kalmanb.solver

import akka.actor.ActorSystem
import akka.actor.Props
import com.typesafe.config.ConfigFactory

object Client extends App {
  val NumWorkers = 5
  val RemoteUrl = "akka.tcp://master@127.0.0.1:2552"
  val customConf = ConfigFactory.parseString("""
      akka {
		  actor {
		    provider = "akka.remote.RemoteActorRefProvider"
		  }
		  remote {
		    enabled-transports = ["akka.remote.netty.tcp"]
		    netty.tcp {
		      port = 2553
		    }
		 }
	  }
      """)

  println("PullClient Starting ...")

  lazy val system = ActorSystem("client", ConfigFactory.load(customConf))

  // Note "actorFor" is a lookup - not creation
  lazy val controller = system.actorFor(RemoteUrl + "/user/controller")

  //// Start workers
  (1 to NumWorkers).foreach { i ⇒
    system.actorOf(Props(new Worker(controller)))
  }

  readLine()
  system.shutdown
  System.exit(0)
}

// Temp only for testing multiple clients
object Client2 extends App {
  val NumWorkers = 1
  val RemoteUrl = "akka.tcp://master@127.0.0.1:2552"
  val customConf = ConfigFactory.parseString("""
      akka {
		  actor {
		    provider = "akka.remote.RemoteActorRefProvider"
		  }
		  remote {
		    enabled-transports = ["akka.remote.netty.tcp"]
		    netty.tcp {
		      port = 2554
		    }
		 }
	  }
      """)

  println("PullClient Starting ...")

  lazy val system = ActorSystem("client", ConfigFactory.load(customConf))

  // Note "actorFor" is a lookup - not creation
  lazy val controller = system.actorFor(RemoteUrl + "/user/controller")

  //// Start workers
  (1 to NumWorkers).foreach { i ⇒
    system.actorOf(Props(new Worker(controller)))
  }

  readLine()
  system.shutdown
  System.exit(0)
}

