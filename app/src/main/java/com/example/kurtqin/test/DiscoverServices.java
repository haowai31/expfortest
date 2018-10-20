package com.example.kurtqin.test;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

public class DiscoverServices {
    private final static ExecutorService executorService = Executors.newCachedThreadPool();
    public final static String REMOTE_TYPE = "_sample._tcp.local.";
    public final static String TAG = "DiscoverServices";



    static class SampleListener implements ServiceListener, ServiceTypeListener {
        private Handler mHandler;
        private StringBuilder stringBuilder = new StringBuilder();
        private int count=0;

        public SampleListener(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void serviceAdded(ServiceEvent event) {
            Log.e(TAG,"Service add : " + event.getName() + "." + event.getType());
            Log.e(TAG,"Service add : " + event.toString());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            Log.e(TAG,"Service removed : " + event.getName() + "." + event.getType());
            Log.e(TAG,"Service removed : " + event.toString());
        }

        @Override
        public void serviceResolved(final ServiceEvent event) {
            ServiceInfo serviceInfo;
            if (event != null) {
                count++;
                serviceInfo = event.getInfo();
                System.out.println("Service removed : " + serviceInfo.toString());
                stringBuilder.append(""+count+": name:"+ serviceInfo.getName() + " type: " + serviceInfo.getType() +" version="+serviceInfo.getPropertyString("version")+" uuid="+serviceInfo.getPropertyString("uuid")+" roomNumber="+serviceInfo.getPropertyString("roomNumber")+ " WanIpAddress="+serviceInfo.getPropertyString("WanIpAddress")+" BoxIpAddress="+serviceInfo.getPropertyString("BoxIpAddress")+"\n");
            }
            Log.e(TAG,"Service resolved: " + event.getInfo());
        }

        @Override
        public void serviceTypeAdded(ServiceEvent event) {
            Log.e(TAG,"TYPE: " + event.getType());
            Log.e(TAG,"TYPE: " + event.toString());
        }

        @Override
        public void subTypeForServiceTypeAdded(ServiceEvent event) {
            Log.e(TAG, "TYPE: " + event.getType());
        }

    }

    private static JmDNS jmDNS;
    public static void start(final Context context, final Handler handler) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                if(jmDNS != null) {
                    Log.e(TAG, "JmDNS is starting, return");

                    DiscoverServices.listDevices("_workstation._tcp.local.");
                    DiscoverServices.listDevices("_teamviewer._tcp.local.");
                    DiscoverServices.listDevices("_companion-link._tcp.local.");

                    return;
                }

                try {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    InetAddress deviceIpAddress = getDeviceIpAddress(wifiManager);

                    WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock("ListServices");
                    multicastLock.setReferenceCounted(true);
                    multicastLock.acquire();

                    Log.e(TAG, "JmDNS.create");

                    jmDNS = JmDNS.create(deviceIpAddress,"FindDevice1");
                    jmDNS.addServiceTypeListener(new SampleListener(handler));
                    jmDNS.addServiceListener("_workstation._tcp.local.", new SampleListener(handler));
                    jmDNS.addServiceListener("_teamviewer._tcp.local.", new SampleListener(handler));
                    jmDNS.addServiceListener("_companion-link._tcp.local.", new SampleListener(handler));

                    Log.e(TAG, "addServiceTypeListener");



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });

    }


    public static void listDevices(String type) {

            ServiceInfo[] list = jmDNS.list(type);
            for (ServiceInfo item : list) {
                System.out.println("Service removed : " + item.toString());

            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }


    private static  boolean close=false;
    public static void close(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                if (jmDNS != null) {
                    try {
                        close=true;
                        jmDNS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private static InetAddress getDeviceIpAddress(WifiManager wifi) {
        InetAddress result = null;
        try {
            WifiInfo wifiinfo = wifi.getConnectionInfo();
            int int_address = wifiinfo.getIpAddress();
            byte[] byte_address = new byte[] { (byte) (int_address & 0xff), (byte) (int_address >> 8 & 0xff), (byte) (int_address >> 16 & 0xff), (byte) (int_address >> 24 & 0xff) };
            result = InetAddress.getByAddress(byte_address);

            Log.e(TAG, result.toString());

        } catch (UnknownHostException ex) {
            Log.w(TAG, String.format("getDeviceIpAddress Error: %s", ex.getMessage()));
        }

        return result;
    }
}
