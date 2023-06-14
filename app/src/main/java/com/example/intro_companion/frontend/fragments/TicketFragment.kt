package com.example.intro_companion.frontend.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.intro_companion.backend.entities.Ticket
import com.example.intro_companion.databinding.FragmentTicketBinding
import com.example.intro_companion.frontend.models.TicketViewModel
import com.example.intro_companion.frontend.models.TicketViewModelFactory
import kotlinx.coroutines.launch

class TicketFragment : Fragment() {

    /* Ticket view screen binding */
    private lateinit var _binding: FragmentTicketBinding;

    private val args: TicketFragmentArgs by navArgs()

    private val viewModel: TicketViewModel by viewModels {
        TicketViewModelFactory(args.id)
    }

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
            FragmentTicketBinding.inflate(inflater, container, false)

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ticket.collect { id ->
                    id?.let { updateUi(it) }
                }
            }
        }
    }

    private fun updateUi(ticket: Ticket) {
        _binding.id.text = ticket.id.toString()
        _binding.date.text = ticket.submittedDate.toString()
        _binding.classification.text = ticket.classification
        _binding.location.text = ticket.location
        _binding.description.text = ticket.description
    }

}
