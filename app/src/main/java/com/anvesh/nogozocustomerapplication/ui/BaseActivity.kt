package com.anvesh.nogozocustomerapplication.ui

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.anvesh.nogozocustomerapplication.R

abstract class BaseActivity: AppCompatActivity() {
    private lateinit var progressBar: ProgressBar

    override fun setContentView(layoutResID: Int) {

        val constraintLayout = layoutInflater.inflate(R.layout.activity_base,null) as ConstraintLayout
        val frameLayout = constraintLayout.findViewById(R.id.activity_container) as FrameLayout

        progressBar = constraintLayout.findViewById(R.id.base_progressbar)

        layoutInflater.inflate(layoutResID, frameLayout, true)

        super.setContentView(layoutResID)
    }

    fun showProgressBar(visibility: Boolean) {
        progressBar.visibility = if(visibility) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard(){
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var v: View? = currentFocus
        if(v == null){
            v = View(this)
        }
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }
}