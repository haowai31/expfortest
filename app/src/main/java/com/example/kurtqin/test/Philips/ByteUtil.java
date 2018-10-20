package com.example.kurtqin.test.Philips;

import android.util.Base64;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public class ByteUtil {
    public static final String GVALUE = "A4D1CBD5C3FD34126765A442EFB99905F8104DD258AC507FD6406CFF14266D31266FEA1E5C41564B777E690F5504F213160217B4B01B886A5E91547F9E2749F4D7FBD7D3B9A92EE1909D0D2263F80A76A6A24C087A091F531DBF0A0169B6A28AD662A4D18E73AFA32D779D5918D08BC8858F4DCEF97C2A24855E6EEB22B3B2E5";
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static final int MAX = 2147483647;
    public static final int MIN = 101;
    public static final String PVALUE = "B10B8F96A080E01DDE92DE5EAE5D54EC52C99FBCFB06A3C69A6A9DCA52D23B616073E28675A23D189838EF1E2EE652C013ECB4AEA906112324975C3CD49B83BFACCBDD7D90C4BD7098488E9C219A73724EFFD6FAE5644738FAA31A4FF55BCCC0A151AF5F0DC8B4BD45BF37DF365C1A65E68CFDA76D4DA708DF1FB2BC2E4A4371";
    public static final int RANDOM_BYTE_ARR_SIZE = 2;

    static {
//        HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    }

    public ByteUtil() {
        super();
    }

    public static byte[] addRandomBytes(byte[] arg6) {
        byte[] v0;
        if(arg6 == null) {
            v0 = null;
        }
        else {
            byte[] v2 = ByteUtil.getRandomByteArray(2);
            int v1 = arg6.length;
            int v3 = v2.length;
            v0 = new byte[v1 + v3];
            System.arraycopy(v2, 0, v0, 0, v3);
            System.arraycopy(arg6, 0, v0, v3, v1);
        }

        return v0;
    }

    public static String bytesToCapitalizedHex(byte[] arg6) {
        char[] v0 = new char[arg6.length * 2];
        int v1;
        for(v1 = 0; v1 < arg6.length; ++v1) {
            int v2 = arg6[v1] & 255;
            v0[v1 * 2] = ByteUtil.HEX_ARRAY[v2 >>> 4];
            v0[v1 * 2 + 1] = ByteUtil.HEX_ARRAY[v2 & 15];
        }

        return new String(v0);
    }

    public static byte[] decodeFromBase64(String arg3) {
        byte[] v0 = null;
        if(arg3 != null) {
            v0 = Base64.decode(arg3.getBytes(Charset.defaultCharset()), 0);
        }

        return v0;
    }

    public static String encodeToBase64(byte[] arg2) throws Exception {
        String v0 = null;
        if(arg2 != null && arg2.length > 0) {
            v0 = Base64.encodeToString(arg2, 0);
        }

        return v0;
    }

    public static String generateRandomNum() {
        return String.valueOf(new Random().nextInt(2147483546) + 102);
    }

    public static byte[] getRandomByteArray(int arg2) {
        byte[] v1 = new byte[arg2];
        new Random().nextBytes(v1);
        return v1;
    }

    public static byte[] hexToBytes(String arg5) {
        int v1 = arg5.length() / 2;
        byte[] v2 = new byte[v1];
        int v0;
        for(v0 = 0; v0 < v1; ++v0) {
            v2[v0] = Integer.valueOf(arg5.substring(v0 * 2, v0 * 2 + 2), 16).byteValue();
        }

        return v2;
    }

    public static byte[] removeRandomBytes(byte[] arg3) {
        byte[] v0 = arg3 == null || arg3.length < 3 ? arg3 : Arrays.copyOfRange(arg3, 2, arg3.length);
        return v0;
    }
}
