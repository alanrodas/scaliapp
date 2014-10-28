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
package com.alanrodas.scaliapp.core

import com.alanrodas.scaliapp._
import com.alanrodas.scaliapp.core.builders.CommandDefinitionBuilder
import com.alanrodas.scaliapp.core.definitions._
import com.alanrodas.scaliapp.core.exceptions._
import com.alanrodas.scaliapp.core.runtime._

/**
 * Defines a set of values and functions in order to identify parameters
 * and work with them.
 */
object CommandManager{
	/** The default value for the short parameter sign. Defaults to ''"-''. */
	val SHORT_PARAM_SIGN = "-"
	/** The default value for the long parameter sign. Defaults to ''"--''. */
	val LONG_PARAM_SIGN = "--"

	/**
	 * ''true'' if the ''arg'' given is a short parameter starting
	 * ''shortParamSign'', ''false'' otherwise.
	 *
	 * That means that the ''arg'' passed not only starts with the
	 * ''shortParamSign'' but also only contains one letter.
	 *
	 * @param arg The argument to test if it's a short parameter.
	 * @param shortParamSign The short parameter sign to check for
	 */
	def isShortParam(arg : String, shortParamSign : String) =
		arg.startsWith(shortParamSign) && arg.length == shortParamSign.length + 1

	/**
	 * ''true'' if the ''arg'' given is a long parameter starting
	 * ''longParamSign'', ''false'' otherwise.
	 *
	 * That means that the ''arg'' passed not only starts with the
	 * ''longParamSign'' but also only contains more than one letter.
	 *
	 * @param arg The argument to test if it's a short parameter.
	 * @param longParamSign The long parameter sign to check for
	 */
	def isLongParam(arg : String, longParamSign : String) =
		arg.startsWith(longParamSign) && arg.length > longParamSign.length + 1

	/**
	 * ''true'' if the ''arg'' given is a short parameter starting
	 * ''shortParamSign'' or a long parameter staring ''longParamSign'',
	 * ''false'' otherwise.
	 *
	 * That is, either is a short parameter, or a long parameter definition.
	 *
	 * @param arg The argument to test if it's a short parameter.
	 * @param shortParamSign The short parameter sign to check for
	 * @param longParamSign The long parameter sign to check for
	 */
	def isDashedArg(arg : String, shortParamSign : String, longParamSign : String) =
			isShortParam(arg, shortParamSign) || isLongParam(arg, longParamSign)

	/**
	 * ''true'' if the ''arg'' given is starts with ''shortParamSign'' or with
	 *  ''longParamSign'', ''false'' otherwise.
	 *
	 * That means that the given ''arg'' is a misspelled or wrong parameter.
	 *
	 * @param arg The argument to test if it's a short parameter.
	 * @param shortParamSign The short parameter sign to check for
	 * @param longParamSign The long parameter sign to check for
	 */
	def isDashed(arg : String, shortParamSign : String, longParamSign : String) =
			arg.startsWith(shortParamSign) || arg.startsWith(longParamSign)

	/**
	 * Return the ''arg'' without the ''shortParamSign'' ot the ''longParamSign''
	 * accordingly.
	 *
	 * If the argument is a short parameter, the short parameter sign is stripped,
	 * if it's a long one, the long parameter sign is stripped. If it's none of both,
	 * the argument is returned as is.
	 *
	 * @param arg The argument to test if it's a short parameter.
	 * @param shortParamSign The short parameter sign to check for
	 * @param longParamSign The long parameter sign to check for
	 */
	def undashArg(arg : String, shortParamSign : String, longParamSign : String) = {
		if (isShortParam(arg, shortParamSign)) arg.replace(shortParamSign, "")
		else if (isLongParam(arg, longParamSign)) arg.replace(longParamSign, "")
		else arg
	}
}

/**
 * Instances of this class are in charge of holding the defined
 * commands, the short and long parameter signs, as well as
 * parsing the arguments and executing the according code.
 */
class CommandManager {
	private var commands = Map[String, CommandDefinition]()
	private var rootCommand : Option[CommandDefinition] = None
	private var shortParamSign = CommandManager.SHORT_PARAM_SIGN
	private var longParamSign = CommandManager.LONG_PARAM_SIGN

