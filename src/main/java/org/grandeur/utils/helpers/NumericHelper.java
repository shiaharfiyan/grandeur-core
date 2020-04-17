package org.grandeur.utils.helpers;

import java.math.BigDecimal;
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
public class NumericHelper {
    /**
     * Try to parse string to Integer
     *
     * @param s string to parse
     * @return Result object that contains parse result in boolean and parsed value. If not succeeded, value will be 0.
     */
    public static Result<Integer> TryParseInteger(String s) {
        Result<Integer> result = new Result<>();

        try {
            result.SetValue(Integer.parseInt(s));
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(0);
            result.SetSucceeded(false);
        }

        return result;
    }

    /**
     * Try to parse string to Float
     *
     * @param s string to parse
     * @return Result object that contains parse result in boolean and parsed value. If not succeeded, value will be 0F.
     */
    public static Result<Float> TryParseFloat(String s) {
        Result<Float> result = new Result<>();

        try {
            result.SetValue(Float.parseFloat(s));
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(0F);
            result.SetSucceeded(false);
        }

        return result;
    }

    /**
     * Try to parse string to Double
     *
     * @param s string to parse
     * @return Result object that contains parse result in boolean and parsed value. If not succeeded, value will be 0D.
     */
    public static Result<Double> TryParseDouble(String s) {
        Result<Double> result = new Result<>();

        try {
            result.SetValue(Double.parseDouble(s));
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(0D);
            result.SetSucceeded(false);
        }

        return result;
    }

    /**
     * Try to parse string to BigInteger
     *
     * @param s string to parse
     * @return return Result object that contains parse result in boolean and parsed value. If not succeeded, value will be BigInteger 0.
     */
    public static Result<BigInteger> TryParseBigInteger(String s) {
        Result<BigInteger> result = new Result<>();

        try {
            result.SetValue(new BigInteger(s));
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(new BigInteger("0"));
            result.SetSucceeded(false);
        }

        return result;
    }

    /**
     * Try to parse string to BigDecimal
     *
     * @param s string to parse
     * @return return Result object that contains parse result in boolean and parsed value. If not succeeded, value will be BigDecimal 0.
     */
    public static Result<BigDecimal> TryParseBigDecimal(String s) {
        Result<BigDecimal> result = new Result<>();

        try {
            result.SetValue(new BigDecimal(s));
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(new BigDecimal("0"));
            result.SetSucceeded(false);
        }

        return result;
    }

    /**
     * Try to parse string to Short
     *
     * @param s string to parse
     * @return return Result object that contains parse result in boolean and parsed value. If not succeeded, value will be Short 0.
     */
    public static Result<Short> TryParseShort(String s) {
        Result<Short> result = new Result<>();

        try {
            result.SetValue(Short.parseShort(s));
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(Short.parseShort("0"));
            result.SetSucceeded(false);
        }

        return result;
    }

    /**
     * Try to parse string to Long
     *
     * @param s string to parse
     * @return return Result object that contains parse result in boolean and parsed value. If not succeeded, value will be Long 0.
     */
    public static Result<Long> TryParseLong(String s) {
        Result<Long> result = new Result<>();

        try {
            result.SetValue(Long.parseLong(s));
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(Long.parseLong("0"));
            result.SetSucceeded(false);
        }

        return result;
    }

    /**
     * Try to parse string to Byte
     *
     * @param s string to parse
     * @return return Result object that contains parse result in boolean and parsed value. If not succeeded, value will be Byte 0.
     */
    public static Result<Byte> TryParseByte(String s) {
        Result<Byte> result = new Result<>();

        try {
            result.SetValue(Byte.parseByte(s));
            result.SetSucceeded(true);
        } catch (Exception e) {
            result.SetValue(Byte.parseByte("0"));
            result.SetSucceeded(false);
        }

        return result;
    }
}
