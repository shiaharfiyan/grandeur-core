package org.grandeur.logging;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class LogPattern {
    private static HashMap<String, SimpleDateFormat> dateFormatters = new HashMap<>();
    private Stack<Token> tokens = new Stack<>();
    private String pattern;

    public LogPattern(String pattern) {
        this.pattern = pattern;
    }

    public static LogPattern Default() {
        return new LogPattern("[%d{yyyy/MM/dd HH:mm:ss,SSS}] [%t] %c %n %l : %v");
    }

    public String GetPattern() {
        return pattern;
    }

    public String Parse(LogRecord record) {
        boolean inVarPattern = false;
        boolean inVarStart = false;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbPattern = new StringBuilder();
        for (char c : GetPattern().toCharArray()) {
            switch (c) {
                case '%':
                    inVarStart = true;
                    break;
                case '{':
                    inVarPattern = true;
                    break;
                case '}':
                    inVarPattern = false;
                    break;
                case 'd':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.DATETIME);
                    }
                    break;
                case 'c':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.NESTEDCONTEXT);
                    }
                    break;
                case 'C':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.ALLNESTEDCONTEXT);
                    }
                    break;
                case 'R':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.REVERSEALLNESTEDCONTEXT);
                    }
                    break;
                case 'x':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.MAPPEDCONTEXT);
                    }
                    break;
                case 'X':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.ALLMAPPEDCONTEXT);
                    }
                    break;
                case 't':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.THREAD);
                    }
                    break;
                case 'z':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.DURATION);
                    }
                    break;
                case 'n':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.NAME);
                    }
                    break;
                case 'i':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.NESTEDCONTEXTID);
                    }
                    break;
                case 'l':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.LEVEL);
                    }
                    break;
                case 'v':
                    if (inVarStart && inVarPattern) {
                        sbPattern.append(c);
                    } else if (inVarStart) {
                        tokens.push(Token.VALUE);
                    }
                    break;
                default:
                    if (inVarStart && !inVarPattern) {
                        inVarStart = false;
                        Process(record, sb, sbPattern);
                        sb.append(c);
                    } else if (inVarStart) {
                        sbPattern.append(c);
                    } else if (!inVarPattern) {
                        sb.append(c);
                    }
            }
        }

        while (tokens.size() != 0) {
            Process(record, sb, sbPattern);
        }

        sb.append(sbPattern.toString());
        return sb.toString();
    }

    public void Process(LogRecord record, StringBuilder sb, StringBuilder sbPattern) {
        if (tokens.size() == 0)
            return;

        Token t = tokens.pop();
        if (t == Token.DATETIME) {
            if (sbPattern.toString().equals("")) {
                sbPattern.append("yyyy-MM-dd HH:mm:ss");
            }
            if (!dateFormatters.containsKey(sbPattern.toString()))
                dateFormatters.put(sbPattern.toString(), new SimpleDateFormat(sbPattern.toString()));

            SimpleDateFormat sdf = dateFormatters.get(sbPattern.toString());
            sb.append(sdf.format(record.GetTimestamp()));
            sbPattern.setLength(0);
        } else if (t == Token.THREAD) {
            sb.append(Thread.currentThread().getName());
        } else if (t == Token.NESTEDCONTEXT) {
            sb.append(DC.Peek().GetValue());
        } else if (t == Token.NESTEDCONTEXTID) {
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
                sb.append(spanBuilder.toString());
            }
        } else if (t == Token.MAPPEDCONTEXT) {
            if (!sbPattern.toString().equals("")) {
                sb.append(DC.Get(sbPattern.toString()));
                sbPattern.setLength(0);
            }
        } else if (t == Token.ALLMAPPEDCONTEXT) {
            if (!sbPattern.toString().equals("")) {
                HashMap<String, String> localMap = DC.Maps();
                StringBuilder kvBuilder = new StringBuilder();
                for (String key : localMap.keySet())
                    kvBuilder.append(key).append("=").append(localMap.get(key)).append(", ");
                kvBuilder.setLength(Math.max(kvBuilder.length() - 2, 0));
                sb.append(kvBuilder.toString());
            }
        } else if (t == Token.VALUE) {
            sb.append(record.GetValue());
        } else if (t == Token.DURATION) {
            sb.append("null");
        } else if (t == Token.NAME) {
            sb.append(record.GetLogger().GetName());
        } else if (t == Token.LEVEL) {
            sb.append(record.GetLevel());
        }
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
        SECTION,
        VALUE,
        LEVEL,
        NAME
    }
}
