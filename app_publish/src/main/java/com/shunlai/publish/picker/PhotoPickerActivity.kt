package com.shunlai.publish.picker

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getui.gs.sdk.GsManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.ScreenUtils
import com.shunlai.common.utils.StatusBarUtil
import com.shunlai.net.util.GsonUtil
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.publish.picker.adapters.PhotoChooseAdapter
import com.shunlai.publish.picker.adapters.PhotoGroupAdapter
import com.shunlai.publish.picker.collection.AlbumCollection
import com.shunlai.publish.picker.collection.SelectCollection
import com.shunlai.publish.picker.entity.Album
import com.shunlai.publish.picker.entity.ChooseUpdateEvent
import com.shunlai.publish.picker.entity.SelectionSpec
import com.shunlai.publish.picker.ui.MediaGridInset
import com.shunlai.publish.picker.ui.PhotoGroupWindow
import com.shunlai.router.RouterManager
import com.shunlai.ui.moveRv.MoveCallBack
import kotlinx.android.synthetic.main.activity_photo_picker.*
import kotlinx.android.synthetic.main.title_photo_picker_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import org.json.JSONObject

class PhotoPickerActivity : BaseActivity(), AlbumCollection.AlbumCallbacks, PhotoGroupAdapter.ItemClickListener {
    override fun getMainContentResId(): Int=R.layout.activity_photo_picker

    override fun getToolBarResID(): Int=R.layout.title_photo_picker_layout

    override fun setTitleColor(): Int=R.color.black_style_title

    private val mWindow by lazy {
        PhotoGroupWindow(mContext,this)
    }

    private var groupData= mutableListOf<Album>()

    private val mChooseAdapter by lazy {
        PhotoChooseAdapter(mContext)
    }

    private val isOnlyPicker by lazy {
        intent.getBooleanExtra(RunIntentKey.IS_ONLY_PICKER,false)
    }

    private val defaultType by lazy {
        intent.getIntExtra("defaultType",1)
    }

    private val limitSize by lazy {
        intent.getIntExtra(RunIntentKey.LIMIT_SIZE,9)
    }

    private val isNeedVideo by lazy {
        intent.getBooleanExtra(RunIntentKey.IS_NEED_VIDEO,false)
    }

    private val collection=AlbumCollection()

    private var currentLimit=1

    override fun afterView() {
        SelectionSpec.showType=defaultType
        SelectCollection.selectItem.clear()
        SelectCollection.selectItem.addAll(intent.getParcelableArrayListExtra(RunIntentKey.CHOOSE_IMAGE_ITEM)?: mutableListOf())
        EventBus.getDefault().register(this)
        StatusBarUtil.showLightStatusBarIcon(this)
        initTitle()
        initListener()
        loadGroupData()
        intiRv()
        currentLimit=limitSize
    }

