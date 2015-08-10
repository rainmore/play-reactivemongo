package entities


import java.util

import entities._
import entities.Region.Region
import entities.Role.Role
import entities.RolloutGroup.RolloutGroup
import org.joda.time.DateTime
import org.joda.time.format.{ISODateTimeFormat, DateTimeFormat}
import play.api.data.validation.ValidationError
import play.api.libs.json
import reactivemongo.bson.{BSONDateTime, BSONObjectID}


trait Entity {

}

// Got it from https://docs.aws.amazon.com/aws-sdk-php/v2/api/class-Aws.Common.Enum.Region.html
object Region extends Enumeration {
  type Region = Value
  val AP_SOUTHEAST_1 = Value("ap-southeast-1")
  val AP_SOUTHEAST_2 = Value("ap-southeast-1")
  val EU_CENTRAL_1 = Value("eu-central-1")
  val US_WEST_1 = Value("us-west-1")
}

object Role extends Enumeration {
  type Role = Value
  val Application, Chancellor, Database, Steward = Value
}

object RolloutGroup extends Enumeration {
  type RolloutGroup = Value
  val Dev, Test, Production = Value
}

case class Cluster(id: Option[BSONObjectID], name: String, region: Region.Region, createdAt: Option[DateTime]) extends Entity

case class Server(id: Option[BSONObjectID], status: Boolean = true, cluster: Cluster, name: String, role: Role, ip: String,
                  serverType: String = "EC2", az: String, createdAt: DateTime)

case class Database(hostname: String, name: String, user: String, pass: String)
case class T2(active: Boolean, rto: Boolean)
case class Tenant(id: Option[BSONObjectID], status: Boolean = true, server: Server, name: String, tenantId: String,
                  directory: String, preferredUrl: String, tag: String, rolloutGroup: RolloutGroup,
                  db: Database, t2: T2, createdAt: DateTime)  extends Entity

case class AppSetting(tenant: Tenant, name: String, certificate: String, icon: String, background: String) extends Entity

object JSONEntity {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = new Reads[E#Value] {
    def reads(json: JsValue): JsResult[E#Value] = json match {
      case JsString(s) => {
        try {
          JsSuccess(enum.withName(s))
        } catch {
          case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
        }
      }
      case _ => JsError("String value expected")
    }
  }

  implicit val regionReads: Reads[Region.Value] = enumReads(Region)
  implicit val regionWrites: Writes[Region.Value] = new Writes[Region.Region] {
    def writes(v: Region): JsValue = JsString(v.toString)
  }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = new Writes[E#Value] {
    def writes(v: E#Value): JsValue = JsString(v.toString)
  }

  implicit val dateTimeReads:Reads[DateTime] = new Reads[DateTime] {
    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(d) => JsSuccess(new DateTime(d.toLong))
      case JsString(s) => parseDate(s) match {
        case Some(d) => JsSuccess(d)
        case None => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date.isoformat", "ISO8601"))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date"))))
    }

    private def parseDate(input: String): Option[DateTime] =
      scala.util.control.Exception.allCatch[DateTime] opt (DateTime.parse(input, ISODateTimeFormat.dateTimeNoMillis()))
  }

  implicit val dateTimeWrites:Writes[DateTime] = new Writes[DateTime] {
    override def writes(o: DateTime): JsValue = JsString(ISODateTimeFormat.dateTimeNoMillis().print(o))
  }

  implicit val bsonObjectIDReads: Reads[BSONObjectID] = new Reads[BSONObjectID] {
    def reads(json: JsValue): JsResult[BSONObjectID] = JsSuccess(BSONObjectID(json.toString()))
  }

  implicit val bsonObjectIDWrites: Writes[BSONObjectID] = new Writes[BSONObjectID] {
    override def writes(o: BSONObjectID): JsValue = JsString(o.stringify)
  }

  implicit val clustorWrites: Writes[Cluster] = (
    (JsPath \ "id").write[Option[BSONObjectID]] and
      (JsPath \ "name").write[String] and
      (JsPath \ "region").write[Region] and
      (JsPath \ "createdAt").write[Option[DateTime]]
    )(unlift(Cluster.unapply))

  implicit val clustorReads: Reads[Cluster] = (
    (JsPath \ "id").readNullable[BSONObjectID] and
      (JsPath \ "name").read[String] and
      (JsPath \ "region").read[Region] and
      (JsPath \ "createdAt").readNullable[DateTime]
    )(Cluster.apply _)
}


object BSONEntity {

  import reactivemongo.bson._

  implicit object BSONDateTimeHandler extends BSONHandler[BSONDateTime, DateTime] {
    def read(time: BSONDateTime) = new DateTime(time.value)
    def write(jdtime: DateTime) = BSONDateTime(jdtime.getMillis)
  }

  implicit object BSONClusterHandler extends BSONHandler[BSONDocument, Cluster] {
    def read(doc: BSONDocument): Cluster = {
      val id = doc.getAs[BSONObjectID]("_id").get
      val name = doc.getAs[String]("name").get
      val region = Region.withName(doc.getAs[String]("region").get)
      val createdAt = doc.getAs[DateTime]("createdAt").get

      Cluster(Some(id), name, region, Some(createdAt))
    }
    def write(cluster: Cluster): BSONDocument = BSONDocument(
      "_id" -> cluster.id.getOrElse(BSONObjectID.generate),
      "name" -> cluster.name,
      "region" -> cluster.region.toString,
      "createdAt" -> cluster.createdAt.getOrElse(DateTime.now())
    )
  }

}