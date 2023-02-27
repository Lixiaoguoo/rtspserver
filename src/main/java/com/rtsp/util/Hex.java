package com.rtsp.util;

public class Hex {
	 /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    /**
     * 空格分隔符
     */
    public static char SPACE = ' ';
    /**
     * 无分隔符
     */
    public static char NOTSEP = (char)0;
    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data
     *            byte[]
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, 0, data.length, true, SPACE);
    }
    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data
     *            byte[]
     * @param toLowerCase
     *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data, int offset, int len, boolean toLowerCase) {
    	 return encodeHex(data, offset, len, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, SPACE);
    }
    public static char[] encodeHex(byte[] data, int offset, int len, boolean toLowerCase, char separator) {
        return encodeHex(data, offset, len, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, separator);
    }
    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data
     *            byte[]
     * @param toDigits
     *            用于控制输出的char[]
     * @return 十六进制char[]
     */
    protected static char[] encodeHex(byte[] data, int offset, int len, char[] toDigits, char separator) {
    	char[] out;
    	if(separator == 0)
        	out = new char[len * 2];
    	else
    		out = new char[len * 3];
        // two characters form the hex value.
        for (int i = offset, j = 0; i < (offset + len); i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
            if(separator != 0)
            	out[j++] = ' ';
        }
        return out;
    }
    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data
     *            byte[]
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, 0, data.length, true, SPACE);
    }
    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data
     *            byte[]
     * @param toLowerCase
     *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data, int offset, int len, boolean toLowerCase) {
    	return encodeHexStr(data, offset, len, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, SPACE);
    }
    public static String encodeHexStr(byte[] data, int offset, int len, boolean toLowerCase, char separator) {
        return encodeHexStr(data, offset, len, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, separator);
    }
    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data
     *            byte[]
     * @param toDigits
     *            用于控制输出的char[]
     * @return 十六进制String
     */
    protected static String encodeHexStr(byte[] data, int offset, int len, char[] toDigits, char separator) {
        return new String(encodeHex(data, offset, len, toDigits, separator));
    }
    /**
     * 将十六进制字符数组转换为字节数组
     *
     * @param data
     *            十六进制char[]
     * @return byte[]
     * @throws RuntimeException
     *             如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
     */
    public static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }
        byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; ) {
            int f = toDigit(data[j]) << 4;
            j++;
            f = f | toDigit(data[j]);
            j++;
            out[i] = (byte) (f & 0xFF);
            i++;
        }
        return out;
    }
    /**
     * 将十六进制字符转换成一个整数
     */
    public static int toDigit(char ch) {
    	if((ch >= '0') && (ch <= '9'))
    		return ch - '0';
    	else if((ch >= 'a') && (ch <= 'f'))
    		return ch - 'a' + 10;
    	else if((ch >= 'A') && (ch <= 'F'))
    		return ch - 'A' + 10;
        return 0;
    }
}
