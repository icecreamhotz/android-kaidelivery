package app.icecreamhot.kaidelivery.ui.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.ui.map.TrackingMapFragment

class SuccessFragment: Fragment() {

    lateinit var btnGoMap: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_success_order, container, false)
        btnGoMap = view.findViewById(R.id.btnSuccessOrder)

        btnGoMap.setOnClickListener {
            val MapsFragment = TrackingMapFragment()
            val transaction = fragmentManager
            transaction?.beginTransaction()
                ?.replace(R.id.contentContainer, MapsFragment)
                ?.commit()
        }

        return view
    }
}