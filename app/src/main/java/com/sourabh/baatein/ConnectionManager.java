package com.sourabh.baatein;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionManager {

    public static boolean checkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null;
        }
        return false;
    }

}