	/**
	 * Set the signs that are going to be used by this command
	 * manager.
	 *
	 * That is, set the arguments that are checked when parsing
	 * the argument inputed by the user. If this command is not
	 * called it defaults to CommandManager.SHORT_PARAM_SIGN and
	 * CommandManager.LONG_PARAM_SIGN as in ''"-"'' and ''"--''.
	 *
	 * @param shortParamSign The short parameter sign
	 * @param longParamSign The long parameter sign
	 */
	def setSigns(shortParamSign : String, longParamSign : String): Unit = {
		this.shortParamSign = shortParamSign
		this.longParamSign = longParamSign
	}

	/** Add a command to the list of commands of this instance */
	def addCommand(command : CommandDefinition) : CommandDefinition = {
		if (commands.contains(command.name) ||
			(rootCommand.isDefined && command.isRoot))
			throw DuplicatedCommandDefinition(command.name)
		if (command.isRoot) rootCommand = Some(command)
		else commands += (command.name -> command)
		command
	}

	/**
	 * Execute the command corresponding to the arguments passed as ''args''.
	 *
	 * The first argument passed in the array is matched against the
	 * declared commands. If there are no matches, the root command is
	 * executed. If there is no root command, then, an error is thrown.
	 * If the root command is executed, the argument array is used as
	 * values and arguments for the root command. If another command is executed
	 * the argument array without the first element is used.
	 *
	 * Once parsed, the callback of the command is executed with the analyzed
	 * arguments in the form of a [[com.alanrodas.scaliapp.core.runtime.Command Command]],
	 * which hold all the information of the invocation.
	 *
	 * @param args The arguments to parse, find the command in, and execute
	 *          the according function.
	 */
	def execute(args : Array[String]) : Unit = {
		val (command, arguments) = getCommand(args)
		command.fold{
			throw new NotFoundCommand(if (args.nonEmpty) Some(args(0)) else command.map(_.name))
		}{ cmd =>
			executeCmd(cmd, arguments)
		}
	}

	/** Return the command to call based on the given arguments */
	private def getCommand(args : Array[String]) = {
		if (shouldCallRoot(args)) (rootCommand, args)
		else if (args.nonEmpty) (commands.get(args(0)), args.tail)
		else (None, Array[String]())
	}

	/** ''true'' if the root command should be called, ''false'' otherwise. */
	private def shouldCallRoot(args : Array[String]) : Boolean =
		rootCommand.isDefined && (args.isEmpty || (args.nonEmpty && !commands.contains(args(0))) )

	/** Execute the command by calling the correct callback and parsing the arguments accordingly. */
	private def executeCmd(command: CommandDefinition, args: Array[String]) : Unit = {
		command match {
			case cmd : FiniteValuesCommandDefinition => cmd.call(parseFiniteValuesArguments(cmd, args))
			case cmd : MultipleValuesCommandDefinition => cmd.call(parseMultipleValuesArguments(cmd, args))
		}
	}

	/**
	 * Parse a finite values command's argument returning the corresponding
	 * [[com.alanrodas.scaliapp.core.runtime.FiniteValuesCommand FiniteValuesCommand]].
	 */
	private def parseFiniteValuesArguments(command : FiniteValuesCommandDefinition, args: Array[String]) : FiniteValuesCommand = {
		val (flags, dashedArgs, notProcessedArgs) = parseArguments(command, args)

		var namedArgs = Map[String, NamedValue]()
		var current = 0
		if (command.valueDefinitions.length < notProcessedArgs.length) {
			throw new ArgumentsExceeded(
				arg().name,
				command.valueDefinitions.length,
				notProcessedArgs.length
			)
		}
		while (current < command.valueDefinitions.length) {
			val param = command.valueDefinitions(current)
			val (value, defined) =
			if (current < notProcessedArgs.length) {
				(Some(notProcessedArgs(current)), true)
			} else {
				if (param.required) throw new RequiredArgumentsNotSatisfied(
						param.name, command.requiredValues().length, notProcessedArgs.length
				)
				else (param.default, false)
			}
			namedArgs += (param.name -> NamedValue(param.name, value.get, defined))
			current += 1
		}
		FiniteValuesCommand(command.name, namedArgs, flags, dashedArgs)
	}

