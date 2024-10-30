package com.griffin.core.base.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.griffin.core.base.R

abstract class AbstractFragment : Fragment(){

    /**
     * initialize view-related components.
     */
    abstract fun initView(view: View, savedInstanceState: Bundle?)

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
    open fun showTitleBar() = false

    /**
     * Error callback.
     *
     * @param message error message
     */
    abstract fun onError(message: String?)

}