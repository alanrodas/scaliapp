/** ********************************************************************************************
  * Scaliapp
  * Version 0.1
  *
  * The primary distribution site is
  *
  * http://scalavcs.alanrodas.com
  *
  * Copyright 2014 alanrodas
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software distributed under the
  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  * either express or implied. See the License for the specific language governing permissions
  * and limitations under the License.
  * *********************************************************************************************/
package com.alanrodas.scaliapp

class NamedParameterBuilder {
	var name : String = ""
	var default : String = ""

	def named(name : String) = {
		this.name = name
		this
	}

	def withDefault(default : String) = {
		new NamedParameter(name, default, false)
	}

	def withNoDefault() = {
		new NamedParameter(name, "", true)
	}
}

trait DashedParameterBuilder {

	var name : String = ""
	var altName : Option[String] = None
	var description : String = ""
	var require : Boolean = false

	def named(name : String) : this.type = {
		this.name = name
		this
	}

	def withAlias(altName : String) : this.type = {
		this.altName = Some(altName)
		this
	}

	def that(description : String) : this.type = {
		this.description = description
		this
	}

	def theName() = if (name.length > altName.getOrElse("").length) name else altName.getOrElse(name)
	def theAltName() = if (theName != name) Some(name) else altName

}

class DashedArgumentParameterBuilder extends DashedParameterBuilder {

	var numArgs : Int = 0
	var defaults = List[String]()

	def required() : this.type = {
		this.require = true
		this
	}

	def accepting(n : Int) : this.type = {
		this.numArgs = n
		this
	}

	def defaultingTo(defaults : List[String]) : this.type = {
		this.defaults = defaults
		this
	}

	def parameters() = {
		new DashedArgumentParameter(theName(), theAltName(), description, require, numArgs, defaults)
	}
}

class FlagParameterBuilder() extends DashedParameterBuilder {

	def asTrue() = {
		new FlagParameter(theName(), theAltName(), description, true)
	}

	def asFalse() = {
		new FlagParameter(theName(), theAltName(), description, false)
	}
}