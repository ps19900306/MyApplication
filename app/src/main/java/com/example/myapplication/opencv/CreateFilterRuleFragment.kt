package com.example.myapplication.opencv


import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentCreateFilterRuleBinding
import com.nwq.base.BaseFragment


/**
 * A simple [Fragment] subclass.
 * Use the [CreateFilterRuleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateFilterRuleFragment : BaseFragment<FragmentCreateFilterRuleBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateFilterRuleBinding {
        return FragmentCreateFilterRuleBinding.inflate(inflater, container, false)
    }


}