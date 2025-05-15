package com.nwq.simplelist

class ICheckTextWrap<T>(val t: T, var check: Boolean, val getText: (T) -> String) : ICheckText<T> {
    override fun setCheckStatus(boolean: Boolean) {
        check = boolean
    }

    override fun isCheckStatus(): Boolean {
        return check
    }

    override fun getText(): String {
        return getText.invoke(t)
    }

    override fun getT(): T {
        return t
    }

}