    private fun initListener(){
        if (isOnlyPicker&&!isNeedVideo){
            if (defaultType==1){
                tv_choose_video.visibility=View.GONE
            }else{
                tv_choose_img.visibility=View.GONE
            }
        }else{
            tv_choose_video.visibility=View.VISIBLE
        }
        tv_next.setOnClickListener {
            if (isOnlyPicker){
                val intent= Intent()
                intent.putParcelableArrayListExtra(RunIntentKey.CHOOSE_IMAGE_ITEM,SelectCollection.selectItem as ArrayList<out Parcelable>)
                intent.putExtra(RunIntentKey.CHOOSE_IMAGE_ITEM_RESOURCE,GsonUtil.toJson(SelectCollection.selectItem))
                setResult(Activity.RESULT_OK,intent)
                finish()
            }else{
                sensorsTrackChooseItem(SelectionSpec.showType,SelectCollection.selectItem.size)
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.CHOOSE_IMAGE_ITEM] = SelectCollection.selectItem
                params[RunIntentKey.PUBLISH_TYPE]=SelectionSpec.showType
                RouterManager.startActivityWithParams(BundleUrl.PHOTO_SIGN_ACTIVITY,this,params)
                finish()
            }
        }
        tv_choose_img.setOnClickListener {
            if (SelectionSpec.showType==1){
                return@setOnClickListener
            }
            SelectionSpec.showType=1
            currentLimit=limitSize
            tv_choose_img.textColor=Color.parseColor("#ffffff")
            tv_choose_video.textColor=Color.parseColor("#999999")
            collection.onDestroy()
            SelectCollection.selectItem.clear()
            loadGroupData()
        }
        tv_choose_video.setOnClickListener {
            if (SelectionSpec.showType==2){
                return@setOnClickListener
            }
            SelectionSpec.showType=2
            currentLimit=1
            tv_choose_video.textColor=Color.parseColor("#ffffff")
            tv_choose_img.textColor=Color.parseColor("#999999")
            collection.onDestroy()
            SelectCollection.selectItem.clear()
            loadGroupData()
        }
    }

    private fun intiRv(){
        rv_choose_img.setHasFixedSize(true)
        rv_choose_img.layoutManager= LinearLayoutManager(mContext, RecyclerView.HORIZONTAL,false)
        rv_choose_img.adapter=mChooseAdapter
        rv_choose_img.addItemDecoration(MediaGridInset(1,  ScreenUtils.dip2px(mContext,8f), true,false))
        val helper= ItemTouchHelper(MoveCallBack(mChooseAdapter))
        helper.attachToRecyclerView(rv_choose_img)
    }

    private fun loadGroupData(){
        collection.onCreate(this,this)
        collection.loadAlbums()
    }

    private fun initTitle(){
        iv_close.setOnClickListener {
            finish()
        }
        tv_title_name.setOnClickListener {
            //显示弹框
            showGroupWindow()
        }
    }

    private fun showGroupWindow(){
        if (groupData.size!=0){
            if (mWindow.isShowing){
                mWindow.dismiss()
                iv_close.visibility= View.VISIBLE
                tv_title_name.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.arrow_expand,0)
            }else{
                mWindow.showData(base_toolbar,groupData)
                iv_close.visibility= View.GONE
                tv_title_name.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.arrow_reduce,0)
            }
        }
    }

    private fun chooseGroup(al:Album){
        tv_title_name.text=al.getDisplayName(mContext)
        val fragment=PhotoPickerFragment.getInstance(al,currentLimit)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commitAllowingStateLoss()
    }

    override fun onAlbumLoad(cursor: Cursor?) {
        groupData.clear()
        cursor?.let {
            for (i in 0 until it.count){
                it.moveToPosition(i)
                val data=Album.valueOf(it)
                groupData.add(data)
            }
        }
        if (groupData.size>0){
            chooseGroup(groupData[0])
        }else{
            toast("没有找到视频文件")
            SelectionSpec.showType=1
            currentLimit=limitSize
            tv_choose_img.textColor=Color.parseColor("#ffffff")
            tv_choose_video.textColor=Color.parseColor("#999999")
            collection.onDestroy()
            SelectCollection.selectItem.clear()
            loadGroupData()
        }
    }

    override fun onAlbumReset() {

    }

    override fun onItemClick(al: Album) {
        chooseGroup(al)
        mWindow.dismiss()
        iv_close.visibility= View.VISIBLE
        tv_title_name.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.arrow_expand,0)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ChooseUpdateEvent){
        if (SelectionSpec.showType==1){
            if (SelectCollection.selectItem.size>0){
                ll_choose_img.visibility=View.VISIBLE
                ll_choice_option.visibility=View.GONE
                ll_next_action.visibility=View.VISIBLE
                tv_cout_notice.text=mContext.resources.getString(R.string.photo_long_click_notice,SelectCollection.selectItem.size,currentLimit)
            }else{
                ll_choose_img.visibility=View.GONE
                ll_choice_option.visibility=View.VISIBLE
                ll_next_action.visibility=View.GONE
            }
            if(event.action==1){
                mChooseAdapter.notifyDataSetChanged()
            }else if (event.action==2){
                mChooseAdapter.notifyDataSetChanged()
            }else{
                mChooseAdapter.notifyDataSetChanged()
            }
        }else{
            if (SelectCollection.selectItem.size>0){
                ll_choice_option.visibility=View.GONE
                ll_next_action.visibility=View.VISIBLE
                tv_cout_notice.text="你只能选择一个视频"
            }else{
                ll_choice_option.visibility=View.VISIBLE
                ll_next_action.visibility=View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        collection.onDestroy()
        SelectCollection.selectItem.clear()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 神策---点击完成选择项目
     */
    private fun sensorsTrackChooseItem(type:Int,num:Int){
        val params= JSONObject()
        params.put("page_name",screenUrl)
        params.put("item_type",type)
        params.put("item_num",num)
        GsManager.getInstance().onEvent("SelectItemClick", params)
    }
}
