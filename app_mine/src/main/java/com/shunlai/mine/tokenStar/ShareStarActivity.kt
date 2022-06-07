package com.shunlai.mine.tokenStar

import android.annotation.SuppressLint
import android.net.Uri
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.FileUtil
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.StatusBarUtil
import com.shunlai.mine.R
import com.shunlai.share.WeChatUtil
import kotlinx.android.synthetic.main.activity_share_star_layout.*

/**
 * @author Liu
 * @Date   2021/9/3
 * @mobile 18711832023
 */
class ShareStarActivity:BaseActivity() {
    override fun getMainContentResId(): Int= R.layout.activity_share_star_layout

    override fun getToolBarResID(): Int=0

    private val posterUrl by lazy {
        intent.getStringExtra(RunIntentKey.POSTER_IMG_URL)
    }

    @SuppressLint("CheckResult")
    override fun afterView() {
        StatusBarUtil.showLightStatusBarIcon(this)
        initListener()
        ImageUtil.showPreView(iv_poster,mContext, Uri.parse(posterUrl?:""))

    }

    private fun initListener(){
        tv_share_we_chat.setOnClickListener {
            val bitmap= FileUtil.createBitmapFromView(iv_poster)
            WeChatUtil.getInstance().shareWeChatWithImg(bitmap)
        }
        tv_share_circle.setOnClickListener {
            val bitmap= FileUtil.createBitmapFromView(iv_poster)
            WeChatUtil.getInstance().shareCircleWithImg(bitmap)

        }
        tv_save_local.setOnClickListener {
            val bitmap= FileUtil.createBitmapFromView(iv_poster)
            FileUtil.saveBitmapToPhoto(mContext,bitmap)

        }
        tv_cancel_share.setOnClickListener {
            finish()
        }
        iv_close.setOnClickListener {
            finish()
        }

    }
}
