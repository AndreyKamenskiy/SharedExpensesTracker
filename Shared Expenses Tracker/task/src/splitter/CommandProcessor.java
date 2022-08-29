package splitter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandProcessor {

    public static final String COMMAND_LINE_FORMAT_ERROR =
            "Command line \"%s\" has wrong format. Use \"[date] command [arguments]\"";

    public static class Parser {
        final String datePattern = "\\d{4}\\.\\d{2}\\.\\d{2}";
        final String command = "(?:(" + datePattern + ") +)?([A-Za-z]+)(?: +([A-Za-z\\d ]+))?";
        final int dateGroupNumber = 1;
        final int commandGroupNumber = 2;
        final int attrsGroupNumber = 3;

        final Pattern commandPattern = Pattern.compile(command);

        LocalDate date;
        String commandName;
        String attributes;

        public Parser(String commandLine) throws IllegalArgumentException {
            Matcher matcher = commandPattern.matcher(commandLine);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(
                        String.format(
                                COMMAND_LINE_FORMAT_ERROR,
                                commandLine
                        )
                );
            }

            String dateStr = matcher.group(dateGroupNumber);
            if (dateStr != null) {
                try {
                    date = LocalDate.parse(dateStr.replace('.', '-'));
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException(
                            "Date parser error: " + dateStr + " - illegal date format."
                    );
                }
            } else {
                date = null;
            }
            commandName = matcher.group(commandGroupNumber); // not null
            attributes = matcher.group(attrsGroupNumber); //could be null
        }

        public boolean hasDate() {
            return date != null;
        }

        public boolean hasAttributes() {
            return attributes != null;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getAttributes() {
            return attributes;
        }

        public String getCommand() {
            return commandName;
        }
    }


    private final Map<String, Command> allCommands;

    public CommandProcessor() {
        allCommands = new HashMap<>();
    }

    public void addCommand(Command command) {
        allCommands.put(command.getName(), command);
    }

    public Command[] getCommands() {
        return allCommands.values().toArray(new Command[0]);
    }

    public void processCommandLine(String commandLine) throws IllegalArgumentException {
        Parser parser = new Parser(commandLine);
        if (!allCommands.containsKey(parser.getCommand())) {
            throw new IllegalArgumentException("Unknown command. Print help to show commands list");
        }
        Command command = allCommands.get(parser.getCommand());
        command.run(parser);
    }

}
