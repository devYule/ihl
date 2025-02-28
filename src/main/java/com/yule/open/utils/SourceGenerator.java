package com.yule.open.utils;


import java.util.List;

public interface SourceGenerator<T> {

    int generate(T[] tables);
}
