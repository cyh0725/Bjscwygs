package com.cyh.bjscwygs;


import android.accounts.NetworkErrorException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

public class NetUtil {


    /**
     * 使用GET访问去访问网络
     *
     * @param username
     * @param password
     * @return 服务器返回的结果
     */
    public static String loginOfGet(String username, String password) {
        HttpURLConnection conn = null;
        try {
            String data = "username=" + username + "&password=" + password;
            URL url = new URL("http://192.168.1.4:8080/AndroidServer/LoginServlet?" + data);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                String state = getStringFromInputStream(is);
                return state;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }


    /**
     * 使用POST访问去访问网络
     *
     * @param deviceId
     * @param phone
     * @return
     */
    public static String ConfigOfPost(String deviceId, String phone) throws NetworkErrorException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://192.168.0.5/Share/Config");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            //post请求的参数
            String data = "deviceId=" + deviceId + "&phone=" + phone;
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                String state = getStringFromInputStream(is);
                return state;
            } else {
                throw new NetworkErrorException("网络错误");
            }
        } catch (Exception e) {
            throw new NetworkErrorException("网络错误");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String InitDataOfPost(String Ip, int ScreenX, int ScreenY, String SystemVersion, String Brand, String Model,String DeviceId,String Sim,String Imsi) throws NetworkErrorException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://192.168.0.5/Share/InitData");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            //post请求的参数
            String data = "Ip=" + Ip + "&ScreenX=" + ScreenX + "&ScreenY=" + ScreenY + "&SystemVersion=" + SystemVersion + "&Brand=" + Brand + "&Model=" + Model+"&DeviceId="+DeviceId+"&Sim="+Sim+"&Imsi="+Imsi;
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
            conn.connect();
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                String state = getStringFromInputStream(is);
                return state;
            } else {
                throw new NetworkErrorException("网络错误");
            }
        } catch (Exception e) {
            throw new NetworkErrorException("网络错误");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 根据输入流返回一个字符串
     *
     * @param is
     * @return
     * @throws Exception
     */
    private static String getStringFromInputStream(InputStream is) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = -1;
        while ((len = is.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }
        is.close();
        String html = baos.toString();
        baos.close();


        return html;
    }
}

