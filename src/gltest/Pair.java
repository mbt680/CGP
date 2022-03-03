package gltest;

/**
 * Pair represents a key, value pair
 */
public class Pair<T,V> {
    private T key;
    private V value;

    /**
     * Constructor
     * @param key
     * @param value
     */
    public Pair(T key, V value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }
}
