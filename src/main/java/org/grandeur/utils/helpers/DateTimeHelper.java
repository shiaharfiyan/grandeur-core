package io.github.shiaharfiyan.utils.helpers;

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
