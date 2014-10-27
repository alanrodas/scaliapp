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

case class NamedParameter(
	val name : String,
	val default : String,
	val required : Boolean
)

class DashedParameter(
	val name : String,
	val altName : Option[String],
	val description : String
)

case class DashedArgumentParameter(
		override val name : String,
		override val altName : Option[String],
		override val description : String,
		val required : Boolean,
		val numArgs : Int,
		val argsDefaults : List[String]
) extends DashedParameter(name, altName, description)

case class FlagParameter(
		override val name : String,
		override val altName : Option[String],
		override val description : String,
		val default : Boolean
) extends DashedParameter(name, altName, description)