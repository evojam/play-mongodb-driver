package com.evojam.mongodb.play.json


import java.util.Calendar

import play.api.libs.json._

import org.joda.time.DateTime

trait DateTimeFormatters {

  implicit object DateTimeFormat extends Format[DateTime] {

    override def writes(o: DateTime) = Json.obj("$date" -> o.getMillis)

    override def reads(json: JsValue) = json match {
      case JsObject(Seq(("$date", JsNumber(timestamp)))) if timestamp.isValidLong =>
        JsSuccess(new DateTime(timestamp.toLong), __)
      case _ =>
        JsError(__, "validation.error.expected.$date")
    }
  }

  implicit object CalendarFormat extends Format[Calendar] {

    override def writes(o: Calendar) = Json.obj("$date" -> o.getTimeInMillis)

    override def reads(json: JsValue) = json match {
      case JsObject(Seq(("$date", JsNumber(timestamp)))) if timestamp.isValidLong =>
        val cal = Calendar.getInstance()
        cal.setTimeInMillis(timestamp.toLong)
        JsSuccess(cal, __)
      case _ =>
        JsError(__, "validation.error.expected.$date")
    }
  }

}