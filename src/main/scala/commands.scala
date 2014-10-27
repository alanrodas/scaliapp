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

abstract class Command(
	val name : String,
	val description : String,
	val dashedParameters : Map[String, DashedArgumentParameter],
	val flags : Map[String, FlagParameter]
) {
	def isRoot = name.isEmpty
	def hasRequiredParameters : Boolean
	def hasDashedArgNamed(name : String) = dashedParameters.contains(name)
	def dashedParameter(name : String) = dashedParameters(name)
	def hasFlagNamed(name : String) = flags.contains(name)
	def flagParameter(name : String) = flags(name)
}

case class ArgumentCommand(
	override val name : String,
	override val description : String,
	val parameters : List[NamedParameter],
	override val dashedParameters : Map[String, DashedArgumentParameter],
	override val flags : Map[String, FlagParameter],
	val callback : CalledArgumentCommand => Unit
) extends Command(name, description, dashedParameters, flags) {
	def hasParameter = !parameters.isEmpty
	def hasRequiredParameters = parameters.find(sa => sa.required == true).isDefined
	def requiredParameters() = parameters.filter(_.required)
}

case class MultiArgumentCommand(
	override val name : String,
	override val description : String,
	val minAmountParameters : Int,
	val maxAmountParameters : Int,
	override val dashedParameters : Map[String, DashedArgumentParameter],
	override val flags : Map[String, FlagParameter],
	val callback : CalledMultiArgumentCommand => Unit
 ) extends Command(name, description, dashedParameters, flags)  {
	def hasRequiredParameters = minAmountParameters > 0
	def hasMaxParameters = maxAmountParameters > 0
}