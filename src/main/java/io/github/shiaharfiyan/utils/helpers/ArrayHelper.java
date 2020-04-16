package io.github.shiaharfiyan.utils.helpers;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

/**
 * Created by Harfiyan on 26/04/2017.
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
