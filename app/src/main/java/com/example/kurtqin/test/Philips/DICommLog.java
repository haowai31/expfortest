package com.example.kurtqin.test.Philips;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class DICommLog {
    public static final String APPLIANCE = "Appliance";
    public static final String APPLIANCE_MANAGER = "ApplianceManager";
    public static final String APP_START_UP = "AppStartUp";
    public static final String CPPCONTROLLER = "CppController";
    public static final String CPPDISCHELPER = "CppDiscoveryHelper";
    public static final String DATABASE = "DatabaseAir";
    public static final String DEVICEPORT = "DevicePort";
    public static final String DISCOVERY = "DiscoveryManager";
    public static final String FIRMWAREPORT = "FirmwarePort";
    public static final String ICPCLIENT = "IcpClient";
    public static final String INDOOR_RDCP = "IndoorRdcp";
    public static final String KPS = "KPS";
    public static final String LOCALREQUEST = "LocalRequest";
    public static final String LOCAL_SUBSCRIPTION = "LocalSubscription";
    public static final String NETWORKMONITOR = "NetworkMonitor";
    public static final String PAIRING = "Pairing";
    public static final String PAIRINGPORT = "PairingPort";
    public static final String PARSER = "DataParser";
    public static final String REMOTEREQUEST = "RemoteRequest";
    public static final String REMOTE_SUBSCRIPTION = "RemoteSubscription";
    public static final String REQUESTQUEUE = "RequestQueue";
    public static final String SCHEDULELISTPORT = "ScheduleListPort";
    public static final String SECURITY = "DISecurity";
    public static final String SSDP = "Ssdp";
    public static final String SSDPHELPER = "SsdpHelper";
    public static final String SUBSCRIPTION = "Subscription";
    public static final String UDP = "UDPSocket";
    public static final String UDPRECEIVER = "UdpEventReceiver";
    public static final String WIFI = "WifiNetworks ";
    public static final String WIFIPORT = "WifiPort";
    public static final String WIFIUIPORT = "WifiUIPort";
    private static boolean isLoggingEnabled;
    private static boolean isSaveToFileEnabled;
    public static BufferedWriter out;

    static {
        DICommLog.isLoggingEnabled = true;
        DICommLog.isSaveToFileEnabled = false;
    }

    public DICommLog() {
        super();
    }

    private static void createFileOnDevice(Boolean arg9) throws IOException {
        if(DICommLog.isSaveToFileEnabled) {
            File v4 = Environment.getExternalStorageDirectory();
            if((v4.canWrite()) && (DICommLog.isExternalStorageWritable())) {
                File v1 = new File(v4 + "/com.philips.purair/logs");
                if(!v1.exists()) {
                    v1.mkdirs();
                }

                DICommLog.out = new BufferedWriter(new FileWriter(new File(v1.getPath(), "Log.txt"), arg9.booleanValue()));
                Date v0 = new Date();
                DICommLog.out.write("Logged at" + String.valueOf(v0.getHours() + ":" + v0.getMinutes() + ":" + v0.getSeconds() + "\n"));
            }
        }
    }

    public static void d(String arg2, String arg3) {
        if(DICommLog.isLoggingEnabled) {
            Log.d(arg2, arg3);
            DICommLog.writeToFile(arg2 + " : " + arg3);
        }
    }

    public static void disableLogging() {
        DICommLog.isLoggingEnabled = false;
    }

    public static void e(String arg2, String arg3) {
        if(DICommLog.isLoggingEnabled) {
            Log.e(arg2, arg3);
            DICommLog.writeToFile(arg2 + " : " + arg3);
        }
    }

    public static void enableLogging() {
        DICommLog.isLoggingEnabled = true;
    }

    public static void finishLoggingToFile() {
        if(DICommLog.isSaveToFileEnabled) {
            try {
                DICommLog.out.close();
            }
            catch(IOException v0) {
                v0.printStackTrace();
            }
        }
    }

    public static void i(String arg2, String arg3) {
        if(DICommLog.isLoggingEnabled) {
            Log.i(arg2, arg3);
            DICommLog.writeToFile(arg2 + " : " + arg3);
        }
    }

    public static void initLoggingToFile() {
        if(DICommLog.isSaveToFileEnabled) {
            try {
                DICommLog.createFileOnDevice(Boolean.valueOf(true));
            }
            catch(IOException v0) {
                v0.printStackTrace();
            }
        }
    }

    private static boolean isExternalStorageWritable() {
        boolean v1 = false;
        if((DICommLog.isSaveToFileEnabled) && ("mounted".equals(Environment.getExternalStorageState()))) {
            v1 = true;
        }

        return v1;
    }

    public static boolean isLoggingEnabled() {
        return DICommLog.isLoggingEnabled;
    }

    public static void v(String arg2, String arg3) {
        if(DICommLog.isLoggingEnabled) {
            Log.v(arg2, arg3);
            DICommLog.writeToFile(arg2 + " : " + arg3);
        }
    }

    public static void w(String arg2, String arg3) {
        if(DICommLog.isLoggingEnabled) {
            Log.w(arg2, arg3);
            DICommLog.writeToFile(arg2 + " : " + arg3);
        }
    }

    private static void writeToFile(String arg5) {
        if((DICommLog.isSaveToFileEnabled) && (Environment.getExternalStorageDirectory().canWrite()) && (DICommLog.isExternalStorageWritable())) {
            try {
                DICommLog.out.write(arg5 + "\n");
            }
            catch(IOException v0) {
                v0.printStackTrace();
            }
        }
    }
}


