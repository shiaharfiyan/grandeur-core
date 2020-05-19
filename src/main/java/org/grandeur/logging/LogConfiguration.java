package org.grandeur.logging;

import com.google.gson.*;
import org.grandeur.FileSystem;
import org.grandeur.logging.abstraction.BaseLogAppender;
import org.grandeur.logging.appenders.ConsoleLogAppender;
import org.grandeur.logging.appenders.FileLogAppender;
import org.grandeur.logging.interfaces.LogAppender;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.Environment;
import org.grandeur.utils.Procedure;
import org.grandeur.utils.helpers.ArrayHelper;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
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
public enum LogConfiguration implements FileSystem {
    Instance;

    private String path;
    private String fileName;
    private long lastModified;

    public void Load() {
        for (LogContext ctx : LogManager.GetAllLogContext()) {
            for (Logger logger : ctx.GetLoggers()) {
                Load(logger);
            }
        }
    }

    public void SetLastModified(long value) {
        this.lastModified = value;
    }

    public long GetLastModified() {
        return this.lastModified;
    }

    public void Load(Logger logger) {
        try {
            File file = new File(GetFullPath());
            if (!file.exists() || file.isDirectory()) {
                Environment.ExportResource("/Grandeur.json", this.getClass());
                return;
            }

            Gson gson = new Gson();
            JsonElement je = new JsonParser().parse(gson.newJsonReader(new FileReader(GetFullPath())));
            JsonArray loggerList = je.getAsJsonObject().get("loggerList").getAsJsonArray();
            String globalPattern = null;
            if (je.getAsJsonObject().has("globalPattern")) {
                globalPattern = je.getAsJsonObject().get("globalPattern").getAsString();
            }

            boolean bindToAll = false;
            long timeMilli = Instant.now().toEpochMilli();
            for (int i = 0; i < loggerList.size(); i++) {
                String jsonBindTo = loggerList.get(i).getAsJsonObject().get("bindTo").getAsString();

                List<LogFilter> logFilterList = new ArrayList<>();
                if (loggerList.get(i).getAsJsonObject().has("filters")) {
                    JsonArray filters = loggerList.get(i).getAsJsonObject().get("filters").getAsJsonArray();
                    for (int j = 0; j < filters.size() ; j++) {
                        JsonObject filter = filters.get(j).getAsJsonObject();
                        LogFilter logFilter = new LogFilter();
                        logFilter.SetMethod(Method.FindWithDefault(filter.get("method").getAsString(), Method.Contains));
                        logFilter.SetArea(Area.FindWithDefault(filter.get("area").getAsString(), Area.Value));
                        logFilter.SetFilter(filter.get("filter").getAsString());
                        logFilterList.add(logFilter);
                    }
                }

                if (jsonBindTo.equals("*")) {
                    bindToAll = true;
                }

                JsonArray appenderList = loggerList.get(i).getAsJsonObject().getAsJsonArray("appenderList");
                LoadAppenderList(logger, appenderList, ArrayHelper.ToArray(LogFilter.class, logFilterList), timeMilli, globalPattern, bindToAll, jsonBindTo);
            }

            if (loggerList.size() == 0)
                logger.AddAppender(new ConsoleLogAppender(logger));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadAppenderList(Logger loggerToBind, JsonArray appenderList,
                                  LogFilter[] logFilters,
                                  long timeMilli,
                                  String globalPattern,
                                  boolean bindToAll,
                                  String jsonBindTo) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (loggerToBind == null)
            return;

        for (int j = 0; j < appenderList.size(); j++) {
            if (!bindToAll && !loggerToBind.GetName().equals(jsonBindTo)) {
                return;
            }

            JsonObject appender = appenderList.get(j).getAsJsonObject();
            Class<?> appenderClass = Class.forName(appender.get("type").getAsString());
            Constructor<?> appenderConstructor = appenderClass.getConstructor(Logger.class);
            String jsonAppenderLevel = appender.get("level").getAsString();
            String jsonAppenderPattern = null;

            if (appender.has("pattern")) {
                jsonAppenderPattern = appender.get("pattern").getAsString();
            } else {
                jsonAppenderPattern = globalPattern;
            }

            LogAppender appenderObject = (BaseLogAppender) appenderConstructor.newInstance(new Object[]{loggerToBind});

            appenderObject.SetLevel(Level.FindWithDefault(jsonAppenderLevel, Level.INFO));
            if (jsonAppenderPattern == null) {
                jsonAppenderPattern = LogPattern.Default().GetPattern();
            }

            appenderObject.SetLogPattern(new LogPattern(jsonAppenderPattern, logFilters));

            if (appenderObject instanceof FileLogAppender) {
                String jsonPath = Environment.Replace(appender.get("path").getAsString());
                String jsonFileName = Environment.Replace(appender.get("fileName").getAsString());

                JsonObject keeper = appender.get("keeper").getAsJsonObject();

                boolean jsonKeeper = keeper.get("enabled").getAsBoolean();
                boolean jsonMoveFile = keeper.get("autoMove").getAsBoolean();
                boolean jsonAutoCreate = keeper.get("autoCreateArchiveFolder").getAsBoolean();
                BigInteger jsonSizeLimit = keeper.get("sizeLimit").getAsBigInteger();
                int jsonFileCountToKeep = keeper.get("fileToKeep").getAsInt();

                JsonNull jsonPrefix = keeper.get("prefix").getAsJsonNull();
                JsonNull jsonSuffix = keeper.get("suffix").getAsJsonNull();

                FileLogAppender fileLogAppender = (FileLogAppender) appenderObject;
                fileLogAppender.SetFileName(jsonFileName);
                fileLogAppender.SetPath(jsonPath);

                fileLogAppender.ReinitializeKeeper();
                fileLogAppender.GetKeeper().SetEnable(jsonKeeper);
                fileLogAppender.GetKeeper().SetFileCountToKeep(jsonFileCountToKeep);
                fileLogAppender.GetKeeper().SetSizeLimit(jsonSizeLimit);
                fileLogAppender.GetKeeper().SetPrefix(jsonPrefix.isJsonPrimitive() ? jsonPrefix.getAsString() : null);
                fileLogAppender.GetKeeper().SetSuffix(jsonSuffix.isJsonPrimitive() ? jsonSuffix.getAsString() : null);
                fileLogAppender.GetKeeper().SetNeedToMove(jsonMoveFile);
                fileLogAppender.GetKeeper().SetAutoCreateArchivedDirectory(jsonAutoCreate);
            }

            loggerToBind.UpdateAppender(appenderObject, timeMilli);
        }

        loggerToBind.RemoveAppender(timeMilli);
    }

    @Override
    public String GetPath() {
        return path;
    }

    @Override
    public void SetPath(String path) {
        this.path = path;
        SetLastModified();
    }

    @Override
    public String GetFileName() {
        return fileName;
    }

    @Override
    public void SetFileName(String fileName) {
        this.fileName = fileName;
        SetLastModified();
    }

    @Override
    public String GetFullPath() {
        String pathSeparator = Environment.IsWindows() ? "\\" : "/";
        return ((this.path != null) && (this.path.endsWith(pathSeparator)) ? this.path + this.fileName : this.path + pathSeparator + this.fileName);
    }

    private void SetLastModified() {
        File file = new File(GetFullPath());
        if (file.exists() && !file.isDirectory()) {
            this.SetLastModified(file.lastModified());
        }
    }

    public void Notify(Procedure preRun, Procedure updatedOccurredRun, Procedure postRun) {
        preRun.Run();
        long currentLastModified = GetLastModified();
        File file = new File(GetFullPath());
        if (currentLastModified != file.lastModified()) {
            SetLastModified(file.lastModified());
            Load();
            updatedOccurredRun.Run();
        }
        postRun.Run();
    }
}
