package com.cm.fm.mall.common.util;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketUtil {
    private static final String TAG = "socket@";

    public SocketUtil() {
    }

    public static String getResult(String ip, int port, String msg) {
        String result = "-99";
        Socket socket = new Socket();
        BufferedReader in = null;
        OutputStreamWriter out = null;

        try {
            Log.d(TAG,"socket to - " + ip + ":" + port + " msg:" + msg);
            InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
            socket.connect(socketAddress, 10000);
            socket.setSoTimeout(10000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
            out.write(msg);
            out.flush();
            result = in.readLine();
        } catch (Exception var16) {
            Log.e(TAG,"socket连接(通信)错误：", var16);
            result = "-88";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                socket.close();
            } catch (Exception var15) {
                var15.printStackTrace();
                result = "-77";
            }

        }
        Log.d(TAG,"request:" + msg);
        Log.d(TAG,"response:" + result.trim());
        return result.trim();
    }
}
