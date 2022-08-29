package splitter;

import java.time.LocalDate;
import java.util.*;

public class Transactions {

    public enum BalanceType {
        OPEN, CLOSE
    }

    /**
     * @param amount only positive
     */
        public record Payment(LocalDate date, Person from, Person to, int amount) {
    }

    List<Payment> payments;

    public Transactions() {
        this.payments = new ArrayList<>();
    }

    public void addTransaction(LocalDate date, Person from, Person to, int amount) {
        payments.add(new Payment(date, from, to, amount));
    }

    public List<Payment> getBalance(LocalDate date, BalanceType type) {
        Map<Pair<Person>, Integer> relations = new HashMap<>();
        LocalDate endDate = date;
        if (type == BalanceType.CLOSE) {
            endDate = endDate.plusDays(1);
        } else {
            //openMonth balance
            endDate = date.withDayOfMonth(1);
        }
        for (Payment payment : payments) {
            if (!payment.date().isBefore(endDate)) {
                continue;
            }
            int amount = payment.amount();
            Pair<Person> pair = new Pair<>(payment.from(), payment.to());
            boolean foundPair = false;
            if (!relations.containsKey(pair)) {
                Pair<Person> reversePair = new Pair<>(payment.to(), payment.from());
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
        // not ordered balance;
        return balance;
    }



}

