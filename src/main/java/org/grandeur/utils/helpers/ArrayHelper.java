package org.grandeur.utils.helpers;

import java.lang.reflect.Array;
import java.util.Collection;
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
public final class ArrayHelper {

    public static String[] StringListToArray(List<String> list) {
        String[] res = new String[list.size()];
        list.toArray(res);
        return res;
    }

    public static <T> T[] ToArray(Class<T> c, List<T> list) {
        T[] res = (T[]) Array.newInstance(c, list.size());
        list.toArray(res);
        return res;
    }

    public static <T> T[] ToArray(Class<T> c, Collection<T> list) {
        T[] res = (T[]) Array.newInstance(c, list.size());
        list.toArray(res);
        return res;
    }

    public static int GetIndex(int count, int index) {
        return (count - (count - (++index == count ? 0 : index))) % count;
    }
}
