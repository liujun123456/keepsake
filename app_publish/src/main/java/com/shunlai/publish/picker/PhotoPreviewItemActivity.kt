package com.shunlai.publish.picker

import android.database.Cursor
import android.view.View
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.publish.picker.adapters.PreviewAdapter
import com.shunlai.publish.picker.collection.AlbumMediaCollection
import com.shunlai.publish.picker.entity.Album
import com.shunlai.publish.picker.entity.Item
import kotlinx.android.synthetic.main.activity_photo_preview.*
import kotlinx.android.synthetic.main.title_photo_picker_layout.*

class PhotoPreviewItemActivity : BaseActivity(), AlbumMediaCollection.AlbumMediaCallbacks  {
    override fun getMainContentResId(): Int=R.layout.activity_photo_preview

    override fun getToolBarResID(): Int= R.layout.title_photo_picker_layout

    override fun setTitleColor(): Int= R.color.black_style_title

    private val mAdapter by lazy {
        PreviewAdapter(mContext,photoDates)
    }

    private var photoDates= mutableListOf<Item>()


    private val currentItem by lazy {
        intent.getIntExtra(RunIntentKey.CHOOSE_IMAGE_INDEX,0)
    }

    private val album by lazy {
        intent.getParcelableExtra<Album>("extra_album")
    }

    private val collection= AlbumMediaCollection()

    override fun afterView() {
        initTitle()
        frag_pager.adapter=mAdapter
        frag_pager.offscreenPageLimit=1
        frag_pager.layoutAnimation=null
        loadImg(album)
    }

    private fun loadImg(album:Album?){
        collection.onCreate(this,this)
        collection.load(album)
    }

    private fun initTitle(){
        iv_close.setOnClickListener {
            finish()
        }
        tv_title_name.visibility=View.GONE
    }

    override fun onAlbumMediaReset() {

    }

    override fun onAlbumMediaLoad(cursor: Cursor?) {
        photoDates.clear()
        cursor?.let {
            for (i in 0 until it.count){
                it.moveToPosition(i)
                val data=Item.valueOf(it)
                photoDates.add(data)
            }
        }
        if (photoDates.size>0){
            mAdapter.notifyDataSetChanged()
            frag_pager.currentItem=currentItem
        }
    }
}
