package com.kutumb.hackerearth.ui

import android.Manifest
import android.app.Activity
import android.content.*

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.kutumb.hackerearth.R
import com.kutumb.hackerearth.adapter.MessageAdapter
import com.kutumb.hackerearth.databinding.HomeFragmentBinding
import com.kutumb.hackerearth.model.SmsModel
import com.kutumb.hackerearth.util.CShowProgress
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment){

    private var mLayoutManager : LinearLayoutManager? = null
    private var binding: HomeFragmentBinding? = null
    private var adapter :  MessageAdapter? = null
    private var smslist : ArrayList<SmsModel> = ArrayList()
    private val viewModel: HomeViewModel by viewModels()
    private var resultstr : String? = null
    @Inject lateinit var progress: CShowProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions.launch(Manifest.permission.READ_SMS)
        setupReceiver()
        startListeningToOTPSMS()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)


        if (checkpermission()){
            setupObservable()
            setupRecycler(smslist)
            viewModel.getAllSms(requireActivity())
        }else{
            Toast.makeText(requireContext(), "No permission Detected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        setupReceiver()
        startListeningToOTPSMS()
    }


    private fun setupObservable(){
        viewModel.apply {
            myMessage.observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()){
                    progress.showProgress(context)
                    Toast.makeText(requireContext(), "No Message Found", Toast.LENGTH_SHORT).show()
                }else{
                    progress.hideProgress()
                    smslist = it as ArrayList<SmsModel>
                    adapter?.setData(smslist)
                }
            }
        }
    }

    private fun setupRecycler(smslist: ArrayList<SmsModel>) {
        binding?.apply{
            mLayoutManager = LinearLayoutManager(requireContext())
            messageRecycler.setHasFixedSize(true)
            messageRecycler.layoutManager = mLayoutManager
            adapter = MessageAdapter(requireContext(), smslist)
            messageRecycler.adapter = adapter
        }
    }


    private fun checkpermission() : Boolean{
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_SMS)  == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }else if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED

        ){
            return false
        }
        return false
    }

    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()) { it ->
        when (it) {
            true -> {
                setupRecycler(smslist)
                viewModel.getAllSms(requireActivity())
                setupObservable()
            }
            false -> {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupReceiver() {
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireContext().registerReceiver(smsVerificationReceiver, intentFilter)
    }

    private fun startListeningToOTPSMS() {
        val task = SmsRetriever.getClient(requireContext()).startSmsUserConsent(null)
    }

    val activityResultLauncher1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        try {
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                Log.d("LogResult2", message.toString())
                resultstr = message!!.filter {
                    it.isDigit()
                }
                Toast.makeText(requireContext(), "OTP from Message $resultstr", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("LogTag", "Consent denied. User can type OTP manually")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            activityResultLauncher1.launch(consentIntent)
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        println("SMS user consent timeout.")
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(smsVerificationReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
