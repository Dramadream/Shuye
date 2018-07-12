package kiven.com.shuye;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kiven.com.shuye.bean.Item;
import kiven.com.shuye.util.ToastUtils;
import kiven.com.shuye.util.Utils;
import kiven.com.shuye.widget.WheelView;

/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/1/22 14:09 <BR/>
 * Desc:            TODO <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 */
public class DialogAdapter implements MaterialDialog.SingleButtonCallback {

    private MainActivity mMainActivity;
    private Item mAddItem;

    private WheelView mWvHour;
    private WheelView mWvMinute;
    private RadioGroup mDialogRgType;
    private RadioButton mDialogRbTypeMilk;
    private RadioButton mDialogRbTypeWater;
    private RadioButton mDialogRbTypeSuFood;
    private EditText mMDialogEtAccount;
    private CheckBox mMDialogRbShit;
    private CheckBox mMDialogRbPaperDiaper;
    private TextView mDialogTvAdd10;
    private TextView mDialogTvAdd30;
    private TextView mDialogTvAdd50;
    private LinearLayout mLlMilkOrWater;
    private LinearLayout mLlSuFood;
    private LinearLayout mLlAddAccount;
    private EditText mDialogEtSuFood;

    private int mHour;
    private int mMinute;
    private boolean mIsAdd = false;
    private Calendar mCalendar;
    /** 是否是当天的数据 */
    private boolean mCurrentDay;

    public DialogAdapter(MainActivity mainActivity, Calendar calendar) {
        this(mainActivity, null, calendar);
    }

    public DialogAdapter(MainActivity mainActivity, Item data, Calendar calendar) {
        mMainActivity = mainActivity;
        mCalendar = calendar;
        if (data == null) {
            mIsAdd = true;
            mAddItem = new Item();
        } else {
            mAddItem = data;
        }
        init();
    }

    private void init() {
        View addItemView = LayoutInflater.from(mMainActivity).inflate(R.layout.dialog_add_item, null);

        new MaterialDialog.Builder(mMainActivity)
                .title(mIsAdd ? "添加" : "修改")
                .customView(addItemView, true)
                .positiveText(R.string.confirm)
                .onPositive(this)
                .negativeText(R.string.cancel)
                .negativeColor(Color.GRAY)
                .show();

        findView(addItemView);
        initUiAndListener();

    }

    private void findView(View addItemView) {
        mWvHour = addItemView.findViewById(R.id.pop_wv_hour);
        mWvMinute = addItemView.findViewById(R.id.pop_wv_minute);
        mDialogRgType = addItemView.findViewById(R.id.dialog_rg_type);
        mDialogRbTypeMilk = addItemView.findViewById(R.id.dialog_rb_type_milk);
        mDialogRbTypeWater = addItemView.findViewById(R.id.dialog_rb_type_water);
        mDialogRbTypeSuFood = addItemView.findViewById(R.id.dialog_rb_type_su_food);
        mMDialogEtAccount = addItemView.findViewById(R.id.dialog_et_account);
        mMDialogRbShit = addItemView.findViewById(R.id.dialog_cb_shit);
        mMDialogRbPaperDiaper = addItemView.findViewById(R.id.dialog_cb_paper_diaper);
        mDialogTvAdd10 = addItemView.findViewById(R.id.dialog_tv_add_10);
        mDialogTvAdd30 = addItemView.findViewById(R.id.dialog_tv_add_30);
        mDialogTvAdd50 = addItemView.findViewById(R.id.dialog_tv_add_50);
        mLlMilkOrWater = addItemView.findViewById(R.id.ll_milk_or_water);
        mLlSuFood = addItemView.findViewById(R.id.ll_su_food);
        mLlAddAccount = addItemView.findViewById(R.id.ll_add_account);
        mDialogEtSuFood = addItemView.findViewById(R.id.dialog_et_su_food);

    }


