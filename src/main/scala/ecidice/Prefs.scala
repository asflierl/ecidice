/*
 * Copyright (c) 2009-2010 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ecidice

import com.jme3.system.AppSettings
import java.util.prefs.Preferences
import collection.JavaConversions._

import ecidice.i18n.L10n
import ecidice.util.Preamble._

class Prefs(root: Preferences) {
  private val jme = root.node("jME3")
  val settings = new AppSettings(true)

  for ((key, value) <- settings) value match {
    case v : java.lang.Integer => 
      settings.putInteger(key, jme.getInt(key, v.intValue))
    case v : java.lang.Boolean =>
      settings.putBoolean(key, jme.getBoolean(key, v.booleanValue))
    case v => settings.putString(key, jme.get(key, v.toString))
  }
  
  def save() = {
    for ((key, value) <- settings) value match {
      case v : java.lang.Integer => jme.putInt(key, v.intValue)
      case v : java.lang.Boolean => jme.putBoolean(key, v.booleanValue)
      case v => jme.put(key, v.toString)
    }
    root.sync()
  }
}
object Prefs {
  def load = new Prefs(Preferences.userRoot().node(L10n.of.appName))
  def uninstall = Preferences.userRoot().node(L10n.of.appName).removeNode()
}
