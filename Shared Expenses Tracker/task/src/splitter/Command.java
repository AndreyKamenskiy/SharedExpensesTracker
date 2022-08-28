package splitter;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Command {

    private final Pattern pattern;
    private Matcher matcher;

    private final Consumer<Matcher> process;


    private final String name;

    public Command(String name, String pattern, Consumer<Matcher> process) {
        this.pattern = Pattern.compile(pattern);
        this.name = name;
        this.process = process;
    }

    public boolean containsName(String commandLine) {
        return commandLine != null && commandLine.contains(name);
    }

    public boolean isMatchCommand(String commandLine) {
        matcher = pattern.matcher(commandLine);
        return matcher.matches();
    }

    public void run() throws IllegalArgumentException {
        process.accept(matcher);
    }

    public String getName() {
        return name;
    }
}
