package com.shunlai.mine.shop.edit

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.toast
import com.shunlai.mine.R
import com.shunlai.mine.dialog.TokenSumDialog
import com.shunlai.mine.entity.bean.DollListBean
import com.shunlai.mine.entity.bean.OwnerUgcBean
import com.shunlai.mine.entity.bean.SceneListBean
import com.shunlai.mine.entity.req.SaveShopReq
import com.shunlai.mine.shop.ShopViewModel
import com.shunlai.mine.shop.adapter.ShopGoodsPagerAdapter
import com.shunlai.mine.shop.edit.fragment.EditBgFragment
import com.shunlai.mine.shop.edit.fragment.EditDiaoPaiFragment
import com.shunlai.mine.shop.edit.fragment.EditImpressionFragment
import com.shunlai.mine.shop.manager.ShopStyleManager
import com.shunlai.mine.utils.ShopCacheUtil
import com.shunlai.ui.danmuku.BitmapUtils
import kotlinx.android.synthetic.main.activity_shop_edit_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author Liu
 * @Date   2021/7/13
 * @mobile 18711832023
 */
class ShopEditActivity : BaseActivity(), TokenSumDialog.OnTokenSumListener {
    override fun getMainContentResId(): Int = R.layout.activity_shop_edit_layout

    override fun getToolBarResID(): Int = 0

    private val tokenDialog by lazy {
        TokenSumDialog(mContext, R.style.custom_dialog, this)
    }

    private var scene: SceneListBean? = null

    private var doll: DollListBean? = null

    private var signBoard: OwnerUgcBean? = null

