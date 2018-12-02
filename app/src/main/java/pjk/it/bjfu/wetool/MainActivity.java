package pjk.it.bjfu.wetool;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAccessibilityService();

        addName("彭名");
    }

    private void initAccessibilityService(){
        if (WeToolUtil.isServiceON(this)){
            WeToolUtil.toastShow(this,"服务已经开启啦~",0);
        }else {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            WeToolConfig.IS_START_FROM_WETOOL = 1;
            startActivity(intent);
        }
    }

    private void addName(String name){
        if (!name.isEmpty()){
            WeToolConfig.nameList.add(name);
        }
    }
}
