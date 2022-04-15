package com.kutumb.hackerearth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kutumb.hackerearth.R
import com.kutumb.hackerearth.databinding.MessageDetailFragmentBinding
import com.kutumb.hackerearth.databinding.SplashFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageDetail : Fragment(R.layout.message_detail_fragment) {

    private var binding : MessageDetailFragmentBinding? = null
    private var sender : String? = null
    private var message : String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = MessageDetailFragmentBinding.bind(view)

        sender = arguments?.getString("sender").toString()
        message = arguments?.getString("message").toString()

        binding?.apply {
            txtHeading.text = sender.toString()
            messagedetail.text = message.toString()

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}