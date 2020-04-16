package io.github.shiaharfiyan.logging;

import com.google.gson.*;
import io.github.shiaharfiyan.FileSystem;
import io.github.shiaharfiyan.logging.abstraction.BaseLogAppender;
import io.github.shiaharfiyan.logging.appenders.ConsoleLogAppender;
import io.github.shiaharfiyan.logging.appenders.FileLogAppender;
import io.github.shiaharfiyan.logging.interfaces.LogAppender;
import io.github.shiaharfiyan.logging.interfaces.Logger;
import io.github.shiaharfiyan.utils.Environment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;

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
        System.out.println("Log Configuration has been loaded for all logger!");
    }

    public void SetLastModified(long value) {
        this.lastModified = value;
    }

    public long GetLastModified() {
        return this.lastModified;
    }

    public boolean Load(Logger logger) {

        File file = new File(GetFullPath());
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Cannot find " + GetFullPath() + " file!");
            return false;
        }

        try {
            Gson gson = new Gson();

            JsonElement je = new JsonParser().parse(gson.newJsonReader(new FileReader(GetFullPath())));
            JsonArray loggerList = je.getAsJsonObject().get("loggerList").getAsJsonArray();
            String globalPattern = null;
            if (je.getAsJsonObject().has("globalPattern")) {
                globalPattern = je.getAsJsonObject().get("globalPattern").getAsString();
            }
            boolean bindToAll = false;
            for (int i = 0; i < loggerList.size(); i++) {
                String jsonBindTo = loggerList.get(i).getAsJsonObject().get("bindTo").getAsString();

                if (jsonBindTo.equals("*")) {
                    bindToAll = true;
                }

                JsonArray appenderList = loggerList.get(i).getAsJsonObject().getAsJsonArray("appenderList");
                LoadAppenderList(logger, appenderList, globalPattern, bindToAll, jsonBindTo);
            }

            if (loggerList.size() == 0)
                logger.AddAppender(new ConsoleLogAppender(logger));

        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("Log Configuration has been loaded for " + logger.GetName());
        return true;
    }

    private void LoadAppenderList(Logger loggerToBind, JsonArray appenderList,
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
            appenderObject.SetLogPattern(new LogPattern(jsonAppenderPattern));

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

            loggerToBind.UpdateAppender(appenderObject);
        }
    }

    @Override
    public String GetPath() {
        return path;
    }

    @Override
    public void SetPath(String path) {
        this.path = path;
        UpdateLastModified();
    }

    @Override
    public String GetFileName() {
        return fileName;
    }

    @Override
    public void SetFileName(String fileName) {
        this.fileName = fileName;
        UpdateLastModified();
    }

    @Override
    public String GetFullPath() {
        String pathSeparator = Environment.IsWindows() ? "\\" : "/";
        return ((this.path != null) && (this.path.endsWith(pathSeparator)) ? this.path + this.fileName : this.path + pathSeparator + this.fileName);
    }

    private void UpdateLastModified() {
        File file = new File(GetFullPath());
        if (file.exists() && !file.isDirectory()) {
            this.SetLastModified(file.lastModified());
        }
    }
}
