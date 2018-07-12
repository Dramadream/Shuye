package kiven.com.shuye;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import kiven.com.shuye.bean.Item;
import kiven.com.shuye.bean.ItemComparator;
import kiven.com.shuye.util.TimeUtil;
import kiven.com.shuye.util.TimeUtils;
import kiven.com.shuye.util.ToastUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    ItemAdapter mRvAdapter;
    private ArrayList<Long> mVpData;

    private Item mAddItem;
    private String mDialogType;
    private Calendar mCalendar;
    private MaterialDialog mDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View popView) {
                Calendar calendar = Calendar.getInstance();
                mCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                mCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                mCalendar.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND));
                new DialogAdapter(MainActivity.this, mCalendar);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        findView();
        initView();

    }


    @Override
    protected void onResume() {
        super.onResume();
        calculate();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_today:
                mCalendar = Calendar.getInstance();
                refreshDate();
                return true;
            case R.id.action_pick_date:
                showDatePickerDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDatePickerDialog() {
        View datePickerView = LayoutInflater.from(this).inflate(R.layout.dialog_date_picker, null);
        mDatePickerDialog = new MaterialDialog.Builder(this)
                .customView(datePickerView, true)
                .show();

        DatePicker datePicker = datePickerView.findViewById(R.id.dialog_date_picker);
        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                , new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ToastUtils.showShortToast(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        // 获取日期数据
                        mCalendar = Calendar.getInstance();
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        mDatePickerDialog.dismiss();


                        refreshDate();
                    }
                });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 成长
        if (id == R.id.nav_growing) {
            // Handle the camera action
            //红包
        } else if (id == R.id.nav_red_paper) {
            // 统计
        } else if (id == R.id.nav_statistics) {
            // 关于我
        } else if (id == R.id.nav_about_me) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private TextView mTvData;
    private TextView mTvBirthdayBetween;
    private TextView mTvTotalMilk;
    private TextView mTvTotalWater;
    private Button mBtnPreDay;
    private Button mBtnNextDay;
    private TextView mTvPaperTimes;
    private SwipeRefreshLayout mSrlMain;
    private RecyclerView mRvMain;

    private void findView() {
        mTvData = (TextView) findViewById(R.id.tv_data);
        mTvTotalMilk = (TextView) findViewById(R.id.tv_total_milk);
        mTvTotalWater = (TextView) findViewById(R.id.tv_total_water);
        mBtnPreDay = (Button) findViewById(R.id.btn_pre_day);
        mBtnNextDay = (Button) findViewById(R.id.btn_next_day);
        mTvPaperTimes = (TextView) findViewById(R.id.tv_paper_times);
        mSrlMain = (SwipeRefreshLayout) findViewById(R.id.srl_main);
        mRvMain = (RecyclerView) findViewById(R.id.rv_main);
        mTvBirthdayBetween = (TextView) findViewById(R.id.tv_birthday_between);
    }


    private void initView() {
        mCalendar = Calendar.getInstance();

        mRvMain.setLayoutManager(new LinearLayoutManager(this));
        mRvAdapter = new ItemAdapter();
        mRvAdapter.attacheActivity(this);

        mRvMain.setAdapter(mRvAdapter);
        mRvAdapter.setData(getItems());

        mRvAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int index) {
                new DialogAdapter(MainActivity.this, mRvAdapter.getData(index), mCalendar);
            }
        });

        mRvAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int index) {
                mRvAdapter.onItemDelete(index);
            }
        });

        mSrlMain.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSrlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRvAdapter.setData(getItems());
                calculate();
                mSrlMain.setRefreshing(false);
            }
        });

        mBtnPreDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentMillis = mCalendar.getTimeInMillis();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentMillis);
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                calendar.set(Calendar.DAY_OF_YEAR, dayOfYear - 1);
                mCalendar.setTimeInMillis(calendar.getTimeInMillis());

                refreshDate();
            }
        });
        mBtnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentMillis = mCalendar.getTimeInMillis();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentMillis);
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                calendar.set(Calendar.DAY_OF_YEAR, dayOfYear + 1);
                mCalendar.setTimeInMillis(calendar.getTimeInMillis());

                refreshDate();
            }
        });
    }

    /**
     * 获取数据
     */
    private List getItems() {

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTimeInMillis(mCalendar.getTimeInMillis());
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);

        long todayMillis = TimeUtil.getCurrentDayMillis(todayCalendar.getTimeInMillis());

        long tomorrowMillis = todayCalendar.getTimeInMillis() + 1000 * 60 * 60 * 24;

        // 当天的所有条目
        List<Item> items = DataSupport.where("time > " + todayMillis + " AND time < " + tomorrowMillis)
                .find(Item.class);

        // 所有条目
        //ArrayList<Item> items = (ArrayList<Item>) DataSupport.findAll(Item.class);
        Collections.sort(items, new ItemComparator());

        return items;
    }


    /**
     * 每日统计
     */
    public void calculate() {
        mTvData.setText(TimeUtils.date2String(mCalendar.getTime(), "yyyy-MM-dd"));
        mTvBirthdayBetween.setText(getAgeInDays());

        int milk = 0;
        int water = 0;
        int paperDiaper = 0;
        for (Item item : mRvAdapter.mDatas) {
            if ("奶".equals(item.getType())) {
                milk += item.getAccount();
            } else if ("水".equals(item.getType())) {
                water += item.getAccount();
            }


            if (item.isPaperDiaper() || item.isShit()) {
                paperDiaper += 1;
            }
        }

        mTvTotalMilk.setText(getString(R.string.total_milk, milk));
        mTvTotalWater.setText(getString(R.string.total_water, water));
        mTvPaperTimes.setText(getString(R.string.total_paper, paperDiaper));
    }

    private String getAgeInDays() {
        Calendar birthday = TimeUtil.getBirthday();

        int year = mCalendar.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);


        // 1.如果月份小于0，是12月之前，年龄减1；月龄+12；
        if (month < 0) {
            year -= 1;
            month += 12;
        }

        // 2.如果日龄小于0，说明是8号之前，月龄减1
        if (day < 0) {
            month -= 1;
            int monthDay = 0;
            // 根据上个月判断多少天
            int currentMonth = mCalendar.get(Calendar.MONTH);
            int preMonth = 0;
            if (currentMonth == 0) {
                preMonth = 11;
            } else {
                preMonth = currentMonth - 1;
            }

            switch (preMonth) {
                case 0:
                case 2:
                case 4:
                case 6:
                case 7:
                case 9:
                case 11:
                    monthDay = 31;
                    break;
                case 3:
                case 5:
                case 8:
                case 10:
                    monthDay = 30;
                    break;
                case 1:
                    // 如果是闰年，那就是29天
                    if (mCalendar.get(Calendar.YEAR) / 4 == 0) {
                        monthDay = 29;
                    } else {
                        monthDay = 28;
                    }
                    break;
            }
            day += monthDay;
        }
        day += 1;

        return getMsgStr(year, month, day);
    }

    /**
     * 获得最终显示的字符串
     */
    @NonNull
    private String getMsgStr(int year, int month, int day) {
        StringBuilder sb = new StringBuilder();
        if (year > 0) {
            sb.append(year + "岁");
        }
        if (month > 0) {
            sb.append(month + "个月");
        }
        sb.append(day + "天");

        return sb.toString();
    }


    /**
     * 刷新界面
     */
    public void refreshDate() {
        mRvAdapter.setData(getItems());
        calculate();
    }
}
