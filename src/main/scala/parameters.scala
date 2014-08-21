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

class SimpleParameter(
	val name : String,
	val default : String,
	val required : Boolean
)

class SimpleParameterBuilder {
	var name : String = ""
	var default : String = ""

	def named(name : String) = {
		this.name = name
		this
	}

	def withDefault(default : String) = {
		new SimpleParameter(name, default, false)
	}

	def withNoDefault() = {
		new SimpleParameter(name, "", true)
	}
}

class Parameter(
	val name : String,
	val shortname : Option[String],
	val description : String,
	val required : Boolean,
	val numArgs : Int,
	val argsDefaults : Map[Int, String]
)

class ParameterBuilder {
	var name : String = ""
	var shortname : Option[String] = None
	var description : String = ""
	var require : Boolean = false

	def named(name : String) = {
		this.name = name
		this
	}

	def shortVersion(shortname : String) = {
		this.shortname = Some(shortname)
		this
	}

	def that(description : String) = {
		this.description = description
		this
	}

	def required() = {
		this.require = true
		this
	}

	def does(callback : List[Parameter] => Int) = {
		this
	}
}