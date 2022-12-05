package com.szmsd.common.core.support;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.util.Assert;

import java.math.BigDecimal;

public class ColorOutput {

    public static String output(AnsiColor ansiColor, Object out) {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        Assert.notNull(out);
        String value = out.toString();
        if (out instanceof BigDecimal) {
            value = toPlainString((BigDecimal) out);
        }
        return AnsiOutput.toString(ansiColor, value);
    }

    public static String BLUE(Object out) {
        return output(AnsiColor.BLUE, out);
    }

    public static String MAGENTA(Object out) {
        return output(AnsiColor.MAGENTA, out);
    }

    public static String BLACK(Object out) {
        return output(AnsiColor.BLACK, out);
    }

    public static String BRIGHT_BLACK(Object out) {
        return output(AnsiColor.BRIGHT_BLACK, out);
    }

    public static String BRIGHT_BLUE(Object out) {
        return output(AnsiColor.BRIGHT_BLUE, out);
    }

    public static String BRIGHT_CYAN(Object out) {
        return output(AnsiColor.BRIGHT_CYAN, out);
    }

    public static String BRIGHT_GREEN(Object out) {
        return output(AnsiColor.BRIGHT_GREEN, out);
    }

    public static String BRIGHT_MAGENTA(Object out) {
        return output(AnsiColor.BRIGHT_MAGENTA, out);
    }

    public static String BRIGHT_RED(Object out) {
        return output(AnsiColor.BRIGHT_RED, out);
    }

    public static String BRIGHT_WHITE(Object out) {
        return output(AnsiColor.BRIGHT_WHITE, out);
    }

    public static String BRIGHT_YELLOW(Object out) {
        return output(AnsiColor.BRIGHT_YELLOW, out);
    }

    public static String CYAN(Object out) {
        return output(AnsiColor.CYAN, out);
    }

    public static String DEFAULT(Object out) {
        return output(AnsiColor.DEFAULT, out);
    }

    public static String GREEN(Object out) {
        return output(AnsiColor.GREEN, out);
    }

    public static String RED(Object out) {
        return output(AnsiColor.RED, out);
    }

    public static String WHITE(Object out) {
        return output(AnsiColor.WHITE, out);
    }

    public static String YELLOW(Object out) {
        return output(AnsiColor.YELLOW, out);
    }

    private static String toPlainString(BigDecimal decimal) {
        String decimalStr = decimal != null ? decimal.toPlainString() : null;
        if (decimalStr != null && decimalStr.indexOf(".") > 0) {
            decimalStr = decimalStr.replaceAll("0+?$", "");
            decimalStr = decimalStr.replaceAll("[.]$", "");
        }
        return decimalStr;
    }

}
