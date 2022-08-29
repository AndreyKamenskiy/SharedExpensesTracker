package splitter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttributeParser {
    // parser for attributes format name1 name2 amount

    public static final String ILLEGAL_ARGUMENTS = "Illegal command arguments";

    final String namePattern = "[A-Za-z]{2,}";
    final String amountPattern = "\\d{1,9}";
    final String attr = String.format("(%s)\\s+(%s)\\s+(%s)", namePattern, namePattern, amountPattern);
    final Pattern attrPattern = Pattern.compile(attr);

    final int nameFromGroupNumber = 1;
    final int nameToGroupNumber = 2;
    final int amountGroupNumber = 3;

    private final String from;
    private final String to;
    private final int amount;

    public AttributeParser(String attrLine) throws IllegalArgumentException {
        Matcher matcher = attrPattern.matcher(attrLine);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENTS);
        }
        from = matcher.group(nameFromGroupNumber);
        to = matcher.group(nameToGroupNumber);
        try {
            amount = Integer.parseInt(matcher.group(amountGroupNumber));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal amount format: " + matcher.group(amountGroupNumber));
        }
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }

}
