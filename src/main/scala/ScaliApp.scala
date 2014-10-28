import com.alanrodas.scaliapp._

object MainApp extends CLIApp {

	command named "push" accepts Seq(
		flag named "verbose" alias "v" as false,
		flag named "quiet" alias "q" as false,
		flag named "dry-run" alias "n" as false,
		flag named "force" alias "f" as false,
		arg named "repo" taking 1 value,
		arg named "recurse-submodules" taking 1 as List("check"),
		arg named "exec" taking 1 value
	) receives Seq(
		value named "target" as "origin",
		value named "branch" as "master"
	) does { commandCall =>
		commandCall.dump()
	}
	command named "add" accepts Seq(
		flag named "quiet" alias "q" as false,
		flag named "dry-run" alias "n" as false,
		flag named "force" alias "f" as false,
		flag named "intent-to-add" alias "N" as false,
		flag named "all" alias "A" as false
	) minimumOf 1 does { commandCall =>
		commandCall.dump()
	}
}