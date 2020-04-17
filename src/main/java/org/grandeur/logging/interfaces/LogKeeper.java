package org.grandeur.logging.interfaces;

import java.io.IOException;
import java.math.BigInteger;
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
