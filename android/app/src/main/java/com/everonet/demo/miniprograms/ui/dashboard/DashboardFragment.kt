package com.everonet.demo.miniprograms.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.everonet.demo.miniprograms.MyReactActivity
import com.everonet.demo.miniprograms.R
import com.everonet.demo.miniprograms.base.BaseFragment
import com.everonet.demo.miniprograms.databinding.FragmentDashboardBinding

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
        MyReactActivity.startActivity(activity, "mini01", "example")
    }

    fun startMiniProgram02(view: View) {
        MyReactActivity.startActivity(activity, "mini02", "TicTacToe")
    }
}