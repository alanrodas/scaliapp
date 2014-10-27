import com.alanrodas.scaliapp._

object HelloWorld extends CLIApp {

	def display(name : String, value : String) =
			println(Console.BLUE + name + ": " + Console.BLACK + value + Console.RESET)

	val builder = (commandManager add "push" withFlag
			(flag named "f" asFalse) withFlag
			(flag named "t" asFalse) withParam
			(param named "goal" withAlias "g" accepting 3 parameters)receiving
		(value named "target" withNoDefault) receiving
		(value named "branch" withDefault "master")
	)
	builder does {commandCall =>
			println("push <target> <branch> called as:")
			display("target", commandCall.arg("target"))
			display("branch", commandCall.arg("branch"))
			for (flag : FlagArgument <- commandCall.allFlags) {
				display("--" + flag.name + "-" + flag.altName.getOrElse(flag.name), flag.value.toString)
			}
			for (dashedArg : DashedArgument <- commandCall.allDashedArgs) {
				display("--" + dashedArg.name + "-" + dashedArg.altName.getOrElse(dashedArg.name), dashedArg.value.toString)
			}
		}
	/*
	commands add "" withMultipleArguments() does {(args, addArgs) =>
		println("root command called as:")
		1
	}
	*/
}