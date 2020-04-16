package org.grandeur.utils.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harfiyan on 25/04/2017.
 */
public final class StringHelper {
    public static String PadRight(String s, int n) {
        if (s == null || n == 0)
            return "";

        return String.format("%1$-" + n + "s", s);
    }

    public static String PadLeft(String s, int n) {
        if (s == null || n == 0)
            return "";

        return String.format("%1$" + n + "s", s);
    }

    public static String PadLeft(String s, int n, char padChar) {
        StringBuilder res = new StringBuilder(s);
        for (int i = s.length(); i < n; i++) {
            res.insert(0, padChar);
        }
        return res.toString();
    }

    public static String PadRight(String s, int n, char padChar) {
        StringBuilder res = new StringBuilder();
        res.append(s);
        for (int i = 0; i < (n - s.length()); i++) {
            res.append(padChar);
        }
        return res.toString();
    }

    public static String TrimLeft(String s, char charToTrim) {
        StringBuilder res = new StringBuilder();
        boolean markToRemove = true;
        char[] chars = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            if (chars[i] != charToTrim && markToRemove)
                markToRemove = false;

            if (!markToRemove) {
                res.append(chars[i]);
            }
        }

        return res.toString();
    }

    public static String TrimRight(String s, char charToTrim) {
        StringBuilder res = new StringBuilder();
        boolean markToRemove = true;
        char[] chars = s.toCharArray();
        int markIndex = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] != charToTrim && markToRemove) {
                markToRemove = false;
                markIndex = i;
            }

            if (!markToRemove) {
                res.append(chars[markIndex - i]);
            }
        }

        return res.toString();
    }

    public static String Trim(String s, char charToTrim) {
        return TrimRight(TrimLeft(s, charToTrim), charToTrim);
    }

    public static String Trim(String s) {
        return Trim(s, ' ');
    }

    public static String Limit(String s, int limit) {
        if (s == null) {
            return "";
        }

        return s.substring(0, Math.min(s.length(), limit));
    }

    public static boolean IsNullOrEmpty(String str, boolean trim) {
        if (str == null)
            return true;

        if (str.length() == 0)
            return true;

        if (trim && str.trim().length() == 0)
            return true;

        return false;
    }

    public static Long[] ExtractNumber(String s) {
        List<Long> numberList = new ArrayList<>();

        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if ((s.toCharArray()[i] < '0' || s.toCharArray()[i] > '9')) {
                if (temp.length() > 0) {
                    numberList.add(Long.parseLong(temp.toString()));
                    temp = new StringBuilder();
                }
            } else {
                temp.append(s.toCharArray()[i]);
            }
        }

        if (temp.length() > 0)
            numberList.add(Long.parseLong(temp.toString()));

        return ArrayHelper.ToArray(Long.class, numberList);
    }
}
