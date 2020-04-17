package org.grandeur;

import org.grandeur.utils.Environment;
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
public abstract class FileSystemBase implements FileSystem {
    protected String path;
    protected String fileName;

    public FileSystemBase(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    @Override
    public String GetPath() {
        return path;
    }

    @Override
    public void SetPath(String path) {
        this.path = path;
    }

    @Override
    public String GetFileName() {
        return fileName;
    }

    @Override
    public void SetFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String GetFullPath() {
        String pathSeparator = Environment.IsWindows() ? "\\" : "/";
        return ((this.path != null) && (this.path.endsWith(pathSeparator)) ? this.path + this.fileName : this.path + pathSeparator + this.fileName);
    }
}
