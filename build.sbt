import slick.codegen.SourceCodeGenerator
import slick.model

scalaVersion := "2.13.8"
name := "slick-demo"
organization := "com.rockthejvm"
version := "1.0"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.postgresql" % "postgresql" % "42.3.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "com.github.tminglei" %% "slick-pg" % "0.20.3",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.20.3"
)

//Slick Code Generation 
slickCodegenSettings
enablePlugins(CodegenPlugin)
slickCodegenDatabaseUrl := "jdbc:postgresql://localhost:5432/postgres"
slickCodegenDatabaseUser := "postgres"
slickCodegenDatabasePassword := "admin"
slickCodegenDriver := slick.jdbc.PostgresProfile
slickCodegenJdbcDriver := "org.postgresql.Driver"
slickCodegenOutputPackage := "com.rockthejvm.generated.models"
slickCodegenCodeGenerator := { (slickModel: model.Model) => new SourceCodeGenerator(slickModel) }

//slickCodegenIncludedTables in Compile := Seq("users") //: this will filter only thee provided tables for generation

//sourceGenerators in Compile += slickCodegen
//run the command `slickCodegen` to generate code