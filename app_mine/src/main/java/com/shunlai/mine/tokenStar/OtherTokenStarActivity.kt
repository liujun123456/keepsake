package com.shunlai.mine.tokenStar

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shunlai.common.BaseActivity
import com.shunlai.common.utils.*
import com.shunlai.mine.MineApiConfig
import com.shunlai.mine.R
import com.shunlai.mine.dialog.TokenStarShareWindow
import com.shunlai.mine.entity.bean.LoginMember
import com.shunlai.mine.entity.bean.MatchResultBean
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.entity.bean.TokenStarUser
import com.shunlai.share.WeChatUtil
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ui.danmuku.BitmapUtils
import com.shunlai.ui.tokenstar.StarUserBean
import com.shunlai.ui.tokenstar.TokenStarView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_other_token_star_layout.*

/**
 * @author Liu
 * @Date   2021/8/12
 * @mobile 18711832023
 */
@SuppressLint("SetTextI18n")
class OtherTokenStarActivity:BaseActivity(), TokenStarShareWindow.TokenStarShareWindowListener,
    TokenStarView.TokenStarItemClickListener  {
    override fun getMainContentResId(): Int = R.layout.activity_other_token_star_layout

    override fun getToolBarResID(): Int=0

    private var memberInfo: MemberInfo?=null

    private var matchResult:MatchResultBean?=null

    private var mineInfo:LoginMember?=null

    private val mViewModel by lazy {
        ViewModelProvider(this).get(TokenStarViewModel::class.java)
    }

    private val mWindow by lazy {
        TokenStarShareWindow(mContext, this)
    }


    override fun afterView() {
        StatusBarUtil.showLightStatusBarIcon(this)
        try {
            memberInfo= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MEMBER_INFO)?:"", MemberInfo::class.java)
            matchResult= GsonUtil.fromJson(intent.getStringExtra(RunIntentKey.MATCH_RESULT)?:"", MatchResultBean::class.java)
            mineInfo=GsonUtil.fromJson(PreferenceUtil.getString(Constant.USER_INFO)?:"", LoginMember::class.java)
        }catch (e: Exception){

        }
        ImageUtil.showCircleImgWithString(iv_avatar,mContext,memberInfo?.avatar)
        tv_user_name.text=memberInfo?.nickName
        tv_user_name.post {
            tv_user_name.isSelected=true
        }
        initListener()
        initViewModel()
        buildMatchResult()
        mViewModel.queryTokenMap(memberInfo?.id?.toString()?:"")
    }

    private fun buildMatchResult(){
        tv_match_score.text="${matchResult?.score}"
        tv_match_content.text=matchResult?.message
        if (matchResult?.score?:0 in 0 until 200){
            tv_match_desc.text="距离很近"
        }else if (matchResult?.score?:0 in 200 until 400){
            tv_match_desc.text="距离近"
        }else if (matchResult?.score?:0 in 400 until 600){
            tv_match_desc.text="距离一般"
        }else if (matchResult?.score?:0 in 600 until 700){
            tv_match_desc.text="距离远"
        }else{
            tv_match_desc.text="距离很远"
        }
        ImageUtil.showCircleImgWithString(iv_other_avatar,mContext,memberInfo?.avatar)
        tv_other_name.text=memberInfo?.nickName

        ImageUtil.showCircleImgWithString(iv_mine_avatar,mContext,mineInfo?.avatar)
        tv_mine_name.text=mineInfo?.nickName
    }

    private fun initViewModel(){
        mViewModel.starMap.observe(this, Observer {
            buildTokenView(it)
        })
        mViewModel.shareImgResp.observe(this, Observer {
            hideBaseLoading()
            val params= mutableMapOf<String,Any?>()
            params[RunIntentKey.POSTER_IMG_URL]=it?:""
            RouterManager.startActivityWithParams(BundleUrl.SHARE_STAR_ACTIVITY,this,params)
        })
        token_star_view.setOnItemClickListener(this)
    }

    private fun initListener(){
        iv_close.setOnClickListener {
            finish()
        }
        do_share.setOnClickListener {
            mWindow.showWindow()
        }
    }

    @SuppressLint("CheckResult")
    fun buildTokenView(starUser:MutableList<TokenStarUser>){
        starUser.sortWith(Comparator { o1, o2 ->
            return@Comparator o1.score-o2.score
        })
        starUser.forEachIndexed { index, tokenStarUser ->
            tokenStarUser.ranking=index+1
        }
        token_star_view.initPointData()
        Observable.fromIterable(starUser).map{
            val bitmap=ImageUtil.getBitmapFromUrl(mContext,it.avatar?:"")
            return@map StarUserBean(it.anotherMemberId?:"", BitmapUtils.getShowPicture(bitmap),it.nickName?:"",it.ranking)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                token_star_view.setData(it)
            }
    }

    override fun onPause() {
        super.onPause()
        token_star_view.onStop()
        sky_view.onStop()
    }

    override fun onResume() {
        super.onResume()
        token_star_view.onStart()
        sky_view.onStart()
    }

    override fun onWeChatShare() {
        WeChatUtil.getInstance().shareWeChatWithWeb("${MineApiConfig.ROOT_URL}/static/app/match/#/index?memberId=${mViewModel.memberId}","茫茫宇宙，你是和我最接近的人吗？","${mineInfo?.nickName}邀请你测试你们的距离",
            BitmapFactory.decodeResource(resources,R.mipmap.share_token_logo))
        mWindow.dismiss()
    }

    override fun onCircleShare() {
        WeChatUtil.getInstance().shareCircleWithWeb("${MineApiConfig.ROOT_URL}/static/app/match/#/index?memberId=${mViewModel.memberId}","茫茫宇宙，你是和我最接近的人吗？","${mineInfo?.nickName}邀请你测试你们的距离",BitmapFactory.decodeResource(resources,R.mipmap.share_token_logo))
        mWindow.dismiss()
    }

    override fun onCopyUrl() {
        mWindow.dismiss()
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", "${MineApiConfig.ROOT_URL}/static/app/match/#/index?memberId=${mViewModel.memberId}")
        cm.setPrimaryClip(mClipData)
        toast("已经复制到剪贴板")
    }

    override fun onBuildImg() {
        mViewModel.shareTokenStar()
        showBaseLoading()
        mWindow.dismiss()
    }

    override fun onStarClick(bean: TokenStarView.StarPointBean) {
//        bean.id?.let {
//            val params= mutableMapOf<String,Any?>()
//            params[RunIntentKey.MEMBER_ID]= it
//            RouterManager.startActivityWithParams(BundleUrl.USER_INFO_ACTIVITY,mContext as FragmentActivity,params)
//        }
    }

}
