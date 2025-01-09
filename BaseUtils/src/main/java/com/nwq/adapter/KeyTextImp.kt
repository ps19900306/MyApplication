package com.nwq.adapter

data class KeyTextImp(val text: String,val key:Int):IKeyText {
    override fun getText(): String {
         return text
    }

    override fun getKey(): Int {
         return key
    }
}