package com.example.kurtqin.test.Philips;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class Request {
    protected final Map mDataMap;
    protected final ResponseHandler mResponseHandler;

    public Request(Map arg1, ResponseHandler arg2) {
        super();
        this.mDataMap = arg1;
        this.mResponseHandler = arg2;
    }

    private static void appendStringArray(StringBuilder arg2, String[] arg3) {
        arg2.append("[");
        int v0;
        for(v0 = 0; v0 < arg3.length; ++v0) {
            arg2.append(Request.wrapIfNotJsonObject(arg3[v0]));
            if(v0 < arg3.length - 1) {
                arg2.append(",");
            }
        }

        arg2.append("]");
    }

    protected static String convertKeyValuesToJson(Map arg8) {
        String v6;
        if(arg8 == null || arg8.size() <= 0) {
            v6 = "{}";
        }
        else {
            StringBuilder v0 = new StringBuilder("{");
            Set v4 = arg8.keySet();
            int v2 = 1;
            Iterator v1 = v4.iterator();
            while(v1.hasNext()) {
                Object v3 = v1.next();
                Object v5 = arg8.get(v3);
                if((v5 instanceof String)) {
                    v0.append("\"").append(((String)v3)).append("\":").append(Request.wrapIfNotJsonObject(((String)v5)));
                }
                else if((v5 instanceof Integer)) {
                    v0.append("\"").append(((String)v3)).append("\":").append(v5);
                }
                else if((v5 instanceof Boolean)) {
                    v0.append("\"").append(((String)v3)).append("\":").append(v5);
                }
                else if((v5 instanceof String[])) {
                    v0.append("\"").append(((String)v3)).append("\":");
                    Request.appendStringArray(v0, ((String[])v5));
                }
                else if((v5 instanceof Map)) {
                    v0.append("\"").append(((String)v3)).append("\":").append(Request.convertKeyValuesToJson(((Map)v5)));
                }
                else {
                    v0.append("\"").append(((String)v3)).append("\":\"").append(v5).append("\"");
                }

                if(v2 < v4.size()) {
                    v0.append(",");
                }

                ++v2;
            }

            v0.append("}");
            v6 = v0.toString();
        }

        return v6;
    }

    public abstract Response execute();

    private static String wrapIfNotJsonObject(String arg3) {
        if(arg3 == null) {
            arg3 = "\"\"";
        }
        else if(!arg3.startsWith("{")) {
            arg3 = String.format("\"%s\"", arg3);
        }

        return arg3;
    }
}
