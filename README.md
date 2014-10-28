Scaliapp
========

Scaliapp is a library to create CLI ready applications in Scala easily.


Installing
----------

To add the library to your project you need to download the current version and add
it in the library location of your choice.

[Download the library](http:\\scaliapp.alanrodas.com\donwloads\com.alanrodas.scaliapp-0.1.jar)

Optionally, you may checkout the code from this repository and build the project using *sbt* to
publish it to your local maven repository.

Just run in a terminal.

```
:> git clone git@github.com:alanrodas/scaliapp.git
:> cd scaliapp
:> sbt publishLocal
```

Then, you should be able to use it by any program using *sbt* in your local machine.
Or, you can grab the *.jar* file from

`~/.ivy2/local/com.alanrodas/scaliapp_2.11/0.1/jars/scaliap_2.11.jar`

Note that Scaliapp is built against Scala *2.11*.

If you want to compile it against another Scala version, you will need to change the
*build.sbt* file in the project folder. Change the *scalaVersion* variable to the
desired version, such as:

`scalaVersion := "2.11.2"`

Then compile with *sbt publish*

>Future version will hopefully be hosted in a maven repository so you can add it
>to your projects easily. Please be patience.


Usage
-----

After installing, you just need to do the following to your main object (MainApp for this example):

1. Import *com.alanrodas.scaliapp._*
2. make MainApp extend *CLIApp*
3. define your commands in the body of the MainApp object using the Scaliapp DSL.

You are all set. Now you just need to call you application with the arguments expected
and everything should be working.


DSL
---

Scaliapp provides a simple set of functions and object builder in order to provide a simple
way to define commands, arguments and flags in the body of the main object.
This set of functions and methods end up generating a DSL like language that you can use to
configure your application expected behavior when called from the command line with different
sets of arguments.


#### Defining the short and long parameter signs

A command line application may expect a number of values to be passed to it, but also a set
of arguments and flags (parameters) that may be used to modify the behavior of the application.
Common examples are the **--force** or **-f** modifiers, or the **--verbose** or **-v**.

As it can be seen, this parameters take a special sign before their identifier. This sign is
what identifies a parameter, and distinguish it from a value pass to the command. In unix systems
the sign for short parameters (that is, one letter only) is *"-"* (dash). Windows systems usually
use the **"/"** sign (slash) for this parameters. For long name parameters (more than one character),
a double dash is used in unix systems (**"--"**) while the same slash is used in windows (**"/"**).

Scaliapp allows you to change the long and short signs used by the application by overriding the
**longParamSign** and the **shortParamSign** variables defined in **CLIApp**.

So to change it, just assign your desired sign as the following example:

```scala
object MainApp extends CLIApp {
    longParamSign = "/"
    shortParamSign = "/"
}
```

Scaliapp uses the unix standard as a default, no matter if you are in a Windows or a Unix system.
In the rest of the document you will find references to flags and arguments called in this format,
but be aware that this can be changed if you wish.

>Future versions may default to the OS specific default, but will allow you to change it by
>using the aforementioned code.


#### Defining flags

A flag is a boolean value that is passed to the application in the form of a parameter.
Common examples of flags in a command line app are *verbose* or *force* which also have their short
versions as *v* and *f*. Flags may be created by the **flag** function that is in scope when importing
Scaliapp.

The **flag** function returns a builder object that needs to be configured correctly in order to
properly define a flag as you want it.

The following is a flag definition for verbose that can be called as *--verbose* or as *-v*:
```scala
flag named "verbose" alias "v" as false
```

The following is also valid and defines the same thing

```scala
flag named "v" alias "verbose" as false
```

The name is always needed, but the alias may be omitted. The **as** method build the actual flag
definition to use, and sets it's default value. Most flags default to *false*, but in rare occasions,
*true* may be the desired default value.

Here is a last example of a flag **-f** that defaults to *true*.

```scala
flag named "f" as true
```

Flags are then added to a command definition so that, when called from the command line,
those arguments are checked for existence. If they were defined, the opposite of the
default defined is returned when asking for it's value.


#### Defining arguments

Arguments are pretty much like flags, but they may take additional values which may need or not
to be present.

Consider for example the argument **"-m"** in the *git commit* command. This command takes one
argument, that is the commit message. The commit message cannot be omitted, so it has no default
value. An argument such as that will be defined as:

```scala
arg named "m" taking 1 value
```

>If the argument is expected to take more than one argument, then **values** may be used instead of
>values. Both methods are analogous and the two versions are provided just for readability.
>For example:
>
>```scala
>arg named "m" taking 1 values
>```
>
>Defines exactly the same argument as above.

