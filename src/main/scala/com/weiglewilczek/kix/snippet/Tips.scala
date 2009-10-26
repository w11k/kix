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
package com.weiglewilczek.kix.snippet

import lib._
import DateHelpers._
import ImgHelpers._
import model._

import Game.notYetStarted_?
import net.liftweb.common._
import net.liftweb.http._
import S.{?, locale}
import SHtml._
import js._
import JsCmds._
import net.liftweb.util.Helpers._
import scala.xml.{NodeSeq, Text}

object Tips {

  def create(game: Game) =
    link("/tips/create", () => currentGame(Full(game)), createImg)

  def editDelete(tip: Tip, game: Game, action: Game => NodeSeq) = {
    def delete = {
      Tip delete_! tip
      SetHtml(game.id.is.toString, action(game))
    }
    renderEditDelete(tip, delete _)
  }

  def editDelete(tip: Tip) = { 
    def delete = {
      Tip delete_! tip
      SetHtml(tip.id.is.toString, NodeSeq.Empty)
    }
    renderEditDelete(tip, delete _)
  }

  def points(tip: Tip) = tip.points.is match {
    case 5 => fiveImg
    case 4 => fourImg
    case 3 => threeImg
    case _ => zeroImg
  }
  
  private object currentSearchedUser extends RequestVar[String]("")
  
  private object currentTip extends RequestVar[Box[Tip]](Empty)
  
  private object currentGame extends RequestVar[Box[Game]](Empty)

  private def renderEditDelete(tip: Tip, jsCmd: () => JsCmd) = 
    link("/tips/edit", () => currentTip(Full(tip)), editImg) ++
    Text("") ++
    ajaxDeleteImg(ajaxInvoke(jsCmd))
}

import Tips._

class Tips extends Logging {

  def myTips(xhtml: NodeSeq) = {
    val oddOrEven = OddOrEven()
    def bindTips(tips: List[Tip]) = {
      def bindAction(game: Box[Game], tip: Tip) =
        if (User.loggedIn_?) 
          if(notYetStarted_?(game)) Tips editDelete tip
          else Tips points tip
        else
          NodeSeq.Empty 
      tips flatMap { tip =>
        val game = tip.game.obj
        bind("tip", chooseTemplate("tips", "list", xhtml),
             "action" -> bindAction(game, tip),
             "game" -> (game map { _.name } openOr ""),
             "date" -> (game map { g => format(g.date.is, locale) } openOr ""),
             "tip" -> tip.goals,
             "result" -> (Result goalsForGame game),
             AttrBindParam("id", Text(tip.id.is.toString), "id"),
             oddOrEven.next)
      }
    }
    bind("tips", xhtml, "list" -> bindTips(Tip findByUser User.currentUser))
  }

  def otherTips(xhtml: NodeSeq) = {
    val oddOrEven = OddOrEven()
    def bindTips(tips: List[Tip]) = tips flatMap { tip =>
      val game = tip.game.obj
      bind("tip", chooseTemplate("tips", "list", xhtml),
           "tipster" -> (tip.user.obj map { _.shortName } openOr ""),
           "game" -> (game map { _.name } openOr ""),
           "date" -> (game map { g => format(g.date.is, locale) } openOr ""),
           "tip" -> tip.goals,
           "result" -> (Result goalsForGame game),
           oddOrEven.next)
    }
    bind("tips", xhtml,
         "tipster" -> text(currentSearchedUser.is, currentSearchedUser(_)),
         "search" -> submit(?("Search"), () => ()),
         "list" -> bindTips(Tip.findNotByUserLikeUserName(User.currentUser, currentSearchedUser.is) filter {
                     _.game.obj map { _.date.is before now } openOr false
                   }))
  }

  def create(xhtml: NodeSeq) =
    createOrEdit(_.game.toForm openOr NodeSeq.Empty, xhtml)

  def edit(xhtml: NodeSeq) = createOrEdit(_.game.asHtml, xhtml)

  private def createOrEdit(game: Tip => NodeSeq, xhtml: NodeSeq) = {
    val referrer = S.referer openOr "."
    val tip = currentTip openOr {
      val newTip = Tip.create.user(User.currentUser)
      for (g <- currentGame.is) newTip.game(g)
      currentTip(Full(newTip))
      newTip
    }
    def handleSave() {
      if (notYetStarted_?(Game findByKey tip.game.is)) tip.save
      else S notice ?("Cannot save tip, because game alredy started!")
      log info "Saved tip: %s".format(tip)
      log debug "referrer=%s".format(referrer)
      S redirectTo referrer
    }
    bind("tip", xhtml,
         "game" -> game(tip),
         "tip" -> tip.toForm,
         "save" -> submit(?("Save"), handleSave))
  }
}
