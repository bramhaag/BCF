# bramhaag's command framework (BCF)
#### A command framework for JDA inspired by [Aikar's Annotation Command Framework](https://github.com/aikar/commands)

## Before you consider using this
This project is under active development, everything is a subject to change and not all features are implemented yet.
In it's current state the project is pretty stable, the only important thing that it lacks now is error handling.

## Getting started
Setting up BCF is easy, just add the following lines of code to your project and you're set
```java
new BCF(jda).setPrefix("my-prefix!")
    .register(new MyCommand())
    .register(new OtherCommand());
```

For more advanced users you can add custom CommandContexts (see "Command Contexts" for more info) by doing `.addContext(Class type, ContextResolver resolver)`

## Example
```java
//Pipes ( | ) are used for aliases. The name of this command is "mycommand" and has 1 alias, "mycmd"
@Command("mycommand|mycmd")
public class MyCommand extends BaseCommand {
    
    //The CommandBase annotation is used for when a command is executed without arguments
    @CommandBase
    //Methods can have any name you want
    public void executeBaseCommand() {
        getChannel().sendMessage("Hey! You've executed 'mycommand' or 'mycmd' without any arguments!").queue();
    }
    
    //The Subcommand annotation is used for subcommands, pipes are used for aliases.
    @Subcommand("mysubcommand")
    public void executeMySubcommand() {
        getChannel().sendMessage("Now you've executed 'mycommand mysubcommand' or 'mycmd mysubcommand'!").queue();
    }
    
    @Subcommand("multiply")
    //Subcommands can have any type of parameters, BCF converts the most common ones automatically, see Command Contexts below for more info
    public void multiply(int first, int second) {
        getChannel().sendMessage("Result: " + (first * second)).queue();
    }
    
    //The optional annotation is used for optional parameters, these should always be last
    @Subcommand("multiplyOptional")
    public void multiplyOptional(int first, int second, @Optional Integer third) {
        if(third == null) {
            getChannel().sendMessage("Result: " + (first * second)).queue();
        } else {
            getChannel().sendMessage("Result: " + (first * second * third)).queue(); 
        }
    }
    
    //Default parameters are like optionals, but with a default value
    @Subcommand("subcommandDefaultValue")
    public void sendAMessage(String myMessage, @Default("my-prefix") String prefix) {
        getChannel().sendMessage(prefix + " " + myMessage).queue();
    }
    
    //String arrays can also be used to collect other all arguments
    @Subcommand("getAllArgs")
    public void getAllArgs(String usefulString, int usefulInt, String[] otherArgs) {
        getChannel().sendMessage("Useful: " + usefulString + ", " + usefulInt).queue();
        getChannel().sendMessage("Other: " + Arrays.toString(otherArgs)).queue();
    }
}
```

## Command Contexts
CommandContexts convert a String to a specific type. The following types are supported out of the box:

#### Primitives
|Primitive|Supported|Class    |Supported|
|---    |---        |---      |---      |
|byte   |✔          |Byte     |✔       |
|short  |✔          |Short    |✔       |
|int    |✔          |Integer  |✔       |
|long   |✔          |Long     |✔       |
|float  |✔          |Float    |✔       |
|double |✔          |Double   |✔       |
|char   |✔          |Character|✔       |
|boolean|✔          |Boolean  |✔       |

#### Java classes
|Class           |Supported|
|---             |---      |
|String          |✔       |
|Object          |✔       |
|Date            |❌       |
|URL             |❌       |
|Pattern         |❌       |
|SimpleDateFormat|❌       |
|Enum            |❌       |
|String[]        |✔       |
|<Type>[]        |❌       |
|List<Type>      |❌       |

#### JDA classes
|Class      |Supported|
|---        |---      |
|User       |✔       |
|Member     |❌       |
|Message    |❌       |
|TextChannel|✔       |
|Guild      |✔       |

## Road Map
Over the course of the next few updates I'm planning on adding and/or improving:
- [ ] Error handling
- [ ] Permission annotation
- [ ] Syntax annotation
- [ ] More CommandContexts
