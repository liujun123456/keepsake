package com.shunlai.publish.picker

import android.app.Activity
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.publish.picker.adapters.PhotoAdapter
import com.shunlai.publish.picker.collection.AlbumMediaCollection
import com.shunlai.publish.picker.entity.*
import com.shunlai.publish.picker.ui.MediaGridInset
import com.shunlai.router.RouterManager
import com.shunlai.ui.moveRv.MoveCallBack
import kotlinx.android.synthetic.main.fragment_photo_picker.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Liu
 * @Date   2021/4/9
 * @mobile 18711832023
 */
class PhotoPickerFragment:Fragment(), AlbumMediaCollection.AlbumMediaCallbacks {

    companion object{
        private const val EXTRA_ALBUM = "extra_album"
        @JvmStatic
        fun getInstance(al:Album,limitSize:Int):PhotoPickerFragment{
            val fragment=PhotoPickerFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_ALBUM,al)
            args.putInt(RunIntentKey.LIMIT_SIZE,limitSize)
            fragment.arguments=args
            return fragment
        }
    }

    private val collection= AlbumMediaCollection()

    private var photoDates= mutableListOf<Item>()

    private val mAdapter by lazy {
        PhotoAdapter(activity!!, mutableListOf(),rv_photo,limitSize)
    }
    private val album by lazy {
        arguments?.getParcelable<Album>(EXTRA_ALBUM)
    }
    private val limitSize by lazy {
        arguments?.getInt(RunIntentKey.LIMIT_SIZE,9)?:9
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        return View.inflate(activity, R.layout.fragment_photo_picker,null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRv()
        loadImg(album)
        EventBus.getDefault().post(ChooseUpdateEvent())
    }

    private fun initRv(){
        rv_photo.setHasFixedSize(true)
        rv_photo.layoutManager=GridLayoutManager(activity,3)
        rv_photo.addItemDecoration(MediaGridInset(3,  resources.getDimensionPixelSize(R.dimen.media_grid_spacing), false,true))
        rv_photo.adapter = mAdapter
    }

    private fun loadImg(album:Album?){
        collection.onCreate(activity!!,this)
        collection.load(album)
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
        mAdapter.dates=photoDates
        mAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MoveUpdateEvent){
        mAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PreviewEvent){
        val params= mutableMapOf<String,Any?>()
        params[RunIntentKey.CHOOSE_IMAGE_INDEX]=event.index
        params["extra_album"]=album
        RouterManager.startActivityWithParams(BundleUrl.PHOTO_PREVIEW_ACTIVITY,activity!!,params)
    }

    override fun onDestroy() {
        super.onDestroy()
        collection.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
