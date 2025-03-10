package com.yule.open.core.chain.impl;

import com.yule.open.core.chain.Chain;
import com.yule.open.database.ConnectionFactory;

public class ConnectionCloser extends Chain {
    public ConnectionCloser(int order) {
        super(order);
    }

    @Override
    public boolean execute() {
        ConnectionFactory.close();
        return true;
    }
}
