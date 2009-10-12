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

object ImgHelper {

  def img(path: String, title: => String) =
    <img src={ path } title={ title } alt={ title } />

  def  edit = img("/images/edit.png", ?("Edit"))

  def  delete = img("/images/delete.png", ?("Delete"))
}
