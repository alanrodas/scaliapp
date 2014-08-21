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

class InvalidCommandExecution(msg : String) extends Exception(msg)

class MultiArgumentAndArgumentIncompatibility()
	extends InvalidCommandDefinition("a multi-argument command cannot receive named arguments")

class MinArgumentsNotSatisfied(command : String, required : Int, given: Int)
		extends InvalidCommandExecution(
			(if (command.isEmpty) "the program" else command ) +
			s" takes $required arguments but " +
			(if (given == 0) "none was given" else s"only $given " +
					(if (given == 1) "was" else "were") + " given")
		)

class MaxArgumentsNotSatisfied(command : String, required : Int, given: Int)
		extends InvalidCommandExecution(
			(if (command.isEmpty) "the program" else command ) +
			s" takes $required arguments but $given " +
					(if (given == 1) "was" else "were") + " given")
