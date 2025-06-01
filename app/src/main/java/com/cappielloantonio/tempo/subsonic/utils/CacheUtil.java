package com.cappielloantonio.tempo.subsonic.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.cappielloantonio.tempo.App;
import com.cappielloantonio.tempo.subsonic.Subsonic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import okhttp3.Interceptor;
import okhttp3.Request;

public class CacheUtil {
    private int maxAge; // 60 seconds
    private int maxStale; // 60 * 60 * 24 * 30 = 30 days (60 seconds * 60 minutes * 24 hours * 30 days)

    public CacheUtil(int maxAge, int maxStale) {
        this.maxAge = maxAge;
        this.maxStale = maxStale;
    }

    public Interceptor onlineInterceptor = chain -> {
        okhttp3.Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .header("Cache-Control", "public, max-age=" + maxAge)
                .removeHeader("Pragma")
                .build();
    };

    public Interceptor offlineInterceptor = chain -> {
        Request request = chain.request();
        if (!isConnected()) {
            request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("Pragma")
                    .build();
        }
        return chain.proceed(request);
    };

    private boolean isConnected() {
        try {
            Subsonic subsonic = App.getSubsonicClientInstance(false);
            String baseUrl = subsonic.getUrl();

            URL url = new URL(baseUrl);
            String host = url.getHost();
            int port = url.getPort();

            if (port == -1) {
                port = url.getProtocol().equals("https") ? 443 : 80;
            }

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 1000); // 1 second timeout
            socket.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
