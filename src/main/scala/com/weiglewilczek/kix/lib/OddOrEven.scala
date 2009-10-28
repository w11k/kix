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

import net.liftweb.http._
import SHtml._
import net.liftweb.util.Helpers._
import scala.xml.Text

/**
 * Utility used for distinguishing odd and even table rows by color.
 */
case class OddOrEven() {

  def next = {
    even_? = !even_?
    AttrBindParam("class", if (even_?) Text("even") else Text("odd"), "class")
  }

  def nextString = {
    even_? = !even_?
    if (even_?) "even" else "odd"
  }

  private var even_? = true
}
