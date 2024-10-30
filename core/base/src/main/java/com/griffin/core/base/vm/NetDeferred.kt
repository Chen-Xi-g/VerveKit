package com.griffin.core.base.vm

import kotlinx.coroutines.Deferred

class NetDeferred<M>(private val deferred: Deferred<M>) : Deferred<M> by deferred {
    override suspend fun await(): M {
        return try {
            deferred.await()
        } catch (e: Exception) {
            throw  e
        }
    }
}