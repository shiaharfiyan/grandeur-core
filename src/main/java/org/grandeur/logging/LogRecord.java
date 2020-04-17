package org.grandeur.logging;

import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.helpers.ArrayHelper;

import java.util.ArrayList;
import java.util.Date;
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
@SuppressWarnings("Duplicates")
public class LogRecord {
    private String value;
    private Date timestamp;
    private Level level;

    private Logger logger;

    public LogRecord(Logger logger, Date timestamp, Level printLevel, String value) {
        this.logger = logger;
        this.timestamp = timestamp;
        this.level = printLevel;
        this.value = value;
    }

    public void SetValue(String value) {
        this.value = value;
    }

    public Date GetTimestamp() {
        return timestamp;
    }

    public Level GetLevel() {
        return level;
    }

    public void SetPrintLevel(Level level) {
        this.level = level;
    }

    public String GetValue() {
        return value;
    }

    public void SetTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String[] Build() {
        List<String> multilineValues = new ArrayList<>();
        if (value != null) {
            String[] values = GetValue().split("\\r?\\n");
            for (String v : values) {
                if (!v.trim().equals("")) {
                    multilineValues.add(v);
                }
            }
        }
        return ArrayHelper.StringListToArray(multilineValues);
    }

    public Logger GetLogger() {
        return logger;
    }
}
