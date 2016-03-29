package copytestapp.copytestapp.etc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Y on 2015-05-08.
 */
public class NetworkState
{
    public static boolean isNetworkStat( Context context )
    {
        ConnectivityManager manager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo lte_4g = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean blte_4g = false;

        if(lte_4g != null) blte_4g = lte_4g.isConnected();

        if( mobile != null ){

            if (mobile.isConnected() || wifi.isConnected() || blte_4g)
                return true;
        }

        else{

            if ( wifi.isConnected() || blte_4g ) return true;
        }

        return false;
    }
}
