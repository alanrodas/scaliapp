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

abstract class ValuedArgument(val value : String)

case class UnnamedArgument(override val value : String) extends ValuedArgument(value)
case class NamedArgument(val name : String, override val value : String) extends ValuedArgument(value)

abstract class Argument[T](
		val name : String,
		val altName : Option[String],
		val value : T,
		val defined : Boolean
) {
	def isFlag = false
}

case class FlagArgument(
		override val name : String,
		override val altName : Option[String],
		override val value : Boolean,
		override val defined : Boolean
) extends Argument[Boolean](name, altName, value, defined) {
	override def isFlag = true
}

case class DashedArgument(
		override val name : String,
		override val altName : Option[String],
		override val value : Seq[String],
		override val defined : Boolean
) extends Argument[Seq[String]](name, altName, value, defined)

abstract class CalledCommand(
		val command : String,
		val flags : Map[String, FlagArgument],
		val dashedArguments : Map[String, DashedArgument]
) {
	def flag(name : String) = flags(name).value
	def dashedArg(name : String) = dashedArguments(name).value
	def allFlags = flags.values.toSet
	def allDashedArgs = dashedArguments.values.toSet
}

case class CalledArgumentCommand(
		override val command : String,
		val arguments : Map[String, NamedArgument],
		override val flags : Map[String, FlagArgument],
		override val dashedArguments : Map[String, DashedArgument]
) extends CalledCommand(command, flags, dashedArguments) {
	def arg(name : String) = arguments(name).value
}

case class CalledMultiArgumentCommand(
		override val command : String,
		val arguments : List[UnnamedArgument],
		override val flags : Map[String, FlagArgument],
		override val dashedArguments : Map[String, DashedArgument]
) extends CalledCommand(command, flags, dashedArguments)