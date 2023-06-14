package com.example.intro_companion.frontend.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.intro_companion.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    /* Login screen binding */
    private lateinit var _binding: FragmentLoginBinding;

    /**
     * Override method onCreateView to create and return the fragment's view hierarchy.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /* Inflate the layout */
        _binding =
            FragmentLoginBinding.inflate(inflater, container, false)

        return _binding.root
    }

    /**
     * Called when the view for this fragment has been created. Sets click listeners for two buttons
     * related to authentication and navigation to the Home destination.
     *
     * @param view The view for this fragment.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Set the click listener for authenticating via SSO */
        _binding.submit.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.loginToHome()
            )
        }

        _binding.callIts.setOnClickListener {
            call()
        }
    }

    /**
     * Starts a phone call to the ITS service.
     *
     * If the app has not been granted the `CALL_PHONE` permission, requests the permission from the user.
     */
    private fun call() {
        if (requirePermissions()) {
            this.activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    ConfirmFragment.PHONE_CALL_REQUEST
                )
            }
            call()
        } else {
            /* Start phone call to ITS */
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${ConfirmFragment.ITS_PHONE}")
            startActivity(intent)
        }
    }

    /**
     * Checks if the app has been granted the `CALL_PHONE` permission.
     *
     * @return `true` if the permission has not been granted, `false` otherwise.
     */
    private fun requirePermissions(): Boolean {
        return this.activity?.let {
            ActivityCompat.checkSelfPermission(
                it,
                Manifest.permission.CALL_PHONE
            )
        } != PackageManager.PERMISSION_GRANTED
    }
}
