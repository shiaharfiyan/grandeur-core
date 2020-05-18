package org.grandeur.logging;

import org.grandeur.utils.helpers.ObjectHelper;

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
    public enum Method {
        StartWith,
        EndWith,
        Contains,
        Equals,
        NotContains,
        Regex;

    public static Method Find(String name) {
        for (Method l : Method.values()) {
            if (name.toUpperCase().equals(l.name().toUpperCase())) {
                return l;
            }
        }

        return null;
    }

    public static Method FindWithDefault(String name, Method default_value) {
        Method l = Find(name);
        return l == null ? default_value : l;
    }

    public static Method Lookup(String name) {
        return ObjectHelper.Lookup(Method.class, name);
    }
}
