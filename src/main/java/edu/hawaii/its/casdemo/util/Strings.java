package edu.hawaii.its.casdemo.util;

import java.util.Arrays;

public final class Strings {

    public static final String EMPTY = "";

    // Private contructor to prevent instantiation.
    private Strings() {
        // Empty.
    }

    public static String fill(final char ch, final int size) {
        char[] fill = new char[size];
        Arrays.fill(fill, ch);

        return new String(fill);
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs == null) {
            return true;
        }
        int len = cs.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return cs != null && cs.length() > 0;
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s != null ? s : "");
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s != null ? s : "");
    }

    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            return value.substring(0, length);
        }
        return value;
    }

    public static String trim(String s) {
        if (s == null) {
            return EMPTY;
        }
        return s.trim();
    }
}
