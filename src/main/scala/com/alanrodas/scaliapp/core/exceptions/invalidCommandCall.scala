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
package com.alanrodas.scaliapp.core.exceptions

abstract class InvalidCommandCall(val command : String, val msg : String) extends RuntimeException(msg)
protected object ExceptionOpt {
	def commandName(cmd : String) = if (cmd.isEmpty) "the program" else cmd
	def givenArgsAmount(amount : Int) = {
		if (amount == 0) "none was"
		else if (amount == 1) "only one was"
		else s"only $amount were"
	}
}
case class ArgumentsExceeded(override val command : String, maxArguments : Int, sendedArguments : Int)
	extends InvalidCommandCall(command, ExceptionOpt.commandName(command) +
			s" takes a maximum of $maxArguments arguments but was called with $sendedArguments arguments")

case class RequiredArgumentsNotSatisfied(override val command : String, minArguments : Int, sendedArguments : Int)
	extends InvalidCommandCall(command, ExceptionOpt.commandName(command) +
			s" takes a minimum of $minArguments arguments but "+ ExceptionOpt.givenArgsAmount(sendedArguments) +" given")

case class NotFoundCommand(cmd : Option[String])
	extends InvalidCommandCall(cmd.fold(""){s=>s},
		cmd.fold[String]("Cannot call the program without a given command") {command =>
			" There is no command \"" + command + "\" defined"
		})
case class InvalidArgumentPassed(override val command : String, name : String)
		extends InvalidCommandCall(command, s"The argument $name does not exist for the command $command")
case class RequiredValueArgumentMissing(override val command : String, val arg : String, val numValueArguments : Int, val sendedArguments : Int)
		extends InvalidCommandCall(command, s"$arg takes $numValueArguments, but " + ExceptionOpt.givenArgsAmount(sendedArguments) + " given.")

