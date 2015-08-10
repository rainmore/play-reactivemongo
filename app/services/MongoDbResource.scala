package services

import com.typesafe.config.ConfigFactory
import reactivemongo.api.collections.bson.BSONCollection

object MongoDbResource {
  private val config = ConfigFactory.load("mongo")

  import reactivemongo.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  // gets an instance of the driver
  // (creates an actor system)
  val driver = new MongoDriver(Some(config))
  val connection = driver.connection(List("localhost"))

  // Gets a reference to the database "plugin"
  val db = connection("megamind")


  val clusters = db.collection[BSONCollection]("clusters")

}
