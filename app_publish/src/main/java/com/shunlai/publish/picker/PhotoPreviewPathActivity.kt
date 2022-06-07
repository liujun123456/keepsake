package com.shunlai.publish.picker

import android.view.View
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.publish.picker.adapters.PreviewPathAdapter
import kotlinx.android.synthetic.main.activity_photo_preview.*
import kotlinx.android.synthetic.main.title_photo_picker_layout.*

class PhotoPreviewPathActivity : BaseActivity() {
    override fun getMainContentResId(): Int=R.layout.activity_photo_preview

    override fun getToolBarResID(): Int= R.layout.title_photo_picker_layout

    override fun setTitleColor(): Int= R.color.black_style_title

    private val mAdapter by lazy {
        PreviewPathAdapter(mContext,chooseItemImg)
    }

    private val chooseItemImg by lazy {
        intent.getParcelableArrayListExtra<PathItem>(RunIntentKey.CHOOSE_IMAGE_ITEM)?: mutableListOf<PathItem>()
    }

    private val currentItem by lazy {
        intent.getIntExtra(RunIntentKey.CHOOSE_IMAGE_INDEX,0)
    }

    override fun afterView() {
        initTitle()
        frag_pager.adapter=mAdapter
        frag_pager.offscreenPageLimit=1
        frag_pager.currentItem=currentItem
        frag_pager.layoutAnimation=null
    }

    private fun initTitle(){
        iv_close.setOnClickListener {
            finish()
        }
        tv_title_name.visibility=View.GONE
    }
}
