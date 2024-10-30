package com.griffin.core.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
/**
 * Abstract class for Activity.
 */
abstract class AbstractActivity : AppCompatActivity(){

    /**
     * initialize view-related components.
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * Data initialization.
     */
    abstract fun obtainData()

    /**
     * Register observer for stream objects.
     */
    abstract fun registerObserver()

    /**
     * Whether to show the title bar, default is not to show.
     */
    open fun showTitleBar() = true

    /**
     * Error callback.
     *
     * @param message error message
     */
    abstract fun onError(message: String?)

}