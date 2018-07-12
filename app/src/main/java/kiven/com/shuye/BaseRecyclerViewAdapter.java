package kiven.com.shuye;

import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2017-3-31.
 * Desc: RecyclerView的Adapter的基类，可在列表类型的RecyclerView中添加头尾，设置点击事件
 * <p>
 * 泛型T： 数据的类型
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseHolder> {


    public static final int TYPE_NORMAL = 0;

    /**
     * RecyclerView中的数据
     */
    public List<T> mDatas;


    /*************************************** Listener *************************************/
    /**
     * item点击事件的监听者
     */
    protected BaseRecyclerViewAdapter.OnItemClickListener mListener;


    public interface OnItemClickListener {
        /**
         * 此处的index是item在Data中的index
         *
         * @param itemView
         * @param index    item在Data中的index
         */
        void onItemClick(View itemView, int index);
    }

    /**
     * 给RecyclerView的item设置点击事件的监听者
     *
     * @param li 具体的处理监听事件的操作者
     */
    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    /**
     * item长按事件的监听者
     */
    protected BaseRecyclerViewAdapter.OnItemLongClickListener mLongListener;

    public interface OnItemLongClickListener {
        /**
         * 此处的index是item在Data中的index
         *
         * @param itemView
         * @param index
         */
        void onItemLongClick(View itemView, int index);
    }

    /**
     * 给RecyclerView的item设置长按事件的监听者
     *
     * @param li 具体的处理监听事件的操作者
     */
    public void setOnItemLongClickListener(OnItemLongClickListener li) {
        mLongListener = li;
    }
    /*************************************** Listener ******************************************/


    /**
     * 设置数据
     *
     * @param datas 展示在列表中的数据
     */
    public void setData(List<T> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }


    /**
     * 添加数据
     *
     * @param datas 展示在列表中的数据
     */
    public void addDatas(List<T> datas) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.addAll(datas);
        notifyDataSetChanged();

    }

    /**
     * 根据数据的索引，返回对应的数据，当没有数据集合的时候，返回值为空
     */
    @Nullable
    public T getData(int index) {
        return mDatas == null ? null : mDatas.get(index);
    }

    /**
     * 获得所有的item的数量
     */
    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size() + mHeaderViews.size() + mFooterViews.size();
    }

    /**
     * 获得列表中除去Header和Footer的数据的数量
     */
    public int getDataCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    @Override
    public int getItemViewType(int position) {

        // 如果是Header
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        }
        // 如果是Footer
        if (isFooterViewPos(position)) {

            return mFooterViews.keyAt(position - getHeadersCount() - mDatas.size());
        }
        // 普通的Item
        int index = position - getHeadersCount();
        return getItemType(index);
    }


    /**
     * 获得普通的itemType，如果子类中有不同的item要复写这个方法
     *
     * @param index 在mData中的位置
     */
    public int getItemType(int index) {
        return TYPE_NORMAL;
    }

    public int getRealIndex(int index) {
        return index - getHeadersCount();
    }

    /**
     * 删除某个item，主要是对数据的处理
     *
     * @param index 在视图中的index
     */
    public void removeItem(int index) {
        mDatas.remove(getRealIndex(index));
        notifyItemRemoved(getRealIndex(index));
        notifyItemRangeRemoved(getRealIndex(index), getItemCount());
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return new EmptyHolder(mHeaderViews.get(viewType));
        }
        if (mFooterViews.get(viewType) != null) {
            return new EmptyHolder(mFooterViews.get(viewType));
        }

        // 创建ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        return createHolder(view, viewType);
    }


    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {

        // 是Header或者Footer，不做处理
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            return;
        }
        // 普通Item，获得数据对应的position，和数据，并处理
        final int index = position - getHeadersCount();
        final T bean = mDatas.get(index);
        final View itemView = holder.itemView;
        holder.bindHolder(bean, index);

        // 点击事件，这里没有为头和尾设置点击事件
        if (mListener != null && itemView != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(itemView, index);
                }
            });
        }
        // 长按事件
        if (mLongListener != null && itemView != null) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongListener.onItemLongClick(itemView, index);
                    return false;
                }
            });
        }
    }

    /**
     * 子类必须实现的方法，返回item布局对应的id
     *
     * @param viewType 展示的item的种类
     * @return 对应的资源文件ID
     */
    protected abstract int getLayoutId(int viewType);

    /**
     * 子类必须实现的方法，根据不同的itemType返回对应的Holder
     *
     * @param view     资源ID解析成的View
     * @param viewType 展示的item的种类
     * @return 展示的item对应的Holder，应为BaseHolder的子类
     */
    protected abstract BaseHolder<T> createHolder(View view, int viewType);


    /**
     * Holder的基类
     * <p>
     * 泛型K： 数据的类型
     */
    public static abstract class BaseHolder<K> extends RecyclerView.ViewHolder {

        public BaseHolder(View itemView) {
            super(itemView);
            findView();
        }

        /**
         * 所有的FindViewById的方法
         */
        protected abstract void findView();

        protected View findViewById(int id) {
            return itemView.findViewById(id);
        }

        /**
         * 设置数据的方法
         *
         * @param bean  item展示的数据的集合，封装成的对象
         * @param index item在data，数据中的真实位置
         */
        public abstract void bindHolder(K bean, int index);

    }

    /**
     * Header和Footer对应的ViewHolder的空实现
     */
    static class EmptyHolder<K> extends BaseHolder<K> {
        public EmptyHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void findView() {
        }

        @Override
        public void bindHolder(K bean, int index) {
        }

    }

    /************************** 多个Header和Footer的实现 ******************************/
    // 实现的原理，是将每个Header和Footer作为一种itemType，绑定ItemViewType的值，并放在MAP中
    // 这里使用SparseArray可以存放int-Object类型的键值对

    /**
     * TODO 静态常量开始的数值是否需要更改
     */
    protected static final int BASE_ITEM_TYPE_HEADER = 10000;
    protected static final int BASE_ITEM_TYPE_FOOTER = 20000;
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }

    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
        notifyItemInserted(getHeadersCount());
    }

    public void addFooterView(View view) {
        mFooterViews.put(mFooterViews.size() + BASE_ITEM_TYPE_FOOTER, view);
        notifyItemInserted(getItemCount());
    }

    public void removeFooterView() {
        mFooterViews.removeAt(0);
        notifyItemRemoved(getItemCount());
    }

    public View getHeaderView() {
        return getHeaderView(0);
    }

    public View getFooterView() {
        return getFooterView(0);
    }

    public View getHeaderView(int index) {
        return mHeaderViews.get(mHeaderViews.keyAt(index));
    }

    public View getFooterView(int index) {
        return mFooterViews.get(mFooterViews.keyAt(index));
    }

    /**
     * 根据在List中的位置判断是否是头或者尾
     *
     * @param position item在ListView中的位置
     */
    protected boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    protected boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getDataCount();
    }
    /************************** 多个Header和Footer的实现 ******************************/

}
