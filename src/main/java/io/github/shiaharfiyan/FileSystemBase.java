package io.github.shiaharfiyan;

import io.github.shiaharfiyan.utils.Environment;

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
