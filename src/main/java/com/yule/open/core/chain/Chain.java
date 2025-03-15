package com.yule.open.core.chain;


public abstract class Chain {

    private Chain next;
    private final int order; // not in actual use

    public Chain(int order) {
        this.order = order;
    }

    public static Chain build(Chain first, Chain... other) {
        Chain prev = first;
        for (Chain chain : other) {
            prev.next = chain;
            prev = chain;
        }
        return first;
    }

    public abstract boolean execute();

    public boolean doNext() {
        if (next == null) return true;
        return next.execute();
    }
}
