package com.example.kurtqin.test.Philips;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
    public EncryptionUtil() {
        super();
    }

    public static byte[] aesDecryptData(byte[] arg9, String arg10) throws Exception {
        int v8 = 16;
        Cipher v0 = Cipher.getInstance("AES/CBC/PKCS7Padding");
        byte[] v5 = new BigInteger(arg10, v8).toByteArray();
        byte[] v3 = v5[0] == 0 ? Arrays.copyOfRange(v5, 1, 17) : Arrays.copyOf(v5, v8);
        v0.init(2, new SecretKeySpec(v3, "AES"), new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
        return v0.doFinal(arg9);
    }

    public static byte[] aesEncryptData(String arg10, String arg11) throws Exception {
        int v8 = 16;
        Cipher v0 = Cipher.getInstance("AES/CBC/PKCS7Padding");
        byte[] v6 = new BigInteger(arg11, v8).toByteArray();
        byte[] v4 = v6[0] == 0 ? Arrays.copyOfRange(v6, 1, 17) : Arrays.copyOf(v6, v8);
        v0.init(1, new SecretKeySpec(v4, "AES"), new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
        return v0.doFinal(ByteUtil.addRandomBytes(arg10.getBytes(Charset.defaultCharset())));
    }

    public static String extractEncryptionKey(String arg4, String arg5, String arg6) throws Exception {
        return ByteUtil.bytesToCapitalizedHex(EncryptionUtil.aesDecryptData(ByteUtil.hexToBytes(arg5), EncryptionUtil.getEvenNumberSecretKey(EncryptionUtil.generateSecretKey(arg4, arg6))));
    }

    public static String generateDiffieKey(String arg5) {
        return ByteUtil.bytesToCapitalizedHex(new BigInteger("A4D1CBD5C3FD34126765A442EFB99905F8104DD258AC507FD6406CFF14266D31266FEA1E5C41564B777E690F5504F213160217B4B01B886A5E91547F9E2749F4D7FBD7D3B9A92EE1909D0D2263F80A76A6A24C087A091F531DBF0A0169B6A28AD662A4D18E73AFA32D779D5918D08BC8858F4DCEF97C2A24855E6EEB22B3B2E5", 16).modPow(new BigInteger(arg5), new BigInteger("B10B8F96A080E01DDE92DE5EAE5D54EC52C99FBCFB06A3C69A6A9DCA52D23B616073E28675A23D189838EF1E2EE652C013ECB4AEA906112324975C3CD49B83BFACCBDD7D90C4BD7098488E9C219A73724EFFD6FAE5644738FAA31A4FF55BCCC0A151AF5F0DC8B4BD45BF37DF365C1A65E68CFDA76D4DA708DF1FB2BC2E4A4371", 16)).toByteArray());
    }

    public static String generateSecretKey(String arg5, String arg6) {
        return ByteUtil.bytesToCapitalizedHex(new BigInteger(arg5, 16).modPow(new BigInteger(arg6), new BigInteger("B10B8F96A080E01DDE92DE5EAE5D54EC52C99FBCFB06A3C69A6A9DCA52D23B616073E28675A23D189838EF1E2EE652C013ECB4AEA906112324975C3CD49B83BFACCBDD7D90C4BD7098488E9C219A73724EFFD6FAE5644738FAA31A4FF55BCCC0A151AF5F0DC8B4BD45BF37DF365C1A65E68CFDA76D4DA708DF1FB2BC2E4A4371", 16)).toByteArray());
    }

    public static String getEvenNumberSecretKey(String arg4) {
        String v1 = arg4;
        if(arg4 != null) {
            v1 = arg4.length() % 2 == 0 ? arg4 : "0" + arg4;
        }

        return v1;
    }
}
