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

import com.alanrodas.scaliapp._
import com.alanrodas.scaliapp.core.definitions.{MultipleValuesCommandDefinition, FiniteValuesCommandDefinition}
import com.alanrodas.scaliapp.core.exceptions._
import com.alanrodas.scaliapp.core.CommandManager

/**
 * Represents an abstract command call and the values used to call it.
 *
 * This is an abstract representation of a command call. That is, this class holds
 * the values of flags and arguments on which a user has called a given command,
 * included the defaulted values in its definition.
 *
 * It has two known concrete subclasses
 * [[FiniteValuesCommand CalledNamedValuedCommand]] and
 * [[MultipleValuesCommand CalledUnnamedValuedCommand]]
 * that represent a call to a [[FiniteValuesCommandDefinition NamedValuedCommand]]
 * and a [[MultipleValuesCommandDefinition UnnamedValuedCommand]] respectively.
 * They both add the values that a command has been called with in a different way.
 *
 * To create an instance of this class, the values that the user has employed when
 * calling the matching command as well as the default values are expected to be given as
 * an argument to the constructor. The command name is also needed.
 *
 * A CommandCall with the matching information is then passed as an argument to the
 * callback in the corresponding [[com.alanrodas.scaliapp.core.definitions.CommandDefinition Command]].
 *
 * @param command The called command name
 * @param passedFlags The Flags that were used to call this command
 * @param passedArgs  The Arguments that were used to call this command
 */
abstract class Command(
	val command : String,
	protected val passedFlags : Iterable[Flag],
	protected val passedArgs : Iterable[Argument]
) {
	private val flags = passedFlags.foldLeft(Map[String, Flag]()){(map, each) =>
		map ++ each.shortName.fold( Map[String, Flag]() ) {s => Map(s->each)} ++
				each.longName.fold( Map[String, Flag]() ){s => Map(s->each)}
	}

	private val arguments = passedArgs.foldLeft(Map[String, Argument]()){(map, each) =>
		map ++ each.shortName.fold( Map[String, Argument]() ) {s => Map(s->each)} ++
				each.longName.fold( Map[String, Argument]() ){s => Map(s->each)}
	}

	private val flagAmount = passedFlags.size
	private val argumentsAmount = passedArgs.size

	private def fetchArgByNameWithError[T <: AbstractArgument[_]](name : String, map : Map[String, T], exception : AbstractInvalidName) =
		try {map(name)} catch {case e : InvalidCommandCall => throw exception}

	/** Returns all the flags for this CommandCall. */
	def allFlags = passedFlags

	/**
	 * Returns a Flag instance for the given name.
	 *
	 * @param name: The name of the flag to retrieve the Flag from
	 * @throws InvalidFlagName if the given name does not match an available flag name for the command
	 */
	def flag(name : String) = fetchArgByNameWithError(name, flags, InvalidFlagName(name, command))

	/**
	 * Returns the value of the flag with the given name.
	 *
	 * @param name: The name of the flag to retrieve the value from
	 * @throws InvalidFlagName if the given name does not match an available flag name for the command
	 */
	def flagValue(name : String) = flag(name).allValues

	/**
	 * Returns ''true'' if the flag's value was passed by the user, ''false'' otherwise.
	 *
	 * @param name: The name of the flag to check if it was passed as an argument by the user
	 * @throws InvalidFlagName if the given name does not match an available flag name for the command
	 */
	def flagPassed(name : String) = flag(name).defined

	/** Returns all the arguments for this CommandCall. */
	def allArguments = passedArgs

	/**
	 * Returns an Argument instance for the given name.
	 *
	 * @param name: The name of the argument to retrieve the Argument from
	 * @throws InvalidArgumentName if the given name does not match an available argument name for the command
	 */
	def argument(name : String) = fetchArgByNameWithError(name, arguments, InvalidArgumentName(name, command))

	/**
	 * Returns the value of the argument with the given name.
	 *
	 * @param name: The name of the argument to retrieve the value from
	 * @throws InvalidArgumentName if the given name does not match an available argument name for the command
	 */
	def argumentValue(name : String) = argument(name).allValues

	/**
	 * Returns ''true'' if the value of a given argument was passed by the user, ''false'' otherwise.
	 *
	 * @param name: The name of the argument to check if it was passed as an argument by the user
	 * @throws InvalidArgumentName if the given name does not match an available argument name for the command
	 */
	def argumentPassed(name : String) = argument(name).defined

	protected def getDumpItem(name : String, defined : Boolean, value : Any) = {
		Console.BLUE + name + Console.RESET + ": (" +
		(if (defined) Console.GREEN + "User defined"
				else Console.RED + "Default") + Console.RESET + ") " +
		value.toString
	}

	/**
	 * Returns a String that represents all the values passed
	 *  to the command with the standard dumping colo codes.
	 */
	protected def getDumpValues : String

	private def getDumpName = Console.BLUE + command + Console.RESET

	private def dumpString(shortParamSign : String, longParamSign : String) = {
		"Called command: " + getDumpName +
		"\nValues are:\n" + getDumpValues +
		"\nFlags Are: (" + Console.BLUE + flagAmount + Console.RESET + ")\n" +
		(for (flag : Flag <- allFlags) yield
			getDumpItem(flag.printName(shortParamSign, longParamSign), flag.defined, flag.allValues)).mkString("\n") +
		"\nArguments are: (" + Console.BLUE + argumentsAmount + Console.RESET + ")\n" +
		(for (da : Argument <- allArguments) yield
			getDumpItem(da.printName(shortParamSign, longParamSign), da.defined, da.allValues)).mkString("\n") +
		"\n"
	}

	/**
	 * Prints in the console all the information about this CommandCall.
	 *
	 * ''This method is intended mostly for debugging purposes, and it should
	 * not be used.''
	 *
	 * This method dumps the information of this CommandCall to the Console
	 * using print for it's output. It uses a color scheme where flag and
	 * argument names are presented in blue, their values in green, and an indicator
	 * telling if the value was given by the user or it's a defaulted value in green
	 * or red, the rest is printed in the default color.
	 */
	@deprecated
	def dump(shortParamSign : String = CommandManager.SHORT_PARAM_SIGN, longParamSign : String =  CommandManager.LONG_PARAM_SIGN) {
		print(dumpString(shortParamSign, longParamSign))
	}
}

