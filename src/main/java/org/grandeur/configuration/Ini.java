package org.grandeur.configuration;

import org.grandeur.FileSystemBase;
import org.grandeur.logging.Context;
import org.grandeur.logging.DC;
import org.grandeur.logging.LogManager;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.Environment;
import org.grandeur.utils.helpers.ArrayHelper;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
@SuppressWarnings("Duplicates")
public class Ini extends FileSystemBase {
    private ConcurrentHashMap<String, IniItem> keyItems = new ConcurrentHashMap<>();
    private Logger logger = LogManager.GetLogger(Ini.class);

    private Ini(String path, String fileName) {
        super(path, fileName);
    }

    public static Ini Load(String configFilename) {
        Ini config = null;
        File file = new File(configFilename);
        if ((file.exists()) && (!file.isDirectory())) {
            config = new Ini(file.getParent(), file.getName());
            config.Update();
        }

        return config;
    }

    public static void PrintConfiguration(Ini config) {
        String[] sections = new String[config.GetAll().keySet().size()];
        config.GetAll().keySet().toArray(sections);
        for (String section : sections) {
            IniItem item = config.GetAll().get(section);
            System.out.println("[" + item.GetSection() + "]");

            String[] keys = new String[item.GetList().keySet().size()];
            item.GetList().keySet().toArray(keys);
            for (String key : keys) {
                System.out.println(key + "=" + item.GetList().get(key));
            }
        }
    }

    public int GetSectionCount() {
        return keyItems.size();
    }


    private ConcurrentHashMap<String, IniItem> GetAll() {
        return keyItems;
    }

    public String[] GetSections() {
        List<String> sections = new ArrayList<>(keyItems.keySet());
        return ArrayHelper.ToArray(String.class, sections);
    }

    public String[] GetKeys(String section) {
        if (keyItems.containsKey(section)) {
            List<String> keys = new ArrayList<>(keyItems.get(section).GetList().keySet());
            return ArrayHelper.ToArray(String.class, keys);
        }

        return null;
    }

    public void Put(String section, String key, Object value) {
        IniItem item = GetAll().get(section);
        if (item == null) {
            item = new IniItem(section);
            item.Put(key, value);

            this.GetAll().put(section, item);
        } else {
            item.Put(key, value);
        }
    }

    public void Save() {
        File file = new File(GetFileName());
        try {
            OutputStream stream = new FileOutputStream(GetFileName());
            OutputStreamWriter writer = new OutputStreamWriter(stream);

            String[] ciKeys = new String[GetAll().keySet().size()];
            GetAll().keySet().toArray(ciKeys);
            for (String ciKey : ciKeys) {
                IniItem item = GetAll().get(ciKey);

                writer.write("[" + ciKey + "]\r\n");

                String[] itemKeys = new String[item.GetList().keySet().size()];
                item.GetList().keySet().toArray(itemKeys);
                for (String iKey : itemKeys) {
                    writer.write(iKey + "=" + item.GetList().get(iKey) + "\r\n");
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean GetBoolean(String section, String key, boolean defvalue) {
        IniItem item = GetAll().get(section);
        if (item != null) {
            Object value = item.GetList().get(key);
            if (value != null) {
                return Boolean.parseBoolean(value.toString());
            }
            return defvalue;
        }
        return defvalue;
    }

    public long GetLong(String section, String key, long defvalue) {
        IniItem item = keyItems.get(section);
        if (item != null) {
            Object value = item.GetList().get(key);
            if (value != null) {
                return Long.parseLong(value.toString());
            }
            return defvalue;
        }
        return defvalue;
    }

    public int GetInteger(String section, String key, int defvalue) {
        IniItem item = keyItems.get(section);
        if (item != null) {
            Object value = item.GetList().get(key);
            if (value != null) {
                return Integer.parseInt(value.toString());
            }
            return defvalue;
        }
        return defvalue;
    }

    public float GetFloat(String section, String key, float defvalue) {
        IniItem item = keyItems.get(section);
        if (item != null) {
            Object value = item.GetList().get(key);
            if (value != null) {
                return Float.parseFloat(value.toString());
            }
            return defvalue;
        }
        return defvalue;
    }

    public double GetDouble(String section, String key, double defvalue) {
        IniItem item = keyItems.get(section);
        if (item != null) {
            Object value = item.GetList().get(key);
            if (value != null) {
                return Double.parseDouble(value.toString());
            }
            return defvalue;
        }
        return defvalue;
    }

    public String GetString(String section, String key, String defvalue) {
        IniItem item = keyItems.get(section);
        if (item != null) {
            Object value = item.GetList().get(key);
            if (value != null) {
                return value.toString();
            }
            return defvalue;
        }
        return defvalue;
    }

    public BigInteger GetBigInteger(String section, String key, BigInteger defvalue) {
        IniItem item = keyItems.get(section);
        if (item != null) {
            Object value = item.GetList().get(key);
            if (value != null) {
                return new BigInteger(value.toString());
            }
            return defvalue;
        }
        return defvalue;
    }

    public void Update() {
        try (Context context = DC.Push("Update")) {
            logger.Info("Updating configuration file [" + GetFullPath() + "]");
            File file = new File(GetFullPath());
            if ((file.exists()) && (!file.isDirectory())) {
                try {
                    String currentLine = "";
                    InputStream fis = new FileInputStream(GetFullPath());
                    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    IniItem item = null;
                    while ((currentLine = br.readLine()) != null) {
                        if (currentLine.startsWith(";") || currentLine.trim().equalsIgnoreCase("")) {
                            continue;
                        }
                        if (currentLine.startsWith("[")) {
                            String section = currentLine.substring(1, currentLine.length() - 1);
                            item = new IniItem(section);
                            GetAll().put(section, item);
                        } else if (item != null) {
                            int pos = currentLine.trim().indexOf("=");
                            if (pos >= 0) {
                                String key = currentLine.substring(0, pos);
                                if (pos < currentLine.length() - 1) {
                                    String value = currentLine.substring(pos + 1);
                                    item.GetList().put(key.trim(), Environment.Replace(value));
                                } else {
                                    item.GetList().put(key.trim(), "");
                                }
                                logger.Info(item.GetSection() + " => " + key.trim() + " => " + item.GetList().get(key.trim()));
                            }
                        }
                    }
                    br.close();
                    isr.close();
                    fis.close();

                    logger.Info("Configuration has been updated!");
                } catch (IOException e) {
                    logger.Exception(e);
                    e.printStackTrace();
                }
            }
        }
    }

    static class IniItem {
        private String section;

        private ConcurrentHashMap<String, Object> list;

        IniItem(String section) {
            this.section = section;
            this.list = new ConcurrentHashMap<>();
        }

        public Object Get(String key) {
            return this.list.get(key);
        }

        public String GetSection() {
            return section;
        }

        public ConcurrentHashMap<String, Object> GetList() {
            return list;
        }

        void Put(String key, Object value) {
            this.list.put(key, value);
        }
    }
}
