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

abstract class InvalidCommandDefinition(val msg : String) extends Exception(msg)

case class DuplicatedCommandDefinition(commandName : String)
		extends InvalidCommandDefinition(s"The command $commandName was already defined.")
case class DuplicatedParameterDefinition(param : String)
		extends InvalidCommandDefinition(s"The command $param was already defined.")
case class MissingNameDefinition(tipo : String)
		extends InvalidCommandDefinition(s"There is an $tipo with no name defined.")
case class InvalidValueOrder(cmd : String, prev : String, current : String)
		extends InvalidCommandDefinition(s"The value $prev is optional and cannot be before a " +
					s"mandatory argument $current for the command $cmd.")