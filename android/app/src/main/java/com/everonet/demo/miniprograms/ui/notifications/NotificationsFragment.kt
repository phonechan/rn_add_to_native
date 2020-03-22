package com.everonet.demo.miniprograms.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.everonet.demo.miniprograms.R
import com.everonet.demo.miniprograms.base.BaseFragment
import com.everonet.demo.miniprograms.databinding.FragmentNotificationsBinding
import com.everonet.demo.miniprograms.ui.mini.MiniAppDetailActivity
import com.everonet.demo.miniprograms.ui.mini.MiniAppListActivity
import com.google.zxing.integration.android.IntentIntegrator

class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_notifications

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this

    }

    fun startMiniList(view: View) {
        MiniAppListActivity.startActivity(activity)
    }

    fun scan(view: View) {
        IntentIntegrator.forSupportFragment(this)
            .setBeepEnabled(true)
            .setPrompt(getString(R.string.scan_prompt))
            .setOrientationLocked(true)
            .initiateScan(arrayListOf(IntentIntegrator.QR_CODE))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
//                Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                MiniAppDetailActivity.startActivity(activity, result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}