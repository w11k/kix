/**
 * Copyright 2009 WeigleWilczek and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weiglewilczek.kix.lib

import model._

import java.text.{DateFormat, SimpleDateFormat}
import DateFormat.{getDateTimeInstance, getTimeInstance, SHORT}
import java.util.{Date, Locale}
import net.liftweb.common._
import net.liftweb.util._
import TimeHelpers.now

/**
 * Some utilities for date handling. We also offer an implicit conversion
 * from String to Date.
 */
object DateHelpers {

  val IsoDateTime = "yyyy-MM-dd'T'HH:mmz"

  def format(date: Date, locale: Locale) = shortDateTimeFormat(locale) format date

  def formatTime(date: Date, locale: Locale) = shortTimeFormat(locale) format date

  def parse(date: String, locale: Locale) = try {
    Full(shortDateTimeFormat(locale) parse date)
  } catch { case e => 
    Log error ("Cannot parse date \"%s\"!".format(date), e)
    Failure("Bad date: " + date, Full(e), Empty) 
  }

  implicit def parseIso(date: String) = dateFormat(IsoDateTime) parse date

  private def dateFormat(pattern: String) = new SimpleDateFormat(pattern)

  private def shortDateTimeFormat(locale: Locale) = getDateTimeInstance(SHORT, SHORT, locale)

  private def shortTimeFormat(locale: Locale) = getTimeInstance(SHORT, locale)
}
