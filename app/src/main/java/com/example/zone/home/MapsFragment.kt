package com.example.zone.home

import android.content.Context
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.Visibility
import com.example.zone.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

class MapsFragment : Fragment() {

private lateinit var map : MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Initialize osmdroid
        val ctx: Context = requireContext()
        val sharedPrefs = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        org.osmdroid.config.Configuration.getInstance().load(ctx, sharedPrefs)
        org.osmdroid.config.Configuration.getInstance().userAgentValue = ctx.packageName

        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        //Initialize MapView
        map = view.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        //Map Control - Zoom and Position
        val mapController = map.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(33.1283, -117.1601)
        mapController.setCenter(startPoint)

        val zoomInBtn = view.findViewById<FloatingActionButton>(R.id.zoom_in_button)
        val zoomOutBtn = view.findViewById<FloatingActionButton>(R.id.zoom_out_button)

        zoomInBtn.setOnClickListener {
            map.controller.zoomIn()
            map.invalidate()
        }

        zoomOutBtn.setOnClickListener {
            map.controller.zoomOut()
            map.invalidate()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map.onDetach()
    }








//    private val callback = OnMapReadyCallback { googleMap ->
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_maps, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)
//    }
}