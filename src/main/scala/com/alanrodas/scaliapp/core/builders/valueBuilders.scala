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
package com.alanrodas.scaliapp.core.builders

import com.alanrodas.scaliapp.core.definitions.{ValueDefinition}

/**
 * A builder to construct instances of
 * [[com.alanrodas.scaliapp.core.definitions.ValueDefinition ValueDefinition]].
 *
 * This class is used for constructing instances using the library's DSL.
 */
class ValueDefinitionBuilder {
	var name : String = ""
	var default : Option[String] = None

	/**
	 * Set the name of the instance to build.
	 *
	 * @param name The name to use in the constructed instance
	 * @return this
	 */
	def named(name : String) = {
		this.name = name
		this
	}

	/**
	 * Set the default value of the instance to build and create the instance.
	 *
	 * @param default The default value to use in the constructed instance
	 * @return The constructed instance
	 */
	def as(default : String) = {
		new ValueDefinition(name, Some(default))
	}

	/**
	 * Create an instance without default value.
	 *
	 * @return The constructed instance
	 */
	def mandatory() = {
		new ValueDefinition(name, None)
	}
}