	/**
	 * Parse the parameters passed in the short or long form, returning
	 * the flags, the arguments, and the elements not parsed.
	 *
	 * @throws InvalidArgumentPassed if there is any argument or flag that is not
	 *     valid, that is, it was not defined in the commands.
	 */
	private def parseArguments(command : CommandDefinition, args : Array[String]) : (Set[Flag], Set[Argument], List[String]) = {
		// This is done as an iteration over all values with an index in order to add a
		// little performance, resulted code is extremly ugly though, but reduced time in half
		// from previous prettier version.
		var i = 0
		var flags = Set[Flag]()
		var watchedFlags = Set[String]() // hold the name of flags processed so far

		var arguments = Set[Argument]()
		var watchedArguments = Set[String]() // hold the value of arguments processed so far

		var processedArgs = Set[Int]() // Hold a value of all processed items in the args array

		while(i < args.length) {
			val arg = args(i)
			if (CommandManager.isDashedArg(arg, shortParamSign, longParamSign)) {
				// Only analyze arguments that are of the -arg or the --arg form
				val undashedArg = CommandManager.undashArg(arg, shortParamSign, longParamSign)
				command.getArgOrFlag(undashedArg) match {
					case some : Some[ParameterDefinition] =>
						some.get match {
							case flag : FlagDefinition =>
								flags += Flag(flag.name, flag.altName, !flag.default, defined = true)
								processedArgs += i
								watchedFlags += flag.name
							case arg : ArgumentDefinition =>
								var argParams = List[String]()
								processedArgs += i
								val mandatoryNumValues =  arg.numArgValues - arg.argValuesDefaults.length
								for (argI <- 0 until arg.numArgValues) {

									if (i+1 < args.length &&
										!CommandManager.isDashedArg(args(i+1), shortParamSign, longParamSign)
									) {
										// The argument value was passed
										argParams = argParams :+ args(i+1)
										i += 1
										processedArgs += i
									}
									else if (argI - mandatoryNumValues > 0) {
										// The argument values was not passed, but there is a default defined
										argParams = argParams :+ arg.argValuesDefaults(argI - mandatoryNumValues)
									} else {
										// There is a missing argument
										throw RequiredValueArgumentMissing(
											command.name,
											arg.printName(shortParamSign, longParamSign),
											arg.numArgValues, argParams.length)
									}
								}
								arguments += Argument(arg.name, arg.altName, argParams, defined = true)
								watchedArguments += arg.name
					}
					case None =>
						if ( arg.forall(char => command.definesFlagNamed(char.toString)) ) {
							// Check for flag combinations, such as -abc instead of -a -b -c
							for (char <- arg) {
								command.definedFlag(char.toString) match {
									case Some(flagParam) =>
										flags += Flag(flagParam.name, flagParam.altName, !flagParam.default, defined = true)
										watchedFlags += flagParam.name
									case None =>
								}
							}
							processedArgs += i
						}
				}
			}
			i += 1
		}
		val left = args.zipWithIndex.foldLeft(List[String]()) { (left, each) =>
			if (!processedArgs.contains(each._2)) left :+ each._1 else left
		}
		flags = flags ++ command.allFlags.filterNot(flag => watchedFlags.contains(flag.name))
				.map(e => Flag(e.name, e.altName, e.default, defined = false))
		arguments = arguments ++ command.allArguments.filterNot(arg => watchedArguments.contains(arg.name))
				.map(arg => Argument(arg.name, arg.altName, arg.argValuesDefaults, defined = false))
		val maybeDashed = left.find(each => CommandManager.isDashed(each, shortParamSign, longParamSign))
		if (maybeDashed.isDefined)
			throw new InvalidArgumentPassed(command.name, maybeDashed.get)
		(flags, arguments, left)
	}


	/**
	 * Parse a multiple values command's argument returning the corresponding
	 * [[com.alanrodas.scaliapp.core.runtime.MultipleValuesCommand MultipleValuesCommand]].
	 */
	private def parseMultipleValuesArguments(command : MultipleValuesCommandDefinition, args: Array[String]) : MultipleValuesCommand = {
		val (flags, dashedArgs, notProcessedArgs) = parseArguments(command, args)
		val unnamedArgs = notProcessedArgs map {e => UnnamedValue(e)}
		if (command.hasValuesAmountMinimum && command.amountMinimum > unnamedArgs.length)
			throw RequiredArgumentsNotSatisfied(command.name, command.amountMinimum, unnamedArgs.length)
		else if (command.hasValuesAmountMaximum && command.amountMaximum > unnamedArgs.length)
			ArgumentsExceeded(command.name, command.amountMinimum, unnamedArgs.length)
		MultipleValuesCommand(command.name, unnamedArgs, flags, dashedArgs)
	}
}