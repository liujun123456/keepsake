package com.shunlai.publish

import com.shunlai.common.BaseApiConfig

/**
 * @author Liu
 * @Date   2021/4/13
 * @mobile 18711832023
 */
object PublishApiConfig: BaseApiConfig() {

    const val UPLOAD_IMG="api/upload/uploadImg"   //上传图片
    const val UPLOAD_VIDEO="api/upload/oss/video"   //上传视频
    const val ADD_WISH="ugc/add/wish"             //上传心愿
    const val PUBLISH="ugc/publish"               //发布笔记
    const val SEARCH_GOODS="union/goods/search"   //查询商品
    const val QUERY_HOT_HT="topic/listRecommend"  //热门话题
}
