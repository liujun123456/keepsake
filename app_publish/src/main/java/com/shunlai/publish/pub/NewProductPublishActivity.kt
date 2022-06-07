package com.shunlai.publish.pub

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.*
import com.shunlai.net.util.GsonUtil
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.common.bean.GoodsBean
import com.shunlai.publish.entity.HuaTiBean
import com.shunlai.common.bean.SavePublishBean
import com.shunlai.publish.picker.ui.MediaGridInset
import com.shunlai.publish.pub.adapter.ImageAdapter
import com.shunlai.router.RouterManager
import com.shunlai.ui.moveRv.MoveCallBack
import kotlinx.android.synthetic.main.activity_new_product_publish.*
import kotlinx.android.synthetic.main.item_sign_goods_layout.view.*

/**
 * @author Liu
 * @Date   2021/9/18
 * @mobile 18711832023
 */
class NewProductPublishActivity:BaseActivity(), ProductPublishView, ImageAdapter.OnHandleViewListener {
    override fun getMainContentResId(): Int= R.layout.activity_new_product_publish

    override fun getToolBarResID(): Int=0

    lateinit var mPresenter :ProductPublishPresenter

    private val imageAdapter by lazy {
        ImageAdapter(mContext, mPresenter.selectItem, rv_photo, this)
    }

    override fun afterView() {
        mPresenter= ProductPublishPresenter(this, this)
        initListener()
        initRv()
        buildHtView()
        buildGoodsView()
        mPresenter.getHotHt()
    }

    private fun initRv(){
        rv_photo.layoutManager = GridLayoutManager(mContext, 3)
        rv_photo.addItemDecoration(
            MediaGridInset(
                3, ScreenUtils.dip2px(mContext,16f), false, true
            )
        )
        rv_photo.adapter = imageAdapter
        val helper = ItemTouchHelper(MoveCallBack(imageAdapter))
        helper.attachToRecyclerView(rv_photo)
    }

