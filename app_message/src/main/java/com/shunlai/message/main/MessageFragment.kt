package com.shunlai.message.main

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.shunlai.common.BaseFragment
import com.shunlai.common.utils.BundleUrl
import com.shunlai.im.entity.ConversionItem
import com.shunlai.message.R
import com.shunlai.message.entity.HomeMsgBean
import com.shunlai.message.entity.event.NotifyMsgEvent
import com.shunlai.message.entity.resp.HomeMsgResp
import com.shunlai.message.main.adapter.MainMessageAdapter
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.fragment_message_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class MessageFragment:BaseFragment(),MessageView {

    override fun createView(): Int= R.layout.fragment_message_layout

    override fun createTitle(): Int=0

    private val mAdapter by lazy {
        MainMessageAdapter(mContext, mutableListOf())
    }

    private val mPresenter by lazy {
        MessagePresenter(mContext,this)
    }

    override fun afterView() {
        initRv()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.queryTotalMsgNum()
    }

    private fun initRv(){
        rv_main_message.layoutManager=LinearLayoutManager(mContext)
        rv_main_message.adapter=mAdapter
    }

    private fun initListener(){
        fun_collect.setOnClickListener {
            mPresenter.updateCollectRead()
            RouterManager.startActivityWithParams(BundleUrl.COLLECT_ACTIVITY,activity!!)
        }

        fun_add_attention.setOnClickListener {
            mPresenter.updateAttentionRead()
            RouterManager.startActivityWithParams(BundleUrl.ATTENTION_ACTIVITY,activity!!)
        }

        fun_eva.setOnClickListener {
            mPresenter.updateCommentRead()
            RouterManager.startActivityWithParams(BundleUrl.COMMENT_ACTIVITY,activity!!)
        }
    }

    override fun getHomeMsg(data: HomeMsgResp) {
        if (data.commentNum?:0>0){
            tv_comment_count.visibility=View.VISIBLE
            tv_comment_count.text=data.commentNum.toString()
        }else{
            tv_comment_count.visibility=View.INVISIBLE
        }
        if (data.followNum?:0>0){
            tv_attention_count.visibility= View.VISIBLE
            tv_attention_count.text=data.followNum.toString()
        }else{
            tv_attention_count.visibility= View.INVISIBLE
        }
        if (data.likesNum?:0>0){
            tv_collect_count.visibility=View.VISIBLE
            tv_collect_count.text=data.likesNum.toString()
        }else{
            tv_collect_count.visibility=View.INVISIBLE
        }
        if (data.pushMessage==null){
            rl_push_layout.visibility=View.GONE
        }else{
            rl_push_layout.visibility=View.VISIBLE
            data.pushMessage?.let {
                tv_push_time.text=it.createTime
                tv_push_content.text=it.content
                if (data.pushNum?:0>0){
                    tv_push_count.text=data.pushNum.toString()
                    tv_push_count.visibility=View.VISIBLE
                }else{
                    tv_push_count.visibility=View.GONE
                }
            }
            rl_push_layout.setOnClickListener {
                EventBus.getDefault().post(NotifyMsgEvent(2))
                RouterManager.startActivityWithParams(BundleUrl.PUSH_MSG_ACTIVITY,mContext as Activity)
            }
        }
        if (data.sysMessage==null){
            rl_sys_layout.visibility=View.GONE
        }else{
            rl_sys_layout.visibility=View.VISIBLE
            data.sysMessage?.let {
                tv_sys_time.text=it.createTime
                tv_sys_content.text=it.content
                if (data.pushNum?:0>0){
                    tv_sys_count.text=data.systemNum.toString()
                    tv_sys_count.visibility=View.VISIBLE
                }else{
                    tv_sys_count.visibility=View.GONE
                }
            }
            rl_sys_layout.setOnClickListener {
                EventBus.getDefault().post(NotifyMsgEvent(1))
                RouterManager.startActivityWithParams(BundleUrl.SYS_MSG_ACTIVITY,mContext as Activity)
            }
        }
    }

    override fun addConversion(conversationList: MutableList<ConversionItem>) {
        refreshAdapter(conversationList)
    }

    override fun updateConversion(conversationList: MutableList<ConversionItem>) {
        refreshAdapter(conversationList)
    }

    private fun refreshAdapter(conversationList: MutableList<ConversionItem>){
        val dataList= mutableListOf<HomeMsgBean>()
        conversationList.forEach {
            dataList.add(mPresenter.buildHomeMsgBean(it))
        }
        val it=mAdapter.mdates.iterator()
        while (it.hasNext()){
            val item=it.next()
            dataList.forEach {item2->
                if (item.conversationID==item2.conversationID){
                    it.remove()
                }
            }
        }
        mAdapter.mdates.addAll(0,dataList)
        mAdapter.notifyDataSetChanged()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            mPresenter.queryTotalMsgNum()
            mPresenter.reloadConversation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }
}
