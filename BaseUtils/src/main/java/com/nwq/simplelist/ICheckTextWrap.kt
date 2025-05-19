package com.nwq.simplelist

class ICheckTextWrap<T>(private val obj: T, var check: Boolean=false, val getText: (T) -> String) : ICheckText<T> {
    override fun setCheckStatus(boolean: Boolean) {
        check = boolean
    }

    override fun isCheckStatus(): Boolean {
        return check
    }

    override fun getText(): String {
        return getText.invoke(obj)
    }

    override fun getT(): T {
        return obj
    }

}