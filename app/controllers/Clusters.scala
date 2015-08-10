package controllers

import java.time.LocalDateTime
import javax.inject.Inject

import entities.BSONEntity.BSONClusterHandler
import entities.{JSONEntity, Region, Cluster}
import entities.Region.Region
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.{ReactiveMongoComponents, MongoController, ReactiveMongoApi}
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import services.MongoDbResource

import scala.concurrent.Future

// Reactive Mongo imports
import reactivemongo.api.Cursor

import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
MongoController,
ReactiveMongoApi,
ReactiveMongoComponents
}

// BSON-JSON conversions/collection
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

import scala.util.{Success, Failure, Random}

class Clusters @Inject() extends ApiController {

//  def find(page: Int) = Action {
//    Ok(Json.toJson(items))
//  }
//
//  def findOne(id: Int) = Action {
//    Ok(items)
//  }

  implicit val clusterBsonHandler = BSONClusterHandler

  val collections = MongoDbResource.clusters

  def index() = Action.async {

    val l: Future[List[BSONDocument]] = collections.find(BSONDocument()).cursor[BSONDocument].collect[List]()

//    val list = Seq(
//      new Cluster(Some(BSONObjectID.generate), "ladfasdfad", Region.AP_SOUTHEAST_1, Some(DateTime.now())),
//      new Cluster(Some(BSONObjectID.generate), "ladfasdfad", Region.AP_SOUTHEAST_1, Some(DateTime.now())),
//      new Cluster(Some(BSONObjectID.generate), "ladfasdfad", Region.AP_SOUTHEAST_1, Some(DateTime.now()))
//    )

//    val j = for(cluster <- clusters) yield Json.toJson(BSONClusterHandler.read(cluster))(JSONEntity.clustorWrites)

//    Ok(Json.toJson(j))

    l.map(list => {
      val j = for(cluster <- list) yield Json.toJson(BSONClusterHandler.read(cluster))(JSONEntity.clustorWrites)
      Ok(Json.toJson(j))
    })
  }

  def add() = Action {
    val cluster = new Cluster(None, "ladfasdfad", Region.AP_SOUTHEAST_1, None)
    val future = collections.insert(clusterBsonHandler.write(cluster))

    future.onComplete {
      case Failure(e) => throw e
      case Success(writeResult) => {
        println(s"successfully inserted document: $writeResult")
      }
    }

    Ok("lllllll")


  }

//
//  def save = Action {
//
//  }
//
//  def delete = Action {
//
//  }

}
