package com.shunlai.main

import com.shunlai.common.BaseApiConfig

/**
 * @author Liu
 * @Date   2021/5/10
 * @mobile 18711832023
 */
object HomeApiConfig : BaseApiConfig() {
    const val QUERY_ALL_HT="topic/listAll"
    const val QUERY_HOT_HT="topic/listRecommend"
    const val QUERY_HT_BY_KEYWORD="topic/listByTag"
    const val QUERY_HT_NEW_UGC="ugc/topic/page"  //话题最新笔记
    const val QUERY_HT_RECOMMEND_UGC="ugc/topic/weight"  //话题推荐笔记
    const val QUERY_HOME_RECOMMEND_UGC="ugc/home/page"   //首页推荐笔记
    const val QUERY_HOME_ATTENTION_UGC="ugc/follow/page"  //首页关注
    const val QUERY_HOME_ATTENTION_TUI_J="ugc/follow/weight/page"  //首页关注推荐
    const val DO_ATTENTION="ugc/member/follow"  //关注
    const val DO_LIKE_OR_COLLECT="ugc/like"  //点赞和收藏
    const val DELETE_UGC="ugc/deleteUgc"   //删除笔记
    const val QUERY_HT_ACTIVITY="activity/detail"  //查询话题活动
    const val QUERY_MESSAGE_NUM="message/queryMessageNum"   //查询消息页的所有消息数据
    const val BRAND_STATE="member/brand/status"  //会员品牌兴趣填写状态
    const val BIND_CID="member/bind/cid"   //绑定cid
    const val QUERY_UGC_CHANNELS="ugc/channels"
}
