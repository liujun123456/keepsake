package com.shunlai.ugc

import com.shunlai.common.BaseApiConfig

/**
 * @author Liu
 * @Date   2021/4/27
 * @mobile 18711832023
 */
object UgcApiConfig:BaseApiConfig() {

    const val GOODS_COMMENT_LIST="ugc/product/comment"    //商品评论列表查询
    const val UGC_GOODS_DETAIL="union/goods/ugcProductDetail"   //商品详情
    const val UGC_SEARCH="ugc/recommend/more"    //笔记列表查询
    const val USER_SEARCH="member/search"    //用户查询
    const val HOT_SEARCH="product/hot/search"   //热门笔记查询
    const val BUILD_POST="ugc/share/info"   //生成分享的图片
    const val UGC_DETAIL_IMG="ugc/detail/img"
    const val UGC_DETAIL_VIDEO="ugc/detail/video"
    const val UGC_COMMENT_LIST="ugc/detail/comments"
    const val UGC_COMMENT_REPLY_List="ugc/detail/comments/reply"
    const val UGC_DO_COMMENT="ugc/comment"     //评论
    const val DO_COMMENT_LIKE="ugc/comment/like"  //评论点赞
    const val DO_ATTENTION="ugc/member/follow"  //关注
    const val DO_LIKE_OR_COLLECT="ugc/like"  //点赞和收藏
    const val DELETE_UGC="ugc/deleteUgc"    //删除笔记
    const val BUILD_GOODS_ORDER="union/goods/generate/link"
    const val BLOCK_USER="blacklist/add"  //拉黑用户

}
