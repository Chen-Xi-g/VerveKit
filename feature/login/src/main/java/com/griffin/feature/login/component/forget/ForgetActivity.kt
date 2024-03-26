package com.griffin.feature.login.component.forget

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.griffin.core.base.activity.HiltBaseActivity
import com.griffin.core.data.model.CaptchaImageModel
import com.griffin.core.utils.gone
import com.griffin.core.utils.statusHeight
import com.griffin.feature.login.R
import com.griffin.feature.login.databinding.ActivityForgetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 描述：
 */
@AndroidEntryPoint
class ForgetActivity : HiltBaseActivity<ActivityForgetBinding>(R.layout.activity_forget) {

    /**
     * 初始化ViewModel
     */
    override val viewModel: ForgetViewModel by viewModels()

    /**
     * 初始化View
     */
    override fun initView(savedInstanceState: Bundle?) {
        binding.vPadding.statusHeight()
        rootBinding.baseTitleLayout.root.gone()
        showContent()
        initClick()
    }

    /**
     * 获取数据
     */
    override fun obtainData() {
        refreshCode()
    }

    /**
     * 注册观察者
     */
    override fun registerObserver() {
        super.registerObserver()
        lifecycleScope.launch {
            viewModel.captchaImage.collect {
                binding.ivCodeContent.setImageBitmap(it.bitmap)
            }
        }
    }

    private fun initClick(){
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.ivCodeContent.setOnClickListener {
            refreshCode()
        }
        binding.tvForgetPassword.setOnClickListener {
            forgetPwd()
        }
        successDialog.setOnDismissListener {
            finish()
        }
        errorDialog.setOnDismissListener {
            refreshCode()
        }
    }

    /**
     * 重写此方法，删除父逻辑，不需要填充状态栏
     */
    override fun paddingWindow() {
    }

    private fun refreshCode() {
        viewModel.getCaptchaImage()
    }

    private fun forgetPwd(){
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val code = binding.etCode.text.toString()
        viewModel.forgetPwd(
            username = username,
            password = password,
            confirmPassword = confirmPassword,
            code = code
        )
    }

}