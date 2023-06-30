package com.adryde.mobile.displaysdk_app

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.adryde.mobile.displaysdk.R
import com.adryde.mobile.displaysdk.util.hasPermission
import com.adryde.mobile.displaysdk.viewmodel.DisplayViewModel
import com.adryde.mobile.displaysdk_app.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private val TAG: String  = "MainFragment"
    private var _binding: FragmentMainBinding? = null

    //private lateinit var navController: NavController

    private val locationUpdateViewModel: DisplayViewModel by activityViewModels()

    private var activityListener: Callbacks? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callbacks) {
            activityListener = context

            // If fine location permission isn't approved, instructs the parent Activity to replace
            // this fragment with the permission request fragment.
            if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                activityListener?.requestFineLocationPermission()
            }
        } else {
            throw RuntimeException("$context must implement LocationUpdateFragment.Callbacks")
        }
    }
    /**
     * Inflate the layout for this fragment
     *
     * @author Yagna Joshi
     * @param container
     * @param inflater
     * @param savedInstanceState
     * @return Returns a constructed view from the xml layout by inflating it
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)


        binding.enableLocation.setOnClickListener {
            activityListener?.requestBackgroundLocationPermission()
        }

        return binding.root

    }


    /**
     * Method is called after view is being created where we call all initialization methods
     *
     * @author Yagna Joshi
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // navController = Navigation.findNavController(view)

        initHousingMainLayout()
        initClickBindings()
        initObservers()

    }



    /**
     * The method initHousingMainLayout is created to initialized the view of fragment's layout.
     *
     * @author Yagna Joshi
     */
    private fun initHousingMainLayout() {
    }


    private fun initObservers() {
        locationUpdateViewModel.receivingLocationUpdates.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { receivingLocation ->
                updateStartOrStopButtonState(receivingLocation)
            }
        )

        locationUpdateViewModel.locationListLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { locations ->
                locations?.let {
                    Log.d(TAG, "Got ${locations.size} locations")

                    if (locations.isEmpty()) {
                        binding.locationOutputTextView.text =
                            getString(R.string.emptyLocationDatabaseMessage)
                    } else {
                        val outputStringBuilder = StringBuilder("")
                        for (location in locations) {
                            outputStringBuilder.append(location.toString() + "\n")
                        }

                        binding.locationOutputTextView.text = outputStringBuilder.toString()
                    }
                }
            }
        )
    }


    /**
     * The method initClickBindings handles all type of click function to the respective
     * UI field that is required to be clickable
     *
     * @author Yagna Joshi
     */
    private fun initClickBindings() {

    }

    override fun onResume() {
        super.onResume()
        updateBackgroundButtonState()
    }

    override fun onPause() {
        super.onPause()

        // Stops location updates if background permissions aren't approved. The FusedLocationClient
        // won't trigger any PendingIntents with location updates anyway if you don't have the
        // background permission approved, but it's best practice to unsubscribing anyway.
        // To simplify the sample, we are unsubscribing from updates here in the Fragment, but you
        // could do it at the Activity level if you want to continue receiving location updates
        // while the user is moving between Fragments.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if ((locationUpdateViewModel.receivingLocationUpdates.value == true) &&
                (!requireContext().hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION))) {
                locationUpdateViewModel.stopLocationUpdates()
            }
        } else {
            locationUpdateViewModel.stopLocationUpdates()
        }*/
    }

    override fun onDetach() {
        super.onDetach()

        activityListener = null
    }

    private fun showBackgroundButton(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            !requireContext().hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            true
        }
    }

    private fun updateBackgroundButtonState() {
        if (showBackgroundButton()) {
            binding.enableLocation.visibility = View.VISIBLE
        } else {
            binding.enableLocation.visibility = View.GONE
        }
    }

    private fun updateStartOrStopButtonState(receivingLocation: Boolean) {
        if (receivingLocation) {
            binding.btnStartTimer.apply {
                text = getString(R.string.stop_receiving_location)
                setOnClickListener {
                    locationUpdateViewModel.stopLocationUpdates()
                }
            }
        } else {
            binding.btnStartTimer.apply {
                text = getString(R.string.start_receiving_location)
                setOnClickListener {
                    locationUpdateViewModel.startLocationUpdates()
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface Callbacks {
        fun requestFineLocationPermission()
        fun requestBackgroundLocationPermission()
    }

    companion object {
        fun newInstance() = MainFragment()
    }

}
