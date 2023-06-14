package com.example.intro_companion.frontend.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.intro_companion.databinding.FragmentReturnBinding

class ReturnFragment : Fragment()  {

    /* Return screen binding */
    private lateinit var _binding: FragmentReturnBinding;

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
            FragmentReturnBinding.inflate(inflater, container, false)

        return _binding.root
    }

    /**
     * Called when the view for this fragment has been created.
     *
     * @param view The view for this fragment.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Set the click listener for returning to new form */
        _binding.submitAnotherRequest.setOnClickListener {
            findNavController().navigate (
                ReturnFragmentDirections.returnToHome()
            )
        }

    }
}