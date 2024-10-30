package com.griffin.core.base.activity

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.griffin.core.base.vm.BaseViewModel
import java.lang.reflect.ParameterizedType


/**
 * 用于没有实现Hilt的ViewModel
 */
abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding>(layoutResId: Int) : HiltBaseActivity<DB>(layoutResId) {

    override val viewModel: VM by lazy {
        createViewModel()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <VM> getVmClazz(obj: Any): Class<VM> {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
    }

    /**
     * Create ViewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this)[getVmClazz(this)]
    }

}