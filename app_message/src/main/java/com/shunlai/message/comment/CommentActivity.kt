package com.shunlai.message.comment

import android.graphics.Rect
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.toast
import com.shunlai.im.face.Emoji
import com.shunlai.im.face.FaceFragment
import com.shunlai.im.face.FaceManager
import com.shunlai.message.R
import com.shunlai.message.comment.adapter.CommentAdapter
import com.shunlai.message.entity.CommentBean
import com.shunlai.message.entity.event.LikeEvent
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.activity_comment_layout.*
import kotlin.math.abs

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class CommentActivity : BaseActivity(),CommentView , CommentAdapter.CommentItemListener ,FaceFragment.OnEmojiClickListener{
    override fun getMainContentResId(): Int= R.layout.activity_comment_layout

    override fun getToolBarResID(): Int= 0

    private val mAdapter by lazy {
        CommentAdapter(mContext, mutableListOf(),this)
    }

    private val mPresenter by lazy {
        CommentPresenter(mContext,this)
    }

    private var currentPosition=0
    private val minHeight = 50
    private var oldBottom = 0
    private var newBottom = 0
    private val rect = Rect()
    private val handler=Handler()
    private var commentBean:CommentBean?=null

    override fun afterView() {
        initTitle()
        initRv()
        initFace()
        initListener()
        mPresenter.queryComment()
    }

    private fun initRv(){
        rv_comment.setLayoutManager(LinearLayoutManager(mContext))
        rv_comment.setAdapter(mAdapter)
        rv_comment.setSRecyclerListener(object : SRecyclerListener {
                override fun loadMore() {
                    mPresenter.loadMoreComment()
                }

                override fun refresh() {

                }

            })
    }

    private fun initTitle(){
        ll_back.setOnClickListener {
            finish()
        }
    }

    private fun initListener(){
        main_layout.viewTreeObserver.addOnGlobalLayoutListener {
            updateWindowHeight()
        }
        v_transition.setOnClickListener {
            hideCommentInput()
        }
        iv_show_emoji.setOnClickListener {
            hideInput(et_msg_input)
            handler.postDelayed({
                updateEmoji()
            },100)
        }
        tv_send.setOnClickListener {
            if (TextUtils.isEmpty(et_msg_input.text.toString())){
                toast("请输入评论内容!")
                return@setOnClickListener
            }
            mPresenter.doComment(commentBean!!,et_msg_input.text.toString())
            et_msg_input.setText("")
            hideCommentInput()
        }
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (abs(verticalOffset) >ScreenUtils.dip2px(mContext,40f)){
                tv_title_content.setText(R.string.comment_title)
            }else{
                tv_title_content.text = ""
            }
        })

    }

    private fun initFace(){
        val fragment= FaceFragment()
        fragment.setListener(this)
        supportFragmentManager.beginTransaction().replace(com.shunlai.im.R.id.emoji_layout,fragment).commitAllowingStateLoss()
    }

    private fun hideCommentInput(){
        if (bottom_menu.visibility==View.GONE)return
        hideInput(et_msg_input)
        bottom_menu.visibility=View.GONE
        v_transition.visibility=View.GONE
        iv_show_emoji.setImageResource(R.mipmap.emoji_icon)
        emoji_layout.visibility=View.GONE
    }

    private fun updateEmoji(){
        if (emoji_layout.visibility== View.VISIBLE){
            emoji_layout.visibility=View.GONE
            iv_show_emoji.setImageResource(com.shunlai.im.R.mipmap.emoji_icon)
            et_msg_input.requestFocus()
            showInput(et_msg_input)
        }else{
            emoji_layout.visibility=View.VISIBLE
            iv_show_emoji.setImageResource(com.shunlai.im.R.mipmap.keyborad_icon)
        }
    }

    private var defaultMargin: Int = 0

    private fun updateWindowHeight() {
        oldBottom = rect.bottom
        main_layout.getWindowVisibleDisplayFrame(rect)
        newBottom = rect.bottom
        if (oldBottom - newBottom > minHeight) {
            val params = bottom_menu.layoutParams as RelativeLayout.LayoutParams
            defaultMargin += oldBottom - newBottom
            params.setMargins(0, 0, 0, defaultMargin)
            bottom_menu.layoutParams = params
        } else if (newBottom - oldBottom > minHeight) {
            val params = bottom_menu.layoutParams as RelativeLayout.LayoutParams
            if (defaultMargin == newBottom - oldBottom) {
                params.setMargins(0, 0, 0, 0)
                bottom_menu.layoutParams = params
                defaultMargin = 0
            } else {
                if (defaultMargin > 0) {
                    defaultMargin -= newBottom - oldBottom
                    params.setMargins(0, 0, 0, defaultMargin)
                    bottom_menu.layoutParams = params
                }
            }
        }
    }


    override fun loadMoreComment(mDates: MutableList<CommentBean>) {
        if (mDates.isEmpty()){
            rv_comment.showNoMore()
        }else{
            mAdapter.mDates.addAll(mDates)
            rv_comment.notifyDataSetChanged()
        }
    }

    override fun refreshComment(mDates: MutableList<CommentBean>) {
        mAdapter.mDates=mDates
        if (mAdapter.mDates.isEmpty()){
            rv_comment.showEmpty()
        }else{
            rv_comment.notifyDataSetChanged()
        }
    }

    override fun updatePraiseState(praiseState:Int) {
        mAdapter.mDates[currentPosition].isLike=praiseState
        rv_comment.notifyItemChanged(currentPosition)
    }

    override fun showLoading(value: String) {
        showBaseLoading()

    }

    override fun dismissLoading() {
        hideBaseLoading()
    }

    override fun onLikeClick(event: LikeEvent) {
        currentPosition=event.position
        mPresenter.doLikeComment(event.commentId)
    }

    override fun onCommentClick(bean:CommentBean) {
        commentBean=bean
        v_transition.visibility=View.VISIBLE
        bottom_menu.visibility= View.VISIBLE
        et_msg_input.requestFocus()
        showInput(et_msg_input)
    }

    override fun onEmojiClick(emoji: Emoji) {
        val index: Int = et_msg_input.selectionStart
        val editable: Editable = et_msg_input.text
        editable.insert(index, emoji.filter)
        FaceManager.handlerEmojiText(et_msg_input, editable.toString(), true)
    }
}
