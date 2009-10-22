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

import net.liftweb.http.S.?
import net.liftweb.http.js._
import scala.xml._

object ImgHelpers {

  def img(src: String, title: => String, attribs: Tuple2[String, String]*) = {
    def metaData = attribs.foldRight[MetaData](Null) { (t, m) => new UnprefixedAttribute(t._1, t._2, m) }
    <img src={ src } title={ title } alt={ title } /> % metaData
  }

  def img(src: String, title: => String, onclick: JsExp) =
    <img src={ src } title={ title } alt={ title } onclick={ onclick } />

  def ensignImg(src: String, name: => String) =
    if (src != null && !src.trim.isEmpty) img(src, name, "height" -> "10px")
    else img("/images/unknown.png", ?("Ensign unknown"), "height" -> "10px")

  def createImg = img("/images/create.png", ?("Create Tip"))

  def editImg = img("/images/edit.png", ?("Edit Tip"))

  def deleteImg = img("/images/delete.png", ?("Delete Tip"))

  def ajaxDeleteImg(onclick: (String, JsExp)) =
    img("/images/delete.png", ?("Delete Tip"), onclick._2)

  def fiveImg = img("/images/up.png", ?("Five Points")) ++
                img("/images/up.png", ?("Five Points"))

  def fourImg = img("/images/up.png", ?("Four Points"))

  def threeImg = img("/images/right.png", ?("Three Points"))

  def zeroImg = img("/images/down.png", ?("Zero Points"))
}
