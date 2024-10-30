package com.griffin.core.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
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
import com.griffin.core.utils.gone
import com.griffin.core.utils.toast
import com.griffin.core.utils.visible
import com.therouter.TheRouter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType

/**
 * 用于实现Hilt的ViewModel
 */
abstract class HiltBaseFragment<DB : ViewDataBinding>(@LayoutRes val layoutResId: Int) : AbstractFragment() {

    /**
     * Root DataBinding
     */
    private lateinit var _rootBinding: BaseRootLayoutBinding
    val rootBinding get() = _rootBinding

    /**
     * Content DataBinding
     */
    private var _binding: DB? = null
    val binding get() = _binding!!

    abstract val viewModel: BaseViewModel

    /**
     * Loading Dialog
     */
    val loadingDialog by lazy {
        LoadingDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createViewWithBinding(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
        registerObserver()
        obtainData()
    }

    private fun createViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View {
        _rootBinding = DataBindingUtil.inflate(inflater, R.layout.base_root_layout, container, false)
        _rootBinding.lifecycleOwner = viewLifecycleOwner
        if (showTitleBar()) {
            _rootBinding.baseTitleLayout.root.visibility = View.VISIBLE
        } else {
            _rootBinding.baseTitleLayout.root.visibility = View.GONE
        }
        initContent()
        return _rootBinding.root
    }

    private fun initContent() {
        _rootBinding.baseContentLayout.removeAllViews()
        _binding = DataBindingUtil.inflate(layoutInflater, layoutResId, _rootBinding.baseContentLayout, true)
        _binding?.lifecycleOwner = viewLifecycleOwner
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
     * @param msg Message
     */
    fun showLoadingDialog(msg: String? = null) {
        if (!msg.isNullOrEmpty()) {
            loadingDialog.updateText(msg)
        }
        loadingDialog.show()
    }

    override fun onError(message: String?) {
        showContent()
        message?.toast()
    }

    /**
     * Set Activity Title
     */
    fun setTitleName(title: String) {
        _rootBinding.baseTitleLayout.baseTitleText.text = title
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}