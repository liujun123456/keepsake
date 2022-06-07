package com.shunlai.publish.pub

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.PathItem
import com.shunlai.common.utils.*
import com.shunlai.net.util.GsonUtil
import com.shunlai.publish.Constant
import com.shunlai.publish.R
import com.shunlai.publish.entity.HuaTiBean
import com.shunlai.publish.picker.ui.MediaGridInset
import com.shunlai.publish.pub.adapter.ImageAdapter
import com.shunlai.publish.pub.adapter.SimpleProductAdapter
import com.shunlai.router.RouterManager
import com.shunlai.ui.ListenerScrollView
import com.shunlai.ui.moveRv.MoveCallBack
import kotlinx.android.synthetic.main.activity_product_publish.*
import kotlinx.android.synthetic.main.title_photo_sign_layout.*
import org.jetbrains.anko.textColor

class ProductPublishActivity : BaseActivity(), ProductPublishView,
    ImageAdapter.OnHandleViewListener {
    override fun getMainContentResId(): Int = R.layout.activity_product_publish

    override fun getToolBarResID(): Int = R.layout.title_photo_sign_layout

    override fun setTitleColor(): Int = R.color.gray_f7

    private val imageAdapter by lazy {
        ImageAdapter(mContext, mPresenter.selectItem, rv_photo, this)
    }

    private val simpleProductAdapter by lazy {
        SimpleProductAdapter(mContext, mPresenter.signGoods)
    }

    private val mPresenter by lazy {
        ProductPublishPresenter(this, this)
    }


    override fun afterView() {
        initTitle()
        initRv()
        initListener()
        checkParams()
    }

    private fun initTitle() {
        ll_back.setOnClickListener {
            setResult()
        }
    }

    private fun initRv() {
        if (mPresenter.publishType == 1) {
            rv_photo.layoutManager = GridLayoutManager(mContext, 3)
            rv_photo.addItemDecoration(
                MediaGridInset(
                    3,ScreenUtils.dip2px(mContext,16f), false, true
                )
            )
            rv_photo.adapter = imageAdapter
            val helper = ItemTouchHelper(MoveCallBack(imageAdapter))
            helper.attachToRecyclerView(rv_photo)
        } else {

            rv_photo.visibility = View.GONE
            rl_video.visibility = View.VISIBLE
            updateVideoState()
        }


        rv_product.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
        rv_product.adapter = simpleProductAdapter

    }


    @SuppressLint("SetTextI18n")
    private fun initListener() {
        tv_add_hua_ti.setOnClickListener {
            RouterManager.startActivityForResultWithParams(
                BundleUrl.HUA_TI_SEARCH_ACTIVITY,
                this,
                mutableMapOf(),
                Constant.OPEN_HT_REQUEST_CODE
            )
        }

        sl_view.setTopScrollListener(object : ListenerScrollView.TopScrollListener {
            override fun onScroll(top: Int) {
                if (top > ScreenUtils.dip2px(mContext, 40f)) {
                    tv_title_content.setText(R.string.publish_title)
                } else {
                    tv_title_content.text = ""
                }
            }
        })

        et_ugc_content.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                tv_content_label.text = "${s?.length ?: 0}/1000"
                checkParams()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        et_ugc_content.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                mPresenter.publish( et_ugc_content.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        tv_publish.setOnClickListener {
            mPresenter.publish( et_ugc_content.text.toString())

        }

        ll_delete_ht.setOnClickListener {
            tv_add_hua_ti.visibility = View.VISIBLE
            ll_hua_ti.visibility = View.GONE
            tv_ht_value.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
            tv_ht_value.text = ""
            mPresenter.ugcHt = null
        }

        ll_add_image.setOnClickListener {
            val params = mutableMapOf<String, Any?>()
            params[RunIntentKey.IS_ONLY_PICKER] = true
            params["defaultType"] = 2
            params[RunIntentKey.LIMIT_SIZE]=1
            RouterManager.startActivityForResultWithParams(
                BundleUrl.PHOTO_PICKER_ACTIVITY,
                this,
                params,
                Constant.OPEN_PICKER_REQUEST_CODE
            )
        }

        rl_show_img.setOnClickListener {
            val params = mutableMapOf<String, Any?>()
            params[RunIntentKey.CAMERA_IMG_PATH] = mPresenter.selectItem[0].path
            params[RunIntentKey.CAMERA_VIDEO_PATH] = Uri.parse(mPresenter.selectItem[0].path)
            RouterManager.startActivityWithParams(BundleUrl.VIDEO_VIEW_ACTIVITY, mContext as FragmentActivity, params)
        }

        ll_delete.setOnClickListener {
            mPresenter.selectItem.clear()
            updateVideoState()
        }


        if (!TextUtils.isEmpty(mPresenter.ugcContent)) {
            et_ugc_content.setText(mPresenter.ugcContent)
            et_ugc_content.setSelection(mPresenter.ugcContent.length - 1)
        }

        if (!TextUtils.isEmpty(mPresenter.ugcHt?.id)) {
            ll_hua_ti.visibility = View.VISIBLE
            tv_add_hua_ti.visibility = View.GONE
            if (mPresenter.ugcHt?.activity==true){
                tv_ht_value.setCompoundDrawablesWithIntrinsicBounds(R.drawable.activity_loading_bg,0,0,0)
                (tv_ht_value.compoundDrawables[0] as AnimationDrawable).start()
                tv_ht_value.text = " ${mPresenter.ugcHt?.tag}"
            }else{
                tv_ht_value.text = "#${mPresenter.ugcHt?.tag}"
            }

        }

    }

    private fun updateVideoState() {
        if (!mPresenter.selectItem.isNullOrEmpty()) {
            ll_add_image.visibility = View.GONE
            rl_show_img.visibility = View.VISIBLE
            tv_video_long.text=TimeUtil.formatSecondsTo00(mPresenter.selectItem[0].duration.div(1000).toInt())
            ImageUtil.showRoundImgWithUriAndRadius(iv_show_img, mContext, Uri.parse(mPresenter.selectItem[0].path),16f)
        } else {
            ll_add_image.visibility = View.VISIBLE
            rl_show_img.visibility = View.GONE
        }
    }

    private fun checkParams() {
        if (TextUtils.isEmpty(et_ugc_content.text.toString()) ||
            mPresenter.selectItem.isNullOrEmpty()) {
            tv_publish.setBackgroundResource(R.drawable.gray_radius_24_bg)
            tv_publish.textColor = Color.parseColor("#999999")
            tv_publish.isEnabled=false
        } else {
            tv_publish.setBackgroundResource(R.drawable.black_radius_24_bg)
            tv_publish.textColor = ContextCompat.getColor(mContext, R.color.white)
            tv_publish.isEnabled=true
        }
    }

    override fun showLoading(value: String) {
        showBaseLoading()
    }

    override fun dismissLoading() {
        hideBaseLoading()
    }

    override fun onHtListBack(htList: MutableList<HuaTiBean>) {

    }

    override fun removeItem(item: PathItem, position: Int) {
        mPresenter.selectItem.removeAt(position)
        imageAdapter.notifyDataSetChanged()
        checkParams()
    }

    override fun onTakePhoto() {
        val params = mutableMapOf<String, Any?>()
        params[RunIntentKey.IS_ONLY_PICKER] = true
        params[RunIntentKey.CHOOSE_IMAGE_ITEM] = imageAdapter.mData
        RouterManager.startActivityForResultWithParams(
            BundleUrl.PHOTO_PICKER_ACTIVITY,
            this,
            params,
            Constant.OPEN_PICKER_REQUEST_CODE
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.OPEN_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val dates = data?.getParcelableArrayListExtra<PathItem>(RunIntentKey.CHOOSE_IMAGE_ITEM)
            dates?.let {
                mPresenter.selectItem.clear()
                mPresenter.selectItem.addAll(it)
                if (mPresenter.publishType == 1) {
                    imageAdapter.notifyDataSetChanged()
                } else {
                    updateVideoState()
                }
            }
            checkParams()
        } else if (requestCode == Constant.OPEN_HT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val htValue = data?.getStringExtra("ht_key_word")
            try {
                mPresenter.ugcHt = GsonUtil.fromJson(htValue?:"",HuaTiBean::class.java)
                mPresenter.ugcHt?.let {
                    ll_hua_ti.visibility = View.VISIBLE
                    tv_add_hua_ti.visibility = View.GONE
                    if (it.activity==true){
                        tv_ht_value.setCompoundDrawablesWithIntrinsicBounds(R.drawable.activity_loading_bg,0,0,0)
                        (tv_ht_value.compoundDrawables[0] as AnimationDrawable).start()
                        tv_ht_value.text = " ${it.tag}"
                    }else{
                        tv_ht_value.text = "#${it.tag}"
                    }

                }
            }catch (e:Exception){

            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun setResult() {
        val params = mutableMapOf<String, Any?>()
        params[RunIntentKey.UGC_CONTENT] = et_ugc_content.text.toString()
        mPresenter.ugcHt?.let {
            params[RunIntentKey.UGC_HT] = GsonUtil.toJson(it)
        }
        params[RunIntentKey.IS_NEED_SAVE] = true
        params[RunIntentKey.CHOOSE_IMAGE_ITEM] = mPresenter.selectItem
        params[RunIntentKey.SIGN_GOODS_LIST] = mPresenter.signGoods
        params[RunIntentKey.PUBLISH_TYPE] = mPresenter.publishType
        RouterManager.startActivityWithParams(BundleUrl.PHOTO_SIGN_ACTIVITY, this, params)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        RunCacheDataUtil.cleanHtCache()
    }
}
