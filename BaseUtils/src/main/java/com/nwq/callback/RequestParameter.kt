package com.nwq.callback

interface RequestParameter<T> {

    public suspend fun onRequestParameter():T?
}