/**
 * This is a concrete implementation for
 * [[com.alanrodas.scaliapp.core.runtime.Command CalledCommand]] for
 * [[FiniteValuesCommandDefinition NamedValuedCommand]].
 * It adds the values that the command was called with as a set of
 * [[NamedValue NamedValue]].
 *
 * Note that the values may contain also defaulted values, as they should be passed
 * when creating instances of this class.
 *
 * @param command The called command name
 * @param values The values that were used to call this command
 * @param passedFlags The Flags that were used to call this command
 * @param passedArgs  The Arguments that were used to call this command
 */
case class FiniteValuesCommand(
	override val command : String,
	protected val values : Map[String, NamedValue],
	override protected val passedFlags : Iterable[Flag],
	override protected val passedArgs : Iterable[Argument]
) extends Command(command, passedFlags, passedArgs) {

	private def fetchValueByNameWithError(name : String, map : Map[String, NamedValue], exception : AbstractInvalidName) =
		try {map(name)} catch {case e : InvalidCommandCall => throw exception}

	/** Returns all the values for this CommandCall. */
	def allValues = values.values.toSet


	/** Return the value of the named value with the given name.
	 *
	 * @param name: The name of the named value to retrieve the value from
	 * @throws InvalidValueName if the given name does not match an available value name for the command
	 */
	def value(name : String) = fetchValueByNameWithError(name, values, InvalidValueName(name, command)).value

	/** Return ''true'' if the value with the given name was passed by the user, ''false'' otherwise.
	  *
	  * @param name: The name of the argument to check if it was passed as an argument by the user
	  * @throws InvalidArgumentName if the given name does not match an available argument name for the command
	  */
	def valuePassed(name : String) = fetchValueByNameWithError(name, values, InvalidValueName(name, command)).defined

	def getDumpValues = {
		(for ( (name, value) <- values)
			yield getDumpItem(value.name, value.defined, value.value)
		).mkString("\n")
	}
}

/**
 * This is a concrete implementation for
 * [[com.alanrodas.scaliapp.core.runtime.Command CalledCommand]] for
 * [[MultipleValuesCommandDefinition UnnamedValuedCommand]].
 * It adds the values that the command was called with as a set of
 * [[UnnamedValue UnnamedValue]].
 *
 * @param command The called command name
 * @param values The values that were used to call this command
 * @param passedFlags The Flags that were used to call this command
 * @param passedArgs  The Arguments that were used to call this command
 */
case class MultipleValuesCommand(
	override val command : String,
	protected val values : List[UnnamedValue],
	override protected val passedFlags : Iterable[Flag],
	override protected val passedArgs : Iterable[Argument]
) extends Command(command, passedFlags, passedArgs) {
	def getDumpValues = Console.GREEN + values.map(_.value).mkString("\n") + Console.RESET
	/** Returns all the values for this CommandCall. */
	def allValues = values
}