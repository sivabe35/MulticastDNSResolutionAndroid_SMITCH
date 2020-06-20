package com.siva.multicastdnsresolutionandroid.ui.main

import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.siva.multicastdnsresolutionandroid.R
import com.siva.multicastdnsresolutionandroid.databinding.MainFragmentBinding
import com.siva.multicastdnsresolutionandroid.interfaces.NsdServiceDiscoveryInterface
import com.siva.multicastdnsresolutionandroid.model.NSDServiceInfo
import com.siva.multicastdnsresolutionandroid.nsdlistener.NSDHelper
import java.util.*


class MainFragment : Fragment(), NsdServiceDiscoveryInterface {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val TAG = MainFragment::class.java.simpleName
    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private lateinit var mainBinding: MainFragmentBinding
    private lateinit var nsdHelper: NSDHelper

    private val serviceName="SMITCH_mDNS_SERVICE"
    private val port=80

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mainBinding = DataBindingUtil.inflate<MainFragmentBinding>(
            inflater,
            R.layout.main_fragment,
            container,
            false
        )
        return mainBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainBinding.lifecycleOwner = this
        mainBinding.multicastDNSViewModel = viewModel

        nsdHelper = NSDHelper(context, this)
        mainBinding.rvServiceList.isNestedScrollingEnabled = false
        mainBinding.rvServiceList.setHasFixedSize(true)
        mainBinding.rvServiceList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mainBinding.rvServiceList.adapter =
            NsdServiceListAdapter(context, ArrayList<NSDServiceInfo>())

        mainBinding.btnPublish.setOnClickListener {
//            if (viewModel.isWifiConnected())
                nsdHelper.registerService(port, serviceName)
        }

        mainBinding.btnScan.setOnClickListener {
            nsdHelper.discoverServices()
        }


        viewModel.nsdServiceInfo.observe(viewLifecycleOwner, Observer {

        })
    }


    override fun onDestroyView() {
        nsdHelper.tearDown()
        super.onDestroyView()
    }

    override fun onNsdServiceConnectionLost(nsdServiceInfo: NsdServiceInfo?) {
        Log.d(TAG, "onNsdServiceConnectionLost : $nsdServiceInfo")

    }

    override fun onNsdServiceInfoFound(nsdServiceInfo: NsdServiceInfo?) {
        Log.d(TAG, "onNsdServiceInfoFound : $nsdServiceInfo")

        activity?.runOnUiThread {
            mainBinding.rvServiceList.adapter?.apply {
                (this as NsdServiceListAdapter).addNsdServiceItem(
                    NSDServiceInfo(
                        nsdServiceInfo?.serviceName,
                        nsdServiceInfo?.serviceType,
                        nsdServiceInfo?.host,
                        nsdServiceInfo?.port
                    )
                )
            }
        }

    }
}
