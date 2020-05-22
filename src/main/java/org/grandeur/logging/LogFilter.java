package org.grandeur.logging;

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
public class LogFilter {
    private Method method;
    private String filter;
    private boolean ignoreCase;
    private Area area;

    public Method GetMethod() {
        return method;
    }

    public void SetMethod(Method method) {
        this.method = method;
    }

    public String GetFilter() {
        return filter;
    }

    public void SetFilter(String filter) {
        this.filter = filter;
    }

    public Area GetArea() {
        return area;
    }

    public void SetArea(Area area) {
        this.area = area;
    }

    public boolean IsIgnoreCase() {
        return ignoreCase;
    }

    public void SetIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
}
