package org.grandeur.utils.helpers;

import java.util.ArrayList;
import java.util.List;
/**
 *     Grandeur - a tool for logging, create config file based on ini and
 *     utils
 *     Copyright (C) 2020 Harfiyan Shia.
 *
 *     This file is part of grandeur-core.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/.
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
