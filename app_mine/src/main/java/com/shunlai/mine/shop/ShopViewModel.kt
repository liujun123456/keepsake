package com.shunlai.mine.shop

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shunlai.common.utils.Constant
import com.shunlai.common.utils.PreferenceUtil
import com.shunlai.common.utils.RunIntentKey
import com.shunlai.common.utils.TimeUtil
import com.shunlai.mine.MineApiConfig
import com.shunlai.mine.MineHttpManager
import com.shunlai.mine.entity.BaseResp
import com.shunlai.mine.entity.bean.*
import com.shunlai.mine.entity.req.FeedingReq
import com.shunlai.mine.entity.req.LeaveMessageReq
import com.shunlai.mine.entity.req.SaveShopReq
import com.shunlai.mine.entity.resp.FeedRecordResp
import com.shunlai.mine.entity.resp.LeaveMsgResp
import com.shunlai.mine.entity.resp.OwnerUgcResp
import com.shunlai.net.CoreHttpSubscriber
import com.shunlai.net.CoreHttpThrowable

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
class ShopViewModel: ViewModel()  {
    private val lifecycleOwner: LifecycleOwner?=null
    private val memberId= PreferenceUtil.getString(Constant.USER_ID)?:""
    val leaveMessageResp:MutableLiveData<BaseResp> = MutableLiveData()
    val feedResp:MutableLiveData<BaseResp> = MutableLiveData()
    val unReadResp:MutableLiveData<Int> = MutableLiveData()
    val receiveMsgResp:MutableLiveData<MutableList<ImpressionBean>> =MutableLiveData()
    val sendMsgResp:MutableLiveData<MutableList<ImpressionBean>> = MutableLiveData()
    val deleteMsgResp:MutableLiveData<BaseResp> =MutableLiveData()
    val blockedMsgResp:MutableLiveData<BaseResp> = MutableLiveData()
    val chooseMsgResp:MutableLiveData<Int> = MutableLiveData()
    val checkDanMuResp:MutableLiveData<Int> = MutableLiveData()
    var danMuList:MutableLiveData<MutableList<DanMuBean>> = MutableLiveData()
    var dollListResp:MutableLiveData<MutableList<DollListBean>> =MutableLiveData()
    var sceneListResp:MutableLiveData<MutableList<SceneListBean>> = MutableLiveData()
    var ownerUgcList:MutableLiveData<MutableList<OwnerUgcBean>> = MutableLiveData()
    var signBoardResp:MutableLiveData<SignBoardBean> =MutableLiveData()
    var feedReceiver:MutableLiveData<FeedRecordResp> =MutableLiveData()
    var feedSend:MutableLiveData<MutableList<FeedRecord>> = MutableLiveData()
    val unReadFeedResp:MutableLiveData<Int> = MutableLiveData()
    var balanceResp:MutableLiveData<TokenBalance> = MutableLiveData()
    var saveEditResp:MutableLiveData<TokenBalance> = MutableLiveData()



