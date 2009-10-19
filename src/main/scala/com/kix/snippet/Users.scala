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
package com.kix.snippet

import model._
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq

class Users {

  def top3(xhtml: NodeSeq) = {
    def bindUsers(tips: List[User]) = tips flatMap { user =>
      bind("user", chooseTemplate("users", "list", xhtml),
           "name" -> user.shortName,
           "points" -> user.points.is)
    }
    bind("users", xhtml, "list" -> bindUsers(User top 3))
  }
}
