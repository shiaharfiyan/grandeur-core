package org.grandeur.configuration;

import org.grandeur.FileSystemBase;
import org.grandeur.logging.DC;
import org.grandeur.logging.LogManager;
import org.grandeur.logging.interfaces.Logger;
import org.grandeur.utils.Environment;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * ==============================================================
 * ======================= DISCLAIMER!!! ========================
 * == DO NOT EDIT IF YOU DO NOT UNDERSTAND THE CODE ROUTINE!!! ==
 * ==============================================================
 * <p>
 * Created By: Harfiyan Shia
 * Created Date: 23 February 2018
 * <p>
 * Last modified By: Harfiyan Shia
 * Last modified Date: 23 February 2018
 * <p>
 * Change Logs
 * Version 1.0.0 (Release Date: 23 February 2018) (First Release)
 * - Section and Key inside configuration file are now case sensitive
 */

@SuppressWarnings("Duplicates")
public class Ini extends FileSystemBase {
    private HashMap<String, IniItem> configurationItems = new HashMap<>();
    private Logger logger = LogManager.GetLogger(Ini.class);

    private Ini(String path, String fileName) {
        super(path, fileName);
    }

    public static Ini Load(String configFilename) throws IOException {
        Ini config = null;
        File file = new File(configFilename);
        if ((file.exists()) && (!file.isDirectory())) {
            config = new Ini(file.getPath(), file.getName());
            config.SetFileName(configFilename);
            try {
                String currentLine = "";
                InputStream fis = new FileInputStream(file.getPath());
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
                        config.GetConfigurationItems().put(section, item);
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
                        }
                    }
                }

                br.close();
                isr.close();
                fis.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        return config;
    }

    public static void PrintConfiguration(Ini config) {
        String[] sections = new String[config.GetConfigurationItems().keySet().size()];
        config.GetConfigurationItems().keySet().toArray(sections);
        for (String section : sections) {
            IniItem item = config.GetConfigurationItems().get(section);
            System.out.println("[" + item.GetSection() + "]");

            String[] keys = new String[item.GetList().keySet().size()];
            item.GetList().keySet().toArray(keys);
            for (String key : keys) {
                System.out.println(key + "=" + item.GetList().get(key));
            }
        }
    }

    public int GetSectionCount() {
        return configurationItems.size();
    }

    public HashMap<String, IniItem> GetConfigurationItems() {
        return configurationItems;
    }

    public void Put(String section, String key, Object value) {
        IniItem item = GetConfigurationItems().get(section);
        if (item == null) {
            item = new IniItem(section);
            item.Put(key, value);

            this.GetConfigurationItems().put(section, item);
        } else {
            item.Put(key, value);
        }
    }

    public void Save() {
        File file = new File(GetFileName());
        try {
            OutputStream stream = new FileOutputStream(GetFileName());
            OutputStreamWriter writer = new OutputStreamWriter(stream);

            String[] ciKeys = new String[GetConfigurationItems().keySet().size()];
            GetConfigurationItems().keySet().toArray(ciKeys);
            for (String ciKey : ciKeys) {
                IniItem item = GetConfigurationItems().get(ciKey);

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
        IniItem item = GetConfigurationItems().get(section);
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
        IniItem item = configurationItems.get(section);
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
        IniItem item = configurationItems.get(section);
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
        IniItem item = configurationItems.get(section);
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
        IniItem item = configurationItems.get(section);
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
        IniItem item = configurationItems.get(section);
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
        IniItem item = configurationItems.get(section);
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
        DC.Push("Update");
        logger.Info("Updating configuration file [" + GetFileName() + "]");
        File file = new File(GetFileName());
        if ((file.exists()) && (!file.isDirectory())) {
            try {
                String currentLine = "";
                InputStream fis = new FileInputStream(file.getPath());
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
                        GetConfigurationItems().put(section, item);
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
            } finally {
                DC.Pop();
            }
        }
    }

    static class IniItem {
        private String section;

        private HashMap<String, Object> list;

        IniItem(String section) {
            this.section = section;
            this.list = new HashMap<>();
        }

        public Object Get(String key) {
            return this.list.get(key);
        }

        public String GetSection() {
            return section;
        }

        public HashMap<String, Object> GetList() {
            return list;
        }

        void Put(String key, Object value) {
            this.list.put(key, value);
        }
    }
}
