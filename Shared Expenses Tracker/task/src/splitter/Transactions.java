package splitter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions {

    public enum BalanceType {
        OPEN, CLOSE
    }

    //immutable class
    public static class Payment {
        private final LocalDate date;
        private final Person from;
        private final Person to;
        private final int amount; // only positive

        public Payment(LocalDate date, Person from, Person to, int amount) {
            this.date = date;
            this.from = from;
            this.to = to;
            this.amount = amount;
        }

        public LocalDate getDate() {
            return date;
        }

        public Person getFrom() {
            return from;
        }

        public Person getTo() {
            return to;
        }

        public int getAmount() {
            return amount;
        }
    }

    List<Payment> payments;

    public Transactions() {
        this.payments = new ArrayList<>();
    }

    public void addTransaction(LocalDate date, Person from, Person to, int amount) {
        payments.add(new Payment(date, from, to, amount));
    }

    public List<Payment> getBalance(LocalDate date) {
        return getBalance(date, BalanceType.CLOSE);
    }

    public List<Payment> getBalance(LocalDate date, BalanceType type) {
        Map<Pair<Person>, Integer> relations = new HashMap<>();
        LocalDate endDate = date;
        if (type == BalanceType.CLOSE) {
            endDate = endDate.plusDays(1);
        }
        for (Payment payment : payments) {
            if (!payment.getDate().isBefore(endDate)) {
                continue;
            }
            int amount = payment.getAmount();
            Pair<Person> pair = new Pair<>(payment.getFrom(), payment.getTo());
            boolean foundPair = false;
            if (!relations.containsKey(pair)) {
                Pair<Person> reversePair = new Pair<>(payment.getTo(), payment.getFrom());
                if (relations.containsKey(reversePair)) {
                    pair = reversePair;
                    amount = - amount;
                    foundPair = true;
                }
            } else {
                foundPair = true;
            }
            if (foundPair) {
                relations.put(pair, relations.get(pair) + amount);
            } else {
                relations.put(pair, amount);
            }

        }
        List<Payment> balance = new ArrayList<>();
        for (Map.Entry<Pair<Person>, Integer> p2p : relations.entrySet()) {
            balance.add(new Payment(null, p2p.getKey().getVal1(), p2p.getKey().getVal2(), p2p.getValue()));
        }
        return balance;
    }

}

