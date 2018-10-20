package com.example.kurtqin.test.Philips;

import org.json.JSONObject;

import java.util.HashMap;

public class ExchangeKeyRequest extends LocalRequest {
    private static final String SECURITY_PORTNAME = "security";
    private static final int SECURITY_PRODUCTID = 0;
    private String mRandomValue;

    public ExchangeKeyRequest(String arg11, int arg12, ResponseHandler arg13) {
        super(arg11, arg12, "security", 0, LocalRequestType.PUT, new HashMap(), arg13, null);
        this.mRandomValue = ByteUtil.generateRandomNum();
        this.mDataMap.put("diffie", EncryptionUtil.generateDiffieKey(this.mRandomValue));
    }

    public Response execute() {
        Response v7;
        String v10 = null;
        String v4 = super.execute().getResponseMessage();
        try {
            JSONObject v1 = new JSONObject(v4);
            String v5 = v1.getString("hellman");
            DICommLog.d("DISecurity", "result hellmam= " + v5 + "     Length:= " + v5.length());
            String v6 = v1.getString("key");
            DICommLog.d("DISecurity", "encrypted key= " + v6 + "    length:= " + v6.length());
            String v2 = EncryptionUtil.extractEncryptionKey(v5, v6, this.mRandomValue);
            DICommLog.i("DISecurity", "decryted key= " + v2);

            EncryptionKey.mEncryptionKey = v2;
            v7 = new Response(v2, 0, this.mResponseHandler);
        }
        catch(Exception v0) {
            DICommLog.e("DISecurity", "Exception during key exchange");
            v7 = new Response(v10, Error.REQUESTFAILED, this.mResponseHandler);
        }

        return v7;
    }
}
