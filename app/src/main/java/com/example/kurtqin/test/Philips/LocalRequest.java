package com.example.kurtqin.test.Philips;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;

public class LocalRequest extends Request {
    public static final String BASEURL_PORTS = "http://%s/di/v%d/products/%d/%s";
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int GETWIFI_TIMEOUT = 3000;
    private final DISecurity mDISecurity;
    private final int mRequestType;
    private final String mUrl;

    public LocalRequest(String arg2, int arg3, String arg4, int arg5, int arg6, Map arg7, ResponseHandler arg8, DISecurity arg9) {
        super(arg7, arg8);
        this.mUrl = this.createPortUrl(arg2, arg3, arg4, arg5);
        this.mRequestType = arg6;
        this.mDISecurity = arg9;
    }

    private OutputStreamWriter appendDataToRequestIfAvailable(HttpURLConnection arg5) throws IOException {
        OutputStreamWriter v1;
        String v0 = this.createDataToSend(this.mDataMap);
        if(v0 == null) {
            v1 = null;
        }
        else {
            if(Build.VERSION.SDK_INT <= 10) {
                arg5.setDoOutput(true);
            }

            v1 = new OutputStreamWriter(arg5.getOutputStream(), Charset.defaultCharset());
            v1.write(v0);
            v1.flush();
        }

        return v1;
    }

    private static final void closeAllConnections(InputStream arg1, OutputStreamWriter arg2, HttpURLConnection arg3) {
        if(arg1 != null) {
            try {
                arg1.close();
            }
            catch(IOException v0) {
                v0.printStackTrace();
            }
        }

        if(arg2 != null) {
            try {
                arg2.close();
            }
            catch(IOException v0) {
                v0.printStackTrace();
            }
        }

        if(arg3 != null) {
            arg3.disconnect();
        }
    }

    private static String convertInputStreamToString(InputStream arg6) throws IOException {
        String v5;
        if(arg6 == null) {
            v5 = "";
        }
        else {
            InputStreamReader v3 = new InputStreamReader(arg6, "UTF-8");
            char[] v0 = new char[1024];
            StringBuilder v4 = new StringBuilder(1024);
            while(true) {
                int v1 = ((Reader)v3).read(v0);
                if(v1 <= 0) {
                    break;
                }

                v4.append(v0, 0, v1);
            }

            v5 = v4.toString();
        }

        return v5;
    }

    @SuppressLint(value={"NewApi"}) private static HttpURLConnection createConnection(URL arg4, String arg5, int arg6, int arg7) throws IOException {
        URLConnection v2_1;
        URLConnection v0 = arg4.openConnection();

        ((HttpURLConnection)v0).setRequestProperty("content-type", "application/json");
        ((HttpURLConnection)v0).setRequestMethod(arg5);
        if(arg6 != -1) {
            ((HttpURLConnection)v0).setConnectTimeout(arg6);
        }

        v2_1 = v0;

        return ((HttpURLConnection)v2_1);
    }

    private String createDataToSend(Map arg5) {
        String v0;
        if(arg5 == null || arg5.size() <= 0) {
            v0 = null;
        }
        else {
            v0 = Request.convertKeyValuesToJson(arg5);
            DICommLog.i("LocalRequest", "Data to send: " + v0);
            if(this.mDISecurity != null) {
                v0 = this.mDISecurity.encryptData(v0);
            }
            else {
                DICommLog.i("LocalRequest", "Not encrypting data");
            }
        }

        return v0;
    }

    private String createPortUrl(String arg5, int arg6, String arg7, int arg8) {
        return String.format("http://%s/di/v%d/products/%d/%s", arg5, Integer.valueOf(arg6), Integer.valueOf(arg8), arg7);
    }

    private String decryptData(String arg2) {
        if(this.mDISecurity != null) {
            arg2 = this.mDISecurity.decryptData(arg2);
        }

        return arg2;
    }