    private fun initListener(){
        tv_cancel.setOnClickListener {
            dealBack()
        }
        tv_publish.setOnClickListener {
            mPresenter.publish(et_ugc_content.text.toString())
        }
        et_ugc_content.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (TextUtils.isEmpty(it.toString())){
                        tv_publish.alpha=0.4f
                        tv_content_label.visibility=View.INVISIBLE
                    }else{
                        tv_publish.alpha=1.0f
                        if (it.toString().length>1000){
                            tv_content_label.visibility=View.VISIBLE
                            tv_content_label.text="超出${(it.toString().length-20)}"
                        }else{
                            tv_content_label.visibility=View.INVISIBLE
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        tv_choose_ht.setOnClickListener {
            RouterManager.startActivityForResultWithParams(
                BundleUrl.HUA_TI_SEARCH_ACTIVITY,
                this,
                mutableMapOf(),
                Constant.OPEN_HT_REQUEST_CODE
            )
        }
        ll_delete_ht.setOnClickListener {
            mPresenter.ugcHt=null
            buildHtView()
        }
        ll_choose_goods.setOnClickListener {
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.SIGN_GOODS_LIST]=mPresenter.signGoods
            RouterManager.startActivityForResultWithParams(
                BundleUrl.PHOTO_SIGN_ACTIVITY,
                this,
                params,
                Constant.OPEN_CHOOSE_GOODS_REQUEST_CODE
            )
        }

        if (!TextUtils.isEmpty(mPresenter.ugcContent)) {
            et_ugc_content.setText(mPresenter.ugcContent)
            et_ugc_content.setSelection(mPresenter.ugcContent.length - 1)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buildHtView(){
        if (mPresenter.ugcHt!=null){
            ll_hua_ti.visibility= View.VISIBLE
            ll_un_choose_ht.visibility=View.GONE
            mPresenter.ugcHt?.let {
                if (it.activity==true){
                    tv_ht_value.setCompoundDrawablesWithIntrinsicBounds(R.drawable.activity_loading_bg,0,0,0)
                    (tv_ht_value.compoundDrawables[0] as AnimationDrawable).start()
                    tv_ht_value.text = " ${it.tag}"
                }else{
                    tv_ht_value.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                    tv_ht_value.text = "#${it.tag}"
                }
            }
        }else{
            ll_hua_ti.visibility= View.GONE
            ll_un_choose_ht.visibility=View.VISIBLE
        }
    }

    private fun buildGoodsView(){
        ll_goods.removeAllViews()
        if (mPresenter.signGoods.isNotEmpty()){
            tv_choose_goods.visibility=View.GONE
            mPresenter.signGoods.forEach {
                val view=View.inflate(mContext,R.layout.item_sign_goods_layout,null)
                val params = LinearLayout.LayoutParams(ScreenUtils.dip2px(mContext, 28f),ScreenUtils.dip2px(mContext, 28f))
                params.leftMargin = ScreenUtils.dip2px(mContext, 4f)
                view.layoutParams=params
                ImageUtil.showRoundImgWithStringAndRadius(
                    view.iv_goods_img,
                    mContext,
                    it.thumb ?: "",
                    4f)
                if (it.evaType==1){
                    view.iv_eva_type.setImageResource(R.mipmap.recommend_icon)
                }else{
                    view.iv_eva_type.setImageResource(R.mipmap.un_recommend_icon)
                }
                ll_goods.addView(view)
            }
            tv_goods_num.text="(${mPresenter.signGoods.size})"
        }else{
            tv_choose_goods.visibility=View.VISIBLE
        }
    }

    private fun dealBack(){
        if (!TextUtils.isEmpty(et_ugc_content.text.toString())){
            val dialog= AlertDialog.Builder(mContext)
                .setPositiveButton("不保存") { dialog, _ ->
                    dialog.dismiss()
                    PreferenceUtil.removeValueWithKey(RunIntentKey.PUBLISH_SAVE)
                    finish()
                }
                .setNegativeButton("保存") { dialog, _ ->
                    dialog.dismiss()
                    savePubData()
                    finish()
                }
                .setTitle("是否保存草稿").create()
            dialog.show()
        }else{
            finish()
        }
    }

    private fun savePubData(){
        val data=GsonUtil.toJson(SavePublishBean().apply {
            content=et_ugc_content.text.toString()
            mPresenter.ugcHt?.let {
                ht=GsonUtil.toJson(it)
            }
            selectImage=mPresenter.selectItem
            signGoods=mPresenter.signGoods
        })
        PreferenceUtil.putString(RunIntentKey.PUBLISH_SAVE,data)
    }


    override fun removeItem(item: PathItem, position: Int) {
        mPresenter.selectItem.removeAt(position)
        if (mPresenter.selectItem.isEmpty()){
            imageAdapter.showType=1
        }
        imageAdapter.notifyDataSetChanged()
    }

    override fun onTakePhoto() {
        val params = mutableMapOf<String, Any?>()
        params[RunIntentKey.IS_ONLY_PICKER] = true
        params[RunIntentKey.IS_NEED_VIDEO] = true
        if (mPresenter.selectItem.isEmpty()){
            RouterManager.startActivityForResultWithParams(BundleUrl.PHOTO_PICKER_ACTIVITY,this,params,
                Constant.OPEN_PICKER_REQUEST_CODE)
        }else{
            params[RunIntentKey.CHOOSE_IMAGE_ITEM] = imageAdapter.mData
            RouterManager.startActivityForResultWithParams(BundleUrl.PHOTO_PICKER_ACTIVITY,this,params,
                Constant.OPEN_PICKER_REQUEST_CODE)
        }
    }

    override fun showLoading(value: String) {
        showBaseLoading()
    }

    override fun dismissLoading() {
        hideBaseLoading()
    }

    @SuppressLint("SetTextI18n")
    override fun onHtListBack(htList: MutableList<HuaTiBean>) {
        htList.forEach {bean->
            val view=TextView(mContext)
            val params=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            params.rightMargin=ScreenUtils.dip2px(mContext,8f)
            view.layoutParams=params
            view.setPadding(ScreenUtils.dip2px(mContext,6f),ScreenUtils.dip2px(mContext,8f),ScreenUtils.dip2px(mContext,8f),ScreenUtils.dip2px(mContext,6f))
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            view.paint?.isFakeBoldText=true
            view.setTextColor(Color.parseColor("#E6000000"))
            view.text = "# ${bean.tag}"
            view.setBackgroundResource(R.drawable.gray_radius_24_bg)
            view.setOnClickListener {
                mPresenter.ugcHt=bean
                buildHtView()
            }
            ll_ht_list.addView(view)
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            dealBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.OPEN_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val dates = data?.getParcelableArrayListExtra<PathItem>(RunIntentKey.CHOOSE_IMAGE_ITEM)
            dates?.let {
                mPresenter.selectItem.clear()
                mPresenter.selectItem.addAll(it)
                if (it.isNotEmpty()&&it[0].type==2){
                    imageAdapter.showType=2
                }else{
                    imageAdapter.showType=1
                }
                imageAdapter.notifyDataSetChanged()
            }
        }else if (requestCode == Constant.OPEN_HT_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val htValue = data?.getStringExtra("ht_key_word")
            try {
                mPresenter.ugcHt = GsonUtil.fromJson(htValue?:"", HuaTiBean::class.java)
                buildHtView()
            }catch (e:Exception){

            }
        }else if (requestCode==Constant.OPEN_CHOOSE_GOODS_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val dates = data?.getParcelableArrayListExtra<GoodsBean>(RunIntentKey.PUBLISH_GOODS)
            dates?.let {
                mPresenter.signGoods.clear()
                mPresenter.signGoods.addAll(it)
                buildGoodsView()
            }
        }
    }
}
