package com.nwq.adapter

data class KeyTextImp(val text1: String,val key1:Int):IKeyText {
    override fun getText(): String {
         return text1
    }

    override fun getKey(): Int {
         return key1
    }
}