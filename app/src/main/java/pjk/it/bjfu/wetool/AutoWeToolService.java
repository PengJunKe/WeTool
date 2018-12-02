package pjk.it.bjfu.wetool;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.logging.Handler;

public class AutoWeToolService extends AccessibilityService {
    private int mode = 1;//1详细通知 2.非详细通知
    private boolean canReply = false;
    private boolean isNeedDelay = false;

    private AccessibilityNodeInfo editText = null;


    public AutoWeToolService() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        WeToolUtil.toastShow(getApplicationContext(), "服务已经为您开启，将自动返回！", 1);
        if (WeToolConfig.IS_START_FROM_WETOOL == 1) {
            for (int i = 0; i < WeToolConfig.BACK_REDO_TIMES; i++) {
                doBack();
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();

        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> text = event.getText();
                if (!text.isEmpty()) {
                    String msg = text.get(0).toString();
                    if (isInside(msg)) {
                        return;
                    }
                    if (msg.contains("你收到了一条消息")) {
                        mode = 2;
                    } else {
                        mode = 1;
                    }

                    if (WeToolConfig.isNeedBeFrend) {
                        String name = msg.split(":")[0];
                        for (int i = 0, length = WeToolConfig.nameList.size(); i < length; i++) {
                            if (name.equals(WeToolConfig.nameList.get(i))) {
                                break;
                            }
                            if (i == length - 1) {
                                return;
                            }
                        }
                    }

                    if (event.getParcelableData() != null
                            && event.getParcelableData() instanceof Notification) {
                        canReply = true;
                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (canReply) {
 /*                    canReply = false;*/
                    reply();

                }
        }

    }

    private void reply() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }

        AccessibilityNodeInfo targetNode = null;
        AccessibilityNodeInfo inReplyPage = null;

        List<AccessibilityNodeInfo> nodeInfos = nodeInfo.
                findAccessibilityNodeInfosByViewId(WeToolConfig.qunId);
        if (!nodeInfos.isEmpty()) {
            targetNode = nodeInfos.get(0);
        }
        for (int i = 0, l = WeToolConfig.nameList.size(); i < l; i++) {
            if (findNodeInfosByText(nodeInfo, WeToolConfig.nameList.get(i)) != null) {
                Log.d("findtargetName:", "success");
                isNeedDelay = false;
                break;
            }
            if (i == l - 1) {
                isNeedDelay = true;
                Log.d("findtargetName:", "fail");

            }
        }

        if (editText == null) {
            List<AccessibilityNodeInfo> editList = nodeInfo.
                    findAccessibilityNodeInfosByViewId(WeToolConfig.editId);
            if (!editList.isEmpty()) {
                editText = editList.get(0);
            }
            if (editText == null) {
                findNodeInfosByName(nodeInfo, "android.widget.EditText");
            }
        }
        targetNode = editText;

        if (targetNode != null) {
            //android >= 21=5.0时可以用ACTION_SET_TEXT
            //android >= 18=4.3时可以通过复制粘贴的方法,先确定焦点，再粘贴ACTION_PASTE
            //使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", WeToolConfig.message);
            clipboard.setPrimaryClip(clip);
            //Log.i("demo", "设置粘贴板");
            //焦点 （n是AccessibilityNodeInfo对象）
            targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //Log.i("demo", "获取焦点");
            //粘贴进入内容
            targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            //Log.i("demo", "粘贴内容");
        }

        if (targetNode != null) { //通过组件查找
            Log.i("demo", "查找发送按钮...");
            targetNode = null;
            List<AccessibilityNodeInfo> sendlist = nodeInfo.findAccessibilityNodeInfosByViewId(WeToolConfig.sendId);
            if (!sendlist.isEmpty())
                targetNode = sendlist.get(0);
            //第二种查找方法
            if (targetNode == null)
                targetNode = findNodeInfosByText(nodeInfo, "发送");
        }

        if (targetNode != null) {
            Log.i("demo", "点击发送按钮中...");
            final AccessibilityNodeInfo n = targetNode;
            performClick(n);

        }
    }

    @Override
    public void onInterrupt() {

    }

    private void doBack() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    //常见的微信内部通知，可自行测试并修改
    private boolean isInside(String msg) {
        boolean result = false;
        if (msg.equals("已复制") || msg.equals("已分享") || msg.equals("已下载"))
            result = true;
        if (msg.length() > 6 && (msg.substring(0, 6).equals("当前处于移动") || msg.substring(0, 6).equals("无法连接到服") || msg.substring(0, 6).equals("图片已保存至") || msg.substring(0, 6).equals("网络连接不可")))
            result = true;
        return result;
    }

    /**
     * 通过文本查找
     */
    public AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //通过组件名递归查找编辑框
    private void findNodeInfosByName(AccessibilityNodeInfo nodeInfo, String name) {
        if (name.equals(nodeInfo.getClassName())) {
            editText = nodeInfo;
            return;
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            findNodeInfosByName(nodeInfo.getChild(i), name);
        }
    }

    /**
     * 点击事件
     */
    public void performClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        if (nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

}
