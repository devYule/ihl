package com.yule.open.utils;

import java.util.List;

public interface SourceGenerator<T, R> {

//    int generate(T[] tables);
    List<R> generate(T info);
}
