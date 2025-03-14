package com.yule.open.utils;

import java.nio.charset.StandardCharsets;

public abstract class StringUtils {

    private static final int caseGap = 'a' - 'A';

    public static String camelFromSnake(String snake, boolean needUpperFirstChar) {
        byte[] bytes = snake.getBytes(StandardCharsets.UTF_8);
        int checkRange = Math.min(bytes.length, 3);
        int cnt = 0;
        for (int i = 0; i < checkRange; i++) {
            if (bytes[i] <= 'Z' && bytes[i] >= 'A') cnt++;
        }
        boolean isAllUpper = checkRange == cnt;
        StringBuilder sb = new StringBuilder();
        boolean underFlag = false;
        boolean firstCharUpperFlag = needUpperFirstChar && bytes[0] <= 'z' && bytes[0] >= 'a';
        if (firstCharUpperFlag) sb.append((char) (bytes[0] - caseGap));
        for (int i = firstCharUpperFlag ? 1 : 0; i < bytes.length; i++) {
            if (bytes[i] == '_') {
                underFlag = true;
                continue;
            }
            String tk = String.valueOf((char) bytes[i]);
            if (!underFlag && isAllUpper && bytes[i] <= 'Z' && bytes[i] >= 'A') {
                tk = String.valueOf((char) (bytes[i] + caseGap));
            }
            if (underFlag) {
                if (bytes[i] <= 'z' && bytes[i] >= 'a') tk = String.valueOf((char) (bytes[i] - caseGap));
                underFlag = false;
            }
            sb.append(tk);
        }
        return sb.toString();
    }

    public static String camelFromSnake(String snake) {
        return camelFromSnake(snake, false);
    }

    public static String snakeFromCamel(String camel) {
        byte[] bytes = camel.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String token = String.valueOf((char) bytes[i]);
            if (bytes[i] >= 'A' && bytes[i] <= 'Z') {
                char c = (char) (bytes[i] - caseGap);
                token = "_" + c;
            }
            sb.append(token);
        }
        return sb.toString();

    }
}