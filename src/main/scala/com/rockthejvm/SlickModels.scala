package com.rockthejvm

import java.time.LocalDate
import slick.lifted.Tag
import slick.jdbc.PostgresProfile
import slick.lifted.ProvenShape
//import slick.jdbc.PostgresProfile.api._
import play.api.libs.json._

final case class Movie(
    id: Long,
    name: String,
    releaseDate: LocalDate,
    lengthInMin: Int
)
final case class Actor(id: Long, name: String)
final case class MovieActorMapping(id: Long, movieId: Long, actorId: Long)

object StreamingProvider extends Enumeration {
  type StreamingProviders = Value
  val Netflix = Value("Netflix")
  val Hulu = Value("Hulu")
  val Disney = Value("Disney")
  val Prime = Value("Prime")
}

final case class StreamingProviderMapping(
    id: Long,
    movieId: Long,
    streamingProvider: StreamingProvider.StreamingProviders
)

final case class MovieLocations(id: Long, movieId: Long, locations: List[String])
final case class MovieProperties(id: Long, movieId: Long, properties: Map[String, String])
final case class ActorDetails(id: Long, actorId: Long, personalDetails: JsValue) 

class SlickTablesGeneric(val profile: PostgresProfile) {
  import profile.api._

  class MovieTable(tag: Tag) extends Table[Movie](tag, "Movie") {
    def id = column[Long]("movie_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def releaseDate = column[LocalDate]("release_date")
    def lengthInMin = column[Int]("length_in_min")
    override def * = (id, name, releaseDate, lengthInMin) <> (Movie.tupled, Movie.unapply)
  }
  lazy val movieTable = TableQuery[MovieTable]

  class ActorTable(tag: Tag) extends Table[Actor](tag, "Actor") {
    def id = column[Long]("actor_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    override def * = (id, name) <> (Actor.tupled, Actor.unapply)
  }

  lazy val actorTable = TableQuery[ActorTable]

  class MovieActorMappingTable(tag: Tag)
      extends Table[MovieActorMapping](tag, "MovieActorMapping") {
    def id = column[Long]("movie_actor_id", O.PrimaryKey, O.AutoInc)
    def movieId = column[Long]("movie_id")
    def actorId = column[Long]("actor_id")
    override def * = (id, movieId, actorId) <> (MovieActorMapping.tupled, MovieActorMapping.unapply)
  }

  lazy val movieActorMappingTable = TableQuery[MovieActorMappingTable]

  class StreamingProviderMappingTable(tag: Tag)
      extends Table[StreamingProviderMapping](tag, "StreamingProviderMapping") {

    implicit val providerMapper =
      MappedColumnType.base[StreamingProvider.StreamingProviders, String](
        e => e.toString,
        s => StreamingProvider.withName(s)
      )

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def movieId = column[Long]("movie_id")
    def streamingProvider = column[StreamingProvider.StreamingProviders]("streaming_provider")
    override def * =
      (
        id,
        movieId,
        streamingProvider
      ) <> (StreamingProviderMapping.tupled, StreamingProviderMapping.unapply)
  }
  lazy val streamingProviderMappingTable = TableQuery[StreamingProviderMappingTable]

  val tables = Seq(movieTable, actorTable, movieActorMappingTable, streamingProviderMappingTable)
  val ddl: profile.DDL = tables.map(_.schema).reduce(_ ++ _)

}

object SlickTables extends SlickTablesGeneric(PostgresProfile)

object SpecialTables {
  val api = CustomPostgresProfile.api
  import api._

  class MovieLocationsTable(tag: Tag) extends Table[MovieLocations](tag, "MovieLocations") {

    def id = column[Long]("movie_location_id", O.PrimaryKey, O.AutoInc)
    def movieId = column[Long]("movie_id")
    def locations = column[List[String]]("locations")
    override def * = (id, movieId, locations) <> (MovieLocations.tupled, MovieLocations.unapply)

  }
  lazy val movieLocationsTable = TableQuery[MovieLocationsTable]

  class MoviePropertiesTable(tag: Tag) extends Table[MovieProperties](tag, "MovieProperties") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.SqlType("bigserial"))
    def movieId = column[Long]("movie_id")
    def properties = column[Map[String, String]]("properties", O.SqlType("hstore"))
    def * = (id, movieId, properties) <> (MovieProperties.tupled, MovieProperties.unapply)
  }
  lazy val moviePropertiesTable = TableQuery[MoviePropertiesTable]

  class ActorDetailsTable(tag: Tag) extends  Table[ActorDetails](tag, "ActorDetails") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.SqlType("bigserial"))
    def actorId = column[Long]("actor_id")
    def personal = column[JsValue]("personal_info")

    def * = (id, actorId, personal)<>(ActorDetails.tupled, ActorDetails.unapply)
  }
  lazy val actorDetailsTable = TableQuery[ActorDetailsTable]

  val specialtables = Seq(movieLocationsTable, moviePropertiesTable, actorDetailsTable)
  val ddl = specialtables.map(_.schema).reduce(_ ++ _)
}

//docker run --name postgresql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=admin -p 5432:5432 -d postgres
//docker run --name postgresql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=admin -p 5432:5432 -d postgis/postgis
