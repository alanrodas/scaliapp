/** ********************************************************************************************
  * Scaliapp
  * Version 0.1
  *
  * The primary distribution site is
  *
  * http://scaliapp.alanrodas.com
  *
  * Copyright 2014 Alan Rodas Bonjour
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
package com.alanrodas.scaliapp.core.definitions

/**
 * Represents a parameter definition for any type of
 * parameter, weather flags or arguments.
 *
 * This class defines the long and short names for the parameter.
 * Short names are of the form ''-f'' while a long version is of
 * the form ''--force''.
 *
 * This class has two known subclasses,
 * [[com.alanrodas.scaliapp.core.definitions.FlagDefinition FlagDefinition]] and
 * [[com.alanrodas.scaliapp.core.definitions.ArgumentDefinition ArgumentDefinition]].
 *
 * @param name The name of the parameter, short or long version
 * @param altName An alternative name for the parameter, short if ''name'' is long, long if it's short.
 */
abstract class ParameterDefinition(
	val name : String,
	val altName : Option[String]
) {
	/** The long name of this parameter if it has one, ''None'' otherwise. */
	val longName = if (name.length > 1) Some(name) else altName
	/** Returns ''true'' if this parameter has a long name, ''false'' otherwise. */
	val hasLongName = longName.nonEmpty
	/** The short name of this parameter if it has one, ''None'' otherwise. */
	val shortName = if (name.length == 1) Some(name) else altName
	/** Returns ''true'' if this parameter has a short name, ''false'' otherwise. */
	val hasShortName = shortName.nonEmpty

	def printName(shortParamSign : String, longParamSign : String) = {
		longName.fold("") { s => longParamSign + s } +
		// give a space between long and short form if both exist
		longName.fold("") { _ => " " } +
		shortName.fold("") { s => shortParamSign + s }
	}
}

/**
 * Represents an argument definition for a command.
 *
 * This is a concrete implementation of
 * [[com.alanrodas.scaliapp.core.definitions.ParameterDefinition ParameterDefinition]].
 *
 * @param name The name of the parameter, short or long version
 * @param altName An alternative name for the parameter, short if ''name'' is long, long if it's short.
 * @param required A boolean stating if this argument is required
 * @param numArgValues The number of argument values that this argument takes
 * @param argValuesDefaults A list of default values for the argument values that this argument takes.
 */
case class ArgumentDefinition(
		override val name : String,
		override val altName : Option[String],
		required : Boolean,
		numArgValues : Int,
		argValuesDefaults : List[String]
) extends ParameterDefinition(name, altName)

/**
 * Represents a flag definition for a command.
 *
 * This is a concrete implementation of
 * [[com.alanrodas.scaliapp.core.definitions.ParameterDefinition ParameterDefinition]].
 *
 * @param name The name of the parameter, short or long version
 * @param altName An alternative name for the parameter, short if ''name'' is long, long if it's short.
 * @param default The default value for this flag
 */
case class FlagDefinition(
		override val name : String,
		override val altName : Option[String],
		default : Boolean
) extends ParameterDefinition(name, altName)