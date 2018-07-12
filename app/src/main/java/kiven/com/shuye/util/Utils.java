package kiven.com.shuye.util;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/1/21 21:10 <BR/>
 * Desc:            TODO <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 */
public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        Utils.sContext = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (sContext != null)
            return sContext;
        throw new NullPointerException("u should init first");
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(float dpValue) {
        final float scale = sContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
