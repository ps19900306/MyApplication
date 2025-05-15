package com.nwq.simplelist

interface ICheckText<T> : IText<T> {

    fun setCheckStatus(boolean: Boolean)

    fun isCheckStatus(): Boolean
}