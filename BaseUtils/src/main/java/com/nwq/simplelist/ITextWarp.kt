package com.nwq.simplelist

class ITextWarp<T>(private val obj: T, val getText: (T) -> String) : IText<T> {
    override fun getText(): String {
        return getText.invoke(obj)
    }

    override fun getT(): T {
        return obj
    }


}