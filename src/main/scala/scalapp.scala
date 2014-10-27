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
package com.alanrodas

package object scaliapp {
	//def command() = new CommandBuilder()
	def param() = new DashedArgumentParameterBuilder()
	def value() = new NamedParameterBuilder()
	def flag() = new FlagParameterBuilder()
	/*
	implicit class ListWithMapBy[V](list: List[V]){
		def mapBy[K](keyFunc: V => K) = {
			list.map(a => keyFunc(a) -> a).toMap
		}
	}
	*/
	def printError(msg : String) = println(Console.RED + msg + Console.RESET)
}
