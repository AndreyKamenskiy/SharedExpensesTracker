package splitter;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;

public class Main {

    public static final int ERROR_STATUS = 1;
    private static CommandProcessor processor;
    private static People people;
    private static Transactions transactions;


    private static boolean keepReading;

    public static void main(String[] args) {
        people = new People();
        transactions = new Transactions();
        processor = new CommandProcessor();
        addCommands();

        String[] lines = {
                "repay Ann",
                "exit",
                "2020.09.30 borrow Ann Bob 20",
                "2020.10.01 repay Ann Bob 10",
                "2020.10.10 borrow Bob Ann 7",
                "2020.10.15 repay Ann Bob 8",
                "repay Bob Ann 5",
                "2020.09.25 balance",
                "2020.09.30 balance open",
                "2020.09.30 balance close",
                "2020.10.20 balance close",
                "balance close"
        };
        int index = 0;

        try (Scanner scanner = new Scanner(System.in)) {
            keepReading = true;
            while (keepReading) {
                //String commandLine = scanner.nextLine();

                if (index == lines.length) break;
                String commandLine = lines[index++];
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

    private static void addCommands() {
        Consumer<Matcher> showHelp = matcher -> {
            for (Command current : processor.getCommands() ) {
                System.out.println(current.getName());
            }
        };
        Command helpCommand = new Command("help","help",showHelp);
        processor.addCommand(helpCommand);

        Consumer<Matcher> exitConsumer = matcher -> exit();
        Command exitCommand = new Command("exit","exit", exitConsumer);
        processor.addCommand(exitCommand);

        //todo: rewrite with commandparser. no pattern for commands, only name.
        //parse command line by template [date] command [arguments]. Therefore parse arguments by template.

        final String datePattern = "\\d{4}\\.\\d{2}\\.\\d{2}";
        final String namePattern = "[A-Za-z]{2,}";
        final String amountPattern = "\\d{1,9}";
        final String args = String.format("(%s)\\s+(%s)\\s+(%s)", namePattern, namePattern, amountPattern);
        final String command = "(%s\\s+)?(%s)\\s+%s";
        final String repayCommandPattern = String.format(command, datePattern, "repay", args);
        final String borrowCommandPattern = String.format(command, datePattern, "borrow", args);
        final Function<Matcher, Boolean> hasDate = matcher -> matcher.group(1) != null;
        final Function<Matcher, LocalDate> getDate = matcher -> {
            LocalDate date;
            if (hasDate.apply(matcher)) {
                date = parseDate(matcher.group(1));
            } else {
                date = LocalDate.now();
            }
            return date;
        };
        final Function<Matcher, String> getFirstName = matcher -> matcher.group(3);
        final Function<Matcher, String> getSecondName = matcher -> matcher.group(4);
        final Function<Matcher, Integer> getAmount = matcher -> Integer.parseInt(matcher.group(5));

        Consumer<Matcher> repayConsumer = matcher -> {
            LocalDate date = getDate.apply(matcher);
            String firstName = getFirstName.apply(matcher);
            String secondName = getSecondName.apply(matcher);
            int amount = getAmount.apply(matcher);
            transactions.addTransaction(
                    date,
                    people.getOrCreatePerson(firstName),
                    people.getOrCreatePerson(secondName),
                    amount
            );
        };

        Consumer<Matcher> borrowConsumer = matcher -> {
            LocalDate date = getDate.apply(matcher);
            String firstName = getFirstName.apply(matcher);
            String secondName = getSecondName.apply(matcher);
            int amount = getAmount.apply(matcher);
            transactions.addTransaction(
                    date,
                    people.getOrCreatePerson(secondName),
                    people.getOrCreatePerson(firstName),
                    amount
            );
        };

        Command repayCommand = new Command("repay", repayCommandPattern, repayConsumer);
        Command borrowCommand = new Command("borrow", borrowCommandPattern, borrowConsumer);
        processor.addCommand(repayCommand);
        processor.addCommand(borrowCommand);

        String balancePattern = String.format("(%s\\s+)?(%s)(\\s+(%s))?", datePattern, "balance","open|close");

        Consumer<Matcher> balanceConsumer = matcher -> {
            LocalDate date = getDate.apply(matcher);
            String typeText = matcher.group(4);
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
                    throw new IllegalArgumentException("unknown balance type - " + typeText);
                }
            }
            showBalance(transactions.getBalance(date, type));
        };

        Command balanceCommand = new Command("balance", balancePattern, balanceConsumer);
        processor.addCommand(balanceCommand);
    }

    private static void showBalance(List<Transactions.Payment> balance) {
        boolean noRepayments = true;
        for (Transactions.Payment p2pBalance : balance) {
            int p1GiveP2 = p2pBalance.getAmount();
            if (p1GiveP2 == 0) {
                continue;
            }
            Person p1 = p2pBalance.getFrom();
            Person p2 = p2pBalance.getTo();
            if (p1GiveP2 < 0) {
                p1GiveP2 = -p1GiveP2;
                Person temp = p1;
                p1 = p2;
                p2 = temp;
            }
            System.out.printf("%s owes %s %d%n", p2.getName(), p1.getName(), p1GiveP2);
            noRepayments = false;
        }
        if (noRepayments) {
            System.out.println("No repayments");
        }
    }


    public static LocalDate parseDate(String yyyy_mm_dd) {
        return LocalDate.parse(yyyy_mm_dd.stripTrailing().replace('.', '-'));
    }

    private static void exit() {
        keepReading = false;
    }

}
