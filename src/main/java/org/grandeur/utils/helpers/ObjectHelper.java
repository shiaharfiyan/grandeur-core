package org.grandeur.utils.helpers;
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
