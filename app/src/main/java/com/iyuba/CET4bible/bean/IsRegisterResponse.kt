package com.iyuba.CET4bible.bean

data class IsRegisterResponse(
    val Amount: String = "",
    val mobile: String = "",
    val message: String = "",
    val result: String = "",
    val uid: Int = 0,
    val isteacher: String = "",
    val expireTime: Int = 0,
    val money: String = "",
    val credits: Int = 0,
    val jiFen: Int = 0,
    val nickname: String = "",
    val vipStatus: Int = -1,
    var imgSrc: String = "",
    val email: String = "",
    var username: String = "未登录",
)