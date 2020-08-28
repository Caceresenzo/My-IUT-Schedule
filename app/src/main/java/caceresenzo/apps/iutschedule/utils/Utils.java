package caceresenzo.apps.iutschedule.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static boolean validate(String... strings) {
        for (String str : strings) {
            if (str == null || str.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasInternetConnection(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static <T> T random(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        return list.get(ThreadLocalRandom.current().nextInt(0, list.size() - 1));
    }

}