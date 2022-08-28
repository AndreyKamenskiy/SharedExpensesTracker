package splitter;

public class Pair<T> {
    T val1;
    T val2;

    public Pair(T val1, T val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?> pair = (Pair<?>) o;

        if (!val1.equals(pair.val1)) return false;
        return val2.equals(pair.val2);
    }

    @Override
    public int hashCode() {
        int result = val1.hashCode();
        result = 31 * result + val2.hashCode();
        return result;
    }

    public T getVal1() {
        return val1;
    }

    public T getVal2() {
        return val2;
    }
}
