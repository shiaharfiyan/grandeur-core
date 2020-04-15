package org.grandeur;

public interface FileSystem {
    String GetPath();

    void SetPath(String path);

    String GetFileName();

    void SetFileName(String fileName);

    String GetFullPath();
}
