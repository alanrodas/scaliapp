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
package com.alanrodas

import com.alanrodas.scaliapp.core.CommandManager
import com.alanrodas.scaliapp.core.builders.{CommandDefinitionBuilder, FlagDefinitionBuilder, ArgumentDefinitionBuilder, ValueDefinitionBuilder}

/**
 * This package defines most of the DSL of Scaliapp.
 */
package object scaliapp {

	/**
	 * Returns an
	 * [[com.alanrodas.scaliapp.core.builders.ArgumentDefinitionBuilder ArgumentDefinitionBuilder]]
	 * to construct a new argument definition.
	 */
	def arg() = new ArgumentDefinitionBuilder()

	/**
	 * Returns an
	 * [[com.alanrodas.scaliapp.core.builders.ValueDefinitionBuilder ValueDefinitionBuilder]]
	 * to construct a new value definition.
	 */
	def value() = new ValueDefinitionBuilder()

	/**
	 * Returns an
	 * [[com.alanrodas.scaliapp.core.builders.FlagDefinitionBuilder FlagDefinitionBuilder]]
	 * to construct a new flag definition.
	 */
	def flag() = new FlagDefinitionBuilder()

	/**
	 * Returns an
	 * [[com.alanrodas.scaliapp.core.builders.CommandDefinitionBuilder CommandDefinitionBuilder]]
	 * to construct a new command definition.
	 */
	def command(implicit commandManager : CommandManager) = new CommandDefinitionBuilder(commandManager)

	/**
	 * Returns an
	 * [[com.alanrodas.scaliapp.core.builders.CommandDefinitionBuilder CommandDefinitionBuilder]]
	 * for the root command (empty name) to construct a new command definition.
	 */
	def root(implicit commandManager : CommandManager) = new CommandDefinitionBuilder(commandManager) named ""
}
