package org.grandeur.logging;

import org.grandeur.utils.helpers.DateTimeHelper;
import org.grandeur.utils.helpers.StringHelper;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Pattern;

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
public class LogPattern {
    private static final String DefaultDateTimePattern = "yyyy-MM-dd HH:mm:ss";
    private static final String DefaultPattern = "[%d{yyyy/MM/dd HH:mm:ss,SSS}] [%t] %c %n %l : %v";
    private static HashMap<String, SimpleDateFormat> dateFormatters = new HashMap<>();
    private Stack<Token> tokens = new Stack<>();
    private String pattern;
    private HashMap<String, LogFilter> logFilterList;

    public LogPattern(String pattern) {
        this.logFilterList = new HashMap<>();
        this.pattern = pattern;
    }
    public LogPattern(String pattern, LogFilter[] filters) {
        this(pattern);
        SetLogFilters(filters);
    }

    public void SetLogFilters (LogFilter[] filters) {
        for(LogFilter filter : filters) {
            logFilterList.put(UUID.randomUUID().toString(), filter);
        }
    }

    public static LogPattern Default() {
        return new LogPattern(DefaultPattern);
    }

    public String GetPattern() {
        return pattern;
    }

    public String Parse(LogRecord record) {
        boolean inVarPattern = false;
        boolean inVarStart = false;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbPattern = new StringBuilder();
        boolean processed = true;

        for (char c : GetPattern().toCharArray()) {
            if (c == '%' || c == '{' || c == '}') {
                switch (c) {
                    case '%':
                        inVarStart = true;
                        continue;
                    case '{':
                        inVarPattern = true;
                        continue;
                    case '}':
                        inVarPattern = false;
                        continue;
                }
            }

            if (inVarStart) {
                if (inVarPattern) {
                    sbPattern.append(c);
                } else if (c == 'd' || c == 'c' || c == 'C' || c == 'R' || c == 'x' || c == 'X' || c == 't' || c == 'z' || c == 'n' || c == 'N' || c == 'i' || c == 'l' || c == 'v') {
                    switch (c) {
                        case 'd':
                            tokens.push(Token.DATETIME);
                            break;
                        case 'c':
                            tokens.push(Token.NESTEDCONTEXT);
                            break;
                        case 'C':
                            tokens.push(Token.ALLNESTEDCONTEXT);
                            break;
                        case 'R':
                            tokens.push(Token.REVERSEALLNESTEDCONTEXT);
                            break;
                        case 'x':
                            tokens.push(Token.MAPPEDCONTEXT);
                            break;
                        case 'X':
                            tokens.push(Token.ALLMAPPEDCONTEXT);
                            break;
                        case 't':
                            tokens.push(Token.THREAD);
                            break;
                        case 'z':
                            tokens.push(Token.DURATION);
                            break;
                        case 'n':
                            tokens.push(Token.SIMPLENAME);
                            break;
                        case 'N':
                            tokens.push(Token.NAME);
                            break;
                        case 'i':
                            tokens.push(Token.NESTEDCONTEXTID);
                            break;
                        case 'l':
                            tokens.push(Token.LEVEL);
                            break;
                        case 'v':
                            tokens.push(Token.VALUE);
                            break;
                    }
                } else {
                    inVarStart = false;
                    processed = processed && Process(record, sb, sbPattern);
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }

        while (tokens.size() != 0) {
            processed = processed && Process(record, sb, sbPattern);
        }

        if (!processed)
            return "";

        sb.append(sbPattern.toString());
        return sb.toString();
    }

    private boolean FindFilter(String source, Area area) {
        for(LogFilter logFilter : logFilterList.values()) {
            if (logFilter.GetArea().GetValue() == area.GetValue()) {
                switch (logFilter.GetMethod().GetValue()) {
                    case Method.ContainsValue:
                        return logFilter.IsIgnoreCase() ?
                                source.toLowerCase().contains(logFilter.GetFilter().toLowerCase()) :
                                source.contains(logFilter.GetFilter());
                    case Method.EqualsValue:
                        return logFilter.IsIgnoreCase() ?
                                source.toLowerCase().equals(logFilter.GetFilter().toLowerCase()) :
                                source.equals(logFilter.GetFilter());
                    case Method.StartWithValue:
                        return logFilter.IsIgnoreCase() ?
                                source.toLowerCase().startsWith(logFilter.GetFilter().toLowerCase()) :
                                source.startsWith(logFilter.GetFilter());
                    case Method.NotContainsValue:
                        return logFilter.IsIgnoreCase() ?
                                !source.toLowerCase().contains(logFilter.GetFilter().toLowerCase()) :
                                !source.contains(logFilter.GetFilter());
                    case Method.EndWithValue:
                        return logFilter.IsIgnoreCase() ?
                                !source.toLowerCase().endsWith(logFilter.GetFilter().toLowerCase()) :
                                !source.endsWith(logFilter.GetFilter());
                    case Method.RegexValue:
                        try {
                            return logFilter.IsIgnoreCase() ?
                                    Pattern.matches(logFilter.GetFilter().toLowerCase(), source.toLowerCase()) :
                                    Pattern.matches(logFilter.GetFilter(), source);
                        } catch (Exception e) {
                            System.out.println("Error while parsing regex: " + e.getMessage());
                            e.printStackTrace();
                        }
                    default:
                        return StringHelper.IsNullOrEmpty(logFilter.GetFilter(), true);
                }
            }
        }

        return true;
    }

    public boolean Process(LogRecord record, StringBuilder sb, StringBuilder sbPattern) {
        if (tokens.size() == 0)
            return true;

        Token t = tokens.pop();
        if (t == Token.DATETIME) {
            if (sbPattern.toString().equals("")) {
                sbPattern.append(DefaultDateTimePattern);
            }
            if (!dateFormatters.containsKey(sbPattern.toString()))
                dateFormatters.put(sbPattern.toString(), new SimpleDateFormat(sbPattern.toString()));

            SimpleDateFormat sdf = dateFormatters.get(sbPattern.toString());
            String dateTime = sdf.format(record.GetTimestamp());
            sbPattern.setLength(0);
            if (!FindFilter(dateTime, Area.Date))
                return false;

            sb.append(dateTime);
        } else if (t == Token.THREAD) {
            if (!FindFilter(Thread.currentThread().getName(), Area.Thread))
                return false;
            sb.append(Thread.currentThread().getName());
        } else if (t == Token.NESTEDCONTEXT) {
            if (!FindFilter(DC.Peek().GetValue(), Area.Context))
                return false;
            sb.append(DC.Peek().GetValue());
        } else if (t == Token.NESTEDCONTEXTID) {
            if (!FindFilter(DC.Peek().GetId(), Area.Context))
                return false;
            sb.append(DC.Peek().GetId());
        } else if (t == Token.ALLNESTEDCONTEXT) {
            if (DC.Stacks(false) == null) {
                sb.append("null");
            } else {
                StringBuilder spanBuilder = new StringBuilder();
                if (Objects.requireNonNull(DC.Stacks(true)).length > 0) {
                    for (Context context : Objects.requireNonNull(DC.Stacks(false)))
                        spanBuilder.append(context.GetValue()).append(" ");
                } else spanBuilder.append("null ");
                spanBuilder.setLength(Math.max(spanBuilder.length() - 1, 0));
                if (!FindFilter(spanBuilder.toString(), Area.Context))
                    return false;
                sb.append(spanBuilder.toString());
            }
        } else if (t == Token.REVERSEALLNESTEDCONTEXT) {
            if (DC.Stacks(true) == null) {
                sb.append("null");
            } else {
                StringBuilder spanBuilder = new StringBuilder();
                if (Objects.requireNonNull(DC.Stacks(true)).length > 0) {
                    for (Context context : Objects.requireNonNull(DC.Stacks(true)))
                        spanBuilder.append(context.GetValue()).append(" ");
                } else spanBuilder.append("null ");
                spanBuilder.setLength(Math.max(spanBuilder.length() - 1, 0));
                if (!FindFilter(spanBuilder.toString(), Area.Context))
                    return false;
                sb.append(spanBuilder.toString());
            }
        } else if (t == Token.MAPPEDCONTEXT) {
            if (sbPattern.toString().equals("")) {
                return ProcessMaps(sb);
            }
            String mappedContext = DC.Peek().Get(sbPattern.toString());
            sbPattern.setLength(0);
            if (!FindFilter(mappedContext, Area.Context)) {
                return false;
            }
            sb.append(mappedContext);
        } else if (t == Token.ALLMAPPEDCONTEXT) {
            if (!sbPattern.toString().equals("")) {
                String mappedContext = DC.Peek().Get(sbPattern.toString());
                sbPattern.setLength(0);
                if (!FindFilter(mappedContext, Area.Context)) {
                    return false;
                }
                sb.append(mappedContext);
                return true;
            }
            return ProcessMaps(sb);
        } else if (t == Token.VALUE) {
            if (!FindFilter(record.GetValue(), Area.Value))
                return false;
            sb.append(record.GetValue());
        } else if (t == Token.DURATION) {
            Context context;
            if ((context = DC.Peek()) != null) {
                Instant startTime = DateTimeHelper.ToInstant(context.GetStartTime(), null, null, null);
                Duration duration = Duration.between(startTime, Instant.now());
                sb.append(duration.getSeconds()).append(".").append(duration.getNano()).append("s");
            }
        } else if (t == Token.NAME) {
            if (!FindFilter(record.GetLogger().GetName(), Area.LoggerName))
                return false;
            sb.append(record.GetLogger().GetName());
        } else if (t == Token.SIMPLENAME) {
            String completeName = record.GetLogger().GetName();
            if (!FindFilter(completeName.substring(completeName.lastIndexOf(".")+1), Area.LoggerName))
                return false;
            sb.append(completeName.substring(completeName.lastIndexOf(".")+1));
        } else if (t == Token.LEVEL) {
            if (!FindFilter(record.GetLevel().name(), Area.Level))
                return false;
            sb.append(record.GetLevel());
        }

        return true;
    }

    private boolean ProcessMaps(StringBuilder sb) {
        HashMap<String, String> localMap = DC.Peek().Maps();
        if (localMap.size() > 0) {
            StringBuilder kvBuilder = new StringBuilder();
            for (String key : localMap.keySet())
                kvBuilder.append(key).append("=").append(localMap.get(key)).append(", ");
            kvBuilder.setLength(Math.max(kvBuilder.length() - 2, 0));
            if (!FindFilter(kvBuilder.toString(), Area.Context)) {
                return false;
            }
            sb.append(kvBuilder.toString());
        } else {
            sb.append("null");
        }

        return true;
    }

    public enum Token {
        DATETIME,
        THREAD,
        NESTEDCONTEXT,
        NESTEDCONTEXTID,
        ALLNESTEDCONTEXT,
        REVERSEALLNESTEDCONTEXT,
        MAPPEDCONTEXT,
        ALLMAPPEDCONTEXT,
        DURATION,
        VALUE,
        LEVEL,
        NAME,
        SIMPLENAME
    }
}
