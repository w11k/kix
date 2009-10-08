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
package com.kix.lib

import java.util._
import java.text._
import DateFormat._
import net.liftweb.util._

object Util {

  val IsoDateTime = "yyyy-MM-dd'T'HH:mmz"

  def format(date: Date, locale: Locale) = dateFormat(locale) format date

  def parse(date: String, locale: Locale) = try {
    Full(dateFormat(locale) parse date)
  } catch { case e => 
    Log error ("Cannot parse date \"%s\"!".format(date), e)
    Failure("Bad date: " + date, Full(e), Empty) 
  }

  implicit def parseIso(date: String) = dateFormat(IsoDateTime) parse date

  private def dateFormat(pattern: String) = new SimpleDateFormat(pattern)

  private def dateFormat(locale: Locale) = getDateTimeInstance(SHORT, SHORT, locale)
}
