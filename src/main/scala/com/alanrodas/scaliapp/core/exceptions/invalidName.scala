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

abstract class AbstractInvalidName(val msg : String) extends RuntimeException(msg)

case class InvalidFlagName(val name : String, val command : String)
		extends AbstractInvalidName(s"The flag $name does not exist for the command $command")
case class InvalidArgumentName(val name : String, val command : String)
		extends AbstractInvalidName(s"The argument $name does not exist for the command $command")
case class InvalidValueName(val name : String, val command : String)
		extends AbstractInvalidName(s"The value $name does not exist for the command $command")
