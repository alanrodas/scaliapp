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

class InvalidCommandDefinition(msg : String) extends Exception(msg)

class InvalidCommandCall(val cmd : String, val msg : String
) extends RuntimeException((if (cmd.isEmpty) "the program" else cmd ) + msg)

class MultiArgumentAndArgumentIncompatibility()
	extends InvalidCommandDefinition("a multi-argument command cannot receive named arguments")

/*
class MinArgumentsNotSatisfied(override  cmd : String, required : Int, given: Int)
		extends InvalidCommandCall(cmd, s"$command takes $required arguments but " +
			(if (given == 0) "none was given" else s"only $given " +
					(if (given == 1) "was" else "were") + " given")
		)

class MaxArgumentsNotSatisfied(override cmd : String, required : Int, given: Int)
		extends InvalidCommandCall(s"$command takes $required arguments but $given " +
					(if (given == 1) "was" else "were") + " given")
*/
class ArgumentsExceeded(override val cmd : String, val maxArguments : Int, val sendedArguments : Int)
	extends InvalidCommandCall(cmd, s" takes a maximum of $maxArguments but was called with $sendedArguments")

class RequiredArgumentsNotSatisfied(override val cmd : String, val minArguments : Int, val sendedArguments : Int)
		extends InvalidCommandCall(cmd, s" takes a minimum of $minArguments but only $sendedArguments were given")