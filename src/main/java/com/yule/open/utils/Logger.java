package com.yule.open.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.Collection;

public class Logger {

    private static Messager msg;

    public static void setMessager(Messager m) {
        msg = m;
    }

    private static void print(Diagnostic.Kind k, String m) {
        msg.printMessage(k, m);
    }

    public static void error(String m) {
        error(m, new RuntimeException(m));
    }

    public static void error(String m, Exception e) {
        print(Diagnostic.Kind.ERROR, m);

        throw new RuntimeException(e);
    }

    public static void info(String m) {
        print(Diagnostic.Kind.NOTE, m);
    }

    public static void warn(String m) {
        print(Diagnostic.Kind.WARNING, m);
    }

    public static <T> void collectionBatchInfo(String prefix, Collection<T> collection) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int idx = 1;
        for (T t : collection) {
            sb.append(t);
            if (!(collection.size() == idx)) sb.append(" ");
        }
        sb.append("]");
        info(prefix + sb);
    }
}
