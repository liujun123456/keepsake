package com.shunlai.ui.srecyclerview.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.shunlai.ui.srecyclerview.SRecyclerView;
import com.shunlai.ui.srecyclerview.viewholder.DefaultLoadMoreViewHolder;
import com.shunlai.ui.srecyclerview.viewholder.DefaultRefreshViewHolder;
import com.shunlai.ui.srecyclerview.viewholder.LoadMoreViewHolder;
import com.shunlai.ui.srecyclerview.viewholder.RefreshViewHolder;
import com.shunlai.ui.srecyclerview.views.FooterView;
import com.shunlai.ui.srecyclerview.views.HeaderView;

/**
 * @author Liu
 * @Date 2019-11-25
 * @mobile 18711832023
 */
public abstract class SRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private int currentStatus=1;

    private Context mContext;

    private static final int REFRESH_TYPE=10086;

    private static final int LOAD_TYPE=10087;

    private int headHeight;

    public SRecyclerAdapter(Context context){
        mContext=context;
    }

    private RefreshViewHolder refreshViewHolder;

    private LoadMoreViewHolder loadMoreViewHolder;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType==REFRESH_TYPE){

            return getRefreshViewHolder();

        }else if (viewType==LOAD_TYPE){
            return getLoadMoreViewHolder();
        }
        return onCreateHolder(viewGroup,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (position==0){
            if (currentStatus== SRecyclerView.Refresh||currentStatus==SRecyclerView.MOVING){
                ((RefreshViewHolder)viewHolder).notifyHeight(headHeight,currentStatus);
            }else {
                ((RefreshViewHolder)viewHolder).notifyHeight(1,currentStatus);
            }
        }else if (position==getItemCount()-1){
            if (currentStatus==SRecyclerView.LoadMore){
                ((LoadMoreViewHolder)viewHolder).notifyHeight(ViewGroup.LayoutParams.WRAP_CONTENT,currentStatus);
            }else if (currentStatus==SRecyclerView.EMPTY_VALUE){
                ((LoadMoreViewHolder)viewHolder).notifyHeight(ViewGroup.LayoutParams.MATCH_PARENT,currentStatus);
            }else if (currentStatus==SRecyclerView.NO_MORE){
                ((LoadMoreViewHolder)viewHolder).notifyHeight(ViewGroup.LayoutParams.WRAP_CONTENT,currentStatus);
            }else {
                ((LoadMoreViewHolder)viewHolder).notifyHeight(1,currentStatus);
            }
        }else {
            onBindHolder(viewHolder,position-1);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return REFRESH_TYPE;
        }else if (position==getItemCount()-1){
            return LOAD_TYPE;
        }
        return getViewType(position-1);
    }

    @Override
    public int getItemCount() {
        return getCount()+2;
    }


    public abstract RecyclerView.ViewHolder onCreateHolder(@NonNull ViewGroup viewGroup, int viewType);

    public abstract void onBindHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position);

    public abstract int getCount();

    public int getViewType(int position){
        return 0;
    }



    public void notifyStatus(int Status,int headHeight){
        this.headHeight=headHeight;
        currentStatus=Status;
        if (currentStatus== SRecyclerView.Refresh||currentStatus==SRecyclerView.MOVING||currentStatus==SRecyclerView.Ready){
            notifyItemChanged(0);
        }
        if (currentStatus==SRecyclerView.LoadMore||currentStatus==SRecyclerView.NO_MORE
                ||currentStatus==SRecyclerView.EMPTY_VALUE){
            notifyItemChanged(getItemCount()-1);
        }
    }

    @Deprecated
    public void setHeadViewHolder(RefreshViewHolder refreshViewHolder){
        this.refreshViewHolder=refreshViewHolder;
    }

    @Deprecated
    public void setFootViewHolder(LoadMoreViewHolder loadMoreViewHolder){
        this.loadMoreViewHolder=loadMoreViewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
        }
    }

    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if ( layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }

    protected void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
        StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        if (getItemViewType(position)==10086 || getItemViewType(position)==10087){
            p.setFullSpan(true);
        }

    }

    public RefreshViewHolder getRefreshViewHolder(){
            HeaderView view =new HeaderView(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            refreshViewHolder=new DefaultRefreshViewHolder(view);
        return refreshViewHolder;
    }

    public LoadMoreViewHolder getLoadMoreViewHolder(){
            FooterView view =new FooterView(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            loadMoreViewHolder=new DefaultLoadMoreViewHolder(view);
        return loadMoreViewHolder;
    }

}
