package com.example.learningpandaai.core.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileCacheNotifier @Inject constructor(){

    private val _updates = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val updates: SharedFlow<Unit> = _updates.asSharedFlow()

    fun notifyProfileCacheUpdated() {
        _updates.tryEmit(Unit)
    }
}