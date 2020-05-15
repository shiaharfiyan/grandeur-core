package org.grandeur.logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
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
public class DC {
    private volatile static ConcurrentHashMap<LogContext, Stack<Context>> ndcContext;

    static {
        ndcContext = new ConcurrentHashMap<>();
    }

    public static Context Push(String context) {
        if (!ndcContext.containsKey(LogManager.GetCurrent()))
            ndcContext.put(LogManager.GetCurrent(), new Stack<>());

        ndcContext.get(LogManager.GetCurrent()).push(new Context(context));
        return Peek();
    }

    public static void Pop() {
        if (!ndcContext.containsKey(LogManager.GetCurrent()))
            return;

        if (ndcContext.get(LogManager.GetCurrent()).size() > 0)
            ndcContext.get(LogManager.GetCurrent()).pop();
    }

    public static Context[] Stacks(boolean reverse) {
        if (!ndcContext.containsKey(LogManager.GetCurrent()))
            return null;

        Context[] contexts = new Context[ndcContext.get(LogManager.GetCurrent()).toArray().length];
        ndcContext.get(LogManager.GetCurrent()).toArray(contexts);
        if (reverse) {
            List<Context> spanList = Arrays.asList(contexts);
            Collections.reverse(spanList);
            spanList.toArray(contexts);
        }
        return contexts;
    }

    public static Context Peek() {
        if (ndcContext.size() > 0) {
            if (!ndcContext.containsKey(LogManager.GetCurrent()))
                ndcContext.put(LogManager.GetCurrent(), new Stack<>());

            if (ndcContext.get(LogManager.GetCurrent()).size() > 0)
                return ndcContext.get(LogManager.GetCurrent()).peek();
        }

        return Context.NULL;
    }
}