When an argument takes an argument that can be omitted, you may specify a default value to it
with the **as** method, and providing a list of the default values.
Consider a command that takes a number, and returns the number converted in an ordering. So:
- 1 -> 1st
- 2 -> 2nd
- 3 -> 3rd
- 4 -> 4th
- 5 -> 5th
and so on.

The argument *ordSuffix* determines the values appended to the four first elements if given.
The default values should be english versions, so "st", "nd", "rd" and "th" will be used if this
argument is not passed. Such an argument will be defined as:

```scala
arg named "ordSuffix" taking 4 as List("st", "nd", "rd", "th")
```

The command with such an argument may be later called as:
```
:> command 5
5th
```
or as:
```
:> command 5 --ordSuffix ro do ro to
5to
```

A last possibility for arguments is that they expect a set of mandatory values, and a set of
optional ones later.

You may define this kind of argument by just passing less values than the amount taken by the
arguments to the default.
Let's take the example of "oneMandatoryTwoOptional". This argument takes 3 values, one of which
is mandatory, and the other two are optional. This will be defined as:

```scala
arg named "oneMandatoryTwoOptional" taking 3 as List("second", "third")
```

The *second* and *third* values will be used if the command is called as:
```
:> command --oneMandatoryTwoOptional first
```
But one value should always be passed to the argument, or an error will raise.


#### Defining values

Some commands expect a specific amount of values passed to them. This values may be mandatory,
or optional and have a default value.

An example is the *git push* command, which expects a **"target"** repository and a **"branch"**
as optional values that default to **"origin"** and **"master"** respectively (I know that this
is not exactly true, but please allow me to simplify *"git push"* behavior for the sake of this guide)

So, a value for this cases also have a name and a default value. Such values may be defined with
the **value** function, and configuring the name and the default value. See the following example:

```scala
value named "target" as 'origin'
```

If you expect a mandatory value, then use **"mandatory"** to build the value instead of **"as"**,
as shown in the following example:

```scala
value named "fileName" mandatory
```

When adding values to a command definition you should consider that the expected order is that the
mandatory values are added first. If you don't follow the right order an exception warning about an
incorrect declaration will raise.


#### Defining commands

Command are the main unit of execution in Scaliapp. Whenever you execute an application
built with Scaliapp, a command is executed.

Each command contains it's own set of flags, arguments and values as well as a callback function to
executed when called. There are two kind of commands, a named command, that is, with a specific name
given (Can be multiple one in an application given that the names are not duplicated), and the root
command (Only one in the application).

For creating the root command the **"root"** function is provided by Scaliapp. This command expects
an implicit *CommandManager* which is provided by CLIApp. We will focus on the *root* case first, and
see the *named* command examples later on.

> If you do not want to extend CLIApp in your application, you can still make use of Scaliapp.
> Just instantiate a CommandManager as an implicit value wherever you want to define your commands
> use the **"setSigns"** method to configure the long and short signs (only if you are not planning to
> use the defaults) and then call **"execute"** on it with the values that your application was run with.

So, if you want to call your application as 
```
myApp <fileName> [-f | --force][-v | --verbose]
```
with optional flags *-v* or *--verbose* and *-f* or *--force** and that takes one mandatory
value by the name of *"fileName"*, you should use the following code in order to instantiate
the command:

```scala
root accepts
    (flag named "verbose" alias "v" as false) accepts
    (flag named "force" alias "f" as false) receives
    (value named "fileName" mandatory) does {command =>
        // Your action to perform on command call here
}
```

The **"accepts"** method in the CommandBuilder defines the flags and arguments for the command.
You may call **"accepts"** with just one parameter or with a sequence of parameters. If you are
planning to support a large set of parameters, it will be better to use the sequence form, as
it's cleaner. The same goes for values, that are added with the **"receives"** method, and can
also take a sequence of values instead of just one. That said, the following code defines exactly
the same command as before:

```scala
root accepts Seq(
    flag named "verbose" alias "v" as false,
    flag named "force" alias "f" as false
) receives Seq(
    value named "fileName" mandatory
) does {command =>
    // Your action to perform on command call here
}
```

The **"does"** method takes the function to execute when this command is called and also
constructs the actual command from the builder. We will return to this command in a later
section, for now, just consider that you need to have the **"does"** method at the end of
your command definition.

In some cases, your command may take a unlimited number of values. Consider for example,
the *"rm"* command in unix, which takes any amount of file names, and deletes them.
For this cases, you may want to use the **"multipleValues"** method instead of **"receives"**.
This method tells Scaliapp that this command takes an unlimited number of values. So, an *"rm"*
like command will be defined as:

```scala
root accepts Seq(
    flag named "force" alias "f" as false
) multipleValues does {command =>
    // Your action to perform on command call here
}
```

