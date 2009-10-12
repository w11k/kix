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

import lib.ImgHelper
import lib.Util._
import model._
import net.liftweb.http._
import S.{?, locale}
import SHtml._
import net.liftweb.util._
import Helpers._
import scala.xml.{NodeSeq, Text}

object Tips {

  object currentTip extends RequestVar[Box[Tip]](Empty)
}

class Tips {

  def myTips(xhtml: NodeSeq) = {
    def editDelete(tip: Tip) = 
      link("edit", () => Tips.currentTip(Full(tip)), ImgHelper.edit) ++
        Text("") ++
        link(".", () => tip.delete_!, ImgHelper.delete)
    def bindTips(tips: List[Tip]) = tips flatMap { tip =>
      val game = tip.game.obj
      bind("tip", chooseTemplate("template", "tip", xhtml),
           "edit-delete" -> (if (notYetStarted_?(game)) editDelete(tip) 
                             else NodeSeq.Empty),
           "game" -> (game map { _.name } openOr ""),
           "date" -> (game map { g => format(g.date.is, locale) } openOr ""),
           "tip" -> tip.goals)
    }
    bind("tips", xhtml, "list" -> bindTips(Tip findByUser User.currentUser))
  }

  def otherTips(xhtml: NodeSeq) = {
    def bindTips(tips: List[Tip]) = tips flatMap { tip =>
      val game = tip.game.obj
      bind("tip", chooseTemplate("template", "tip", xhtml),
           "tipster" -> (tip.user.obj map { _.shortName } openOr ""),
           "game" -> (game map { _.name } openOr ""),
           "date" -> (game map { g => format(g.date.is, locale) } openOr ""),
           "tip" -> tip.goals)
    }
    bind("tips", xhtml, 
         "list" -> bindTips(Tip findNotByUser User.currentUser filter {
                     _.game.obj map { _.date.is before timeNow } openOr false
                   }))
  }

  def edit(xhtml: NodeSeq) = {
    val tip = Tips.currentTip openOr {
      val newTip = Tip.create.user(User.currentUser)
      Tips.currentTip(Full(newTip))
      newTip
    }
    def handleSave() {
      if (notYetStarted_?(Game findByKey tip.game.is)) tip.save
      else S notice ?("Cannot save tip, because game alredy started!")
      S redirectTo "."
    }
    bind("tip", xhtml,
         "game" -> tip.game.toForm,
         "tip" -> tip.toForm,
         "save" -> submit(?("Save"), handleSave))
  }
}
