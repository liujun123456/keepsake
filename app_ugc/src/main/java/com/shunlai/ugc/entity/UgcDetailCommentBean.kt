package com.shunlai.ugc.entity

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class UgcDetailCommentBean:BaseResp() {
    var id:String?=""
    var content:String?=""
    var commentMid:String?=""
    var commentTime:String?=""
    var nickName:String?=""
    var isLike:Int?=0
    var publishMid:String?=""
    var likeNum:Int?=0
    var avatar:String?=""
    var commentNums:Int?=0
    var commentId:String?=""
    var replyContent:String?=""
    var replyMid:String?=""
    var replyTime:String?=""
    var replyNickName:String?=""
    var isAuthor:String?=""
    var displayTime:String?=""
    var ugcComments:MutableList<UgcDetailCommentBean> = mutableListOf()
    var childPage=1

}
