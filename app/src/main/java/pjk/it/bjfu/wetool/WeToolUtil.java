package pjk.it.bjfu.wetool;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;

import java.util.List;

public class WeToolUtil {

    public static boolean isServiceON(Context context){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo>
                runningServices = activityManager.getRunningServices(100);
        if (runningServices.size() < 0 ){
            return false;
        }
        for (int i = 0;i<runningServices.size();i++){
            ComponentName service = runningServices.get(i).service;
            if (service.getClassName().equals(WeToolConfig.ACCESSIBILITYSERVICE_NAME)){
                return true;
            }
        }
        return false;
    }

    public static void toastShow(Context context,String msg,int time){
        if (time == 0){
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        }else if (time == 1){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        }
    }
}
