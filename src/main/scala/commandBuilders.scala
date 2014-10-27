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

class CommandBuilder(val name : String, val commandManager : CommandManager) {

	var description = ""
	var dashedParameters = Map[String, DashedArgumentParameter]()
	var flags = Map[String, FlagParameter]()
	
	def that(description : String) :this.type = {
		this.description = description.trim()
		this
	}

	def withMultipleArguments() : MultiArgumentCommandBuilder = {
		MultiArgumentCommandBuilder(this)
	}

	def receiving(param : NamedParameter) : ArgumentCommandBuilder = {
		ArgumentCommandBuilder(this).receiving(param)
	}

	def withParam(param : DashedArgumentParameter) : this.type = {
		this.dashedParameters += (param.name -> param)
		if (param.altName.isDefined) this.dashedParameters += (param.altName.get -> param)
		this
	}

	def withFlag(flag : FlagParameter) : this.type = {
		this.flags += (flag.name -> flag)
		if (flag.altName.isDefined) this.flags += (flag.altName.get -> flag)
		println("THE FLAGS ARE " + this.flags)
		this
	}
}

object ArgumentCommandBuilder {
	def apply(builder : CommandBuilder) = {
		val acb = new ArgumentCommandBuilder(builder.name, builder.commandManager)
		acb.description = builder.description
		acb.dashedParameters = builder.dashedParameters
		acb.flags = builder.flags
		acb
	}
}
class ArgumentCommandBuilder(override val name : String, override val commandManager : CommandManager)
		extends CommandBuilder(name, commandManager) {
	var parameters : List[NamedParameter] = Nil

	override def receiving(param : NamedParameter) : this.type = {
		this.parameters = this.parameters :+ param
		this
	}

	def does(callback : CalledArgumentCommand => Unit) = {
		commandManager.addCommand(
			new ArgumentCommand(name, description, parameters, dashedParameters, flags, callback)
		)
	}
}

object MultiArgumentCommandBuilder {
	def apply(builder : CommandBuilder) = {
		new MultiArgumentCommandBuilder(builder.name, builder.commandManager)
	}
}
class MultiArgumentCommandBuilder(
		override val name : String,
		override val commandManager : CommandManager
) extends CommandBuilder(name, commandManager) {
	var minMultiArgs = -1
	var maxMultiArgs = -1

	def does(callback : CalledMultiArgumentCommand => Unit) = {
		commandManager.addCommand(
			new MultiArgumentCommand(name, description, minMultiArgs, maxMultiArgs, dashedParameters, flags, callback)
		)
	}
}

