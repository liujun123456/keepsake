package com.shunlai.mine.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.shunlai.common.utils.BundleUrl
import com.shunlai.common.utils.ImageUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.mine.R
import com.shunlai.mine.entity.bean.MemberInfo
import com.shunlai.mine.entity.bean.TokenStarUser
import com.shunlai.net.util.GsonUtil
import com.shunlai.router.RouterManager
import com.shunlai.ui.danmuku.BitmapUtils
import com.shunlai.ui.tokenstar.StarUserBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_token_star_layout.*
import kotlinx.android.synthetic.main.dialog_token_star_layout.*
import kotlinx.android.synthetic.main.dialog_token_star_layout.iv_avatar
import kotlinx.android.synthetic.main.dialog_token_star_layout.token_star_view
import kotlinx.android.synthetic.main.dialog_token_star_layout.tv_user_name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * @author Liu
 * @Date   2021/8/19
 * @mobile 18711832023
 */
class TokenStarDialog(var mContext: Context, var themeResId:Int):Dialog(mContext,themeResId) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_token_star_layout)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        ll_close_dialog.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    fun setData(memberInfo:MemberInfo?,type:Int,starUser:MutableList<TokenStarUser>){
        ImageUtil.showCircleImgWithString(iv_avatar,mContext,memberInfo?.avatar)
        tv_user_name.text=memberInfo?.nickName

        tv_user_name.post {
            tv_user_name.isSelected=true
        }

        starUser.sortWith(Comparator { o1, o2 ->
            return@Comparator o1.score-o2.score
        })
        starUser.forEachIndexed { index, tokenStarUser ->
            tokenStarUser.ranking=index+1
        }
        token_star_view.initPointData()
        GlobalScope.launch(Dispatchers.IO) {
            delay(500)
            launch(Dispatchers.Main) {
                Observable.fromIterable(starUser).map{
                    val bitmap=ImageUtil.getBitmapFromUrl(mContext,it.avatar?:"")
                    return@map StarUserBean(it.anotherMemberId?:"", BitmapUtils.getShowPicture(bitmap),it.nickName?:"",it.ranking)
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
                        token_star_view.setData(it)
                    }
            }

        }
        if (type==0){
            if (memberInfo?.nickName?.length?:0>4){
                tv_notice.text="${memberInfo?.nickName?.substring(0,4)}...的TOKEN宇宙"
            }else{
                tv_notice.text="${memberInfo?.nickName}的TOKEN宇宙"
            }
            tv_go_test.text="测试和ta的频率"
            tv_go_test.setOnClickListener {
                val params= mutableMapOf<String,Any?>()
                params[RunIntentKey.MEMBER_INFO]= GsonUtil.toJson(memberInfo!!)
                RouterManager.startActivityWithParams(BundleUrl.STAR_MATCH_ACTIVITY,mContext as FragmentActivity,params)
                dismiss()
            }
        }else if (type==1){
            if (memberInfo?.nickName?.length?:0>4){
                tv_notice.text="${memberInfo?.nickName?.substring(0,4)}...还没开启Token宇宙"
            }else{
                tv_notice.text="${memberInfo?.nickName}还没开启Token宇宙"
            }
            tv_go_test.text="下次再来"
            tv_go_test.setOnClickListener {
                dismiss()
            }
        }else{
            tv_notice.text="你还没开启Token宇宙"
            tv_go_test.text="开启Token宇宙"
            tv_go_test.setOnClickListener {
                RouterManager.startActivityWithParams(BundleUrl.TOKEN_STAR_CHOOSE_BRAND,mContext as FragmentActivity)
                dismiss()
            }
        }
    }
}
