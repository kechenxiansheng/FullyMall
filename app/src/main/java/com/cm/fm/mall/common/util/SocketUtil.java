package com.cm.fm.mall.common.util;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket 客户端工具类
 * 两个函数可以融合在一起，记得优化
 */
public class SocketUtil {
    private static final String TAG = "socket@";

    public SocketUtil() {
    }
    /* ip */
    public static String getResultWithIp(String ip, int port, String msg) {
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
    /* 域名 */
    public static String getResultWithDomain(String domain, int port, String msg) {
        String result = "-99";
        Socket socket = null;
        BufferedReader in = null;
        OutputStreamWriter out = null;
        try {
            Log.d(TAG,"socket to - " + domain + ":" + port + " msg:" + msg);
            InetAddress inetAddress = InetAddress.getByName(domain);
            socket = new Socket(inetAddress, 10000);
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
                if(socket != null){
                    socket.close();
                }
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
