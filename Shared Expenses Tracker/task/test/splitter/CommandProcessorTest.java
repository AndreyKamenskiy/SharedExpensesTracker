package splitter;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class CommandProcessorTest {

    @Test
    public void parserTest1() {
        String commandLine = "2020.09.30 borrow Ann Bob 20";
        CommandProcessor.Parser parser = new CommandProcessor.Parser(commandLine);
        assert parser.hasDate();
        assert parser.getDate().isEqual(LocalDate.parse("2020-09-30"));
        assert parser.getCommand().equals("borrow");
        assert parser.hasAttributes();
        assert parser.getAttributes().equals("Ann Bob 20");
    }

    @Test
    public void parserTest2() {
        String commandLine = "repay Bob Ann 5";
        CommandProcessor.Parser parser = new CommandProcessor.Parser(commandLine);
        assert !parser.hasDate();
        assert parser.getDate() == null;
        assert parser.getCommand().equals("repay");
        assert parser.hasAttributes();
        assert parser.getAttributes().equals("Bob Ann 5");
    }

    @Test
    public void parserTest() {
        String commandLine = "exit";
        CommandProcessor.Parser parser = new CommandProcessor.Parser(commandLine);
        assert !parser.hasDate();
        assert parser.getDate() == null;
        assert parser.getCommand().equals("exit");
        assert !parser.hasAttributes();
        assert parser.getAttributes() == null;
    }

}