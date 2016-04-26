package csp;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Martyna on 20.04.2016.
 */
public class Cell {
    public int value = 0;
    public int[] domain;
    public int size;
    public int numberOfZeroElementsInDomain;

    public Cell() {

    }

    public Cell(int value, int size) {
        this.value = value;
        this.size = size;
        this.prepareDomain();
        numberOfZeroElementsInDomain = 0;
    }

    public Cell(int value, int[] domain, int size) {
        this.value = value;
        this.domain = domain;
        this.size = size;
        numberOfZeroElementsInDomain = 0;
    }

    public Cell(int value, int[] domain, int size, int numberOfZeroElementsInDomain) {
        this.value = value;
        this.domain = domain;
        this.size = size;
        this.numberOfZeroElementsInDomain = numberOfZeroElementsInDomain;
    }

    public int[] prepareDomain() {
        domain = new int[size];
        numberOfZeroElementsInDomain = 0;
        IntStream.range(1, size + 1).forEach(val -> domain[val-1] = new Integer(val));
        return domain;
    }

    public String toString(){
        return Arrays.toString(domain);
    }


}
