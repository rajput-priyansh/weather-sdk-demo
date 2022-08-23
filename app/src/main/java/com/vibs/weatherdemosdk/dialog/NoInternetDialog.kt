package com.vibs.weatherdemosdk.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.vibs.weatherdemosdk.R

class NoInternetDialog(context: Context, var onClickListener:View.OnClickListener) : AlertDialog(context) {

    private var btnRetry: Button? = null

    companion object{
        private const val TAG = "BBNoInternetDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_no_internet,
            null,false)
        setContentView(view)
        initUi()
        setCanceledOnTouchOutside(false)
    }
    private fun initUi() {
        btnRetry = findViewById(R.id.btn_retry)

        btnRetry?.setOnClickListener(onClickListener)
    }

}