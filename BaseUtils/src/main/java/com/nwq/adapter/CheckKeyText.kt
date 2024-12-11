package com.nwq.adapter


data class CheckKeyText(val id: Int, val tag: String, var isChecked: Boolean = false) : IKeyText {
    override fun getText(): String {
        return tag
    }

    override fun getKey(): Int {
        return id
    }
}
