package kiven.com.shuye;

import android.app.Application;

import org.litepal.LitePal;

import kiven.com.shuye.util.Utils;

/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/1/21 14:36 <BR/>
 * Desc:            TODO <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initUtil();
        initLitepal();
    }

    private void initUtil() {
        Utils.init(this);
    }

    /**
     * 初始化数据库
     */
    private void initLitepal() {
        LitePal.initialize(this);
    }
}
