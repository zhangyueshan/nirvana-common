package com.nirvana.common.utils;

/**
 * HEX字符串工具类。
 */
public class HexUtils {

    private static final String HEX_CHARS = "0123456789abcdef";

    private HexUtils() {
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            sb.append(HexUtils.HEX_CHARS.charAt(aB >>> 4 & 0x0F));
            sb.append(HexUtils.HEX_CHARS.charAt(aB & 0x0F));
        }
        return sb.toString();
    }

    public static byte[] toByteArray(String hex) {
        byte[] buf = new byte[hex.length() / 2];
        int j = 0;
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) ((Character.digit(hex.charAt(j++), 16) << 4) | Character
                    .digit(hex.charAt(j++), 16));
        }
        return buf;
    }

}
