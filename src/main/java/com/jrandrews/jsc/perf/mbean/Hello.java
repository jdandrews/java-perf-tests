package com.jrandrews.jsc.perf.mbean;

public class Hello implements HelloMBean {
    private static final int DEFAULT_CACHE_SIZE = 200;

    private final String name = "Reginald";
    private int cacheSize = DEFAULT_CACHE_SIZE;

    @Override
    public void sayHello() {
        System.out.println("hello, world");
    }

    @Override
    public int add(int x, int y) {
        return x + y;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public synchronized void setCacheSize(int size) {
        this.cacheSize = size;
        System.out.println("Cache size now " + this.cacheSize);
    }

}
