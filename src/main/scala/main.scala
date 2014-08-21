import com.alanrodas.scaliapp._

object HelloWorld extends CLIApp {
	commands add "push" that "Just print the status of the command generated" receiving
		(value named "target" withNoDefault) receiving
		(value named "branch" withDefault "master") does {(args, addArgs) => 1}
	commands add "" withMultipleArguments() does {(args, addArgs) =>
		println(args.length)
		1
	}
}