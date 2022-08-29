package splitter;

import java.util.HashMap;
import java.util.Map;

public class People {

    private final Map<String, Person> people = new HashMap<>();

    public Person addPerson(String name) {
        Person newPerson = new Person(name);
        people.put(name, newPerson);
        return newPerson;
    }

    public Person getOrCreatePerson(String name) {
        if (people.containsKey(name)) {
            return people.get(name);
        }
        return addPerson(name);
    }

}
