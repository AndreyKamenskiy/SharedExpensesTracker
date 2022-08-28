package splitter;

import java.util.ArrayList;
import java.util.List;

public class CommandProcessor {

    private List<Command> allCommands;

    public CommandProcessor() {
        allCommands = new ArrayList<>();
    }

    public void addCommand(Command command) {
        allCommands.add(command);
    }

    public Command[] getCommands() {
        return allCommands.toArray(new Command[allCommands.size()]);
    }

    public void processCommandLine(String commandLine) throws IllegalArgumentException {
        boolean unknownCommand = true;
        for(Command current : allCommands) {
            if (current.isMatchCommand(commandLine)) {
                try {
                    current.run();
                } catch (IllegalArgumentException ex) {
                    System.out.printf("Command %s executes with error:%s%n", current.getName(), ex.getMessage());
                }
                unknownCommand = false;
                break;
            }
        }
        if (unknownCommand) {
            throw new IllegalArgumentException("Unknown command. Print help to show commands list");
        }
    }

}
