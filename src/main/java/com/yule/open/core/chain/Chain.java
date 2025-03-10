package com.yule.open.core.chain;

import com.yule.open.core.context.ProcessContext;

public interface Chain {
    void execute(ProcessContext context);

    void next(ProcessContext context);
}
