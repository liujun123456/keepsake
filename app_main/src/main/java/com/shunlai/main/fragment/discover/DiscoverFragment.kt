package com.shunlai.main.fragment.discover

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getui.gs.sdk.GsManager
import com.google.android.material.appbar.AppBarLayout
import com.shunlai.common.bean.GoodsBean
import com.shunlai.common.utils.*
import com.shunlai.main.ActionInterface
import com.shunlai.main.HomeActivity
import com.shunlai.main.R
import com.shunlai.main.entities.HuaTiBean
import com.shunlai.main.entities.UgcBean
import com.shunlai.main.entities.UgcGoodsBean
import com.shunlai.main.fragment.discover.adapter.DiscoverAdapter
import com.shunlai.main.ht.adapter.HuaTiAdapter2
import com.shunlai.router.RouterManager
import com.shunlai.ui.MediaGridInset
import com.shunlai.ui.UgcActionWindow
import com.shunlai.ui.srecyclerview.RefreshGridInset
import com.shunlai.ui.srecyclerview.SRecyclerListener
import kotlinx.android.synthetic.main.fragment_discover_layout.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * @author Liu
 * @Date   2021/5/6
 * @mobile 18711832023
 */
class DiscoverFragment : Fragment(), DiscoverView, ActionInterface, UgcActionWindow.ActionListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover_layout, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private val mAdapter by lazy {
        DiscoverAdapter(activity!!, mutableListOf(), this, 1)
    }

    private val hotAdapter by lazy {
        HuaTiAdapter2(activity!!, mutableListOf())
    }

    private val mPresenter by lazy {
        DiscoverPresenter(viewLifecycleOwner, this, this)
    }

    private val ugcWindow by lazy {
        UgcActionWindow(activity!!, this)
    }

    private fun initView() {
        rv_discover.setAdapter(mAdapter)
        rv_discover.setLayoutManager(LinearLayoutManager(activity))
        rv_discover.getRecyclerView()
            .addItemDecoration(RefreshGridInset(1, ScreenUtils.dip2px(activity, 8f), false, true))

        rv_hot_huati.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        rv_hot_huati.addItemDecoration(
            MediaGridInset(
                1,
                ScreenUtils.dip2px(activity, 16f),
                false,
                false
            )
        )
        rv_hot_huati.adapter = hotAdapter
        initListener()

        mPresenter.queryHotHt()
        mPresenter.queryHomeUgc(buildUgcList())

        rv_discover.setSRecyclerListener(object : SRecyclerListener {
            override fun loadMore() {
                mPresenter.loadMoreHomeUgc(mutableListOf())
            }

            override fun refresh() {
                mPresenter.queryHomeUgc(buildUgcList())
            }
        })

    }

    private fun initListener() {
        tv_more_hua_ti.setOnClickListener {
            RouterManager.startActivityWithParams(BundleUrl.HUA_TI_ACTIVITY, activity!!)
        }
        app_bar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            rv_discover.isCanRefresh = verticalOffset == 0
        })
    }

    override fun onHtLoad(data: MutableList<HuaTiBean>) {
        hotAdapter.mData = data
        hotAdapter.notifyDataSetChanged()
    }

    override fun onHomeRecommendUgc(data: MutableList<UgcBean>) {
        mAdapter.mData.addAll(0, data)
        if (mAdapter.mData.isEmpty()) {
            rv_discover.showEmpty()
        } else {
            rv_discover.notifyItemRangeInserted(0, data.size)
        }
    }

    override fun onMoreHomeRecommendUgc(data: MutableList<UgcBean>) {
        if (data.isEmpty()) {
            rv_discover.showNoMore()
        } else {
            mAdapter.mData.addAll(data)
            rv_discover.notifyDataSetChanged()
        }
    }

    var actionPosition = -1

    override fun doAttention(position: Int, memberId: String) {
        actionPosition = position
        mPresenter.doAttention(memberId)
    }

    override fun doLike(position: Int, ugcId: String, isLike: Boolean) {
        actionPosition = position
        mPresenter.doLike(ugcId, isLike)
    }

    override fun doCollect(position: Int, ugcId: String, isCollect: Boolean) {
        actionPosition = position
        mPresenter.doCollect(ugcId, isCollect)
    }

    var actionUgcId: String? = null
    override fun doMore(position: Int, ugcId: String, memberId: String) {
        actionPosition = position
        actionUgcId = ugcId
        if (memberId == PreferenceUtil.getString(Constant.USER_ID)) {
            ugcWindow.showWindow(1)
        } else {
            ugcWindow.showWindow(0)
        }
    }

    override fun doEva(bean: UgcGoodsBean) {
        EventBus.getDefault().post(GoodsBean().apply {
            name = bean.productName
            cateId = bean.cateId
            thumb = bean.productImg
            subCateId = bean.subCateId
            price = bean.price
            type = bean.type
            productId = bean.productId
        })
    }

    override fun showLoading(value: String) {
        (activity as HomeActivity).showBaseLoading()
    }

    override fun dismissLoading() {
        (activity as HomeActivity).hideBaseLoading()
    }

    override fun onAttention(result: Int) {
        mAdapter.mData[actionPosition].isFollow = result.toString()
        rv_discover.notifyItemChanged(actionPosition)
    }

    override fun onLike(result: Int) {
        var likes = mAdapter.mData[actionPosition].likes ?: 0
        if (result == 1) {
            trackUgcLike(actionPosition)
            mAdapter.mData[actionPosition].whetherLikes = true
            likes += 1
        } else {
            mAdapter.mData[actionPosition].whetherLikes = false
            likes -= 1
        }
        mAdapter.mData[actionPosition].likes = likes
        rv_discover.notifyItemChanged(actionPosition)

    }

    override fun onCollect(result: Int) {
        var collects = mAdapter.mData[actionPosition].favorites ?: 0
        if (result == 1) {
            mAdapter.mData[actionPosition].whetherFavorites = true
            collects += 1
        } else {
            mAdapter.mData[actionPosition].whetherFavorites = false
            collects -= 1
        }
        mAdapter.mData[actionPosition].favorites = collects
        rv_discover.notifyItemChanged(actionPosition)
    }

    override fun onDeleteUgc(result: Int) {
        if (result == 1) {
            mAdapter.mData.removeAt(actionPosition)
            rv_discover.notifyItemRemoved(actionPosition)
        }
    }

    private fun buildUgcList(): MutableList<Int> {
        val params = mutableListOf<Int>()
        mAdapter.mData.forEach {
            try {
                params.add((it.ugcId ?: "0").toInt())
            } catch (e: Exception) {

            }
        }
        return params
    }

    override fun onDeleteAction() {
        mPresenter.doUgcDelete(actionUgcId ?: "")
    }

    override fun onComplaintAction() {
        val params = mutableMapOf<String, Any?>()
        params[RunIntentKey.UGC_ID] = actionUgcId
        RouterManager.startActivityWithParams(
            BundleUrl.COMPLAIN_ACTIVITY,
            activity as FragmentActivity,
            params
        )
    }

    var currentTime: Long? = null
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            trackHomeIn()
        } else {
            currentTime?.let {
                if (System.currentTimeMillis() - it > 1000) {
                    trackHomeStay(System.currentTimeMillis() - it)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            currentTime?.let {
                if (System.currentTimeMillis() - it > 1000) {
                    trackHomeStay(System.currentTimeMillis() - it)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            currentTime = System.currentTimeMillis()
        }

    }

    private fun trackHomeIn() {
        currentTime = System.currentTimeMillis()
        val params = JSONObject()
        params.put("from_page", RunCacheDataUtil.lastPage)
        params.put("is_login", !TextUtils.isEmpty(PreferenceUtil.getString(Constant.USER_ID)))
        GsManager.getInstance().onEvent("Home_feed_page", params)
        RunCacheDataUtil.lastPage = "Home"
    }

    private fun trackHomeStay(duration: Long) {
        val params = JSONObject()
        params.put("event_duration", duration)
        params.put("page_name", "Home")
        params.put("from_page", RunCacheDataUtil.lastPage)
        params.put("is_login", !TextUtils.isEmpty(PreferenceUtil.getString(Constant.USER_ID)))
        GsManager.getInstance().onEvent("Home_feed_stay", params)
    }

    private fun trackUgcLike(position: Int) {
        val bean = mAdapter.mData[position]
        val params = JSONObject()
        bean.topic?.id?.let {
            params.put("topic_ids", it)
        }
        params.put("uid", bean.memberId)
        params.put("Page_name", "Home")
        params.put("note_type", bean.ugcType)
        params.put("note_id", bean.ugcId)
        GsManager.getInstance().onEvent("like_note", params)
    }

}
