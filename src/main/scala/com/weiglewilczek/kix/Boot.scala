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
package com.weiglewilczek.kix

import model._
import lib._
import DateHelpers._

import java.sql.{Connection, DriverManager}
import java.util.{Date, Locale}
import javax.mail.{Authenticator, PasswordAuthentication}
import net.liftweb.common._
import net.liftweb.http._
import S.?
import net.liftweb.mapper._
import net.liftweb.sitemap._
import Loc._
import net.liftweb.util._

/**
 * Here we configure our Lift application.
 */
class Boot extends Bootable with Logging {

  override def boot() {
    log info "Booting kix, please stand by ..."

    // i18n and formatting stuff
    LiftRules.early append { _ setCharacterEncoding "UTF-8" }
    LiftRules.resourceNames = "messages" :: "teams" :: Nil
    LiftRules.localeCalculator = 
      req => SessionLocale.is openOr LiftRules.defaultLocaleCalculator(req)
    LiftRules.formatDate = 
      date => format(if (date != null) date else new Date(), S.locale)
    LiftRules.parseDate = s => s match {
      case null => Empty
      case s    => parse(s, S.locale)
    }

    // Where do we look for Lift packages?
    LiftRules addToPackages getClass.getPackage

    // SiteMap
    val ifLoggedIn = If(() => User.loggedIn_?, () => RedirectResponse("/index"))
    val ifAdmin = If(() => User.superUser_?, () => RedirectResponse("/index"))
    val homeMenu = Menu(Loc("home", ("index" :: Nil) -> false, "Home"))
    val gamesMenu = Menu(Loc("games", ("games" :: "index" :: Nil) -> false, ?("Match Schedule")))
    val groupsMenu = Menu(Loc("groups", ("groups" :: "index" :: Nil) -> false, ?("Groups")))
    val tipsSubMenu = Menu(Loc("tips.create", ("tips" :: "create" :: Nil) -> false, ?("Create Tip"), Hidden)) :: 
                      Menu(Loc("tips.edit", ("tips" :: "edit" :: Nil) -> false, ?("Edit Tip"), Hidden)) ::
                      Nil
    val tipsMenu = Menu(Loc("tips", ("tips" :: "index" :: Nil) -> false, ?("Tips"), ifLoggedIn), tipsSubMenu: _*)
    val chatMenu = Menu(Loc("chat", ("chat":: "index" :: Nil) -> false, ?("Chat"), ifLoggedIn))
    val aboutMenu = Menu(Loc("about", ("about":: "index" :: Nil) -> false, ?("About kix")))
    val adminSubMenu = Team.menus ::: Game.menus ::: Result.menus
    val adminMenu = Menu(Loc("admin", ("admin" :: Nil) -> true, "Admin", ifAdmin), adminSubMenu: _*)
    val menus = homeMenu ::
                gamesMenu ::
                groupsMenu ::
                tipsMenu ::
                chatMenu ::
                aboutMenu ::
                adminMenu ::
                User.sitemap
    LiftRules setSiteMap SiteMap(menus : _*)

    // Mailer configuration
    val user = Props get "mail.user" openOr "kixwjaxchallenge@googlemail.com"
    val pwd = Props get "mail.pwd" openOr "thewinnerislift"
    Mailer.authenticator = Full(new Authenticator {
      override def getPasswordAuthentication = new PasswordAuthentication(user , pwd)
    })

    // DB configuration
    val dbVendor = 
      new StandardDBVendor(Props get "db.driver" openOr "org.h2.Driver",
                           Props get "db.url" openOr "jdbc:h2:kix",
                           Empty, Empty) {
      override def maxPoolSize = Props getInt "db.pool.size" openOr 3
    }
    DB.defineConnectionManager(DefaultConnectionIdentifier, dbVendor)
    Schemifier.schemify(true, Log.infoF _, Team, Game, Result, Tip, User)
    DB addLogFunc { (query, time) =>
      log debug ("All queries took " + time + "ms.")
      query.allEntries foreach { 
        case DBLogEntry(stmt, duration) => log debug (stmt + " took " + duration + "ms.")
      }
    }
    
    // TODO Remove for production: Let's always have a default admin
    User.eventuallyCreateAdmin()

    log info "Successfully booted kix.com. Have fun!"
  }
}