    /**
     * ????????????
     */
    fun leaveMessage(content:String,id:String){
        val req= LeaveMessageReq(content,id,memberId,TimeUtil.getTime(System.currentTimeMillis()))
        MineHttpManager.postByModel(lifecycleOwner,MineApiConfig.LEAVE_MESSAGE,req).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                leaveMessageResp.postValue(BaseResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: Int?) {
                leaveMessageResp.postValue(BaseResp())
            }

        })

    }

    /**
     * ??????
     */
    fun doFeeding(id:String){
        val req= FeedingReq(TimeUtil.getTime(System.currentTimeMillis()),"0",id,memberId)
        MineHttpManager.postByModel(lifecycleOwner,MineApiConfig.FEEDING,req).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                feedResp.postValue(BaseResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: Int?) {
                feedResp.postValue(BaseResp())
            }
        })

    }


    /**
     * ????????????
     */
    fun deleteLeaveMessage(id:String){
        val params = mutableMapOf<String,Any>()
        params["id"]=id
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.DELETE_LEAVE_MESSAGE,params).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                deleteMsgResp.postValue(BaseResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: Int?) {
                deleteMsgResp.postValue(BaseResp())
            }

        })

    }

    /**
     * ????????????
     */
    fun blockedLeaveMessage(id:String){
        val params = mutableMapOf<String,Any>()
        params["id"]=id
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.BLOCKED_LEAVE_MESSAGE,params).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                blockedMsgResp.postValue(BaseResp().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: Int?) {
                blockedMsgResp.postValue(BaseResp())
            }

        })

    }

    /**
     * ????????????
     */
    fun chooseLeaveMessage(id:String,flag:Int){
        val params = mutableMapOf<String,Any>()
        params["id"]=id
        params["selectedFlag"]=if (flag==0) 1 else 0
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.DO_SELECTED_LEAVE_MESSAGE,params).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                chooseMsgResp.postValue(null)
            }

            override fun onSuccess(t: Int?) {
                chooseMsgResp.postValue(t)
            }

        })
    }


    /**
     * ??????????????????
     */
    fun unReadLeaveMessage(memberId:String){
        val params = mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.UN_READ_LEAVE_MESSAGE,params).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: Int?) {
                unReadResp.postValue(t?:0)
            }

        })
    }

    /**
     * ??????????????????
     */
    fun clearUnReadLeaveMessage(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.MARK_UN_READ_LEAVE_MESSAGE,params).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: Int?) {
                unReadResp.postValue(t?:0)
            }

        })
    }

    /**
     * ?????????????????????
     */
    fun querySendMessage(page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.SEND_LEAVE_MESSAGE,params).subscribe(object :CoreHttpSubscriber<LeaveMsgResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                sendMsgResp.postValue(mutableListOf())
            }

            override fun onSuccess(t: LeaveMsgResp?) {
                sendMsgResp.postValue(t?.data?: mutableListOf())
            }

        })

    }

    /**
     * ?????????????????????
     */
    fun queryReceiveMessage(page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.RECEIVE_LEAVE_MESSAGE,params).subscribe(object :CoreHttpSubscriber<LeaveMsgResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                receiveMsgResp.postValue(mutableListOf())
            }

            override fun onSuccess(t: LeaveMsgResp?) {
                receiveMsgResp.postValue(t?.data?: mutableListOf())
            }

        })
    }

    /**
     * ??????/?????? ??????
     */
    fun openDanMu(barrageSwitch:Int){
        val params= mutableMapOf<String,Any>()
        params["barrageSwitch"]=barrageSwitch
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.OPEN_DAN_MU,params).subscribe(object :CoreHttpSubscriber<String>{
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t: String?) {

            }

        })
    }

    /**
     * ??????????????????
     */
    fun checkDanMu(){
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.IS_OPEN_DAN_MU, mutableMapOf()).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                checkDanMuResp.postValue(null)
            }

            override fun onSuccess(t: Int?) {
                checkDanMuResp.postValue(t)
            }

        })
    }

    /**
     * ??????????????????
     */
    fun queryAllDanMu(id:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=id
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.ALL_SELECTED_LEAVE_MESSAGE,params).subscribe(object :CoreHttpSubscriber<MutableList<DanMuBean>>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                danMuList.postValue( mutableListOf())
            }

            override fun onSuccess(t: MutableList<DanMuBean>?) {
                danMuList.postValue(t?: mutableListOf())
            }

        })
    }

    /**
     * ?????????????????????
     */
    fun queryAllDoll(){
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.DOLL_LIST,
            mutableMapOf()).subscribe(object :CoreHttpSubscriber<MutableList<DollListBean>>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                dollListResp.postValue(mutableListOf())
            }

            override fun onSuccess(t: MutableList<DollListBean>?) {
                dollListResp.postValue(t?: mutableListOf())
            }

        })
    }

    /**
     * ??????????????????
     */
    fun queryAllScene(){
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.SCENE_LIST,
            mutableMapOf()).subscribe(object :CoreHttpSubscriber<MutableList<SceneListBean>>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                sceneListResp.postValue(mutableListOf())
            }

            override fun onSuccess(t: MutableList<SceneListBean>?) {
                sceneListResp.postValue(t?: mutableListOf())
            }

        })
    }

    /**
     * ??????????????????
     */
    fun querySignBoardInfo(id:String){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=id
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.SIGNBOARD_INFO,
            params).subscribe(object :CoreHttpSubscriber<SignBoardBean>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                signBoardResp.postValue(SignBoardBean().apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t: SignBoardBean?) {
                signBoardResp.postValue(t?:SignBoardBean().apply {
                    buildError("???????????????")
                })
            }

        })
    }

    /**
     * ??????C?????????
     */
    fun saveShopEdit(req: SaveShopReq){
        MineHttpManager.postByModel(lifecycleOwner,MineApiConfig.SAVE_SHOP_EDIT,req).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                saveEditResp.postValue(TokenBalance(0).apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t:Int?) {
                saveEditResp.postValue(TokenBalance(t?:0))
            }

        })
    }

    /**
     * ?????????????????????
     */
    fun queryOwnerUgc(page: Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.OWNER_UGC_LIST,params).subscribe(object :CoreHttpSubscriber<OwnerUgcResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                ownerUgcList.postValue(mutableListOf())
            }

            override fun onSuccess(t:OwnerUgcResp?) {
                ownerUgcList.postValue(t?.data?: mutableListOf())
            }

        })
    }

    /**
     * ???????????????token???
     */
    fun queryOwnerToken(){
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.OWNER_TOKEN, mutableMapOf()).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                balanceResp.postValue(TokenBalance(0).apply {
                    buildError(throwable.msg)
                })
            }

            override fun onSuccess(t:Int?) {
                balanceResp.postValue(TokenBalance(t?:0))
            }

        })
    }

    /**
     * ????????????????????????
     */
    fun querySendFeed(page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.FEED_SEND, params).subscribe(object :CoreHttpSubscriber<FeedRecordResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                feedSend.postValue(mutableListOf())
            }

            override fun onSuccess(t:FeedRecordResp?) {
                feedSend.postValue(t?.data?: mutableListOf())
            }

        })
    }

    /**
     * ????????????????????????
     */
    fun queryReceiverFeed(page:Int){
        val params= mutableMapOf<String,Any>()
        params["page"]=page
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.FEED_RECEIVER, params).subscribe(object :CoreHttpSubscriber<FeedRecordResp>{
            override fun onFailed(throwable: CoreHttpThrowable) {
                feedReceiver.postValue(FeedRecordResp())
            }

            override fun onSuccess(t:FeedRecordResp?) {
                feedReceiver.postValue(t?:FeedRecordResp())
            }

        })
    }

    /**
     * ????????????????????????
     */
    fun queryFeedMsg(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.getByParams(lifecycleOwner,MineApiConfig.FEED_MESSAGE, params).subscribe(object :CoreHttpSubscriber<Int>{
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t:Int?) {
                unReadFeedResp.postValue(t?:0)
            }

        })
    }

    /**
     * ????????????????????????
     */
    fun clearFeedMsg(){
        val params= mutableMapOf<String,Any>()
        params[RunIntentKey.MEMBER_ID]=memberId
        MineHttpManager.postByParams(lifecycleOwner,MineApiConfig.FEED_MESSAGE_AS_READ, params).subscribe(object :CoreHttpSubscriber<String>{
            override fun onFailed(throwable: CoreHttpThrowable) {

            }

            override fun onSuccess(t:String?) {

            }

        })
    }

}
