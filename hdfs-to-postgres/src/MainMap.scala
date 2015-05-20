package com.app

import org.apache.hadoop.io.{ LongWritable, Text }
import org.apache.hadoop.mapreduce.Mapper
import scala.util.parsing.json.JSON

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.postgresql.util.PGobject

import org.joda.time.DateTime

class MainMap extends Mapper[LongWritable, Text, Text, Text] {

  type MapperContext = Mapper[LongWritable, Text, Text, Text]#Context

  case class Result(groupName: String = "", bib: Double = 0, time: String = "", age: Double = 0)

  override def map(key: LongWritable, value: Text, context: MapperContext): Unit = {

    val json = value.toString.split('\t')(1)

    var logMe = ""

    JSON.parseFull(json) match {
      case Some(value) =>
        val map: Map[String, Any] = value.asInstanceOf[Map[String, Any]]
        val result = Result(
          groupName = map("groupName").asInstanceOf[String],
          bib = map("bib").asInstanceOf[Double],
          time = map("time").asInstanceOf[String],
          age = map("age").asInstanceOf[Double])

        var con: Connection = null
        var pst: PreparedStatement = null

        val url = "jdbc:postgresql://postgres.lacolhost.com/postgres"
        val user = "postgres"
        val password = "LifeTime1"

        try {
          con = DriverManager.getConnection(url, user, password)
          pst = con.prepareStatement("insert into foo (some_json) values (?)")
          
          val jsonObject = new PGobject()
          jsonObject.setType("json")
          jsonObject.setValue(json)
          
          pst.setObject(1, jsonObject)
          pst.executeUpdate

          context.write(new Text("foo"), new Text("successfully logged at " + DateTime.now))
        } catch {
          case ex: SQLException =>
            context.write(new Text("foo"), new Text(ex.getMessage()))
        } finally {
          try {
            if (pst != null)
              pst.close
            if (con != null)
              con.close
          } catch {
            case ex: SQLException =>
              context.write(new Text("foo"), new Text(ex.getMessage()))
          }
        }

      case (None) =>
    }
  }

}
