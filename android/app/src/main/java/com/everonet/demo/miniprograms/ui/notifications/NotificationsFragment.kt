package com.everonet.demo.miniprograms.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.everonet.demo.miniprograms.R
import com.everonet.demo.miniprograms.base.BaseFragment
import com.everonet.demo.miniprograms.databinding.FragmentNotificationsBinding
import com.everonet.demo.miniprograms.ui.mini.MiniAppListActivity

class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun getLayoutId(): Int = R.layout.fragment_notifications

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this
        notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        notificationsViewModel.text.observe(this, Observer {
            mBinding.textNotifications.text = it
        })
    }

    fun startMiniList(view: View) {
        MiniAppListActivity.startActivity(activity)
    }
}