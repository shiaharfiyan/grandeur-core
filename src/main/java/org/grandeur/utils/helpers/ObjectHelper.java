package io.github.shiaharfiyan.utils.helpers;

public final class ObjectHelper {
    public static <T> Result<T> TryCast(Object s) {
        Result<T> result = new Result<>();

        try {
            result.SetValue((T) s);
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(null);
            result.SetSucceeded(false);
        }

        return result;
    }

    public static <T extends Enum<T>> T Lookup(Class<T> t, String id) {
        try {
            return Enum.valueOf(t, id);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Invalid value for enum " + t.getSimpleName() + ": " + id);
        }
    }
}
