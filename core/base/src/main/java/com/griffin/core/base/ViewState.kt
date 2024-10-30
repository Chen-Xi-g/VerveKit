package com.griffin.core.base

sealed class ViewState(
    val msg: String? = null
) {
    /**
     * Callback for loading Dialog.
     *
     * @param msgInfo Text displayed during loading.
     */
    data class Loading(private val msgInfo: String?) :
        ViewState(msg = msgInfo)

    /**
     * Callback for load failure Dialog.
     *
     * @param msgInfo Error message.
     */
    data class Error(
        private val msgInfo: String?
    ) : ViewState(msg = msgInfo)

    /**
     * Callback for load success Toast.
     *
     * @param msgInfo Success message.
     */
    data class Success(
        private val msgInfo: String? = null
    ) : ViewState(msg = msgInfo)
}