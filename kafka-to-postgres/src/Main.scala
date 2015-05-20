package com.app

import scala.util.parsing.json.JSON
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import org.postgresql.util.PGobject
import org.joda.time.DateTime
import kafka.consumer.KafkaConsumer

object Main extends J2SELogging {

  case class Result(groupName: String = "", bib: Integer = 0, time: String = "", age: Integer = 0)

  def main(args: Array[String]) {

    val topic = args(0)
    val zookeeper = args(1)
    val groupId = args(2)
    val readFromStart = args(3).toBoolean
    val postgresUrl = args(4)
    val postgresUser = args(5)
    val postgresPwd = args(6)

    val consumer = new KafkaConsumer(topic, groupId, zookeeper)

    def exec(binaryObject: Array[Byte]) = {
      val message = new String(binaryObject)
      println(message)
      JSON.parseFull(message) match {
        case Some(value) =>
          val map: Map[String, Any] = value.asInstanceOf[Map[String, Any]]
          val result = Result(
            groupName = map("groupName").asInstanceOf[String],
            bib = map("bib").asInstanceOf[Integer],
            time = map("time").asInstanceOf[String],
            age = map("age").asInstanceOf[Integer])

          var con: Connection = null
          var pst: PreparedStatement = null

          try {
            con = DriverManager.getConnection("jdbc:postgresql://" + postgresUrl, postgresUser, postgresPwd)

            pst = con.prepareStatement("insert into foo (some_json) values (?)")

            val jsonObject = new PGobject()
            jsonObject.setType("json")
            jsonObject.setValue(message)

            pst.setObject(1, jsonObject)
            pst.executeUpdate

            log.info("successfully logged at " + DateTime.now)
          } catch {
            case ex: SQLException =>
              log.error(ex.getMessage())
          } finally {

            try {
              if (pst != null)
                pst.close
              if (con != null)
                con.close
            } catch {
              case ex: SQLException =>
                log.error(ex.getMessage())
            } finally {
              consumer.close
            }
          }

        case (None) =>
      }
    }

    consumer.read(exec)
  }
}