package com.example.cashmanagerapplication.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.cashmanagerapplication.LoginActivity
import com.example.cashmanagerapplication.R
import com.example.cashmanagerapplication.databinding.FragmentProfileBinding
import com.example.cashmanagerapplication.preferences.PreferenceManager
import com.example.cashmanagerapplication.util.PrefUtil


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val pref: PreferenceManager by lazy { PreferenceManager (requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        binding.tvBalanceprofile.text = requireActivity().intent.getStringExtra("balance")
    }

    override fun onStart() {
        super.onStart()
        getAvatar()
    }

    private fun getAvatar() {
        binding.imgProfileavatar.setImageResource( pref.getInt(PrefUtil.pref_avatar)!! )
        binding.tvProfilename.text = pref.getString(PrefUtil.pref_name)
        binding.tvProfileusername.text = pref.getString(PrefUtil.pref_username)
        binding.tvDate.text = pref.getString(PrefUtil.pref_date)
    }

    private fun setupListener() {
        binding.imgProfileavatar.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_avatarFragment)
        }
        binding.cvLogout.setOnClickListener{
            pref.clear()
            Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(requireActivity(), LoginActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
            )
            requireActivity().finish()
        }
    }
}