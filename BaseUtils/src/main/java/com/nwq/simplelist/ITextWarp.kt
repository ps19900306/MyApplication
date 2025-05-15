package com.nwq.simplelist

class ITextWarp<T>(val t: T, val getText: (T) -> String) : IText<T> {
    override fun getText(): String {
        return getText.invoke(t)
    }

    override fun getT(): T {
        return t
    }

}