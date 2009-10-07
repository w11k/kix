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
package bootstrap.liftweb

import com.kix.model._
import java.sql._
import java.util.Locale
import net.liftweb.http._
import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._

class Boot {

  def boot() {
    Log info "Booting kix.com, please stand by ..."

    // Use SLF4J for logging
    Slf4jLogBoot.enable()

    // Freeze locale as GERMAN
    LiftRules.localeCalculator = request => Locale.GERMAN

    // Add "messages.properties" resources
    LiftRules.resourceNames = "Messages" :: Nil

    // Use UTF-8
    LiftRules.early append { _ setCharacterEncoding "UTF-8" }

    // Use com.kix to resolve snippets and views
    LiftRules addToPackages "com.kix"

    // Setup sitemap: Home, ..., CRUD stuff, ...
    val menus = Menu(Loc("home", ("index" :: Nil) -> false, "Home")) ::
                // Menu(Loc("teams", ("teams" :: Nil) -> false, "Teams")) ::
                //Menu(Loc("tips", ("tips" :: Nil) -> true, "Tips")) ::
                User.sitemap
    LiftRules setSiteMap SiteMap(menus : _*)

    // Setup database
    DB.defineConnectionManager(DefaultConnectionIdentifier , DBVendor)
    Schemifier.schemify(true, Log.infoF _, Group, Team, Game, Result, Tip, User)
    User.eventuallyCreateAdmin()

    Log info "Successfully booted kix.com. Have fun!"
  }
}

private object DBVendor extends ConnectionManager {
  def newConnection(id: ConnectionIdentifier): Box[Connection] = {
    try {
      // Use H2 driver and local database
      Class forName "org.h2.Driver"
      val con = DriverManager getConnection "jdbc:h2:~/.h2/kix"
      
      Log debug "Successfully got new Connection."
      Full(con)
    } catch {
      case e: Exception => {
        // TODO Better exception handling!
        e.printStackTrace
        Empty
      }
    }
  }
  def releaseConnection(con: Connection) {
    con.close
  }
}
