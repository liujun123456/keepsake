package com.shunlai.ugc.entity.resp

/**
 * @author Liu
 * @Date   2021/5/11
 * @mobile 18711832023
 */
class UgcDetailImgResp {
    var likeMembers:MutableList<LikeMember>? = mutableListOf()

    class LikeMember{
        var avatar:String?=""
    }

}
