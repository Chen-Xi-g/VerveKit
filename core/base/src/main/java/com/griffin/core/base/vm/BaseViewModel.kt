package com.griffin.core.base.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffin.core.base.ViewState
import com.griffin.core.data.model.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel基类
 */
open class BaseViewModel : ViewModel() {

    companion object {
        /**
         * Displays nothing.
         */
        const val ERROR_NOTING = 0

        /**
         * Show Toast.
         */
        const val ERROR_TOAST = 1
    }

    /**
     * ViewState
     */
    private val _viewState = MutableSharedFlow<ViewState>()
    val viewState: SharedFlow<ViewState> = _viewState.asSharedFlow()

    /**
     * Show loading Dialog.
     *
     * @param message Loading dialog message.
     */
    fun loadingDialog(message: String? = "Loading...") {
        viewModelScope.launch {
            _viewState.emit(ViewState.Loading(message))
        }
    }

    /**
     * Show error info.
     *
     * @param message Error message.
     */
    fun error(
        message: String? = "Load failed."
    ) {
        viewModelScope.launch {
            _viewState.emit(ViewState.Error(message))
        }
    }

    /**
     * Show success info.
     *
     * @param message Success message.
     */
    fun success(
        message: String? = null
    ) {
        viewModelScope.launch {
            _viewState.emit(ViewState.Success(msgInfo = message))
        }
    }

    protected fun emit(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    /**
     * Handle request.
     *
     * @param message Loading dialog message.
     * @param isLoadingDialog true: Show loading dialog, false: Do not show loading dialog.
     * @param block Request block.
     */
    protected fun handleRequest(
        message: String? = "Loading...",
        isLoadingDialog: Boolean = false,
        errorType: Int = ERROR_TOAST,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (isLoadingDialog) {
            loadingDialog(message)
        }
        viewModelScope.launch {
            coroutineContext.ensureActive()
            try {
                block.invoke(this)
            } catch (e: Exception) {
                e.printStackTrace()
                errorBlock?.invoke(this)
                if (errorType == ERROR_TOAST) {
                    error(e.message)
                }
            }
            this@BaseViewModel.success()
        }
    }

    protected inline fun <reified T> CoroutineScope.net(
        errorType: Int = ERROR_TOAST,
        noinline block: suspend () -> Resource<T>
    ): Deferred<T?> = NetDeferred(async(Dispatchers.IO + SupervisorJob()) {
        coroutineContext.ensureActive()
        when (val result = block.invoke()) {
            is Resource.Success -> {
                result.data
            }

            is Resource.Error -> {
                when (errorType) {
                    ERROR_TOAST -> {
                        throw Exception(result.message)
                    }

                    else -> {
                        success()
                    }
                }
                null
            }
        }
    })
}