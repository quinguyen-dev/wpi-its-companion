package com.example.intro_companion.frontend.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.intro_companion.GeoFences
import com.example.intro_companion.backend.entities.Request
import com.example.intro_companion.databinding.FragmentFormBinding
import com.example.intro_companion.frontend.models.RequestViewModel
import com.google.android.material.button.MaterialButton

class FormFragment : Fragment() {

    /* Base form screen binding */
    private lateinit var _binding: FragmentFormBinding;

    /* ViewModel controller */
    private lateinit var _viewModel: RequestViewModel

    /* Submit button reference */
    private lateinit var submit: MaterialButton

    /* TextWatcher to disable submit button */
    private val validationWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkValidation()
        }
    }

    companion object {
        /* Class-level variables for the submit button */
        const val ALPHA_ACTIVE = 255
        const val ALPHA_INACTIVE = 100
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {/* Inflate the layout */
        _binding = FragmentFormBinding.inflate(inflater, container, false)

        submit = _binding.submit

        checkValidation()

        _viewModel = ViewModelProvider(requireActivity())[RequestViewModel::class.java]

        /* Add listener to each EditText instances */
        _binding.fullName.addTextChangedListener(validationWatcher)
        _binding.contactEmail.addTextChangedListener(validationWatcher)
        _binding.description.addTextChangedListener(validationWatcher)

        /* Set the text to the current request */
        _binding.fullName.setText(_viewModel.request.value?.name)
        _binding.email.setText(_viewModel.request.value?.emailWPI)
        _binding.id.setText(_viewModel.request.value?.idWPI)
        _binding.contactEmail.setText(_viewModel.request.value?.emailContact)
        _binding.contactPhone.setText(_viewModel.request.value?.phone)

        return _binding.root
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("FormFragment", "FormFragment destroyed")
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

        /* Set the click listener for going to the suggestions screen */
        _binding.submit.setOnClickListener {
            val request = Request(
                _binding.fullName.text.toString(),
                _binding.email.text.toString(),
                _binding.id.text.toString(),
                _binding.contactEmail.text.toString(),
                _binding.contactPhone.text.toString(),
                _binding.description.text.toString(),
                GeoFences.campusRegion != "",
                GeoFences.currentBuilding
            )
            
            _viewModel.setRequest(request)

            findNavController().navigate(
                FormFragmentDirections.formToSuggestions()
            )
        }
    }

    /**
     * Checks the validation of the input fields and enables or disables the submit button accordingly.
     * The function verifies whether the fullName, contactEmail, and description fields are empty or not
     * using the TextUtils.isEmpty() function. If any of the fields are empty, the function sets the
     * variable valid to false, otherwise, it sets it to true.
     *
     * If the variable valid is true, the function enables the submit button and sets the background alpha
     * to ALPHA_ACTIVE, otherwise, it disables the submit button and sets the background alpha to ALPHA_INACTIVE.
     */
    private fun checkValidation() {
        val valid =
            !(TextUtils.isEmpty(_binding.fullName.text) || TextUtils.isEmpty(_binding.contactEmail.text) || TextUtils.isEmpty(
                _binding.description.text
            ))

        submit.isEnabled = valid
        submit.background.alpha = if (valid) ALPHA_ACTIVE else ALPHA_INACTIVE
    }

}