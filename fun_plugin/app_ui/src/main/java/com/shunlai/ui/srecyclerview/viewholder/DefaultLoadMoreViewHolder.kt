package com.shunlai.ui.srecyclerview.viewholder

import android.view.View
import com.shunlai.ui.srecyclerview.SRecyclerView
import kotlinx.android.synthetic.main.refresh_footer_view.view.*

/**
 * @author Liu
 * @Date   2019-12-26
 * @mobile 18711832023
 */
class DefaultLoadMoreViewHolder(var view: View):LoadMoreViewHolder(view){

    override fun notifyHeight(height: Int, status: Int) {
        super.notifyHeight(height, status)
        view.spinkit_view.visibility=if (status== SRecyclerView.LoadMore){
            View.VISIBLE
        }else{
            View.GONE
        }

        view.tv_no_more.visibility=if (status== SRecyclerView.NO_MORE){
            View.VISIBLE
        }else{
            View.GONE
        }

        view.ll_empty_value.visibility=if (status== SRecyclerView.EMPTY_VALUE){
            View.VISIBLE
        }else{
            View.GONE
        }
    }
}

