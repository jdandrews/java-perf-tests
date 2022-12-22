package com.jrandrews.jsc.perf;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * The PythagoreanCup can be filled to a certain point, but once past that point, it empties itself. This implementation
 * uses soft references, so they can be garbage collected if necessary. This allows us to fill memory, but recover it
 * if we really need to.
 * 
 * This is used to consume memory temporarily.
 */
public class PythagoreanCup<T> {
    private int maximumSize;
    private List<SoftReference<T>> contents = new ArrayList<>();

    public PythagoreanCup(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public void add(T object) {
        if (contents.size() < maximumSize)
            contents.add(new SoftReference<T>(object));
        else
            contents.clear();
    }
}
