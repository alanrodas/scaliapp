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

abstract class Command(
	val name : String,
	val description : String,
	val dashedArguments : Map[String, Parameter],
) {
	def isRoot = name.isEmpty
	def hasRequiredArguments : Boolean
	def hasDashedArgNamed(name : String) = dashedArguments.contains(name)
	def dashedArgument(name : String) = dashedArguments(name)
}

class ArgumentCommand(
	override val name : String,
	override val description : String,
	val arguments : List[SimpleParameter],
	override val dashedArguments : Map[String, Parameter],
	val callback : (List[UnnamedArgument]) => Int
) extends Command(name, description, dashedArguments) {
	def hasArguments = !arguments.isEmpty
	def hasRequiredArguments = arguments.find(sa => sa.required == true).isDefined
}

class MultiArgumentCommand(
	override val name : String,
	override val description : String,
	val minAmountArguments : Int,
	val maxAmountArguments : Int,
	override val dashedArguments : Map[String, Parameter],
	val callback : (Map[String, NamedArgument]) => Int
 ) extends Command(name, description, dashedArguments)  {
	def hasRequiredArguments = minAmountArguments > 0
	def hasMaxArguments = maxAmountArguments > 0
}

class CommandBuilder(val name : String, val commandManager : CommandManager) {
	var dashedArguments = Map[String, Parameter]()
	var arguments : List[SimpleParameter] = Nil
	var description = ""

	def that(description : String) = {
		this.description = description.trim()
		this
	}

	def withMultipleArguments() = {
		this.isMultiArg = true
		new MultiArgumentCommandBuilder(this)
	}

	def requiring(min : Int) = {
		if (!isMultiArg) throw
				new InvalidCommandDefinition("Only multi-argument command can require a minimum amount of arguments")
		this.minMultiArgs = min
		this
	}

	def upTo(max : Int) = {
		if (!isMultiArg) throw
				new InvalidCommandDefinition("Only multi-argument command can require a maximum amount of arguments")
		this.maxMultiArgs = max
		this
	}

	def receiving(arg : SimpleParameter) = {
		if (isMultiArg) throw new MultiArgumentAndArgumentIncompatibility()
		this.arguments :+ arg
		this
	}

	def withArg(arg : Parameter) = {
		this.dashedArguments += (arg.name -> arg)
		if (arg.shortname.isDefined) this.dashedArguments += (arg.shortname.get -> arg)
		this
	}

	def does(callback : (List[UnnamedArgument]) => Int) = {
		commandManager.addCommand(new ArgumentCommand(name, description, arguments, dashedArguments, callback)
	}

	def does(callback : (List[SimpleParameter], List[Parameter]) => Int) = {
		commandManager.addCommand(
			if (isMultiArg) new MultiArgumentCommand(name, description, minMultiArgs, maxMultiArgs, dashedArguments, callback)
			else new ArgumentCommand(name, description, arguments, dashedArguments, callback)
		)
	}
}

class MultiArgumentCommandBuilder(override val name : String, override val commandManager : CommandManager)
		extends CommandBuilder(name, commandManager) {
	var minMultiArgs = -1
	var maxMultiArgs = -1

	def requiring(min : Int) = {
		if (!isMultiArg) throw
				new InvalidCommandDefinition("Only multi-argument command can require a minimum amount of arguments")
		this.minMultiArgs = min
		this
	}

	def upTo(max : Int) = {
		if (!isMultiArg) throw
				new InvalidCommandDefinition("Only multi-argument command can require a maximum amount of arguments")
		this.maxMultiArgs = max
		this
	}


	def does(callback : (List[UnnamedArgument]) => Int) = {
		commandManager.addCommand(new ArgumentCommand(name, description, arguments, dashedArguments, callback)
	}

	def does(callback : (List[SimpleParameter], List[Parameter]) => Int) = {
		commandManager.addCommand(
			if (isMultiArg) new MultiArgumentCommand(name, description, minMultiArgs, maxMultiArgs, dashedArguments, callback)
			else new ArgumentCommand(name, description, arguments, dashedArguments, callback)
		)
	}
}

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

	def execute(args : Array[String]) : Seq[ValuedArgument[_]] = {
		if (shouldCallRoot(args)) parseArguments(root.get, args)
		else {parseArguments(commands(args(0)), args.tail)}
	}

	private def shouldCallRoot(args : Array[String]) : Boolean = {
		root.isDefined && (
			args.isEmpty ||
			(!commands.contains(args(0)) && !root.get.hasRequiredArguments)
		)
	}

	private def parseArguments(command: Command, args: Array[String]) : Seq[ValuedArgument[_]] = {
		command match {
			case cmd : ArgumentCommand => parseSimpleArguments(cmd, args)
			case cmd : MultiArgumentCommand => parseMultiArguments(cmd, args)
		}
	}

	private def parseSimpleArguments(command : ArgumentCommand, args: Array[String]) : Seq[NamedArgument[_]] = {
		val requiredArgs = command.arguments.filter(_.required)
		if (requiredArgs.length > args.length)
			throw  new MinArgumentsNotSatisfied(command.name, requiredArgs.length, args.length)
		if (command.arguments.length < args.length)
			throw new MaxArgumentsNotSatisfied(command.name, command.arguments.length, args.length)

		var result = Vector[NamedArgument[_]]()
		var argNum = 0
		var i = 0
		while (i < args.length) {
			val arg = args(i)
			if (!isDashed(arg)) {
				result :+ NamedArgument(command.arguments(argNum).name, arg)
			} else {
				if (command.hasDashedArgNamed(arg)) {
					// i += command.dashedArgument(arg).numArgs
				}
			}
			i += 1
		}
		result
	}

	private def parseMultiArguments(command : MultiArgumentCommand, args: Array[String]) : Seq[UnnamedArgument[_]] = {
		if (command.hasRequiredArguments && command.minAmountArguments > args.length)
			throw new MinArgumentsNotSatisfied(command.name, command.minAmountArguments, args.length)
		if (command.hasMaxArguments && command.maxAmountArguments < args.length)
			throw new MaxArgumentsNotSatisfied(command.name, command.minAmountArguments, args.length)

		args.filterNot(isDashed(_)).map(each => {println(each); UnnamedArgument(each)})
	}

	private def isDashed(arg: String) = {
		arg.startsWith("-")
	}
}