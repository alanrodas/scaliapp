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

class CommandManager {
	var commands = Map[String, Command]()
	var root : Option[Command] = None

	def add(name : String) : CommandBuilder = {
		new CommandBuilder(name.trim(), this)
	}

	def addCommand(command : Command) : Command = {
		if (command.isRoot) root = Some(command)
		else commands += (command.name -> command)
		command
	}

	def execute(args : Array[String]) : CalledCommand = {
		if (shouldCallRoot(args)) parseArguments(root.get, args)
		else {parseArguments(commands(args(0)), args.tail)}
	}

	private def shouldCallRoot(args : Array[String]) : Boolean = {
		root.isDefined && (
				args.isEmpty ||
						(!commands.contains(args(0)) && !root.get.hasRequiredParameters)
				)
	}

	private def parseArguments(command: Command, args: Array[String]) : CalledCommand = {
		command match {
			case cmd : ArgumentCommand => parseSimpleArguments(cmd, args)
			case cmd : MultiArgumentCommand => parseMultiArguments(cmd, args)
		}
	}

	private def parseSimpleArguments(command : ArgumentCommand, args: Array[String]) : CalledArgumentCommand = {
		val (flags, usedFlags) = parseFlags(command, args)
		val (dashedArgs,usedArgs) = parseDashedArg(command, args)
		val arguments = args.filterNot((arg) => usedFlags.contains(arg) || usedArgs.contains(arg))

		var namedArgs = Map[String, NamedArgument]()
		var current = 0
		if (command.parameters.length < arguments.length) {
			throw new ArgumentsExceeded(param.name, command.parameters.length, arguments.length)
		}
		while (current < command.parameters.length) {
			val param = command.parameters(current)
			val value =
			(if (current < arguments.length) {
				arguments(current)
			} else {
				if (param.required) throw new RequiredArgumentsNotSatisfied(
						param.name, command.requiredParameters().length, arguments.length
				)
				else param.default
			})
			namedArgs += (param.name -> NamedArgument(param.name, value))
			current += 1
		}
		CalledArgumentCommand(command.name, namedArgs, flags, dashedArgs)
	}

	private def parseFlags(command : Command, args : Array[String]) : (Map[String, FlagArgument], List[String]) = {
		var flags = Map[String, FlagArgument]()
		var appliedFlags = List[String]()
		for(arg <- args) {
			if (arg.startsWith("-")) {
				// Only analyze arguments that are of the -arg or the --arg form
				val undashedArg = arg.replaceAll("-", "")
				if (command.hasFlagNamed(undashedArg)) {
					val flagParam = command.flagParameter(undashedArg)
					flags = addFlag(flags, flagParam, true)
					appliedFlags = appliedFlags :+ arg
				} else if (
					!command.hasDashedArgNamed(undashedArg) &&
					arg.forall(char => command.hasFlagNamed(char.toString))
				) {
					// Check for flag combinations, such as -abc instead of -a -b -c
					for (char <- arg) {
						if (command.hasFlagNamed(char.toString)) {
							val flagParam = command.flagParameter(char.toString)
							flags = addFlag(flags, flagParam, true)
						}
					}
					appliedFlags = appliedFlags :+ arg
				}
			}
		}
		// Add all the rest of the flags of the command as not defined flags
		for ((name, flagParam) <- command.flags) {
			if (!flags.contains(name)) {
				flags = addFlag(flags, flagParam, false)
			}
		}
		(flags, appliedFlags)
	}

	private def addFlag(flags : Map[String, FlagArgument], flagParam : FlagParameter, defined : Boolean) : Map[String, FlagArgument] = {
		val flagArg = FlagArgument(flagParam.name, flagParam.altName, !flagParam.default, defined)
		(if (flagParam.altName.isDefined) {
			flags + (flagParam.altName.get ->flagArg)
		} else {
			flags
		}) + (flagParam.name -> flagArg)
	}

	private def parseDashedArg(command : Command, args : Array[String]) : (Map[String, DashedArgument], List[String]) = {
		var dashedArguments = Map[String, DashedArgument]()
		var appliedArgs = List[String]()
		var current = 0
		var currentArgs = List[String]()
		while (current < args.length) {
			val arg = args(current)
			if (arg.startsWith("-")) {
				val undashedArg = arg.replaceAll("-", "")
				if (command.hasDashedArgNamed(undashedArg)) {
					appliedArgs = appliedArgs :+ arg
					val dashedArgParam = command.dashedParameter(undashedArg)
					for (argI <- 0 until dashedArgParam.numArgs) {
						current += 1
						if (current < args.length && !args(current).startsWith("-")) {
							appliedArgs = appliedArgs :+ args(current)
							currentArgs = currentArgs :+ args(current)
						}
						else {
							if (current < dashedArgParam.argsDefaults.length) {
								currentArgs = currentArgs :+ dashedArgParam.argsDefaults(argI)
							}
						}
					}
					val dashedArg = DashedArgument(dashedArgParam.name, dashedArgParam.altName, currentArgs, true)
					dashedArguments += (dashedArgParam.name -> dashedArg)
					if (dashedArgParam.altName.isDefined) {
						dashedArguments += (dashedArgParam.altName.get -> dashedArg)
					}
				}
			}
			current += 1
		}
		// Add all the rest of the dashed parameters of the command as not defined dahed args
		for ((name, dashedArgParam) <- command.dashedParameters) {
			if (!dashedArguments.contains(name)) {
				val dashedArg = DashedArgument(dashedArgParam.name, dashedArgParam.altName, dashedArgParam.argsDefaults, false)
				dashedArguments += (dashedArgParam.name -> dashedArg)
				if (dashedArgParam.altName.isDefined) {
					dashedArguments += (dashedArgParam.altName.get -> dashedArg)
				}
			}
		}
		(dashedArguments, appliedArgs)
	}


	private def parseMultiArguments(command : MultiArgumentCommand, args: Array[String]) : CalledMultiArgumentCommand = {
		/*
		if (command.hasRequiredParameters && command.minAmountParameters > args.length)
			throw new MinArgumentsNotSatisfied(command.name, command.minAmountParameters, args.length)
		if (command.hasMaxParameters && command.maxAmountParameters < args.length)
			throw new MaxArgumentsNotSatisfied(command.name, command.minAmountParameters, args.length)
		*/
		//args.filterNot(isDashed(_)).map(each => {println(each); UnnamedArgument(each)})
		null
	}

	private def isDashed(arg: String) = {
		arg.startsWith("-")
	}
}