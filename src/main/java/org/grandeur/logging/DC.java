package org.grandeur.logging;

import java.util.*;
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
    private volatile static ConcurrentHashMap<LogContext, HashMap<String, String>> mdcContexts;

    static {
        ndcContext = new ConcurrentHashMap<>();
        mdcContexts = new ConcurrentHashMap<>();
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

    public static HashMap<String, String> Maps() {
        if (!mdcContexts.containsKey(LogManager.GetCurrent()))
            mdcContexts.put(LogManager.GetCurrent(), new HashMap<>());

        return (HashMap<String, String>) mdcContexts.get(LogManager.GetCurrent()).clone();
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

    public static void Put(String key, String value) {
        if (!mdcContexts.containsKey(LogManager.GetCurrent()))
            mdcContexts.put(LogManager.GetCurrent(), new HashMap<>());

        mdcContexts.get(LogManager.GetCurrent())
                .put(key, value);
    }

    public static void Remove(String key) {
        if (!mdcContexts.containsKey(LogManager.GetCurrent()))
            mdcContexts.put(LogManager.GetCurrent(), new HashMap<>());

        mdcContexts.get(LogManager.GetCurrent())
                .remove(key);
    }

    public static String Get(String key) {
        if (mdcContexts.size() > 0) {
            if (!mdcContexts.containsKey(LogManager.GetCurrent()))
                mdcContexts.put(LogManager.GetCurrent(), new HashMap<>());

            if (mdcContexts.get(LogManager.GetCurrent()).containsKey(key)) {
                return mdcContexts.get(LogManager.GetCurrent())
                        .get(key);
            }
        }

        return "null";
    }
}
