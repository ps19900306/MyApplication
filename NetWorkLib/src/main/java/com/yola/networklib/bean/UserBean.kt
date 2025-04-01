package com.yola.networklib.bean

//存储用户信息
data class UserBean(val username: String, val passTime: String){

    override fun toString(): String {
        return "UserBean(username='$username', passTime='$passTime')"
    }
}



