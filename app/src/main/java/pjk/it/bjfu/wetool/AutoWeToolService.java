package pjk.it.bjfu.wetool;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

public class AutoWeToolService extends AccessibilityService {
    public AutoWeToolService() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        if (WeToolConfig.IS_START_FROM_WETOOL == 1){
            for (int i = 0; i < WeToolConfig.BACK_REDO_TIMES; i++){
                doBack();
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    private void doBack() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }


}
