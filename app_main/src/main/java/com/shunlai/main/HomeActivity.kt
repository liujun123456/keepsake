package com.shunlai.main

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.igexin.sdk.PushManager
import com.shunlai.common.BaseActivity
import com.shunlai.common.bean.JumpEvent
import com.shunlai.common.bean.MessageNotifyEvent
import com.shunlai.common.utils.*
import com.shunlai.router.RouterManager
import com.shunlai.ui.TabSwitchLayout
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.lang.Exception

/**
 * @author Liu
 * @Date   2021/4/2
 * @mobile 18711832023
 */
class HomeActivity : BaseActivity() {
    override fun getMainContentResId(): Int = R.layout.activity_home

    override fun getToolBarResID(): Int = 0

    val PERMISSION_REQUEST_CODE = 10086

    private val fragmentList = mutableListOf<Fragment>()
    private val tabs = mutableListOf<TabSwitchLayout>()
    private var currentTab = -1
    private val mViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }


    override fun afterView() {
        checkPermission()
        initTab()
        initFragment()
        initListener()
        mViewModel.queryBrandState()
        PushManager.getInstance().getClientid(mContext)?.let {
            mViewModel.bindCidWithUser(it)
        }
        EventBus.getDefault().register(this)
    }

    private fun initListener() {
        tab_home_page.setOnClickListener {
            updateFragment(0)
        }
        tab_fuc_page.setOnClickListener {
            updateFragment(1)
        }
        tab_mine_page.setOnClickListener {
            updateFragment(2)
        }
        mViewModel.brandState.observe(this, Observer {
            if (it==0){
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.IS_FROM_HOME]=true
                RouterManager.startActivityWithParams(BundleUrl.TOKEN_STAR_CHOOSE_BRAND,this,params)
            }
        })
    }

    private fun buildTab() {
        tab_home_page.initIcon(
            R.mipmap.ic_icon_home_choose, R.mipmap.ic_icon_home_un_choose,
            "首页", R.color.app_icon_choose, R.color.app_icon_un_choose
        )
        tab_fuc_page.initIcon(
            R.mipmap.ic_icon_notification_choose, R.mipmap.ic_icon_notification_un_choose,
            "消息", R.color.app_icon_choose, R.color.app_icon_un_choose
        )
        tab_mine_page.initIcon(
            R.mipmap.ic_icon_people_choose, R.mipmap.ic_icon_people_un_choose,
            "我的", R.color.app_icon_choose, R.color.app_icon_un_choose
        )
    }


    private fun initTab() {
        tabs.add(tab_home_page)
        tabs.add(tab_fuc_page)
        tabs.add(tab_mine_page)
    }

    private fun initFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        fragmentList.add(RouterManager.navigationFragment(BundleUrl.FRAGMENT_HOME) as Fragment)
        fragmentList.add(RouterManager.navigationFragment(BundleUrl.FRAGMENT_FUC) as Fragment)
        fragmentList.add(RouterManager.navigationFragment(BundleUrl.FRAGMENT_MINE) as Fragment)
        fragmentList.forEach {
            transaction.add(R.id.frame_layout, it)
        }
        transaction.commitAllowingStateLoss()
        updateFragment(0)
    }

    private fun updateFragment(index: Int) {
        if (index == currentTab) return
        val transaction = supportFragmentManager.beginTransaction()
        hideFragment(transaction)
        transaction.show(fragmentList[index])
        transaction.commitAllowingStateLoss()
        currentTab = index
        updateTab(index)
    }

    private fun updateTab(index: Int) {
        buildTab()
        tabs.forEach {
            it.setCheckState(false)
        }
        tabs[index].setCheckState(true)
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        fragmentList.forEachIndexed { _, fragment ->
            transaction.hide(fragment)
        }
    }

    private val handler= Handler()
    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            handler.postDelayed({
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            },1000)
        }
    }

    var lastBackTime = 0L

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentTab != 0) {
                updateFragment(0)
                return true
            } else {
                if (System.currentTimeMillis() - lastBackTime < 2000) {
                    return super.onKeyDown(keyCode, event)
                } else {
                    toast("再次点击退出")
                    lastBackTime = System.currentTimeMillis()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    @SuppressLint("Range")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageNotifyEvent) {
        if (event.boolean) {
            msg_notice.visibility = View.VISIBLE
        } else {
            msg_notice.visibility = View.GONE
        }
    }
    @SuppressLint("Range")
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onEvent(jumpEvent: JumpEvent){
        try {
            jumpEvent.path?.let { it ->
                val jsonParams=JSONObject(jumpEvent.params?:"")
                val params= mutableMapOf<String,Any?>()
                jsonParams.keys().forEach {key->
                    params[key]="${jsonParams[key]}"
                }
                RouterManager.startActivityWithParams(it,this,params)
            }
        }catch (e:Exception){

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
