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
package com.alanrodas.scaliapp.core.runtime

/**
 * This is an abstract implementation of any kind of argument,
 * weather flags or arguments.
 *
 * This class defines things such as the long and short name
 * of the argument, a defined accessor to check if the argument
 * has effectively been passed as an argument or if the default
 * was used.
 *
 * It additionally defines an ''allValues'' method that returns
 * a value of type ''T''. This method can be used in order to retrieve
 * the value of the argument as the type defined by the subclasses of
 * AbstractArgument.
 *
 * There are two known subclasses of AbstractArgument
 * [[Flag Flag]] and
 * [[Argument Argument]]. The first one defines
 * ''T'' as ''Boolean'', while the second one allows for multiple
 * values to be retrieved in the form of a ''Seq[String]''. They both
 * also provide more specific forms to fetch the value of the argument.
 *
 * @param name The name of the argument or flag, weather short or long
 * @param altName An additional optional name for the argument or flag. short if name was long, and long if it was short
 * @param value The value of the current argument
 * @param defined ''true'' if it was defined by the user, ''false'' otherwise
 * @tparam T The type of the values that this argument represents
 */
abstract class AbstractArgument[T](
		private val name : String,
		private val altName : Option[String],
		private val value : T,
		val defined : Boolean
) {
	/** Returns the long name if this AbstractArgument has one, or None otherwise. */
	val longName = if (name.length > 1) Some(name) else altName
	/** Returns ''true'' if it has a long name, ''false'' otherwise. */
	val hasLongName = longName.nonEmpty
	/** Returns the short name if this AbstractArgument has one, or None otherwise. */
	val shortName = if (name.length == 1) Some(name) else altName
	/** Returns ''true'' if it has a short name, ''false'' otherwise. */
	val hasShortName = shortName.nonEmpty
	/** Returns all the values of this AbstractArgument. */
	val allValues = value

	def printName(shortParamSign : String, longParamSign : String) = {
		longName.fold("") { s => longParamSign + s } +
		// give a space between long and short form if both exist
		longName.fold("") { _ => " " } +
		shortName.fold("") { s => shortParamSign + s }
	}
}

/**
 * This is a special case of
 * [[com.alanrodas.scaliapp.core.runtime.AbstractArgument AbstractArgument]]
 * where the value is a Boolean.
 *
 * This class provides the ''value'' method as a way to access the
 * argument value.
 *
 * @param name The name of the argument or flag, weather short or long
 * @param altName An additional optional name for the argument or flag. short if name was long, and long if it was short
 * @param value The value of the current argument
 * @param defined ''true'' if it was defined by the user, ''false'' otherwise
 */
case class Flag(
		private val name : String,
		private val altName : Option[String],
		value : Boolean,
		override val defined : Boolean
) extends AbstractArgument[Boolean](name, altName, value, defined)

/**
 * This is a special case of
 * [[com.alanrodas.scaliapp.core.runtime.AbstractArgument AbstractArgument]]
 * where the value is a Seq[String].
 *
 * This class provides the ''valuesAmount'' that returns the number
 * of values passed to the argument, as well as a ''value[T](i : Int)''
 * that returns the value at position ''i'' as an instance of ''T''
 * providing that an implicit conversion between ''String'' and the
 * type ''T''.
 *
 * Scaliapp provides the package [[com.alanrodas.scaliapp.implicits]] that
 * provides a set of implicit conversions between ''String'' and different
 * types, such as ''Int'' and ''Boolean''.
 *
 * @param name The name of the argument or flag, weather short or long
 * @param altName An additional optional name for the argument or flag. short if name was long, and long if it was short
 * @param values The sequence of values of the current argument
 * @param defined ''true'' if it was defined by the user, ''false'' otherwise
 */
case class Argument(
		private val name : String,
		private val altName : Option[String],
		values : Seq[String],
		override val defined : Boolean
) extends AbstractArgument[Seq[String]](name, altName, values, defined) {
	/**
	 * Returns the i-th value in the sequence of passed values to the current
	 * argument as an instance of ''T'', provided that an implicit ''String''
	 * to ''T'' conversion is given.
	 *
	 * Scaliapp provides the package [[com.alanrodas.scaliapp.implicits]] that
	 * provides a set of implicit conversions between ''String'' and different
	 * types, such as ''Int'' and ''Boolean''.
	 *
	 * @param i The index of the element to fetch
	 * @param f An implicit function that converts from ''String'' to type ''T''
	 * @tparam T The type in which to convert the value to
	 * @return The value in the i-th position in the sequence of passed values as an instance of ''T''
	 */
	def value[T](i : Int)(implicit f : String => T) : T = values(i)

	/** Returns the number of values passed to the argument. */
	def valuesAmount = values.length
}