    private void initUiAndListener() {
        /**
         * 时间选择器
         */
        // 可循环
        mWvHour.setCycleDisable(false);
        mWvMinute.setCycleDisable(false);
        // 文字大小
        mWvHour.setTextSize(Utils.dp2px(6));
        mWvMinute.setTextSize(Utils.dp2px(6));


        // 设置数据和监听器
        List<String> hour = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hour.add("0" + i);
        }
        for (int i = 10; i <= 23; i++) {
            hour.add(String.valueOf(i));
        }
        mWvHour.setItems(hour);
        mWvHour.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index, String item) {
                mHour = index;
            }
        });
        // 设置数据和监听器
        List<String> minute = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            minute.add("0" + i);
        }
        for (int i = 10; i <= 59; i++) {
            minute.add(String.valueOf(i));
        }
        mWvMinute.setItems(minute);
        mWvMinute.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index, String item) {
                mMinute = index;
            }
        });

        // 设置时间
        // 如果是添加，当前时间，如果是修改，获取时间
        if (!mIsAdd) {
            mCalendar.setTimeInMillis(mAddItem.getTime());
        }
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mWvHour.setSelectedIndex(mHour);
        mWvMinute.setSelectedIndex(mMinute);

        /**
         * 奶还是水，还是辅食，如果是添加，直接是奶
         */
        if (mIsAdd) {
            mAddItem.setType(mMainActivity.getString(R.string.milk));
            mDialogRbTypeMilk.setChecked(true);
        } else if ("奶".equals(mAddItem.getType())) {
            mDialogRbTypeMilk.setChecked(true);
            changeMilkAndWaterVisiable(true);
        } else if ("水".equals(mAddItem.getType())) {
            mDialogRbTypeWater.setChecked(true);
            changeMilkAndWaterVisiable(true);
        } else {
            mDialogRbTypeSuFood.setChecked(true);
            changeMilkAndWaterVisiable(false);
        }

        mDialogRgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.dialog_rb_type_milk:
                        mAddItem.setType(mMainActivity.getString(R.string.milk));
                        changeMilkAndWaterVisiable(true);
                        break;
                    case R.id.dialog_rb_type_water:
                        mAddItem.setType(mMainActivity.getString(R.string.water));
                        changeMilkAndWaterVisiable(true);
                        break;
                    case R.id.dialog_rb_type_su_food:
                        mAddItem.setType(mMainActivity.getString(R.string.supplement_food));
                        changeMilkAndWaterVisiable(false);
                        break;
                }
            }
        });


        /**
         * 量
         */
        if (!mIsAdd) {
            mMDialogEtAccount.setText(String.valueOf(mAddItem.getAccount()));
        }
        mMDialogEtAccount.setSelection(mMDialogEtAccount.getText().length());
        mMDialogEtAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s)
                    mMDialogEtAccount.setSelection(s.length());
            }
        });
        View.OnClickListener addAccountListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_tv_add_10:
                        mMDialogEtAccount.setText(String.valueOf(getAccount() + 10));
                        break;
                    case R.id.dialog_tv_add_30:
                        mMDialogEtAccount.setText(String.valueOf(getAccount() + 30));
                        break;
                    case R.id.dialog_tv_add_50:
                        mMDialogEtAccount.setText(String.valueOf(getAccount() + 50));
                        break;
                }
            }
        };
        mDialogTvAdd10.setOnClickListener(addAccountListener);
        mDialogTvAdd30.setOnClickListener(addAccountListener);
        mDialogTvAdd50.setOnClickListener(addAccountListener);

        /**
         * 辅食
         */
        if (!mIsAdd) {
            mDialogEtSuFood.setText(mAddItem.getSupplementFood());
        }
        mDialogEtSuFood.setSelection(mDialogEtSuFood.getText().length());
        mDialogEtSuFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s)
                    mDialogEtSuFood.setSelection(s.length());
            }
        });

        /**
         * 拉还是换
         */
        if (mAddItem.isShit()) {
            mMDialogRbShit.setChecked(true);
            mMDialogRbPaperDiaper.setChecked(true);
        } else if (mAddItem.isPaperDiaper()) {
            mMDialogRbShit.setChecked(false);
            mMDialogRbPaperDiaper.setChecked(true);
        } else {
            mMDialogRbShit.setChecked(false);
            mMDialogRbPaperDiaper.setChecked(false);
        }

        mMDialogRbShit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAccountEtEmpty();
                    mMDialogRbPaperDiaper.setChecked(true);
                    mAddItem.setShit(true);
                } else {
                    mAddItem.setShit(false);
                }
            }
        });
        mMDialogRbPaperDiaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAccountEtEmpty();
                    mAddItem.setPaperDiaper(true);
                } else {
                    mMDialogRbShit.setChecked(false);
                    mAddItem.setPaperDiaper(false);
                }
            }
        });

    }

    /**
     * 切换水奶和辅食的显示
     *
     * @param visiable true：水奶显示
     * @param visiable false：辅食显示
     */
    private void changeMilkAndWaterVisiable(boolean visiable) {
        mLlMilkOrWater.setVisibility(visiable ? View.VISIBLE : View.GONE);
        mLlAddAccount.setVisibility(visiable ? View.VISIBLE : View.GONE);
        mLlSuFood.setVisibility(visiable ? View.GONE : View.VISIBLE);
    }

    /**
     * 将空的EditText设置为0
     */
    private void setAccountEtEmpty() {
        if (TextUtils.isEmpty(mMDialogEtAccount.getText().toString())) {
            mMDialogEtAccount.setText("0");
        }
    }


    /**
     * 点击确定的操作
     *
     * @param dialog
     * @param which
     */
    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        // 获取时间
        KLog.i(mCalendar.getTimeInMillis());

        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);

        mAddItem.setTime(mCalendar.getTimeInMillis());
        KLog.i(mCalendar.getTimeInMillis());


        //奶还是水在Listener已经完成
        // 量
        if (mAddItem.getType().equals("奶") || mAddItem.getType().equals("水")) {
            // 奶或者水
            String accountStr = mMDialogEtAccount.getText().toString();
            if (TextUtils.isEmpty(accountStr)) {
                ToastUtils.showShortToast(R.string.toast_empty_account);
                return;
            }
            mAddItem.setAccount(Integer.valueOf(accountStr));
        } else {
            // 辅食
            mAddItem.setSupplementFood(mDialogEtSuFood.getText().toString());
        }
        mAddItem.save();
        // 刷新数据
        mMainActivity.refreshDate();
    }

    private int getAccount() {
        String msg = mMDialogEtAccount.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            return 0;
        } else {
            return Integer.valueOf(msg);
        }
    }
}
