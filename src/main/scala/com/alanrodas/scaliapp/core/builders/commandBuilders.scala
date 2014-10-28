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

import com.alanrodas.scaliapp.core.CommandManager
import com.alanrodas.scaliapp.core.definitions._
import com.alanrodas.scaliapp.core.exceptions.{InvalidValueOrder, DuplicatedParameterDefinition}
import com.alanrodas.scaliapp.core.runtime._

/**
 * A builder to construct instances of
 * [[com.alanrodas.scaliapp.core.definitions.CommandDefinition CommandDefinition]].
 *
 * This class is used for constructing instances using the library's DSL. At the point
 * of creating a new command with the DSL, there is no certainty if it will be a finite
 * values command, or a multiple values command. This forces this class to be a concrete
 * one, and return a most specified version when a certain condition is met such as stating
 * that it receives multiple values.
 *
 * @param commandManager The CommandManager to add the created CommandDefinition to. If using CLIApp,
 *          by using the provided DSL ''commands add "myCommand"'', the CLIApp command manager is used.
 */
class CommandDefinitionBuilder(val commandManager : CommandManager) {

	var arguments = List[ArgumentDefinition]()
	var flags = List[FlagDefinition]()
	var name = ""

	private var allParameters = Set[String]()

	private def registerParam(param : ParameterDefinition) = {
		allParameters += param.name
		if (param.altName.isDefined) allParameters += param.altName.get
	}

	private def definedAlready(param : ParameterDefinition) = {
		allParameters.contains(param.name) ||
		param.altName.fold(false){altName => allParameters.contains(altName)}
	}

	private def alreadyDefinedName(param : ParameterDefinition) = {
		if (allParameters.contains(param.name)) param.name
		else param.altName.get
	}

	private def validateAndRegisterParam(param : ParameterDefinition) {
		if (definedAlready(param)) {
			throw DuplicatedParameterDefinition(alreadyDefinedName(param))
		}
		registerParam(param)
	}

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

	/** Returns this command definition builder as a multiple values command definition builder. */
	def multipleValues : MultipleValuesCommandDefinitionBuilder = {
		MultipleValuesCommandDefinitionBuilder(this)
	}

	/**
	 * Convert this instance in a multiple values definition builder and sets it's
	 * minimum number of values.
	 */
	def minimumOf(n : Int) : MultipleValuesCommandDefinitionBuilder = multipleValues.minimumOf(n)

	/**
	 * Convert this instance in a multiple values definition builder and sets it's
	 * maximum number of values.
	 */
	def maximumOf(n : Int) : MultipleValuesCommandDefinitionBuilder = multipleValues.maximumOf(n)

	/** Returns this command definition builder as a finite values command definition builder. */
	def receives(value : ValueDefinition) : FiniteValuesCommandDefinitionBuilder = {
		FiniteValuesCommandDefinitionBuilder(this).receives(value)
	}

	/** Returns this command definition builder as a finite values command definition builder. */
	def receives(values : Iterable[ValueDefinition]) : FiniteValuesCommandDefinitionBuilder = {
		FiniteValuesCommandDefinitionBuilder(this).receives(values)
	}
	
	/** Adds the given parameter as to the instance to be constructed. */
	def accepts(param : ParameterDefinition) : this.type = {
		validateAndRegisterParam(param)
		param match {
			case flag : FlagDefinition => flags = flags :+ flag
			case arg : ArgumentDefinition => arguments = arguments :+ arg
		}
		this
	}

	/** Adds the given parameters as to the instance to be constructed. */
	def accepts(params : Iterable[ParameterDefinition]) : this.type = {
		for(param <- params) this accepts param
		this
	}
}


object FiniteValuesCommandDefinitionBuilder {
	/**
	 * Constructs an instance of
	 * [[com.alanrodas.scaliapp.core.builders.FiniteValuesCommandDefinitionBuilder FiniteValuesCommandDefinitionBuilder]],
	 * from the given [[com.alanrodas.scaliapp.core.builders.CommandDefinitionBuilder CommandDefinitionBuilder]].
	 */
	def apply(builder : CommandDefinitionBuilder) = {
		val newBuilder = new FiniteValuesCommandDefinitionBuilder(builder.commandManager)
		newBuilder.name = builder.name
		newBuilder.arguments = builder.arguments
		newBuilder.flags = builder.flags
		newBuilder
	}
}
/**
 * A builder to construct instances of
 * [[com.alanrodas.scaliapp.core.definitions.FiniteValuesCommandDefinition FiniteValuesCommandDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 *
 * @param commandManager The CommandManager to add the created CommandDefinition to. If using CLIApp,
 *          by using the provided DSL ''commands add "myCommand"'', the CLIApp command manager is used.
 */
class FiniteValuesCommandDefinitionBuilder(override val commandManager : CommandManager)
		extends CommandDefinitionBuilder(commandManager) {
	var values : List[ValueDefinition] = Nil
	private var lastWasOptional = false
	
	/** Adds a new value definition to the constructed instance. */
	override def receives(value : ValueDefinition) : this.type = {
		if (value.required && lastWasOptional) {
			throw InvalidValueOrder(name,values.last.name,value.name)
		}
		lastWasOptional = !value.required
		this.values = this.values :+ value
		this
	}

	/** Adds a new set of value definitions to the constructed instance. */
	override def receives(values : Iterable[ValueDefinition]) : this.type = {
		for(value <- values) this receives value
		this
	}

	/** Constructs and adds a FiniteValuesCommandDefinition to this builder CommandManager. */
	def does(callback : FiniteValuesCommand => Unit) = {
		commandManager.addCommand(
			new FiniteValuesCommandDefinition(name, values, arguments, flags, callback)
		)
	}
}

object MultipleValuesCommandDefinitionBuilder {
	/**
	 * Constructs an instance of
	 * [[com.alanrodas.scaliapp.core.builders.FiniteValuesCommandDefinitionBuilder FiniteValuesCommandDefinitionBuilder]],
	 * from the given [[com.alanrodas.scaliapp.core.builders.CommandDefinitionBuilder CommandDefinitionBuilder]].
	 */
	def apply(builder : CommandDefinitionBuilder) = {
		val newBuilder = new MultipleValuesCommandDefinitionBuilder(builder.commandManager)
		newBuilder.name = builder.name
		newBuilder.arguments = builder.arguments
		newBuilder.flags = builder.flags
		newBuilder
	}
}
/**
 * A builder to construct instances of
 * [[com.alanrodas.scaliapp.core.definitions.MultipleValuesCommandDefinition MultipleValuesCommandDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 *
 * @param commandManager The CommandManager to add the created CommandDefinition to. If using CLIApp,
 *          by using the provided DSL ''commands add "myCommand"'', the CLIApp command manager is used.
 */
class MultipleValuesCommandDefinitionBuilder(override val commandManager : CommandManager)
		extends CommandDefinitionBuilder(commandManager) {
	private var minMultiArgs : Option[Int] = None
	private var maxMultiArgs : Option[Int] = None

	/** Sets the number of minimum argument for the constructed instance. */
	override def minimumOf(n : Int) : this.type = {
		minMultiArgs = Some(n)
		this
	}

	/** Sets the number of maximum argument for the constructed instance. */
	override def maximumOf(n : Int) : this.type = {
		maxMultiArgs = Some(n)
		this
	}

	/** Constructs and adds a MultipleValuesCommandDefinition to this builder CommandManager. */
	def does(callback : MultipleValuesCommand => Unit) = {
		commandManager.addCommand(
			new MultipleValuesCommandDefinition(name, minMultiArgs, maxMultiArgs, arguments, flags, callback)
		)
	}
}

