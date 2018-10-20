package com.example.kurtqin.test.Philips;

import java.nio.charset.Charset;

public class DISecurity {

    public DISecurity() {
        super();
    }

    public String decryptData(String arg12) {
        String v4 = null;
        String v3;
        String v7 = null;
        DICommLog.i("DISecurity", "decryptData data:  " + arg12);

        String v6 = EncryptionKey.mEncryptionKey;
        DICommLog.i("DISecurity", "Decryption - Key   " + v6);
        v3 = null;
        if(arg12 != null && !arg12.isEmpty()) {
            if(v6 != null && !v6.isEmpty()) {
                arg12 = arg12.trim();
                try {
                    v4 = new String(ByteUtil.removeRandomBytes(EncryptionUtil.aesDecryptData(ByteUtil.decodeFromBase64(arg12.trim()), v6)), Charset.defaultCharset());
                }
                catch(Exception v5) {
                    v5.printStackTrace();
                    DICommLog.i("DISecurity", "Failed to decrypt data");
                }

                try {
                    DICommLog.i("DISecurity", "Decrypted data: " + v4);
                    v3 = v4;
                }
                catch(Exception v5) {
                    v3 = v4;
                }

                return v3;
            }

            DICommLog.i("DISecurity", "Did not decrypt data - key is null");
            DICommLog.i("DISecurity", "Failed to decrypt data");
            v3 = v7;
        }
        else {
            DICommLog.i("DISecurity", "Did not decrypt data - data is null");
            v3 = v7;
        }

        return v3;
    }

    public String encryptData(String arg8) {
        String v2 = null;
        String v3 = EncryptionKey.mEncryptionKey;
        if(v3 != null && !v3.isEmpty()) {
            if(arg8 != null && !arg8.isEmpty()) {
                try {
                    v2 = ByteUtil.encodeToBase64(EncryptionUtil.aesEncryptData(arg8, v3));
                    DICommLog.i("DISecurity", "Encrypted data: " + v2);
                }
                catch(Exception v0) {
                    v0.printStackTrace();
                    DICommLog.i("DISecurity", "Failed to encrypt data with key - Error: " + v0.getMessage());
                }

                return v2;
            }

            DICommLog.i("DISecurity", "Did not encrypt data - Data is null or Empty");
        }
        else {
            DICommLog.i("DISecurity", "Did not encrypt data - Key is null or Empty");
        }

        return v2;
    }

}
