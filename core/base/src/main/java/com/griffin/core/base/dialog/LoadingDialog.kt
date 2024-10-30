package com.griffin.core.base.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import com.griffin.core.base.R
import com.griffin.core.utils.runMain

/**
 * 加载中弹窗
 */
class LoadingDialog(
    context: Context,
    private var message: String = context.getString(R.string.base_loading_text),
    @StyleRes themeResId: Int = R.style.LoadingDialog
) : Dialog(context, themeResId) {
    private var tvLoadingText: AppCompatTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_dialog_loading_layout)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        tvLoadingText = findViewById(R.id.base_loading_text)
        tvLoadingText?.text = message
    }

    override fun show() {
        runMain{
            super.show()
        }
    }

    fun updateText(text: String) {
        runMain {
            tvLoadingText?.text = text
        }
        message = text
    }
}