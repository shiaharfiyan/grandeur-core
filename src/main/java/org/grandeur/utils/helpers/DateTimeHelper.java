package org.grandeur.utils.helpers;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
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
public final class DateTimeHelper {

    public static final String DefaultFormat = "yyyy/MM/dd HH:mm:ss,SSS";
    private static SimpleDateFormat completeFormatter = new SimpleDateFormat(DefaultFormat);

    /**
     * Convert datetime to sql timestamp format
     *
     * @param date   date in string
     * @param format date format. e.g yyyy-MM-dd HH:mm:ss
     * @return Sql timestamp in string
     */
    public static String ToSqlTimestamp(String date, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date parsedDate = dateFormat.parse(date);
        Timestamp timestamp = new Timestamp(parsedDate.getTime());

        return timestamp.toString();
    }

    public static String GetCompleteDateFormat(Date d) {
        return completeFormatter.format(d);
    }

    public static String Format(Date d, String format) {
        return new SimpleDateFormat(format).format(d);
    }

    public static Date Parse(String s) throws ParseException {
        return completeFormatter.parse(s);
    }

    public static String GetCompleteDateFormat(Instant instant) {
        return DateTimeFormatter.ofPattern(DefaultFormat)
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    public static Instant ToInstant(String dateTime, String format, Locale locale, ZoneId zone) {
        if (locale == null)
            locale = Locale.getDefault();

        if (zone == null)
            zone = ZoneId.systemDefault();

        if (StringHelper.IsNullOrEmpty(format, true))
            format = DefaultFormat;

        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(format, locale);
        LocalDateTime dtLocal = LocalDateTime.parse(dateTime, dtFormatter);
        ZonedDateTime dtZone = dtLocal.atZone(zone);

        return dtZone.toInstant();
    }
}
