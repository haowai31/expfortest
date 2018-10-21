package com.example.kurtqin.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.resourcepool.ssdp.client.SsdpClient;
import io.resourcepool.ssdp.model.DiscoveryListener;
import io.resourcepool.ssdp.model.DiscoveryRequest;
import io.resourcepool.ssdp.model.SsdpRequest;
import io.resourcepool.ssdp.model.SsdpService;
import io.resourcepool.ssdp.model.SsdpServiceAnnouncement;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button poweron = findViewById(R.id.button);
        final Button poweroff = findViewById(R.id.button2);
        Button discover = findViewById(R.id.button3);
        Button philips = findViewById(R.id.button4);
        Button close = findViewById(R.id.button5);
        philips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.example.kurtqin.test", "com.example.kurtqin.test.Philips.PhilipsActivity");
                MainActivity2.this.startActivity(intent);
            }
        });
        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discover();
                disCover1();
            }
        });
        poweron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poweron();
            }
        });
        poweroff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poweroff();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        log = findViewById(R.id.textView);
        log.setBackgroundColor(Color.BLACK);
        log.setTextColor(Color.GREEN);
        log.setTextSize(25);
        log.setMovementMethod(ScrollingMovementMethod.getInstance());
        handler = new Handler();
        sp = getSharedPreferences("ip", Context.MODE_PRIVATE);
        progress();
        ctl();
    }
    private void exit() {
        Intent intent = new Intent(this, CloseActivity.class);
        startActivity(intent);
    }
    private void ctl() {

            String intent = getIntent().getStringExtra("ctl");
            if (intent != null) {
                if (intent.contains("1")) {
                    stage1();
                }
                if (intent.contains("2")) {
                    stage2();
                }
                if (intent.contains("3")) {
                    stage3();
                }
            }else {
                stage1();
            }




    }
    private void stage1() {
        status = "stage1";
        stage1.start();
    }
    private void stage2() {
        status = "stage2";
        stage2.start();
    }
    private void stage3() {
        status = "stage3";
        stage3.start();
    }

    private Socket socket;
    private String mDevAddress;
    private byte[] revv;
    private String msg = "00";
    private String powerip = "192.168.1.102";
    private String mLocAddress;
    private String token;
    private int deviceid;
    private Map<String, byte[]> device = new HashMap<String, byte[]>();
    private TextView log;
    private Handler handler = null;
    private ArrayList<String> ip = null;
    private int ipindex = 0;
    private ArrayList<String> ssdpip = null;
    private String message = "";
    private Thread stage1;
    private Thread stage2;
    private Thread stage3;
    private String status = "";
    private SharedPreferences sp;

    private void progress() {

        stage1 = new Thread() {
            @Override
            public void run() {
                message = "Stage 1 : Discover IoT Devices\n[%0] Discovering...\n";
                handler.post(control);
                discover();
                //disCover1();
            }
        };
        stage2 = new Thread() {
            @Override
            public void run() {
                try {
                } catch (Exception e) {
                    //
                }
                message = "Stage 2 : Power off all discovered devices\n";
                handler.post(control);
                try {
                    sleep(2000);
                } catch (Exception e) {
                    //
                }
                poweroff();

            }
        };
        stage3 = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (Exception e) {
                    //
                }
                message = "Stage 3 : Power on all discovered devices\n";
                handler.post(control);
                poweron();
            }
        };
    }

    Runnable control = new Runnable() {
        @Override
        public void run() {
            log.append(message);
            int offset=log.getLineCount()*log.getLineHeight();
            if(offset>(log.getHeight())){
                log.scrollTo(0,offset-log.getHeight()+log.getLineHeight()+1);
            }
        }
    };


    public void gettoken() {
        new Thread() {
            @Override
            public void run(){
                try {
                    DatagramSocket socket = new DatagramSocket();
                    String text = "21310020ffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
                    socket.setSoTimeout(4000);
                    byte[] buf = hexToByteArray(text);
                    System.out.println("!!! Data is read to send!" );
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(powerip), 54321);
                    socket.send(packet);
                    System.out.println("!!! Data Send to " + powerip + "!");

                    byte[] revMsg = new byte[255];
                    DatagramPacket recpacket = new DatagramPacket(revMsg, revMsg.length);
                    try {
                        socket.receive(recpacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String rev = bytesToHex(revMsg).substring(0,64);
                    token = rev.substring(32, 64);
                    deviceid = Integer.valueOf(rev.substring(16, 24), 16);
                    System.out.println("!!!" + deviceid);

                    rev = msg + rev;
                    System.out.println("!!!" + rev);
                    revMsg = hexToByteArray(rev);
                    System.out.println("!!!" +bytesToHex(revMsg));




                    DatagramSocket socket1 = new DatagramSocket();
                    socket1.setSoTimeout(4000);
                    DatagramPacket packet1 = new DatagramPacket(revMsg, revMsg.length, InetAddress.getByName("118.24.168.131"), 20015);
                    socket1.send(packet1);
                    byte[] revMsg1 = new byte[255];
                    DatagramPacket recpacket1 = new DatagramPacket(revMsg1, revMsg1.length);
                    try {
                        socket1.receive(recpacket1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    revMsg1 = hexToByteArray(bytesToHex(revMsg1).substring(0,192));
                    System.out.println(bytesToHex(revMsg1));

                    DatagramSocket socket2 = new DatagramSocket();
                    socket2.setSoTimeout(4000);
                    System.out.println("!!! Payload is read to send!" );
                    DatagramPacket packet2 = new DatagramPacket(revMsg1, revMsg1.length, InetAddress.getByName(powerip), 54321);
                    socket2.send(packet2);
                    System.out.println("!!! Payload Sended!" );
                    byte[] revMsg2 = new byte[255];
                    DatagramPacket recpacket2 = new DatagramPacket(revMsg2, revMsg2.length);
                    try {
                        socket2.receive(recpacket2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(bytesToHex(revMsg2));


                } catch (IOException e) {
                    System.out.println("!!!");
                    e.printStackTrace();
                }
                handler.post(getnextip);
            }

        }.start();

    }

    Runnable getnextip = new Runnable() {
        @Override
        public void run() {
            if (ipindex < ip.size()-1) {
                ipindex += 1;
                powerip = ip.get(ipindex);
                gettoken();
            }
        }
    };


    public void seton() {
        msg = "11";
    }

    public void setoff() {
        msg = "00";
    }
    private void getip(){
        SharedPreferences sp = getSharedPreferences("ip", Context.MODE_PRIVATE);
        ip = new ArrayList<String>();
        for (int i=0;i<sp.getInt("len", 0);i++) {
            int tmp = i+1;
            String data = sp.getString("ip" + tmp, null);
            ip.add(data);
        }
    }

    public void poweron() {
        seton();
        ipindex = 0;
        getip();
        if (ip.size() > 0){
            powerip = ip.get(0);
            gettoken();
        }

    }

    public void poweroff() {
        setoff();
        getip();
        ipindex = 0;
        if (ip.size() > 0){
            powerip = ip.get(0);
            gettoken();
        }
    }


    public void discover() {
        ip = new ArrayList<String>();
        new Thread() {
            @Override
            public void run() {
                mDevAddress = getHostIP();
                String[] x = mDevAddress.split("\\.");
                System.out.println("!!! "+mDevAddress);
                String top = x[0] + '.' + x[1] + '.' + x[2] + '.';

                for (int i = 2;i<255;i++) {
                    String tmp = top + i;
                    System.out.println("To discover " + tmp);
                    try {
                        DatagramSocket socket = new DatagramSocket();
                        socket.setSoTimeout(200);
                        String text = "21310020ffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
                        byte[] buf = hexToByteArray(text);
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(tmp), 54321);
                        socket.send(packet);
                        System.out.println("!!! Data Sended to " + tmp);
                        if (i % 20 == 0) {
                            double rate = i / 254.0 * 100;
                            message = "[%" + (int)rate + "] Discovering...\n";
                            handler.post(control);
                        }

                        byte[] revMsg = new byte[255];
                        DatagramPacket recpacket = new DatagramPacket(revMsg, revMsg.length);
                        try {
                            socket.receive(recpacket);
                            String tmpp = bytesToHex(revMsg).substring(32,34);
                            if (!tmpp.equals("ff")) {
                                System.out.println("!!! find device: " + tmp);
                                device.put(tmp, revMsg);
                                SharedPreferences.Editor editor = sp.edit();
                                int index = ip.size()+1;
                                String key = "ip" + index;
                                editor.putString(key, tmp);
                                editor.apply();
                                ip.add(tmp);
                            }

                        } catch (IOException e) {
                            //e.printStackTrace();
                        }

                    }
                    catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
                message = "[%100] Find " + ip.size() + " IoT devices\n";
                handler.post(updatelog);
            }
        }.start();

    }

    Runnable updatelog = new Runnable() {
        @Override
        public void run() {
            log.append(message);
            int offset=log.getLineCount()*log.getLineHeight();
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("len", ip.size());
            editor.apply();
            if(offset>(log.getHeight())){
                log.scrollTo(0,offset-log.getHeight()+log.getLineHeight()+1);
            }
            try {
                Thread.sleep(5000);
                finish();
            } catch (Exception e){
                //
            }

        }
    };

    public String getHostIP() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        int ipAddressInt = wm.getConnectionInfo().getIpAddress();
        String ipAddress = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddressInt & 0xff), (ipAddressInt >> 8 & 0xff), (ipAddressInt >> 16 & 0xff), (ipAddressInt >> 24 & 0xff));

        return ipAddress;
    }


    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private void disCover1() {
        SsdpClient client = SsdpClient.create();
        DiscoveryRequest all = SsdpRequest.discoverAll();
        ssdpip = new ArrayList<String>();
        client.discoverServices(all, new DiscoveryListener() {

            @Override
            public void onFailed(Exception ex) {
                //System.out.println("Found service: " + ex.toString());
            }

            @Override
            public void onServiceDiscovered(SsdpService service) {
                String tmp = service.toString();
                String roterip = mDevAddress.substring(0, mDevAddress.lastIndexOf('.')) + ".1";
                String devip = service.getRemoteIp().getHostAddress();
                if (!devip.equals(roterip)) {
                    if (!ssdpip.contains(devip)) {
                        ssdpip.add(devip);
                    }
                }
                System.out.println("Found service: " + service);
            }

            @Override
            public void onServiceAnnouncement(SsdpServiceAnnouncement announcement) {
                System.out.println("Service announced something: " + announcement);
            }
        });
    }


}
