package kiven.com.shuye;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import kiven.com.shuye.bean.Item;
import kiven.com.shuye.util.TimeUtils;
import kiven.com.shuye.util.ToastUtils;

/**
 * Author:          Kevin <BR/>
 * CreatedTime:     2018/1/21 11:32 <BR/>
 * Desc:            TODO <BR/>
 * <p/>
 * ModifyTime:      <BR/>
 * ModifyItems:     <BR/>
 */
public class ItemAdapter extends BaseRecyclerViewAdapter<Item> {

    private MainActivity mMainActivity;

    public void attacheActivity(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item;
    }

    @Override
    protected BaseHolder<Item> createHolder(View view, int viewType) {
        return new Holder(view);
    }


    public void onItemDelete(final int position) {

        final Item item = mDatas.get(position);
        final String timeStr = TimeUtils.millis2String(item.getTime(), "HH:mm");
        String msg = "是否要删除" + timeStr + "时间的纪录？";

        new MaterialDialog.Builder(mMainActivity)
                .title(R.string.warning)
                .content(msg)
                .negativeText(R.string.cancel)
                .negativeColor(Color.GRAY)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mMainActivity.refreshDate();
                    }
                })
                .positiveText(R.string.confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mDatas.remove(position);
                        notifyItemRemoved(position);
                        item.delete();
                        mMainActivity.refreshDate();
                        String deleteMsg = "你删除了" + timeStr + "时间的纪录";
                        ToastUtils.showLongToast(deleteMsg);
                    }
                })
                .show();
    }


    class Holder extends BaseRecyclerViewAdapter.BaseHolder<Item> {

        public Holder(View itemView) {
            super(itemView);
        }

        private TextView mTime;
        private TextView mType;
        private TextView mAccount;
        private TextView mPaperDiaper;
        private TextView mShit;
        private TextView mEmpty;

        @Override
        protected void findView() {
            mTime = (TextView) findViewById(R.id.time);
            mType = (TextView) findViewById(R.id.type);
            mAccount = (TextView) findViewById(R.id.account);
            mPaperDiaper = (TextView) findViewById(R.id.paper_diaper);
            mShit = (TextView) findViewById(R.id.shit);
            mEmpty = (TextView) findViewById(R.id.empty);
        }

        @Override
        public void bindHolder(Item bean, int index) {
            mTime.setText(TimeUtils.millis2String(bean.getTime(), "HH:mm"));
            mType.setText(bean.getType());
            if (bean.getType().equals("奶") || bean.getType().equals("水")) {
                // 如果是奶或者水
                if (bean.getAccount() == 0) {
                    setTypeAndAccountEmpty();
                } else {
                    mAccount.setText(mMainActivity.getString(R.string.account, bean.getAccount()));
                }
            } else {
                // 如果是辅食
                mAccount.setText(bean.getSupplementFood());
            }
            mPaperDiaper.setVisibility(bean.isPaperDiaper() ? View.VISIBLE : View.GONE);
            mShit.setVisibility(bean.isShit() ? View.VISIBLE : View.GONE);

            if (bean.isShit()) {
                mShit.setVisibility(View.VISIBLE);
                mPaperDiaper.setVisibility(View.GONE);
                mEmpty.setVisibility(View.GONE);
            } else if (bean.isPaperDiaper()) {
                mShit.setVisibility(View.GONE);
                mPaperDiaper.setVisibility(View.VISIBLE);
                mEmpty.setVisibility(View.GONE);
            } else {
                mShit.setVisibility(View.GONE);
                mPaperDiaper.setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);
            }

            //mPaperDiaper.setBackgroundColor(bean.getPaperDiaper() ? Color.BLUE : Color.TRANSPARENT);
            //mShit.setBackgroundColor(bean.getShit() ? Color.BLUE : Color.TRANSPARENT);
        }

        private void setTypeAndAccountEmpty() {
            mType.setText(R.string.shit_or_paper_diaper);
            mAccount.setText(R.string.shit_or_paper_diaper);
        }


    }
}
