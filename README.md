# CommandAPI
CommandAPI is a small library that allows you to create commands in Spigot like in BungeeCord.
No need to edit the plugin.yml all the time anymore!

## Usage
Using CommandAPI is super easy! All you need to do is:
* Create a class extending "SpigotCommand"
* Once created, register it using:
```java
CommandManager.getInstance().registerCommand(MyCustomCommand.class);

// or

CommandManager.getInstance().registerCommand(new MyCustomCommand())
```

Unregistering a command can be done as following:
```java
final SpigotCommand myCustomCommand = new MyCustomCommand();
// First register command ofcourse
CommandManager.getInstance().registerCommand(myCustomCommand)

// When you would (for some reason) have to unregister the command:
CommandManager.getInstance().unregisterCommand(myCustomCommand);
```