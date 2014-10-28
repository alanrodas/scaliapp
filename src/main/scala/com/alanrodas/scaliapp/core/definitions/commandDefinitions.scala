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

import com.alanrodas.scaliapp.core.runtime.{Command, FiniteValuesCommand, MultipleValuesCommand}

/**
 * Represents a command definition for any kind of command weather named or not.
 *
 * This has two known concrete subclasses,
 * [[com.alanrodas.scaliapp.core.definitions.FiniteValuesCommandDefinition FiniteValuesCommand]] and
 * [[com.alanrodas.scaliapp.core.definitions.MultipleValuesCommandDefinition MultipleValuesCommand]].
 *
 * This class provides defines the name of the command and common arguments such as flags
 * and arguments of the form --arg.
 * 
 * @param name The name of the command definition
 * @param passedArgDefinitions The arguments that this command accepts
 * @param passedFlagDefinitions The flags that this command accepts
 */
abstract class CommandDefinition(
	val name : String,
	private val passedArgDefinitions : Iterable[ArgumentDefinition],
	private val passedFlagDefinitions : Iterable[FlagDefinition]
) {
	private val argDefinitions = passedArgDefinitions.foldLeft(Map[String, ArgumentDefinition]()) {(map, each) =>
		map ++ each.shortName.fold( Map[String, ArgumentDefinition]() ) {s => Map(s->each)} ++
				each.longName.fold( Map[String, ArgumentDefinition]() ){s => Map(s->each)}
	}
	private val flagDefinitions = passedFlagDefinitions.foldLeft(Map[String, FlagDefinition]()) {(map, each) =>
		map ++ each.shortName.fold( Map[String, FlagDefinition]() ) {s => Map(s->each)} ++
				each.longName.fold( Map[String, FlagDefinition]() ){s => Map(s->each)}
	}

	protected type CmdArg <: Command

	/** Returns ''true'' if this is the root command, ''false'' otherwise. */
	def isRoot = name.isEmpty

	/** Returns ''true'' if this command has any ''required'' values.*/
	def definesRequiredValues : Boolean

	/** Returns ''true'' if this command defines an argument by the name ''name'', ''false'' otherwise. */
	def definesArgumentNamed(name : String) = argDefinitions.contains(name)
	/**
	 * Returns some argument definition by the name of ''name'' or ''None'' if there
	 * are no argument definition matching that name.
	 */
	def definedArgument(name : String) = argDefinitions.get(name)
	/** Returns all the arguments defined by this command. */
	def allArguments = passedArgDefinitions

	/** Returns ''true'' if this command defines a flag by the name ''name'', ''false'' otherwise. */
	def definesFlagNamed(name : String) = flagDefinitions.contains(name)
	/**
	 * Returns some flag definition by the name of ''name'' or ''None'' if there
	 * are no flag definition matching that name.
	 */
	def definedFlag(name : String) = flagDefinitions.get(name)
	/** Returns all the flags defined by this command. */
	def allFlags = passedFlagDefinitions

	/**
	 * Execute the command callback with the given
	 * [[com.alanrodas.scaliapp.core.runtime.Command CalledCommand]].
	 *
	 * @param cmd The CalledCommand with all the arguments and values
	 *          passed as an argument to the command
	 */
	def call(cmd : CmdArg) : Unit

	/**
	 * Returns some ArgumentParameter or some FlagParameter by the name ''name'' if it exists, None otherwise.
	 *
	 * @param name The name of the argument parameter or flag parameter to fetch
	 */
	def getArgOrFlag(name : String) : Option[ParameterDefinition] = {
		if (definesFlagNamed(name)) definedFlag(name)
		else if (definesArgumentNamed(name)) definedArgument(name)
		else None
	}
}

/**
 * This class represents a command definition for commands that receive a
 * finite amount of values.
 *
 * This is a concrete implementation of
 * [[com.alanrodas.scaliapp.core.definitions.CommandDefinition CommandDefinition]].
 *
 * @param name The name of the command definition
 * @param valueDefinitions The values that this command accept
 * @param passedArgDefinitions The arguments that this command accepts
 * @param passedFlagDefinitions The flags that this command accepts
 * @param callback The function to execute when this command is called
 */
case class FiniteValuesCommandDefinition(
	override val name : String,
	valueDefinitions : List[ValueDefinition],
	private val passedArgDefinitions : Iterable[ArgumentDefinition],
	private val passedFlagDefinitions : Iterable[FlagDefinition],
	callback : FiniteValuesCommand => Unit
) extends CommandDefinition(name, passedArgDefinitions, passedFlagDefinitions) {
	override protected type CmdArg = FiniteValuesCommand
	/** Returns ''true'' if this command takes a value, ''false'' otherwise. */
	def definesValues = valueDefinitions.nonEmpty
	def definesRequiredValues = valueDefinitions.exists(sa => sa.required)
	/** Returns a list of required value definitions. */
	def requiredValues() = valueDefinitions.filter(_.required)
	def call(cmd : CmdArg) = callback(cmd)
}

/**
 * This class represents a command definition for commands that receive a
 * finite amount of values.
 *
 * This is a concrete implementation of
 * [[com.alanrodas.scaliapp.core.definitions.CommandDefinition CommandDefinition]].
 *
 * @param name The name of the command definition
 * @param minAmountValues The minimum amount of values that this command takes
 * @param maxAmountValues The maximum amount of values that this command takes
 * @param passedArgDefinitions The arguments that this command accepts
 * @param passedFlagDefinitions The flags that this command accepts
 * @param callback The function to execute when this command is called
 */
case class MultipleValuesCommandDefinition(
	override val name : String,
	minAmountValues : Option[Int],
	maxAmountValues : Option[Int],
	private val passedArgDefinitions : Iterable[ArgumentDefinition],
	private val passedFlagDefinitions : Iterable[FlagDefinition],
	callback : MultipleValuesCommand => Unit
 ) extends CommandDefinition(name, passedArgDefinitions, passedFlagDefinitions)  {
	override protected type CmdArg = MultipleValuesCommand
	def definesRequiredValues = minAmountValues.isDefined
	/** Returns ''true'' if this command takes a maximum amount of values, ''false'' otherwise. */
	def hasValuesAmountMaximum = maxAmountValues.isDefined
	/** Returns the maximum amount of values that this command accepts. */
	def amountMaximum = maxAmountValues.get
	/** Returns ''true'' if this command takes a minimum amount of values, ''false'' otherwise. */
	def hasValuesAmountMinimum = definesRequiredValues
	/** Returns the minimum amount of values that this command accepts. */
	def amountMinimum = minAmountValues.get
	def call(cmd : CmdArg) = callback(cmd)
}