package csp;

/**
 * Created by Martyna on 26.04.2016.
 */
public class KeyPair {
    int i = 0;
    int j = 0;

    public KeyPair(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyPair)) return false;
        KeyPair key = (KeyPair) o;
        return i == key.i && j == key.j;
    }

    @Override
    public int hashCode() {
        int result = i;
        result = 31 * result + j;
        return result;
    }
}
