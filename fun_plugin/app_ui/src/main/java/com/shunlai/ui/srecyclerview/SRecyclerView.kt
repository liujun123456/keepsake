package com.shunlai.ui.srecyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shunlai.ui.R
import com.shunlai.ui.srecyclerview.adapter.SRecyclerAdapter
import java.lang.RuntimeException

/**
 * @author  AsureLiu on 2017/11/28.
 */
class SRecyclerView : ViewGroup{
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private var mSpanSizeLookup: DefaultSpanSizeLookup?=null
    private var mAdapter: SRecyclerAdapter?=null
    private val interceptOffset = 15
    private var lastY: Float = 0f
    private var defaultHeadHeight=0
    private var moveHeight=0
    private var mContext: Context
    private var mLastVisibleItem: Int = 0
    private var mFirstVisibleItem: Int = 0
    private var mIsTop = true
    private var status = Ready

    var isCanLoadMore=true
    var isCanRefresh=true



    companion object {
        const val Ready = 1
        const val Refresh = 2
        const val LoadMore = 3
        const val MOVING = 4
        const val NO_MORE=5
        const val EMPTY_VALUE=6
    }

    constructor(context: Context) : super(context, null) {
        this.mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        this.mContext = context
        initView()
        initAttr(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
        initAttr(attrs)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = paddingTop
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val headerLeft = measuredWidth / 2 - child.measuredWidth / 2
            child.layout(headerLeft, top, headerLeft + child.measuredWidth, top + child.measuredHeight)
            top += child.measuredHeight
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            height += child.measuredHeight
        }
        setMeasuredDimension(width, height)
    }

    private fun initAttr(attrs: AttributeSet){
        val typeArray=mContext.obtainStyledAttributes(attrs, R.styleable.SRecyclerView)
        defaultHeadHeight=typeArray.getDimensionPixelOffset(R.styleable.SRecyclerView_head_height,ScreenUtils.dip2px(mContext,50f))
        isCanLoadMore=typeArray.getBoolean(R.styleable.SRecyclerView_canLoadMore,true)
        isCanRefresh=typeArray.getBoolean(R.styleable.SRecyclerView_canRefresh,true)
        typeArray.recycle()

    }

    private fun initView() {
        initRecyclerView()
        addView(mRecyclerView)
    }


    private fun initRecyclerView() {
        val params =LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mRecyclerView = RecyclerView(mContext)
        mLayoutManager = LinearLayoutManager(mContext)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.itemAnimator = null
        mRecyclerView.addOnScrollListener(onScrollListener)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutParams = params
    }

    fun getRecyclerView():RecyclerView{
        return mRecyclerView
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager){
        mLayoutManager=layoutManager
        if (mLayoutManager is GridLayoutManager){
            if (mAdapter==null){
                throw RuntimeException("you must setAdapter first")
            }
            mSpanSizeLookup=object: DefaultSpanSizeLookup(mAdapter!!,mLayoutManager as GridLayoutManager){
                override fun spanSize(position: Int): Int {
                    return 1
                }
            }
            (mLayoutManager as GridLayoutManager).spanSizeLookup=mSpanSizeLookup

        }
        mRecyclerView.layoutManager=mLayoutManager
    }

    fun setSpanSize(spanSizeLookup: DefaultSpanSizeLookup){
        if (mLayoutManager is GridLayoutManager){
            mSpanSizeLookup=spanSizeLookup
            (mLayoutManager as GridLayoutManager).spanSizeLookup=mSpanSizeLookup
        }
    }

    fun setHeadHeight(dp:Float){
        defaultHeadHeight=ScreenUtils.dip2px(mContext,dp)
    }

    fun setAdapter(adapter: SRecyclerAdapter){
        mRecyclerView.adapter=adapter
        mAdapter=adapter
    }

    fun doRefresh() {
        status = Refresh
        moveHeight=defaultHeadHeight
        mRecyclerView.scrollToPosition(0)
        mAdapter?.notifyStatus(status,moveHeight)
        listener?.refresh()
    }

    fun notifyDataSetChanged(){
        complete()
        mAdapter?.notifyDataSetChanged()
    }

    fun notifyItemRangeInserted(start:Int,size:Int){
        complete()
        mAdapter?.notifyItemRangeInserted(start+1,size)
    }

    fun notifyItemRemoved(position:Int){
        mAdapter?.notifyItemRemoved(position+1)
    }

    fun notifyItemChanged(position: Int){
        mAdapter?.notifyItemChanged(position+1)
    }


    fun showNoMore(){
        status = NO_MORE
        moveHeight=0
        mAdapter?.notifyStatus(status,0)
        mAdapter?.notifyDataSetChanged()
    }
    fun showEmpty(){
        status = EMPTY_VALUE
        moveHeight=0
        mAdapter?.notifyStatus(status,0)
        mAdapter?.notifyDataSetChanged()
    }

    fun complete() {
        status = Ready
        moveHeight=0
        mAdapter?.notifyStatus(status,0)
    }
    /**
     * 屏蔽Recyclerview的触摸事件（下拉刷新的时候）
     */

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action =ev.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> lastY = ev.rawY
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> return false
            MotionEvent.ACTION_MOVE ->if (mIsTop && ev.rawY - lastY > interceptOffset&&isCanRefresh) return true

        }
        return false
    }

    private var offsetY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isCanRefresh||(status== Refresh || status== LoadMore)) {
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                offsetY = event.rawY - lastY
                scrollToOffset(offsetY)
            }
            MotionEvent.ACTION_UP -> {
                if (moveHeight >=defaultHeadHeight) {
                    doRefresh()
                } else {
                    complete()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun scrollToOffset(offsetY: Float) {
        var value = (offsetY / 2.0f).toInt()
        if (offsetY <= 0) value = 0
        moveHeight=value
        status= MOVING
        mAdapter?.notifyStatus(status,value)

    }

    private fun showFootView() {
        status = LoadMore
        mAdapter?.notifyStatus(status,0)
    }

    var listener: SRecyclerListener?=null

    fun setSRecyclerListener(listener: SRecyclerListener){
        this.listener=listener
    }

    var onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mRecyclerView.computeVerticalScrollExtent()+mRecyclerView.computeVerticalScrollOffset()>=mRecyclerView.computeVerticalScrollRange()
                        && status == Ready &&isCanLoadMore) {
                    showFootView()
                    listener?.loadMore()
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (recyclerView.layoutManager is StaggeredGridLayoutManager){
                val layoutManager=recyclerView.layoutManager as StaggeredGridLayoutManager
                val lastPositions= IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(lastPositions)
                mLastVisibleItem=findMax(lastPositions)

                layoutManager.findFirstVisibleItemPositions(lastPositions)
                mFirstVisibleItem=findMax(lastPositions)
            }else if (recyclerView.layoutManager is LinearLayoutManager||recyclerView.layoutManager is GridLayoutManager){
                mLastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                mFirstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            }

            mIsTop = mFirstVisibleItem == 0
        }
    }

    private fun findMax(lastPositions:IntArray):Int {
        var  max = lastPositions[0]
        for (value in lastPositions.indices){
            if (value>max){
                max=value
            }
        }
        return max
    }

}
