package com.example.zone.home

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.zone.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.util.Locale
import androidx.core.view.isVisible
import androidx.core.graphics.toColorInt
import com.example.zone.AdapterClasses.CustomInfoWindowAdapter

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowLongClickListener {

    private lateinit var map: GoogleMap
    private lateinit var addressEditText: EditText
    private lateinit var eventNameEditText: EditText
    private lateinit var eventTimeEditText: EditText
    private lateinit var addEventButton: Button
    private lateinit var inputPanel: CardView
    private lateinit var toggleFormFab: FloatingActionButton
    private lateinit var closeFormButton: ImageButton
    private lateinit var rootView: View

    // List to store all events
    private val eventsList = mutableListOf<EventData>()
    // Map to store marker references
    private val markersMap = mutableMapOf<String, Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize UI elements
        addressEditText = rootView.findViewById(R.id.address_edit_text)
        eventNameEditText = rootView.findViewById(R.id.event_name_edit_text)
        eventTimeEditText = rootView.findViewById(R.id.event_time_edit_text)
        addEventButton = rootView.findViewById(R.id.add_event_button)
        inputPanel = rootView.findViewById(R.id.input_panel)
        toggleFormFab = rootView.findViewById(R.id.toggle_form_fab)
        closeFormButton = rootView.findViewById(R.id.close_form_button)

        // Set up the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Set up the add event button click listener
        addEventButton.setOnClickListener {
            addEventMarker()
        }

        // Set up toggle form FAB click listener
        toggleFormFab.setOnClickListener {
            toggleInputPanel()
        }

        // Set up close form button click listener
        closeFormButton.setOnClickListener {
            hideInputPanel()
        }

        return rootView
    }

    private fun toggleInputPanel() {
        if (inputPanel.isVisible) {
            hideInputPanel()
        } else {
            showInputPanel()
        }
    }

    private fun showInputPanel() {
        // Use animation instead of manual translation
        val slideDown = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        inputPanel.startAnimation(slideDown)
        inputPanel.visibility = View.VISIBLE
    }

    private fun hideInputPanel() {
        // Use animation instead of manual translation
        val slideUp = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        slideUp.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                inputPanel.visibility = View.GONE
            }
        })
        inputPanel.startAnimation(slideUp)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Default location (San Francisco) with appropriate zoom level
        val defaultLocation = LatLng(37.7749, -122.4194)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        // Enable zoom controls
        map.uiSettings.isZoomControlsEnabled = true

        // Enable the default map toolbar to show the Google Maps icon
        map.uiSettings.isMapToolbarEnabled = true

        // Set custom info window adapter
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(requireActivity()))

        // Set info window long click listener for marker deletion
        map.setOnInfoWindowLongClickListener(this)

        // Add click listener for markers
        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            // Return false to allow default behavior (including map toolbar)
            false
        }

        // Adjust FAB position to not overlap with Google logo
        adjustFabPosition()
    }

    private fun adjustFabPosition() {
        // We need to wait for the layout to be fully drawn
        toggleFormFab.post {
            // Add some extra margin to avoid overlapping with Google logo
            val params = toggleFormFab.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            // Use hardcoded values instead of resource dimensions
            params.bottomMargin = dpToPx(40) // Enough space to avoid Google logo
            params.leftMargin = dpToPx(16)   // Standard margin from left edge
            toggleFormFab.layoutParams = params
        }
    }

    // Helper method to convert dp to pixels
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onInfoWindowLongClick(marker: Marker) {
        // Show confirmation dialog before deleting the marker
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete") { _, _ ->
                // Remove from our events list
                val eventData = marker.tag as? EventData
                eventData?.let { event ->
                    eventsList.remove(event)
                }

                // Remove from markers map
                val markerId = marker.id
                markersMap.remove(markerId)

                // Remove the marker from the map
                marker.remove()

                Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)

        // Create the dialog before showing to customize it
        val dialog = dialogBuilder.create()

        // Show the dialog
        dialog.show()

        // Get a color from the theme's colorAccent or use a fallback color
        val colorAccent: Int = try {
            val typedValue = android.util.TypedValue()
            requireContext().theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
            typedValue.data
        } catch (e: Exception) {
            // Fallback to a decent visible color (purple) if colorAccent cannot be resolved
            "#673AB7".toColorInt() // Same color as your add button
        }

        // Apply colors to dialog buttons to ensure visibility
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorAccent)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorAccent)
    }

    private fun addEventMarker() {
        val address = addressEditText.text.toString().trim()
        val eventName = eventNameEditText.text.toString().trim()
        val eventTime = eventTimeEditText.text.toString().trim()

        if (address.isEmpty() || eventName.isEmpty() || eventTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // For Android Tiramisu (API 33) and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // The geocoding result will be handled in the callback
            getLocationFromAddress(address)
        } else {
            // For older Android versions that use the synchronous API
            val latLng = getLocationFromAddress(address)

            if (latLng != null) {
                addMarkerWithLocation(latLng, address)
            } else {
                Toast.makeText(requireContext(), "Could not find address. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocationFromAddress(strAddress: String): LatLng? {
        val geocoder = Geocoder(requireContext(), Locale.US)

        // If running on Android Tiramisu (API 33) or higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Use the new callback-based approach
            geocoder.getFromLocationName(strAddress, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)

                    // Continue with adding the marker
                    requireActivity().runOnUiThread {
                        addMarkerWithLocation(latLng, strAddress)
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Could not find address. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return null // Function now returns null as the result is handled in callback
        } else {
            // For older Android versions, use the deprecated method
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(strAddress, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    return LatLng(address.latitude, address.longitude)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    private fun addMarkerWithLocation(latLng: LatLng, address: String) {
        val eventName = eventNameEditText.text.toString().trim()
        val eventTime = eventTimeEditText.text.toString().trim()

        // Create EventData object
        val eventData = EventData(
            name = eventName,
            time = eventTime,
            address = address,
            latLng = latLng
        )

        // Add to our events list
        eventsList.add(eventData)

        // Add marker with event information
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(eventName)
            .snippet("Time: $eventTime\nAddress: $address")

        // Add the marker to the map
        val marker = map.addMarker(markerOptions)

        // Store event data with marker
        marker?.tag = eventData

        // Store marker reference
        marker?.let { markersMap[it.id] = it }

        // Move camera to the marker
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        // Clear input fields
        addressEditText.text.clear()
        eventNameEditText.text.clear()
        eventTimeEditText.text.clear()

        // Hide the input panel after adding a marker
        hideInputPanel()

        Toast.makeText(requireContext(), "Event added successfully!", Toast.LENGTH_SHORT).show()

        // Show a help toast about long press to delete
        Toast.makeText(requireContext(), "Long press on event info to delete", Toast.LENGTH_LONG).show()
    }
}