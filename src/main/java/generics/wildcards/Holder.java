package generics.wildcards;

import java.util.Objects;

public class Holder<T> {
    private T value;
    public Holder() {}
    public Holder(T val) { value = val; }
    public void set(T val) { value = val; }
    public T get() { return value; }
    @Override public boolean equals(Object o) {
        return o instanceof Holder &&
                Objects.equals(value, ((Holder)o).value);
    }
    @Override public int hashCode() {
        return Objects.hashCode(value);
    }
}
