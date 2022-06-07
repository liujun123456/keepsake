package com.shunlai.message

import com.shunlai.common.BaseApiConfig

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
object MessageApiConfig: BaseApiConfig() {


    const val ATTENTION_USER="ugc/member/follow"    //关注和不关注

    const val REPLY_COMMENT="ugc/comment"   //回复评论接口

    const val COMMENT_LIKE="ugc/comment/like"  //点赞接口

    const val UGC_COMPLAIN="ugc/complaint"     //投诉接口

    const val UPLOAD_IMG="api/upload/uploadImg"

    const val QUERY_ATTENTION="message/queryFollowMessage"   //查询关注列表

    const val QUERY_COMMENT_DATA="message/comment/list"   //查询评论列表

    const val QUERY_LIKE_OR_FOLLOW="message/likeOrCollectMessage"   //查询点赞和收藏

    const val QUERY_SYSTEM_MESSAGE="message/systemMessage"    //查询系统消息或者推送消息

    const val QUERY_MESSAGE_NUM="message/queryMessageNum"   //查询消息页的所有消息数据


    const val UPDATE_MESSAGE_READ="message/updateMessageRead"   //更新推送消息和系统消息为已读

    const val UPDATE_ATTENTION_MESSAGE_READ="message/updateSocialMessageRead"  //更新点赞，关注，收藏，消息为已读

    const val UPDATE_COMMENT_MESSAGE_READ="message/update/ugc/message/read"   //更新评论消息为已读

}
