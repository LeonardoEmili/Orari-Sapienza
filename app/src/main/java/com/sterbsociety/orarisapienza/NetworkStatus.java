package com.sterbsociety.orarisapienza;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by juma on 18/04/17. Reference site: https://androidstudy.com/2017/04/17/android-detect-internet-connection-status/
 */
public class NetworkStatus {

    /**
     * We use this class to determine if the application has been connected to either WIFI Or Mobile
     * Network, before we make any network request to the server.
     * The class uses two permission - INTERNET and ACCESS NETWORK STATE, to determine the user's
     * connection stats.
     */

    private static NetworkStatus instance;
    private static final String defaultMACAddress = "02:00:00:00:00:00";
    private boolean connected = false;

    public static NetworkStatus getInstance() {
        if (instance == null)
             instance = new NetworkStatus();
        return instance;
    }

    @SuppressWarnings("deprecation")
    public boolean isOnline(Activity activity) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * Code taken from: https://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code/13007325#13007325
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface mInterface : interfaces) {
                if (interfaceName != null) {
                    if (!mInterface.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = mInterface.getHardwareAddress();
                if (mac==null) return defaultMACAddress;
                StringBuilder sb = new StringBuilder();
                for (byte aMac : mac) sb.append(String.format("%02X:",aMac));
                if (sb.length()>0) sb.deleteCharAt(sb.length()-1);
                return sb.toString();
            }
        } catch (Exception ignored) {

        }
        return defaultMACAddress;
    }
}