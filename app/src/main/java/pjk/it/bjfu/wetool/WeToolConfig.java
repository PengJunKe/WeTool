package pjk.it.bjfu.wetool;

import java.util.ArrayList;

public class WeToolConfig {

    //微信6.3.18相关组件的id，微信版本更新后随之修改即可
    static String qunId = "com.tencent.mm:id/ei";
    static String editId = "com.tencent.mm:id/yq";
    static String sendId = "com.tencent.mm:id/yw";

    static String message = "I love you!";

    public static int BACK_REDO_TIMES = 2;//重复返回键的次数
    public static int IS_START_FROM_WETOOL = 0;//是否是从我们的应用打开的
    public static String ACCESSIBILITYSERVICE_NAME = "AutoWeToolService";

    public static ArrayList<String> nameList = new ArrayList<>();
    public static boolean isNeedBeFrend = true;
}
