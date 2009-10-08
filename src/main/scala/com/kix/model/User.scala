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
package com.kix.model

import net.liftweb.mapper._
import net.liftweb.util.Log
import scala.xml.NodeSeq

 /**
 * Helper for a persistent user.
 */
object User extends User with MetaMegaProtoUser[User] {

  override def signupFields = firstName :: lastName :: email :: password :: Nil

  override def skipEmailValidation = true

  override def loginXhtml = surround(super.loginXhtml)

  override def signupXhtml(user: User) = surround(super.signupXhtml(user))

  override def lostPasswordXhtml = surround(super.lostPasswordXhtml)

  override def editXhtml(user: User) = surround(super.editXhtml(user))

  override def changePasswordXhtml = surround(super.changePasswordXhtml)

  def tipsters = findAll(NotBy(superUser, true))

  def deleteAllTipsters() = bulkDelete_!!(NotBy(superUser, true))

  def eventuallyCreateAdmin() {
    if (find(By(email, "admin@kix.com")).isEmpty) {
      create.email("admin@kix.com")
            .firstName("Admin")
            .password("kixadmin")
            .superUser(true)
            .validated(true).save
      Log info "Created admin user: admin@kix.com, kixadmin"
    }
  }

  private def surround(xhtml: => NodeSeq) = 
    <lift:surround with="default" at="content">
      <lift:Msgs><lift:error_class>error</lift:error_class></lift:Msgs>
      { xhtml }
    </lift:surround>
}

/**
* A persistent user.
*/
class User extends MegaProtoUser[User] {

  override def getSingleton = User
}
