package org.grandeur.logging.interfaces;

import java.io.IOException;
import java.math.BigInteger;

public interface LogKeeper {
    void Keep() throws IOException;

    void SetEnable(boolean enabled);

    BigInteger GetSizeLimit();

    void SetSizeLimit(BigInteger sizeLimit);

    String GetPrefix();

    void SetPrefix(String prefix);

    String GetSuffix();

    void SetSuffix(String suffix);

    boolean IsEnabled();

    int GetFileCountToKeep();

    void SetFileCountToKeep(int num);

    boolean GetNeedToMove();

    void SetNeedToMove(boolean move);

    boolean GetAutoCreateArchivedDirectory();

    void SetAutoCreateArchivedDirectory(boolean value);
}