In this case, we are defining the command to accept an unlimited number of values, but we may
want to declare an expected minimum or maximum amount of values. For example *"rm"* needs at
least one file name to be passed. This can be achieves with **"minimumOf"** and **"maximumOf"**
methods. So for a command that takes one or more values, you may define it as:

```scala
root accepts Seq(
    flag named "force" alias "f" as false
) multipleValues minimumOf 1 does {command =>
    // Your action to perform on command call here
}
```

For the sake of simplicity, you may avoid the **"multipleValues"** method if you have a minimum
or maximum defined, so the above becomes:

```scala
root accepts Seq(
    flag named "force" alias "f" as false
) minimumOf 1 does {command =>
    // Your action to perform on command call here
}
```

Instead of having multiple applications and delegate the passed arguments, you may have one
applications with multiple commands. This is where named commands comes in. Command in the form
of *"git push"* or *"git add"* may be defined as named commands "push" and "add".

>Of course nothing stops you from delegating to a second application if you wish, but you
>are not forced to do so.

A named command may be defined with the **"command"** function, and setting it's name with the
**"named"** method. This commands can then be configured as the previously seen commands. Here
is an example of "push" and "add" commands.

```scala
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
    // Your action to perform on command call here
}
command named "add" accepts Seq(
    flag named "quiet" alias "q" as false,
    flag named "dry-run" alias "n" as false,
    flag named "force" alias "f" as false,
    flag named "intent-to-add" alias "N" as false,
    flag named "all" alias "A" as false
) minimumOf 1 does { commandCall =>
    // Your action to perform on command call here
}
```

This previous sample pretty much sums up what you can define as a command
in Scaliapp.


Performing actions and using the data
-------------------------------------

As we have seen, each command takes a function in the **"does"** method
that is the function to call when that given command is called. This function
takes a *"Command"*, which holds all the values passed to the command by the user,
as well as the default values.

Typically you should want to analyze the values passed to your function.
The following operations can be performed in order to access the data.
* allFlags (Returns all the flags defined for this command)
* allArguments (Returns all arguments defined for this command)
* flag(name : String) (Returns the flag by the name or alias "name")
* argument(name : String) (Returns the argument by the name or alias "name")
* flagPassed(name : String) (Returns true if the flag by the name or alias "name" has been passed by the user)
* argumentPassed(name : String) (Returns true if the argument by the name or alias "name" has been passed by the user)
* flagValue(name : String) (Returns the value of the flag by the name or alias "name")
* argumentValue(name : String) (Returns the value of the sbtargument by the name or alias "name")
* name (Returns the name of the called command)

Additionally, if the command takes a defined set of values that are identified by name you can use
the following set of methods to access the values:
* allValues (Returns all the values for this command)
* value(name : String) (Returns the value by the name or alias "name")
* valuePassed(name : String) (Returns true if the value by the name "name" has been passed by the user)

If the command takes an unlimited number of values, you can get them with the following:
* allValues (Returns all the values for this command)


Exceptions
----------

Scaliapp throws a set of different exceptions in different occasions.

Scaliapp will throw an InvalidCommandDefinition if any defined command
is invalid. This will happen if:
* Any parameter does not have a name.
* There are two commands with the same name.
* There are two parameters with the same name or alias.
* A mandatory value is defined after an optional value.

Scaliapp also throws two other kind of errors. An InvalidCommandCall is
thrown when the set of arguments used to call the application is not a valid one
* A parameter that was not defined was passed
* The minimum number of values was not met
* The maximum number of values was exceeded
* There is no root command and the first argument passed does not match any defined command name

This last case will throw a NotFoundCommand exception, which is a special case of
InvalidCommandCall.

> This kind of exceptions are catched by CLIApp and a special action is performed.
> You can set the function to execute in case the command was not found, or in case
> the command call is invalid.

The third kind of exception thrown is an AbstractInvalidName, which is thrown when
you want to access a value or parameter with a name that does not exists.


Handling Exceptions
-------------------

The CLIApp trait provides two variables, **"onCommandNotFound"** and **"onInvalidCommandCall"**.
This variables are of the type **"NotFoundCommand => Unit"** and **"InvalidCommandCall => Unit"**
respectively. These functions are executed when an error of the former types is raised, so you can
take a specific action in case a command call was invalid. By default, the **"onCommandNotFound"**
and **"onInvalidCommandCall"** just output to the console the error message with a nice red color.
If you want you can modify these functions in order to perform a more specific action.

```scala
object MainApp extends CLIApp {
    onCommandNotFound = {e => printGeneralHelp()}
    onInvalidCommandCall = {e => printHelpFor(e.command)}
}
```

