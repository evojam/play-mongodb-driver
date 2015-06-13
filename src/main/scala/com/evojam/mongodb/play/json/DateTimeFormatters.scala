package com.evojam.mongodb.play.json


import java.util.Calendar

import play.api.libs.json._

import org.joda.time.DateTime

trait DateTimeFormatters {

  implicit object DateTimeFormat extends Format[DateTime] {

    override def writes(o: DateTime) = Json.obj("$date" -> o.getMillis)

    override def reads(json: JsValue) =
      json.validate[JsObject].map(_.value).flatMap {
        case Seq(("$date", JsNumber(ts))) if ts.isValidLong =>
          JsSuccess(new DateTime(ts.toLong))
        case _ => JsError(__, "validation.error.expected.$date")
      }
  }

  implicit object CalendarFormat extends Format[Calendar] {

    override def writes(o: Calendar) = Json.obj("$date" -> o.getTimeInMillis)

    override def reads(json: JsValue) =
      json.validate[JsObject].map(_.value).flatMap {
        case Seq(("$date", JsNumber(ts))) if ts.isValidLong =>
          val cal = Calendar.getInstance()
          cal.setTimeInMillis(ts.toLong)
          JsSuccess(cal, __)
        case _ => JsError(__, "validation.error.expected.$date")
      }
  }

}