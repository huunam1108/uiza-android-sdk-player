package vn.uiza.utils.util;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import vn.uiza.utils.constant.MemoryConstants;
import vn.uiza.utils.constant.TimeConstants;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class ConvertUtilsTest {

    @Test
    public void bytes2HexString() {
        byte[] bytes = "Bytes to Hex String".getBytes();
        String hexString = "427974657320746F2048657820537472696E67";
        assertEquals(ConvertUtils.bytes2HexString(bytes), hexString);
    }

    @Test
    public void hexString2Bytes() {
        String hexString = "48657820537472696E6720746F204279746573";
        byte[] bytes = "Hex String to Bytes".getBytes();
        assertArrayEquals(ConvertUtils.hexString2Bytes(hexString), bytes);
    }

    @Test
    public void hex2Dec() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        char[] hexes = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
                'D', 'E', 'F'};
        int[] decs = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        Method hex2Dec = ConvertUtils.class.getDeclaredMethod("hex2Dec", char.class);
        hex2Dec.setAccessible(true);
        for (int i = 0; i < hexes.length; i++) {
            char hex = hexes[i];
            int dec = decs[i];
            assertEquals(hex2Dec.invoke(ConvertUtils.class, hex), dec);
        }
    }

    @Test
    public void chars2Bytes() {
        char[] chars = "Chars to Bytes".toCharArray();
        byte[] bytes = new byte[]{0x43, 0x68, 0x61, 0x72, 0x73, 0x20, 0x74, 0x6f, 0x20, 0x42, 0x79,
                0x74, 0x65, 0x73};
        assertArrayEquals(ConvertUtils.chars2Bytes(chars), bytes);
    }

    @Test
    public void bytes2Chars() {
        byte[] bytes = new byte[]{0x42, 0x79, 0x74, 0x65, 0x73, 0x20, 0x74, 0x6f, 0x20, 0x43, 0x68,
                0x61, 0x72, 0x73};
        char[] chars = "Bytes to Chars".toCharArray();
        assertArrayEquals(ConvertUtils.bytes2Chars(bytes), chars);
    }

    @Test
    public void memorySize2Byte() {
        assertEquals(ConvertUtils.memorySize2Byte(-1, MemoryConstants.BYTE), -1);
        assertEquals(ConvertUtils.memorySize2Byte(-1, MemoryConstants.KB), -1);
        assertEquals(ConvertUtils.memorySize2Byte(-1, MemoryConstants.MB), -1);
        assertEquals(ConvertUtils.memorySize2Byte(-1, MemoryConstants.GB), -1);
        assertEquals(ConvertUtils.memorySize2Byte(2, MemoryConstants.BYTE), 2);
        assertEquals(ConvertUtils.memorySize2Byte(2, MemoryConstants.KB), 2048L);
        assertEquals(ConvertUtils.memorySize2Byte(2, MemoryConstants.MB), 2097152L);
        assertEquals(ConvertUtils.memorySize2Byte(2, MemoryConstants.GB), 2147483648L);
    }

    @Test
    public void byte2MemorySize() {
        assertEquals(ConvertUtils.byte2MemorySize(-1, MemoryConstants.BYTE), -1D);
        assertEquals(ConvertUtils.byte2MemorySize(-1, MemoryConstants.KB), -1D);
        assertEquals(ConvertUtils.byte2MemorySize(-1, MemoryConstants.MB), -1D);
        assertEquals(ConvertUtils.byte2MemorySize(-1, MemoryConstants.GB), -1D);
        assertEquals(ConvertUtils.byte2MemorySize(2, MemoryConstants.BYTE), 2D);
        assertEquals(ConvertUtils.byte2MemorySize(2048L, MemoryConstants.KB), 2D);
        assertEquals(ConvertUtils.byte2MemorySize(2097152L, MemoryConstants.MB), 2D);
        assertEquals(ConvertUtils.byte2MemorySize(2147483648L, MemoryConstants.GB), 2D);
    }

    @Test
    public void byte2FitMemorySize() {
        assertEquals("256.001B", ConvertUtils.byte2FitMemorySize(256));
        assertEquals("2.001KB", ConvertUtils.byte2FitMemorySize(2048));
        assertEquals("2.001MB", ConvertUtils.byte2FitMemorySize(2097152));
        assertEquals("2.001GB", ConvertUtils.byte2FitMemorySize(2147483648L));
    }

    @Test
    public void timeSpan2Millis() {
        assertEquals(ConvertUtils.timeSpan2Millis(2, TimeConstants.DAY), 2 * TimeConstants.DAY);
        assertEquals(ConvertUtils.timeSpan2Millis(2, TimeConstants.HOUR), 2 * TimeConstants.HOUR);
        assertEquals(ConvertUtils.timeSpan2Millis(2, TimeConstants.MIN), 2 * TimeConstants.MIN);
        assertEquals(ConvertUtils.timeSpan2Millis(2, TimeConstants.SEC), 2 * TimeConstants.SEC);
        assertEquals(ConvertUtils.timeSpan2Millis(2, TimeConstants.MSEC), 2 * TimeConstants.MSEC);
    }

    @Test
    public void millis2TimeSpan() {
        assertEquals(ConvertUtils.millis2TimeSpan(2 * TimeConstants.DAY, TimeConstants.DAY), 2);
        assertEquals(ConvertUtils.millis2TimeSpan(2 * TimeConstants.HOUR, TimeConstants.HOUR), 2);
        assertEquals(ConvertUtils.millis2TimeSpan(2 * TimeConstants.MIN, TimeConstants.MIN), 2);
        assertEquals(ConvertUtils.millis2TimeSpan(2 * TimeConstants.SEC, TimeConstants.SEC), 2);
        assertEquals(ConvertUtils.millis2TimeSpan(2 * TimeConstants.MSEC, TimeConstants.MSEC), 2);
    }

    @Test
    public void bytes2Bits() {
        byte[] bytes = "Bytes to Bits".getBytes();
        String bits = "0100001001111001011101000110010101110011001000000111010001101111001000000" +
                "1000010011010010111010001110011";
        assertEquals(ConvertUtils.bytes2Bits(bytes), bits);
    }

    @Test
    public void bits2Bytes() {
        byte[] bytes = "Bits to Bytes".getBytes();
        String bits = "0100001001101001011101000111001100100000011101000110111100100000010000100" +
                "1111001011101000110010101110011";
        assertArrayEquals(ConvertUtils.bits2Bytes(bits), bytes);
    }

    @Test
    public void inputStream2Bytes_bytes2InputStream() {
        byte[] bytes = "Input Stream and Bytes".getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(bytes,
                ConvertUtils.inputStream2Bytes(ConvertUtils.bytes2InputStream(bytes)));
    }

    @Test
    public void outputStream2Bytes_bytes2OutputStream() {
        byte[] bytes = "Output Stream and Bytes".getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(bytes,
                ConvertUtils.outputStream2Bytes(ConvertUtils.bytes2OutputStream(bytes)));
    }

    @Test
    public void inputStream2String_string2InputStream() {
        String string = "Input Stream and String";
        String charset = "UTF-8";
        assertEquals(string, ConvertUtils.inputStream2String(
                ConvertUtils.string2InputStream(string, charset), charset));
    }


    @Test
    public void outputStream2String_string2OutputStream() {
        String string = "Input Stream and String";
        String charset = "UTF-8";
        assertEquals(string, ConvertUtils.outputStream2String(
                ConvertUtils.string2OutputStream(string, charset), charset));
    }
}
