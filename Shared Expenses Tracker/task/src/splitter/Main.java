package splitter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {

    public static final int ERROR_STATUS = 1;

    private static final String CONSUMER_ERROR = "Consumer error: %s consumer call for %s command.";
    private static CommandProcessor processor;
    private static People people;
    private static Transactions transactions;


    private static boolean keepReading;

    public static void main(String[] args) {
        people = new People();
        transactions = new Transactions();
        processor = new CommandProcessor();
        addCommands();

        try (Scanner scanner = new Scanner(System.in)) {
            keepReading = true;
            while (keepReading) {
                String commandLine = scanner.nextLine();
                try {
                    processor.processCommandLine(commandLine);
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.printf("Something went wrong: %s", ex.getMessage());
            System.exit(ERROR_STATUS);
        }

    }

    private static void addCommands() throws IllegalArgumentException {
        processor.addCommand(createHelpCommand());
        processor.addCommand(createExitCommand());
        processor.addCommand(createBorrowCommand());
        processor.addCommand(createRepayCommand());
        processor.addCommand(createBalanceCommand());
    }

    private static Command createHelpCommand() throws IllegalArgumentException {
        final String commandName = "help";
        Consumer<CommandProcessor.Parser> consumer = parser -> {
            checkConsumer(commandName, parser);
            if (parser.hasDate() || parser.hasAttributes()) {
                throw new IllegalArgumentException(AttributeParser.ILLEGAL_ARGUMENTS);
            }
            List<String> commands = Arrays.stream(processor.getCommands())
                    .map(Command::getName)
                    .sorted(String::compareTo).toList();
            for (String current : commands) {
                System.out.println(current);
            }
        };
        return new Command(commandName, consumer);
    }

    private static Command createExitCommand() throws IllegalArgumentException {
        final String commandName = "exit";
        Consumer<CommandProcessor.Parser> consumer = parser -> {
            checkConsumer(commandName, parser);
            if (parser.hasDate() || parser.hasAttributes()) {
                throw new IllegalArgumentException(AttributeParser.ILLEGAL_ARGUMENTS);
            }
            exit();
        };
        return new Command(commandName, consumer);
    }

    private static Command createBorrowCommand() throws IllegalArgumentException {
        final String commandName = "borrow";
        Consumer<CommandProcessor.Parser> consumer = parser -> {
            checkConsumer(commandName, parser);
            checkAttributes(parser);
            LocalDate date = getDate(parser);
            AttributeParser attrs = new AttributeParser(parser.getAttributes());
            String firstName = attrs.getFrom();
            String secondName = attrs.getTo();
            int amount = attrs.getAmount();
            transactions.addTransaction(
                    date,
                    people.getOrCreatePerson(secondName),
                    people.getOrCreatePerson(firstName),
                    amount
            );
        };
        return new Command(commandName, consumer);
    }

    private static Command createRepayCommand() throws IllegalArgumentException {
        final String commandName = "repay";
        Consumer<CommandProcessor.Parser> consumer = parser -> {
            checkConsumer(commandName, parser);
            checkAttributes(parser);
            LocalDate date = getDate(parser);
            AttributeParser attrs = new AttributeParser(parser.getAttributes());
            String firstName = attrs.getFrom();
            String secondName = attrs.getTo();
            int amount = attrs.getAmount();
            transactions.addTransaction(
                    date,
                    people.getOrCreatePerson(firstName),
                    people.getOrCreatePerson(secondName),
                    amount
            );
        };
        return new Command(commandName, consumer);
    }

    private static Command createBalanceCommand() throws IllegalArgumentException {
        final String commandName = "balance";
        Consumer<CommandProcessor.Parser> consumer = parser -> {
            checkConsumer(commandName, parser);
            LocalDate date = getDate(parser);
            String typeText = parser.getAttributes();
            Transactions.BalanceType type;
            if (typeText == null) {
                type = Transactions.BalanceType.CLOSE;
            } else {
                typeText = typeText.stripLeading();
                if ("open".equals(typeText)) {
                    type = Transactions.BalanceType.OPEN;
                } else if ("close".equals(typeText)) {
                    type = Transactions.BalanceType.CLOSE;
                } else {
                    throw new IllegalArgumentException(AttributeParser.ILLEGAL_ARGUMENTS);
                }
            }
            showBalance(transactions.getBalance(date, type));
        };
        return new Command(commandName, consumer);
    }


    private static LocalDate getDate(CommandProcessor.Parser parser) throws IllegalArgumentException {
        LocalDate date;
        if (parser.hasDate()) {
            date = parser.getDate();
        } else {
            date = LocalDate.now();
        }
        return date;
    }

    private static void checkConsumer(String commandName, CommandProcessor.Parser parser)
            throws IllegalArgumentException {
        if (!commandName.equals(parser.getCommand())) {
            throw new IllegalArgumentException(String.format(CONSUMER_ERROR, commandName, parser.getCommand()));
        }
    }

    private static void checkAttributes(CommandProcessor.Parser parser) {
        if (!parser.hasAttributes()) {
            throw new IllegalArgumentException(AttributeParser.ILLEGAL_ARGUMENTS);
        }
    }


    private static void showBalance(List<Transactions.Payment> balance) {
        boolean noRepayments = true;
        List<String> payments = new ArrayList<>();
        for (Transactions.Payment p2pBalance : balance) {
            int p1GiveP2 = p2pBalance.amount();
            if (p1GiveP2 == 0) {
                continue;
            }
            Person p1 = p2pBalance.from();
            Person p2 = p2pBalance.to();
            if (p1GiveP2 < 0) {
                p1GiveP2 = -p1GiveP2;
                Person temp = p1;
                p1 = p2;
                p2 = temp;
            }
            payments.add(String.format("%s owes %s %d%n", p2.name(), p1.name(), p1GiveP2));
            noRepayments = false;
        }
        if (noRepayments) {
            System.out.println("No repayments");
        }
        payments.stream().sorted(String::compareTo).forEach(System.out::print);
    }

    private static void exit() {
        keepReading = false;
    }

}
