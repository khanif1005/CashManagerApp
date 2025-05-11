package com.example.cashmanagerapplication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashmanagerapplication.R
import com.example.cashmanagerapplication.adapter.AvatarAdapter
import com.example.cashmanagerapplication.databinding.FragmentAvatarBinding

class AvatarFragment : Fragment() {

    private lateinit var binding: FragmentAvatarBinding
    private lateinit var avatarAdapter: AvatarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
    }

    private fun setupList() {
        val avatars = arrayListOf<Int>(
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6
        )
        avatarAdapter = AvatarAdapter(avatars, object : AvatarAdapter.AdapterListener{
            override fun onClick(avatar: Int) {

            }

        })
        binding.listavatar.adapter = avatarAdapter
    }
}