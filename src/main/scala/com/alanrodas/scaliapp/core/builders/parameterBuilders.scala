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
package com.alanrodas.scaliapp.core.builders

import com.alanrodas.scaliapp.core.definitions.{ArgumentDefinition, FlagDefinition}
import com.alanrodas.scaliapp.core.exceptions.MissingNameDefinition


/**
 * An abstract class definition for constructing instances
 * of subclasses of
 * [[com.alanrodas.scaliapp.core.definitions.ParameterDefinition ParameterDefinition]].
 * using this class known subclasses.
 *
 * There are two known subclasses for this class,
 * [[com.alanrodas.scaliapp.core.builders.ArgumentDefinitionBuilder ArgumentDefinitionBuilder]] and
 * [[com.alanrodas.scaliapp.core.builders.FlagDefinitionBuilder FlagDefinitionBuilder]].
 *
 * This class is used for constructing instances using the library's DSL.
 */
protected abstract class ParameterDefinitionBuilder() {

	var name : String = ""
	var altName : Option[String] = None
	var require : Boolean = false

	/**
	 * Set the name of the instance to build.
	 *
	 * @param name The name to use in the constructed instance
	 * @return this
	 */
	def named(name : String) : this.type = {
		this.name = name
		this
	}

	/**
	 * Set the alternative name of the instance to build.
	 *
	 * @param altName The name to use in the constructed instance
	 * @return this
	 */
	def alias(altName : String) : this.type = {
		this.altName = Some(altName)
		this
	}

}

/**
 * A builder to construct instances of
 * [[com.alanrodas.scaliapp.core.definitions.ArgumentDefinition ArgumentDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 */
class ArgumentDefinitionBuilder extends ParameterDefinitionBuilder {

	var numArgValues : Int = 0
	var defaults = List[String]()

	/**
	 * Set the created instance as a required argument.
	 * 
	 * @return this
	 */
	def required() : this.type = {
		this.require = true
		this
	}

	/**
	 * Set the number of argument values that the instance will take.
	 *
	 * @param n The number of arguments to accept as a positive integer
	 * @return this
	 */
	def taking(n : Int) : this.type = {
		this.numArgValues = n
		this
	}

	/**
	 * Set the default values for the argument values that this instance will take.
	 *
	 * Note that {{{defaults.length <= n}}} where ''n'' is the number
	 * of argument that the argument will take, passed ussing the
	 * ''accepting'' command.
	 *
	 * @param defaults the default values for the arguments that this argument will take.
	 * @return The constructed instance
	 */
	def as(defaults : List[String]) = {
		if (name.isEmpty) throw MissingNameDefinition("argument")
		new ArgumentDefinition(name, altName, require, numArgValues, defaults)
	}

	/** Constructs the instance with no defaults and returns it. */
	def values = {
		as(Nil)
	}

	/** Constructs the instance with no defaults and returns it. */
	def value = {
		this.values
	}
}

/**
 * A builder to construct instances of
 * [[com.alanrodas.scaliapp.core.definitions.FlagDefinition FlagDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 */
class FlagDefinitionBuilder() extends ParameterDefinitionBuilder {

	/** Build the instance with a default value of ''default''. */
	def as(default : Boolean) = {
		if (name.isEmpty) throw MissingNameDefinition("flag")
		new FlagDefinition(name, altName, default)
	}
}