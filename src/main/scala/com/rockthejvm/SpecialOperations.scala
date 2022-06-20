package com.rockthejvm

import scala.concurrent.Future
import com.rockthejvm.FutureLogger.FutureLoggerXtension
import MyExecContext._

object SpecialOperations {

  import SpecialTables.api._
  def saveMovieLocations(movieLocation: MovieLocations): Future[Int] = {
    val insertLocation = SpecialTables.movieLocationsTable += movieLocation
    Connection.db.run(insertLocation)
  }

  def getMoviesByLocation(location: String) = {
    // val q = SpecialTables.movieLocationsTable.filter(_.locations.@>(List(location)))
    val locQuery = SpecialTables.movieLocationsTable.filter(location.bind === _.locations.any)
    val joinQuerry = locQuery.join(SlickTables.movieTable).on(_.movieId === _.id)
    Connection.db.run(joinQuerry.result).map(_.map(_._2))
  }

  def getMoviesFilmedInLocations(locations: List[String]) = {
    // NOTE: Make sure the implicit exists for List[String], otherwise it will cause error.
    // implicit for List will not match for Seq, but reverse is true
    val q = SpecialTables.movieLocationsTable.filter(_.locations @& locations.bind)
    Connection.db.run(q.result)
  }

  def saveMovieProperties(movieProperties: MovieProperties): Future[Int] = {
    Connection.db.run(SpecialTables.moviePropertiesTable += movieProperties)
  }

  def getMoviesByDistributor(distributor: String): Future[Seq[MovieProperties]] = {
    val condition = Map("distributor" -> distributor)
    val query = SpecialTables.moviePropertiesTable.filter(_.properties @> condition.bind)
    Connection.db.run(query.result)
  }

  def saveActorDetails(actorDetails: ActorDetails): Future[Int] = {
    Connection.db.run(SpecialTables.actorDetailsTable += actorDetails)
  }

  def getActorsBornOn(year: String): Future[Seq[ActorDetails]] = {
    Connection.db.run(
      SpecialTables.actorDetailsTable.filter(_.personal.+>>("birthYear") === year.bind).result
    )
  }

}
