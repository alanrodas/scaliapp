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
package com.alanrodas.scaliapp.core.definitions

/**
 * This class represents a
 * [[com.alanrodas.scaliapp.core.runtime.NamedValue NamedValue]]
 * definition.
 *
 * @param name The name of the value definition
 * @param default The default value that this definition takes
 */
case class ValueDefinition(
	name : String,
	default : Option[String]
) {
	/** ''true'' if the value is required for any call, ''false'' otherwise */
	def required = default.isEmpty
}
// TODO Default should be an Option. If default is given, then it's not required.