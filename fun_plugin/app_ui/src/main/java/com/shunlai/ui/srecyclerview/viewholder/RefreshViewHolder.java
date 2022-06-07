package com.shunlai.ui.srecyclerview.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Liu
 * @Date 2019-11-26
 * @mobile 18711832023
 */
public class RefreshViewHolder extends RecyclerView.ViewHolder {

    public RefreshViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void notifyHeight(int height,int status){
        itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                height));
    }
}
