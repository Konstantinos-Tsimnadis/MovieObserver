package com.kTs.movieobserver.ui.settingsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.kTs.movieobserver.R
import com.kTs.movieobserver.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by lazy {
        val activity = requireNotNull(this.activity)
        val vMFactory = SettingsViewModel.Factory(activity.application)
        ViewModelProvider(this, vMFactory).get(SettingsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: SettingsFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.recreateActivity.observe(viewLifecycleOwner, Observer { recreate ->
            if(recreate){
                activity?.recreate()
                viewModel.recreateActivityCompleted()
            }
        })
        return binding.root
    }

}
