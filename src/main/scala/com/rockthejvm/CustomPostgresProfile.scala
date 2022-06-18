package com.rockthejvm

import com.github.tminglei.slickpg.ExPostgresProfile
import com.github.tminglei.slickpg._
import com.github.tminglei.slickpg.geom.PgPostGISExtensions
import play.api.libs.json.JsValue
import play.api.libs.json.Json

trait CustomPostgresProfile
    extends ExPostgresProfile
    with PgJsonSupport
    with PgPlayJsonSupport
    with PgArraySupport
    with PgHStoreSupport
    with PgDate2Support {
  override def pgjson = "jsonb"
  override protected def computeCapabilities: Set[slick.basic.Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api = CustomPGAPI
  object CustomPGAPI
      extends API
      with JsonImplicits
      with HStoreImplicits
      with ArrayImplicits
      with DateTimeImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val playJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](
        pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)

  }

}

object CustomPostgresProfile extends CustomPostgresProfile
