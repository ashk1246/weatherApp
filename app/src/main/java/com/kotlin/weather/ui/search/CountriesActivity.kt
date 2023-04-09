package com.kotlin.weather.ui.search

import android.content.Intent
import android.os.Bundle
import com.kotlin.weather.base.BaseActivity
import com.kotlin.weather.databinding.ActivitySearchBinding
import com.kotlin.weather.utils.LogUtil

/**
 * Created by Ashok
 */

class CountriesActivity : BaseActivity<ActivitySearchBinding>() {

    companion object {
        const val RESULT_CODE: Int = LogUtil.RESULT_CODE
        const val RESULT_DATA: String = "RESULT_DATA"
    }

    override fun createBinding(): ActivitySearchBinding =
        ActivitySearchBinding.inflate(layoutInflater)

    /**
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.button.setOnClickListener {
            val intent = Intent()
            intent.putExtra(RESULT_DATA, binding.editTextTextCityName.text.toString())
            setResult(RESULT_CODE, intent)
            finish()
        }
    }
}
