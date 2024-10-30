package com.griffin.core.base.activity

import android.animation.Animator
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.griffin.core.base.R
import com.griffin.core.base.ViewState
import com.griffin.core.base.databinding.BaseRootLayoutBinding
import com.griffin.core.base.dialog.LoadingDialog
import com.griffin.core.base.vm.BaseViewModel
import com.griffin.core.dialog.ErrorDialog
import com.griffin.core.dialog.SuccessDialog
import com.griffin.core.utils.isDarkTheme
import com.griffin.core.utils.isVisible
import com.griffin.core.utils.runMain
import com.griffin.core.utils.toast
import com.therouter.TheRouter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.jessyan.autosize.AutoSizeCompat

/**
 * Activity的主要逻辑实现
 *
 * @param DB DataBinding
 */
abstract class HiltBaseActivity<DB : ViewDataBinding>(@LayoutRes val layoutResId: Int) :
    AbstractActivity() {

    /**
     * Root DataBinding
     */
    private lateinit var _rootBinding: BaseRootLayoutBinding
    val rootBinding get() = _rootBinding

    /**
     * Content DataBinding
     */
    private lateinit var _binding: DB
    val binding get() = _binding

    abstract val viewModel: BaseViewModel

    /**
     * Loading Dialog
     */
    val loadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        statusIconDark(!isDarkTheme())
//        navigationIconDark(!isDarkTheme())
        lockOrientation(isPortrait())
        _rootBinding = DataBindingUtil.setContentView(this, R.layout.base_root_layout)
        _rootBinding.lifecycleOwner = this
        initContent()
        initClick()
        initView(savedInstanceState)
        registerObserver()
        obtainData()
    }

    private fun initContent() {
        if (showTitleBar()) {
            _rootBinding.baseTitleLayout.root.visibility = View.VISIBLE
        } else {
            _rootBinding.baseTitleLayout.root.visibility = View.GONE
        }
        _rootBinding.baseContentLayout.removeAllViews()
        _binding = DataBindingUtil.inflate(
            layoutInflater,
            layoutResId,
            _rootBinding.baseContentLayout,
            true
        )
        _binding.lifecycleOwner = this
    }

    private fun initClick() {
        _rootBinding.baseTitleLayout.baseBackButton.setOnClickListener {
            finish()
        }
    }

    override fun registerObserver() {
        lifecycleScope.launch {
            viewModel.viewState.collectLatest {
                when (it) {
                    is ViewState.Error -> onError(it.msg)
                    is ViewState.Loading -> showLoadingDialog(it.msg)
                    is ViewState.Success -> showContent()
                }
            }
        }
    }

    /**
     * Show success content.
     */
    fun showContent() {
        loadingDialog.dismiss()
    }

    /**
     * Show loading dialog.
     *
     * @param msg Loading dialog message.
     */
    fun showLoadingDialog(msg: String? = null) {
        if (!msg.isNullOrEmpty()) {
            loadingDialog.updateText(msg)
        }
        loadingDialog.show()
    }

    /**
     * Set title name.
     */
    fun setTitleName(title: String) {
        _rootBinding.baseTitleLayout.baseTitleText.text = title
    }

    override fun onError(message: String?) {
        showContent()
        message?.toast()
    }

    open fun isPortrait(): Boolean = true

    /**
     * Lock screen orientation.
     *
     * @param isPortrait true: Portrait, false: Landscape
     */
    fun lockOrientation(isPortrait: Boolean) {
        requestedOrientation = if (isPortrait) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    /**
     * Hide input method when touch outside EditText.
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        /*        if (ev?.action == MotionEvent.ACTION_DOWN) ev.let {
                    if (isShouldHideInput(currentFocus, it)) {
                        hideKeyboard()
                    }
                }*/
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(view: View?, ev: MotionEvent): Boolean {
        if (view is EditText) {
            val l: IntArray = intArrayOf(0, 0)
            view.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom: Int = top + view.getHeight()
            val right: Int = (left
                    + view.getWidth())
            return !(ev.x > left && ev.x < right
                    && ev.y > top && ev.y < bottom)
        }
        return false
    }

    fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard() {
        val im =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    /**
     * Set status bar color.
     */
    fun statusBarColor(@ColorRes color: Int) {
        window.statusBarColor = getColor(color)
    }

    /**
     * Set status bar icon color.
     *
     * @param isDark true: Black icon, false: White icon
     */
    fun statusIconDark(isDark: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = isDark
        }
    }

    /**
     * Set navigation bar color.
     */
    fun navigationBarColor(@ColorRes color: Int) {
        window.navigationBarColor = getColor(color)
    }

    /**
     * Set navigation bar icon color.
     *
     * @param isDark true: Black icon, false: White icon
     */
    fun navigationIconDark(isDark: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightNavigationBars = isDark
        }
    }

}