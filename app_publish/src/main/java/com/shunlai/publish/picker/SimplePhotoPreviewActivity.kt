package com.shunlai.publish.picker

import android.net.Uri
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.publish.R
import kotlinx.android.synthetic.main.activity_img_preview_layout.*
import java.io.File
import java.lang.Exception

/**
 * @author Liu
 * @Date   2020/11/24
 * @mobile 18711832023
 */
class SimplePhotoPreviewActivity: BaseActivity() {
    override fun getMainContentResId(): Int=
        R.layout.activity_img_preview_layout

    override fun getToolBarResID(): Int=0

    override fun afterView() {
        val imgUrl=intent.getStringExtra(RunIntentKey.IMAGE_URL)
        imgUrl?.let {
            try {
                if (imgUrl.startsWith("http")) {
                    ImageUtil.showPreView(iv_pre_img, mContext, Uri.parse(imgUrl))
                }else{
                    ImageUtil.showPreView(iv_pre_img, mContext, Uri.fromFile(File(imgUrl) ))
                }
            }catch (e:Exception){

            }
        }
        iv_pre_img.setOnViewTapListener { view, x, y ->
            finish()
        }
    }
}
