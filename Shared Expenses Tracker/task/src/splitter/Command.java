package splitter;

import java.util.function.Consumer;

public class Command {

    private final Consumer<CommandProcessor.Parser> process;

    private final String name;

    public Command(String name, Consumer<CommandProcessor.Parser> process) {
        this.name = name;
        this.process = process;
    }

    public void run(CommandProcessor.Parser parser) throws IllegalArgumentException {
        process.accept(parser);
    }

    public String getName() {
        return name;
    }


}
