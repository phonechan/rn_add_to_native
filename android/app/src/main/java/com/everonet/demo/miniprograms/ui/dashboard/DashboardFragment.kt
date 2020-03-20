package com.everonet.demo.miniprograms.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.everonet.demo.miniprograms.App
import com.everonet.demo.miniprograms.MyReactActivity
import com.everonet.demo.miniprograms.R
import com.everonet.demo.miniprograms.base.BaseFragment
import com.everonet.demo.miniprograms.databinding.FragmentDashboardBinding
import com.everonet.demo.miniprograms.util.FileUtils
import com.everonet.demo.miniprograms.util.ZipUtils

class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun getLayoutId(): Int = R.layout.fragment_dashboard

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        dashboardViewModel.text.observe(this, Observer {
            mBinding.textDashboard.text = it
        })
    }

    fun startMiniProgram01(view: View) {
        MyReactActivity.startActivity(activity, "mini01", "NativebaseKitchenSink")
    }

    fun startMiniProgram02(view: View) {
        MyReactActivity.startActivity(activity, "mini02", "TicTacToe")
    }

    fun startMiniProgram03(view: View) {
        MyReactActivity.startActivity(activity, "mini03", "HotUpdateDemo")
    }

    fun unzip(view: View) {
//        val pathIn = Environment.getExternalStorageDirectory().absolutePath + File.separator + "1/Ge89c3ed65bf141758aa240e30e9c67ec_1.bundle"
//        val pathOut =
//            Environment.getExternalStorageDirectory().absolutePath + File.separator + "1/Ge89c3ed65bf141758aa240e30e9c67ec"


        // 解压缩
        val pathIn =
            "${App.instance.externalCacheDir}${FileUtils.PATH_DOWNLOAD}G31f75ea1e3454e6b886ca6e4fe97971e_5"
        val pathOut: String =
            "${App.instance.externalCacheDir}${FileUtils.PATH_DOWNLOAD}G31f75ea1e3454e6b886ca6e4fe97971e"
        ZipUtils.unzip(pathIn, pathOut)
    }
}