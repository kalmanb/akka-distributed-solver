package com.kalmanb.solver

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props

object ClientApp extends App {
  val NumWorkers = 10
  val RemoteUrl = "akka.tcp://master@127.0.0.1:2552"

  println("PullClient Starting ...")

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
  val system = ActorSystem("client", ConfigFactory.load(customConf))

  // Note "actorFor" is a lookup - not creation
  val controller = system.actorFor(RemoteUrl + "/user/controller")

  // Start workers
  (1 to NumWorkers).foreach { i ⇒
    system.actorOf(Props(new Worker(controller)))
  }

  readLine()
  system.shutdown
  System.exit(0)
}

