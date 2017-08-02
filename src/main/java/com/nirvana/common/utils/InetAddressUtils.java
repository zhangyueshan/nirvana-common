package com.nirvana.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by InThEnd on 2016/5/4.
 * IP MAC 工具类。
 */
public class InetAddressUtils {

    public static String getIP() throws UnknownHostException {
        InetAddress ia = InetAddress.getLocalHost();
        return ia.getHostAddress();
    }

    public static String getMacAddress() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String mac;
        if (os.startsWith("windows")) {
            mac = getWindowsMac();
        } else {
            mac = getUnixMac();
        }
        return mac;
    }


    private static String getWindowsMac() throws SocketException, UnknownHostException {
        String mac = "";
        StringBuilder sb = new StringBuilder(mac);
        NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getByName(getIP()));
        byte[] macs = ni.getHardwareAddress();
        for (byte bt : macs) {
            mac = Integer.toHexString(bt & 0xFF);
            if (mac.length() == 1) {
                mac = '0' + mac;
            }
            sb.append(mac).append("-");
        }
        mac = sb.toString();
        mac = mac.substring(0, mac.length() - 1);
        return mac.toUpperCase();
    }

    private static String getUnixMac() throws IOException {
        String mac = null;
        BufferedReader bufferedReader;
        Process process;
        process = Runtime.getRuntime().exec("ifconfig eth0");
        bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        int index;
        while ((line = bufferedReader.readLine()) != null) {
            index = line.toLowerCase().indexOf("hwaddr");
            if (index >= 0) {
                mac = line.substring(index + "hwaddr".length() + 1).trim();
                break;
            }
        }
        bufferedReader.close();
        return mac == null ? null : mac.toUpperCase();
    }


}