    private val shopManager by lazy {
        ShopStyleManager(this)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this).get(ShopViewModel::class.java)
    }

    override fun afterView() {
        EventBus.getDefault().register(this)
        scene = intent.getParcelableExtra(RunIntentKey.SCENE_KEY)
        doll = intent.getParcelableExtra(RunIntentKey.MODEL_ID)
        signBoard = intent.getParcelableExtra("signBoard")
        pager_view.adapter = ShopGoodsPagerAdapter(
            supportFragmentManager, mutableListOf(
                EditImpressionFragment(), EditBgFragment(), EditDiaoPaiFragment(signBoard)
            )
        )
        initListener()
        shopManager.initView(rl_shop_style)
        initView()
        initViewModel()

    }

    private fun initViewModel() {
        mViewModel.balanceResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess) {
                tokenDialog.show()
                tokenDialog.setData(if (buyScene) null else scene, if (buyDoll) null else doll,it.balance)
            } else {
                toast(it.errorMsg)
            }
        })
        mViewModel.saveEditResp.observe(this, Observer {
            hideBaseLoading()
            if (it.isSuccess) {
                toast("编辑成功")
                val intent=Intent()
                intent.putExtra(RunIntentKey.SCENE_KEY,scene?.sceneId)
                intent.putExtra(RunIntentKey.MODEL_ID,doll?.modelId)
                setResult(Activity.RESULT_OK,intent)
                finish()
            } else {
                toast(it.errorMsg)
            }
        })
    }

    private fun initListener() {
        pager_view.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                updateTab(position)
            }

        })
        rl_avatar.setOnClickListener {
            pager_view.currentItem = 0
        }
        rl_decoration.setOnClickListener {
            pager_view.currentItem = 1
        }
        rl_sign.setOnClickListener {
            pager_view.currentItem = 2
        }
        ll_back.setOnClickListener {
            finish()
        }
        tv_shop_save.setOnClickListener {
            saveEdit()
        }
    }

    private var buyScene = true

    private var buyDoll = true

    private fun saveEdit() {
        buyScene = true
        buyDoll = true
        scene?.let {
            if (it.buyFlag == "0"&&it.tokenPrice!="0") {
                buyScene = false
            }
        }
        doll?.let {
            if (it.buyFlag == "0"&&it.tokenPrice!="0") {
                buyDoll = false
            }
        }
        if (buyScene && buyDoll) {
            val req = SaveShopReq()
            req.modelId = doll?.modelId ?: "1"
            req.sceneId = scene?.sceneId ?: "1"
            if (TextUtils.isEmpty(signBoard?.ugcId)) {
                req.enabled = "0"
            } else {
                req.enabled = "1"
                req.ugcId = signBoard?.ugcId!!
            }
            mViewModel.saveShopEdit(req)
            showBaseLoading()
        } else {
            mViewModel.queryOwnerToken()
            showBaseLoading()

        }
    }

    private fun updateTab(position: Int) {
        if (position == 1) {
            iv_avatar_label.setImageResource(R.mipmap.icon_avatar_b_a40)
            v_avatar_div.visibility = View.GONE
            iv_decoration_label.setImageResource(R.mipmap.icon_decoration_b_a90)
            v_decoration_div.visibility = View.VISIBLE
            iv_sign_label.setImageResource(R.mipmap.icon_sign_b_a40)
            v_sign_div.visibility = View.GONE

            ground_glass.visibility = View.GONE
            label_view.visibility = View.GONE

        } else if (position == 2) {
            iv_avatar_label.setImageResource(R.mipmap.icon_avatar_b_a40)
            v_avatar_div.visibility = View.GONE
            iv_decoration_label.setImageResource(R.mipmap.icon_decoration_b_a40)
            v_decoration_div.visibility = View.GONE
            iv_sign_label.setImageResource(R.mipmap.icon_sign_b_a90)
            v_sign_div.visibility = View.VISIBLE

            ground_glass.visibility = View.GONE
            label_view.visibility = View.GONE
        } else {
            iv_avatar_label.setImageResource(R.mipmap.icon_avatar_b_a90)
            v_avatar_div.visibility = View.VISIBLE
            iv_decoration_label.setImageResource(R.mipmap.icon_decoration_b_a40)
            v_decoration_div.visibility = View.GONE
            iv_sign_label.setImageResource(R.mipmap.icon_sign_b_a40)
            v_sign_div.visibility = View.GONE

            ground_glass.visibility = View.VISIBLE
            label_view.visibility = View.VISIBLE
        }
    }

    private fun initView() {
        shopManager.initSceneAndDoll(scene?.sceneId!!, doll?.modelId!!)
        label_view.setResourcePath(
            ShopCacheUtil.getShopDollById(doll?.modelId!!).actionLocalPath ?: ""
        )
        signBoard?.let {
            onEvent(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(bean: OwnerUgcBean) {
        signBoard = bean
        if (TextUtils.isEmpty(bean.ugcId)) {
            ll_d_p.visibility = View.INVISIBLE
        } else {
            ll_d_p.visibility = View.VISIBLE
            ImageUtil.showRoundImgWithStringAndRadius(iv_ugc_image, mContext, bean.firstImage, 8f)
            tv_ugc_title.text = bean.content
            tv_ugc_title?.post {
                tv_ugc_title.isSelected=true
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(bean: DollListBean) {
        doll = bean
        initView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(bean: SceneListBean) {
        scene = bean
        initView()
    }

    override fun onResume() {
        super.onResume()
        shopManager.onResume()
        label_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        shopManager.onPause()
        label_view.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onTokenConfirm(isCheckScene: Boolean, isCheckDoll: Boolean) {
        val req = SaveShopReq()
        if (isCheckScene){
            req.sceneId = scene?.sceneId ?: "1"
        }
        if (isCheckDoll){
            req.modelId = doll?.modelId ?: "1"
        }
        if (TextUtils.isEmpty(signBoard?.ugcId)) {
            req.enabled = "0"
        } else {
            req.enabled = "1"
            req.ugcId = signBoard?.ugcId!!
        }
        mViewModel.saveShopEdit(req)
        showBaseLoading()
    }
}
