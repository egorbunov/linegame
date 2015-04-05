package org.spbstu.svg;

import android.graphics.Path;

import java.util.Iterator;

/**
 * Created by Egor Gorbunov on 05.04.2015.
 * Email: egor-mailbox@ya.ru
 * Github username: egorbunov
 */
public class PathParser {
    private final static char MOVE_TO = 'm';
    private final static char BEZIER = 'c';
    private final static char END_CURVE = 'z';
    private final static char LINE_TO = 'l';
    private final static char H_LINE_TO = 'h';
    private final static char V_LINE_TO = 'v';
    private final static char SMOOTH_CURVE = 's';

    private static boolean isAlphabetic(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * Parse given via text string svg-format path
     * @param pathStr string, which contains a path in svg-format (without tag '<path>')
     * @return Android Path, which contains the path given by string...
     */
    public static Path parsePath(String pathStr) {
        String[] tokens = pathStr.split("[,\\s]");

        float lastX = 0f;
        float lastY = 0f;
        Path path = new Path();
        int i = 0;
        while (i < tokens.length) {
            if (tokens[i].charAt(0) == END_CURVE)
                break;

            boolean isRel = true;

            if (Character.isUpperCase(tokens[i].charAt(0))) {
                // absolute
                isRel = false;
                lastX = 0f;
                lastY = 0f;
            }

            char command = tokens[i++].charAt(0);

            float x = lastX, y = lastY;
            while (!isAlphabetic(tokens[i].charAt(0))) {
                switch (Character.toLowerCase(command)) {
                    case MOVE_TO:
                        x = Float.valueOf(tokens[i++]) + lastX;
                        y = Float.valueOf(tokens[i++]) + lastY;
                        path.moveTo(x, y);
                        break;
                    case LINE_TO:
                        x = Float.valueOf(tokens[i++]) + lastX;
                        y = Float.valueOf(tokens[i++]) + lastY;
                        path.lineTo(x, y);
                        break;
                    case H_LINE_TO:
                        x = Float.valueOf(tokens[i++]) + lastX;
                        path.lineTo(x, lastY);
                        lastX = x;
                        break;
                    case V_LINE_TO:
                        y = Float.valueOf(tokens[i++]) + lastY;
                        path.lineTo(lastX, y);
                        break;
                    case BEZIER:
                        float x1 = Float.valueOf(tokens[i++]) + lastX;
                        float y1 = Float.valueOf(tokens[i++]) + lastY;
                        float x2 = Float.valueOf(tokens[i++]) + lastX;
                        float y2 = Float.valueOf(tokens[i++]) + lastY;
                        x = Float.valueOf(tokens[i++]) + lastX;
                        y = Float.valueOf(tokens[i++]) + lastY;
                        path.cubicTo(x1, y1, x2, y2, x, y);
                        break;
                    case SMOOTH_CURVE:
                        break;
                }
                if (isRel) {
                    lastX = x;
                    lastY = y;
                }
            }
            lastX = x;
            lastY = y;
        }

        return path;
    }
}