    public Response execute() {
        Response v7_1 = null;
        HttpURLConnection v0 = null;
        DICommLog.d("LocalRequest", "Start request LOCAL");
        DICommLog.i("LocalRequest", "Url: " + this.mUrl + ", Requesttype: " + this.mRequestType);
        InputStream v2 = null;
        OutputStreamWriter v3 = null;
        int v4 = -1;
        try {
            String method = null;
            if(this.mRequestType == LocalRequestType.DELETE) {
                method = "DELETE";
            } else if(this.mRequestType == LocalRequestType.GET) {
                method = "GET";
            } else if(this.mRequestType == LocalRequestType.PUT) {
                method = "PUT";
            } else if(this.mRequestType == LocalRequestType.POST) {
                method = "POST";
            }
            v0 = LocalRequest.createConnection(new URL(this.mUrl), method, CONNECTION_TIMEOUT, GETWIFI_TIMEOUT);
            if(v0 != null) {
                if(this.mRequestType == LocalRequestType.PUT || this.mRequestType == LocalRequestType.POST) {
                    if(this.mDataMap != null && !this.mDataMap.isEmpty()) {
                        v3 = this.appendDataToRequestIfAvailable(v0);
                    }
                }
                else if(this.mRequestType == LocalRequestType.DELETE) {
                    this.appendDataToRequestIfAvailable(v0);
                }

                v0.connect();
                v4 = v0.getResponseCode();

                if(v4 == 200) {
                    v2 = v0.getInputStream();
                    v7_1 = this.handleHttpOk(v2);

                    LocalRequest.closeAllConnections(v2, v3, v0);
                    DICommLog.d("LocalRequest", "Stop request LOCAL - responsecode: " + v4);
                }
                else if(v4 == 400) {
                    v2 = v0.getErrorStream();
                    v7_1 = this.handleBadRequest(v2);

                    LocalRequest.closeAllConnections(v2, v3, v0);
                    DICommLog.d("LocalRequest", "Stop request LOCAL - responsecode: " + v4);
                }
                else if(v4 == 502) {
                    v7_1 = new Response(null, Error.BADGATEWAY, this.mResponseHandler);

                    LocalRequest.closeAllConnections(v2, v3, v0);
                    DICommLog.d("LocalRequest", "Stop request LOCAL - responsecode: " + v4);
                }
                else {
                    v2 = v0.getErrorStream();
                    String v5 = LocalRequest.convertInputStreamToString(v2);
                    DICommLog.e("LocalRequest", "REQUESTFAILED - " + v5);
                    v7_1 = new Response(v5, Error.REQUESTFAILED, this.mResponseHandler);


                    LocalRequest.closeAllConnections(v2, v3, v0);
                    DICommLog.d("LocalRequest", "Stop request LOCAL - responsecode: " + v4);
                }

                return v7_1;
            }

            DICommLog.e("LocalRequest", "Request failed - no wificonnection available");
            v7_1 = new Response(null, Error.NOWIFIAVAILABLE, this.mResponseHandler);
        }
        catch(Throwable v7) {
            LocalRequest.closeAllConnections(v2, v3, v0);
            DICommLog.d("LocalRequest", "Stop request LOCAL - responsecode: " + v4);
        }

        LocalRequest.closeAllConnections(v2, v3, v0);
        DICommLog.d("LocalRequest", "Stop request LOCAL - responsecode: " + v4);
        return v7_1;
    }

    private Response handleBadRequest(InputStream arg5) throws IOException {
        String v0 = LocalRequest.convertInputStreamToString(arg5);
        DICommLog.e("LocalRequest", "BAD REQUEST - " + v0);
        if(this.mDISecurity != null) {
            DICommLog.e("LocalRequest", "Request not properly encrypted - notifying listener");
        }

        return new Response(v0, Error.BADREQUEST, this.mResponseHandler);
    }

    private Response handleHttpOk(InputStream arg7) throws IOException {
        Response v2;
        String v5 = null;
        String v0 = LocalRequest.convertInputStreamToString(arg7);
        if(v0 == null) {
            DICommLog.e("LocalRequest", "Request failed - null reponse");
            v2 = new Response(v5, Error.REQUESTFAILED, this.mResponseHandler);
        }
        else {
            String v1 = this.decryptData(v0);
            if(v1 == null) {
                DICommLog.e("LocalRequest", "Request failed - failed to decrypt");
                v2 = new Response(v5, Error.REQUESTFAILED, this.mResponseHandler);
            }
            else {
                DICommLog.i("LocalRequest", "Received data: " + v1);
                v2 = new Response(v1, Error.BADGATEWAY, this.mResponseHandler);
            }
        }

        return v2;
    }
}