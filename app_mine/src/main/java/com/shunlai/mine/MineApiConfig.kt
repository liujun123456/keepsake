package com.shunlai.mine

import com.shunlai.common.BaseApiConfig

/**
 * @author Liu
 * @Date   2021/4/20
 * @mobile 18711832023
 */
object MineApiConfig: BaseApiConfig() {

    const val SEND_VERIFY_CODE="member/sendSms"

    const val LOGIN_WITH_VERIFY="member/verify/login"

    const val LOGIN_WITH_WE_CHAT="member/login/wechat"

    const val LOGIN_OUT="member/logout"

    const val BIND_MOBILE="member/rebind/phone"

    const val BIND_WE_CHAT="member/bind/wx"

    const val UN_BIND_WE_CHAT="member/unbind/wx"

    const val QUERY_MEMBER_UGC="member/allUgc"

    const val QUERY_MEMBER_GOODS="member/showcase"

    const val QUERY_LIKE_UGC="member/likeUgc"

    const val QUERY_MEMBER_INFO="member/info"

    const val QUERY_USER_WALL_PHOTO="picture/option/wall"

    const val QUERY_USER_ALL_WALL_PHOTO="picture/wall"

    const val UPDATE_USER_WALL_PHOTO="picture/update/wall"

    const val MINE_FOLLOW="member/follow/list"

    const val MINE_FUN_S="member/fees/list"

    const val ATTENTION_USER="ugc/member/follow"

    const val UPDATE_USER_INFO="member/update/info"

    const val QUERY_USER_LABEL="member/selectList"

    const val UPDATE_USER_LABEL="member/addRelation"

    const val SKIN_THEME="theme/skin/list"

    const val UPDATE_SKIN_THEME="theme/bind/skin"

    const val QUERY_ORDER="order/third/platform/list"

    const val QUERY_TOKEN_SCORE="member/queryTokenScore"

    const val QUERY_TOKEN_SCORE_LIST="member/token/log/list"

    const val DELETE_UGC="ugc/deleteUgc"

    const val DO_LIKE_OR_COLLECT="ugc/like"  //点赞和收藏

    const val UPLOAD_IMG="api/upload/uploadImg"   //上传图片

    const val LEAVE_MESSAGE="impression/send"  //留言

    const val FEEDING="feed/sendFeed" //投喂

    const val DELETE_LEAVE_MESSAGE="impression/deleted" //删除留言

    const val BLOCKED_LEAVE_MESSAGE="impression/blocked" //屏蔽留言

    const val UN_READ_LEAVE_MESSAGE="impression/count" //留言未读消息

    const val MARK_UN_READ_LEAVE_MESSAGE="impression/read" //标记留言信息为已读

    const val ALL_SELECTED_LEAVE_MESSAGE="impression/barrageList" //查询所有精选弹幕

    const val OPEN_DAN_MU="impression/barrageSwitch"   //改变弹幕开关状态

    const val IS_OPEN_DAN_MU="impression/queryBarrage"   //是否开启弹幕

    const val RECEIVE_LEAVE_MESSAGE="impression/receive/page"   //收到的留言

    const val SEND_LEAVE_MESSAGE="impression/send/page"   //发送的留言

    const val DO_SELECTED_LEAVE_MESSAGE="impression/selected"  //精选或者未精选

    const val SOURCE_LIST="principal/sourceList"   //查询主理人资源和场景资源

    const val DOLL_LIST="principal/ownerModelList"   //查询主理人列表

    const val SCENE_LIST="principal/ownerSceneList"   //查询场景列表

    const val SIGNBOARD_INFO="principal/signboard"   //查询用户吊牌信息

    const val SAVE_SHOP_EDIT="principal/savePrincipal"   //保存修改信息

    const val OWNER_UGC_LIST="ugc/ownerUgcList"   //查询自己发布并审核通过的笔记

    const val OWNER_TOKEN="tokenCoin/remainder"   //查询自己的tokenB

    const val FEED_RECEIVER="feed/receive/page"   //查询收到的投喂

    const val FEED_SEND="feed/send/page"   //查询发送的投喂

    const val FEED_MESSAGE="feed/countFeeds"   //查询被投喂消息记录

    const val FEED_MESSAGE_AS_READ="feed/feedsRead"   //投喂消息记录置为已读

    const val BLOCK_USER="blacklist/add"  //拉黑用户

    const val CHECK_BLOCK_USER="blacklist/getStatus"  //查询用户拉黑状态

    const val BLACK_LIST="blacklist/getBlacklist"  //查询黑名单列表

    const val CANCEL_BLACK="blacklist/cancel"  //取消黑名单

    const val SEND_TEST_RECORD="member/distance/activeTesting"  //获取自己发起的测试记录

    const val RECEIVER_TEST_RECORD="member/distance/passiveTesting"  //获取别人发起的测试记录

    const val TOKEN_STAR_MAP="member/distance/map"  //获取距离关系图

    const val MATCH_DISTANCE="member/distance/match"  //计算星球距离

    const val RECOMMEND_BRAND_LIST="member/brand/listRecommended"  //获取所有推荐品牌列表

    const val SAVE_BRAND_STATE="member/brand/save"  //保存会员品牌信息

    const val QUERY_ALL_BRAND_LIST="member/brand/search"  //根据关键字查询品牌

    const val SKIP_BRAND="member/brand/skip"  //跳过填写品牌兴趣流程

    const val BRAND_STATE="member/brand/status"  //会员品牌兴趣填写状态

    const val OWNER_BRAND_LIST="member/brand/ownerList"  //会员品牌兴趣填写状态

    const val CHECK_BRAND_STATE="member/brand/statusCheck"  //匹配校验结果

    const val SHARE_TOKEN_STAR="share/poster/token"  //分享token星球


}
