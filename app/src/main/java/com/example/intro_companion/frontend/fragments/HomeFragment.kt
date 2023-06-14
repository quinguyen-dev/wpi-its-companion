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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.intro_companion.databinding.FragmentHomeBinding
import com.example.intro_companion.frontend.adapters.TicketAdapter
import com.example.intro_companion.frontend.models.HomeViewModel
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    /* Home screen binding */
    private lateinit var _binding: FragmentHomeBinding;

    /* ViewModel controller */
    private val homeViewModel: HomeViewModel by viewModels()

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {/* Inflate the layout */
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        _binding.activeTickets.layoutManager = LinearLayoutManager(context)

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.tickets.collect { ticket ->
                    _binding.activeTickets.adapter =
                        TicketAdapter(ticket) { id ->
                            findNavController().navigate(
                                HomeFragmentDirections.homeToTicket(id)
                            )
                        }
                }
            }
        }

        /* Set the click listener for navigating to the form */
        _binding.startNewTicket.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.homeToForm()
            )
        }
    }

}