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
 * Represents any value passed as an argument to a command.
 *
 * This abstract class represent any form of value passed, weather
 * named, or unnamed. It has two known subclasses,
 * [[com.alanrodas.scaliapp.core.runtime.UnnamedValue UnnamedValue]] and
 * [[com.alanrodas.scaliapp.core.runtime.NamedValue NamedValue]].
 *
 * @param value The value passed to the command
 */
abstract class AbstractValue(val value : String)

/**
 * Represents an unnamed value passed to a command.
 *
 * This is a concrete implementation of
 * [[com.alanrodas.scaliapp.core.runtime.AbstractValue AbstractValue]]
 * for unnamed values, that means, the values that are passed to a command
 * that accepts multiple arguments.
 *
 * @param value The value passed to the command
 */
case class UnnamedValue(override val value : String) extends AbstractValue(value)

/**
 * Represents a named value passed to a command.
 *
 * This is a concrete implementation of
 * [[com.alanrodas.scaliapp.core.runtime.AbstractValue AbstractValue]]
 * for named values, that means, the values that are passed to a command
 * that accepts a concrete amount of arguments.
 *
 * This class also provides accessors for the name of the value and
 * a boolean stating if the value was passed as an argument or if it
 * is the default value.
 *
 * @param name The name of the value passed to the command
 * @param value The value passed to the command
 * @param defined ''true'' if it was defined by the user, ''false'' otherwise
 */
case class NamedValue(name : String, override val value : String, defined : Boolean)
		extends AbstractValue(value) {

	/**
	 * Returns the value as an instance of the type ''T''
	 *
	 * @param converter The function to convert the String value to a ''T''
	 * @tparam T Any type argument that can be used
	 */
	def value[T](implicit converter : String => T) : T = value
}