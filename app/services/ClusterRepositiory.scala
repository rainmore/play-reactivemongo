package services

import javax.inject.{Singleton, Inject}

import entities.Cluster
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

@Singleton
class ClustersService @Inject() (val reactiveMongoApi: ReactiveMongoApi) {

//  lazy val db = reactiveMongoApi.db

//  val collection = db.collection[BSONCollection]("cluster")

//  def add(cluster: Cluster): Future[WriteResult] = collection.insert(Cluster.BSONWrite.write(cluster))

//  def update(cluster: Cluster): Future[WriteResult] = collection.update(Cluster.BSONWrite.write(cluster))
//
//  def delete(cluster: Cluster): Unit = collection.remove(Cluster.BSONWrite.write(cluster))

}
