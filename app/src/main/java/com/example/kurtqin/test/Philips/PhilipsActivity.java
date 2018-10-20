package com.example.kurtqin.test.Philips;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.kurtqin.test.DiscoverServices;
import com.example.kurtqin.test.R;
import com.example.kurtqin.test.SnowApplication;

import com.example.kurtqin.test.Account.data.AccountInfo;
import com.example.kurtqin.test.Account.utils.CloudHelper;


import org.apache.http.conn.util.InetAddressUtils;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;

public class PhilipsActivity extends AppCompatActivity {
    Button button;
    Button discover_button;
    Button left_btn;
    Button right_btn;
    Button before_btn;
    Button behind_btn;
    String TAG = "PhilipsActivity";
    DISecurity s = null;

    /* 用于 udpReceiveAndTcpSend 的3个变量 */
    Socket socket = null;
    MulticastSocket ms = null;
    DatagramPacket dp;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_philip);
        button = (Button)findViewById(R.id.wifi_btn);
        discover_button = (Button)findViewById(R.id.discover_btn);
        left_btn = (Button)findViewById(R.id.left_btn);
        right_btn = (Button)findViewById(R.id.right_btn);
        before_btn = (Button)findViewById(R.id.before_btn);
        behind_btn = (Button)findViewById(R.id.behind_btn);
        s = new DISecurity();

        if(android.os.Build.VERSION.SDK_INT>9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        discover_button.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                disCover1();
            }});

        button.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                String Test3 = generateDiffieKey("123456");
                Log.e(TAG, Test3);

            }});

        left_btn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                String Test3 = startLeft("123456");

                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                stopLeft("123456");

            }});

        right_btn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                String Test3 = startRight("123456");

                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                stopLeft("123456");

            }});

        before_btn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                String Test3 = startFront("123456");

                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                stopLeft("123456");

            }});

        behind_btn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                String Test3 = startBehind("123456");

                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                stopLeft("123456");

            }});

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DiscoverServices.close();
    }

    private void disCover1() {
        SsdpClient client = SsdpClient.create();
        DiscoveryRequest all = SsdpRequest.discoverAll();
        client.discoverServices(all, new DiscoveryListener() {

            @Override
            public void onFailed(Exception ex) {
                System.out.println("Found service: " + ex.toString());
            }

            @Override
            public void onServiceDiscovered(SsdpService service) {
                System.out.println("Found service: " + service);
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                System.out.println("Service announced something: " + announcement);
            }
        });
    }

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 0 :
                    String userId = "54748881";
                    String sid = "miotstore";
                    String passToken = "V1:frY4eivBqOSlw/t8lfCJPVr80H9QF1Mfdd78/u2BMubIRTRhkHOCZc+8B0zN3bXC8h7azw96zpmn+271q6bj+U7JjUBMQocNngtXCPXZs5kS4o5q7+vQu595VF2YepLO4lvr2DI1FAVeDr1MBgc1Iyz9PcBA/Wx9/Xc4e+Pt5ycnccaUcP7rIjsQTFukzG9PGnAt+fN5atydNqis2vFHQL3foJWncpYK9h4vclTnaDYacmMBmkygigcxP55mycRy";
                    try {
                        AccountInfo account = CloudHelper.getServiceTokenByPassToken(userId, passToken, sid);
                        Log.e("MainActivity", account.toString());
                    } catch (Exception e) {
                        Log.e("MainActivity", e.toString());
                    }

                    break;

                case 1:

                    break;

                default :
                    break;
            }
        };
    };

    public String startDiscover() {
        //DiscoverServices.start(SnowApplication.getAppContext(), handler);


        new udpBroadCast("hi ~!").start();

        /* 开一个线程 接收udp多播 并 发送tcp 连接*/
        UPnPDiscovery discover = new UPnPDiscovery(SnowApplication.getAppContext());
        discover.execute();

        return "";
    }

    /* 发送udp多播 */
    private  class udpBroadCast extends Thread {
        MulticastSocket sender = null;
        DatagramPacket dj = null;
        InetAddress group = null;

        byte[] data = new byte[1024];

        public udpBroadCast(String dataString) {
            data = dataString.getBytes();
        }

        @Override
        public void run() {
            try {
                sender = new MulticastSocket();
                group = InetAddress.getByName("224.0.0.1");
                dj = new DatagramPacket(data,data.length,group,6789);
                sender.send(dj);
                sender.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class UPnPDiscovery extends AsyncTask {
        HashSet<String> addresses = new HashSet<>();
        Context ctx;

        public UPnPDiscovery(Context context) {
            ctx = context;
        }


        @Override
        protected Object doInBackground(Object[] params) {

            WifiManager wifi = (WifiManager) ctx.getSystemService(ctx.getApplicationContext().WIFI_SERVICE);

            if (wifi != null) {

                WifiManager.MulticastLock lock = wifi.createMulticastLock("The Lock 111");
                lock.acquire();

                DatagramSocket socket = null;

                try {

                    InetAddress group = InetAddress.getByName("239.255.255.250");
                    int port = 1900;
                    String query =
                            "M-SEARCH * HTTP/1.1\r\n" +
                                    "HOST: 239.255.255.250:1900\r\n" +
                                    "MAN: \"ssdp:discover\"\r\n" +
                                    "MX: 1\r\n" +
                                    "ST: urn:schemas-upnp-org:service:AVTransport:1\r\n" +  // Use for Sonos
                                    //"ST: ssdp:all\r\n"+  // Use this for all UPnP Devices
                                    "\r\n";

                    socket = new DatagramSocket(port);
                    socket.setReuseAddress(true);

                    DatagramPacket dgram = new DatagramPacket(query.getBytes(), query.length(), group, port);
                    socket.send(dgram);

                    long time = System.currentTimeMillis();
                    long curTime = System.currentTimeMillis();

                    // Let's consider all the responses we can get in 1 second
                    while (curTime - time < 1000) {
                        DatagramPacket p = new DatagramPacket(new byte[12], 12);
                        socket.receive(p);

                        String s = new String(p.getData(), 0, p.getLength());
                        if (s.toUpperCase().equals("HTTP/1.1 200")) {
                            addresses.add(p.getAddress().getHostAddress());
                        }

                        Log.e(TAG, s);

                        curTime = System.currentTimeMillis();
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                    socket.close();
                }
                lock.release();
            }
            return null;
        }
    }


    /*接收udp多播 并 发送tcp 连接*/
    private class udpReceiveAndtcpSend extends Thread {
        @Override
        public void run() {
            byte[] data = new byte[1024];
            try {
                InetAddress groupAddress = InetAddress.getByName("224.0.0.1");
                ms = new MulticastSocket(6789);
                ms.joinGroup(groupAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(TAG, "udpReceiveAndtcpSend");

            while (true) {
                try {
                    dp = new DatagramPacket(data, data.length);
                    if (ms != null)
                        ms.receive(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (dp.getAddress() != null) {
                    final String quest_ip = dp.getAddress().toString();

                    /* 若udp包的ip地址 是 本机的ip地址的话，丢掉这个包(不处理)*/

                    //String host_ip = getLocalIPAddress();

                    String host_ip = getLocalHostIp();

                    Log.e(TAG, "host_ip:  --------------------  " + host_ip);
                    Log.e(TAG, "quest_ip: --------------------  " + quest_ip.substring(1));

                    if( (!host_ip.equals(""))  && host_ip.equals(quest_ip.substring(1)) ) {
                        continue;
                    }

                    final String codeString = new String(data, 0, dp.getLength());

                    Log.e(TAG, "收到来自: \n" + quest_ip.substring(1) + "\n" +"的udp请求\n");
                    Log.e(TAG, "请求内容: " + codeString + "\n\n");


                    try {
                        final String target_ip = dp.getAddress().toString().substring(1);
                        socket = new Socket(target_ip, 8080);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                        try {
                            if (socket != null)
                                socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        }
        catch(SocketException e)
        {
            Log.e("feige", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;
    }

    private InetAddress getDeviceIpAddress(WifiManager wifi) {
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


    public String generateDiffieKey(String arg5) {
        ExchangeKeyRequest v0 = new ExchangeKeyRequest("192.168.1.106", 1, new ResponseHandler() {
            public void onError(int arg3, String arg4) {
            }

            public void onSuccess(String arg3) {
                Log.e(PhilipsActivity.this.TAG, "onSuccess" + arg3);
            }
        });
        v0.execute();
        return "";
    }

    public String startLeft(String arg5) {
        HashMap body = new HashMap();
        body.put("ManTurn", "LF");
        body.put("ManStraight", "OF");
        LocalRequest v0 = new LocalRequest("192.168.1.106", 1,"local", 1, LocalRequestType.PUT, body,new ResponseHandler() {
            public void onError(int arg3, String arg4) {
            }

            public void onSuccess(String arg3) {
                Log.e(PhilipsActivity.this.TAG, "onSuccess" + arg3);
            }
        },s);
        v0.execute();
        return "";
    }


    public String startRight(String arg5) {
        HashMap body = new HashMap();
        body.put("ManTurn", "RT");
        body.put("ManStraight", "OF");
        LocalRequest v0 = new LocalRequest("192.168.1.106", 1,"local", 1, LocalRequestType.PUT, body,new ResponseHandler() {
            public void onError(int arg3, String arg4) {
            }

            public void onSuccess(String arg3) {
                Log.e(PhilipsActivity.this.TAG, "onSuccess" + arg3);
            }
        },s);
        v0.execute();
        return "";
    }


    public String startFront(String arg5) {
        HashMap body = new HashMap();
        body.put("ManTurn", "OF");
        body.put("ManStraight", "FW");
        LocalRequest v0 = new LocalRequest("192.168.1.106", 1,"local", 1, LocalRequestType.PUT, body,new ResponseHandler() {
            public void onError(int arg3, String arg4) {
            }

            public void onSuccess(String arg3) {
                Log.e(PhilipsActivity.this.TAG, "onSuccess" + arg3);
            }
        },s);
        v0.execute();
        return "";
    }

    public String startBehind(String arg5) {
        HashMap body = new HashMap();
        body.put("ManTurn", "OF");
        body.put("ManStraight", "BW");
        LocalRequest v0 = new LocalRequest("192.168.1.106", 1,"local", 1, LocalRequestType.PUT, body,new ResponseHandler() {
            public void onError(int arg3, String arg4) {
            }

            public void onSuccess(String arg3) {
                Log.e(PhilipsActivity.this.TAG, "onSuccess" + arg3);
            }
        },s);
        v0.execute();
        return "";
    }


    public String stopLeft(String arg5) {
        HashMap body = new HashMap();
        body.put("ManTurn", "OF");
        body.put("ManStraight", "OF");
        LocalRequest v0 = new LocalRequest("192.168.1.106", 1,"local", 1, LocalRequestType.PUT, body,new ResponseHandler() {
            public void onError(int arg3, String arg4) {
            }

            public void onSuccess(String arg3) {
                Log.e(PhilipsActivity.this.TAG, "onSuccess" + arg3);
            }
        },s);
        v0.execute();
        return "";
    }

}
