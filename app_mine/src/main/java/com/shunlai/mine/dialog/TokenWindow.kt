package com.shunlai.mine.dialog

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.BaseApiConfig
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.im.utils.ScreenUtil
import com.shunlai.mine.MineApiConfig
import com.shunlai.mine.MineHttpManager
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.TokenTotalBean
import com.shunlai.router.RouterManager
import kotlinx.android.synthetic.main.window_token_layout.view.*

/**
 * @author Liu
 * @Date   2021/5/18
 * @mobile 18711832023
 */
class TokenWindow(var mContext: Context): PopupWindow() {
    var score="0"
    init {
        val view= View.inflate(mContext, R.layout.window_token_layout,null)
        contentView=view
        width= ViewGroup.LayoutParams.MATCH_PARENT
        height=  ViewGroup.LayoutParams.MATCH_PARENT
        isClippingEnabled=false
        isOutsideTouchable = true
        isFocusable=true
        animationStyle= R.style.PopupBottomInAnimation
        initView(view)
        update()
    }

    private fun initView(view: View){
        view.iv_close_window.setOnClickListener {
            dismiss()
        }
        view.query_more.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.WEB_TITLE]="Token分"
            params[RunIntentKey.WEB_URL]="${MineApiConfig.ROOT_URL}/static/app/xwsapp/#/tokenscore?token=${score}"
            RouterManager.startActivityWithParams(BundleUrl.LIGHT_APP_ACTIVITY,mContext as FragmentActivity,params)
            dismiss()
        }
    }

    fun setToken(token:TokenTotalBean,userType:Int){
        this.score=token.tokenScore?:"0"
        if (userType==0){
            contentView.query_more.visibility=View.VISIBLE
        }else{
            contentView.query_more.visibility=View.GONE
        }
        contentView.tv_token_score.text="Token分   $score"
        contentView.tv_communityInteraction.text=token.communityInteraction?:"0"
        contentView.tv_contentSelection.text=token.contentSelection?:"0"
        contentView.tv_brandInfluence.text=token.brandInfluence?:"0"

    }
}
