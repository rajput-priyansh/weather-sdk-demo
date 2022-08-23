package com.vibs.weatherdemosdk

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.vibs.weatherdemosdk.base.BaseActivity
import com.vibs.weatherdemosdk.databinding.ActivityMainBinding
import com.vibs.weatherdemosdk.listener.WeatherListener
import com.vibs.weatherdemosdk.ui.WeatherDetailsFragment
import com.vibs.weatherdemosdk.view_model.WeatherViewModel

class MainActivity : BaseActivity(), WeatherListener {

    private lateinit var binding: ActivityMainBinding

    private val weatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initData()

        initUi()

        observer()
    }

    override fun initData() {
        super.initData()
    }

    override fun initUi() {
        super.initUi()

        this.setAsActionBar(binding.toolBar)

        binding.toolBar.title = getString(R.string.app_name)

        addFragmentReplace(binding.container.id, WeatherDetailsFragment(), false)

    }

    override fun observer() {
        super.observer()

    }

}