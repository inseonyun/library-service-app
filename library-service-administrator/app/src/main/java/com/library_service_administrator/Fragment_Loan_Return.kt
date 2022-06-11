package com.library_service_administrator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.library_service_administrator.databinding.FragmentLoanReturnBinding

class Fragment_Loan_Return : Fragment() {
    // ViewBinding
    private var _binding: FragmentLoanReturnBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentLoanReturnBinding.inflate(inflater, container, false)
        val view = binding.root



        // Inflate the layout for this fragment
        return view
    }
}