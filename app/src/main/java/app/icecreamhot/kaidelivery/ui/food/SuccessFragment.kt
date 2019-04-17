package app.icecreamhot.kaidelivery.ui.food

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.ui.map.TrackingMapFragment
import app.icecreamhot.kaidelivery.ui.order.WatingOrderFragment

class SuccessFragment: Fragment() {

    companion object {
        fun newInstance(order_id: Int, order_name: String): WatingOrderFragment {
            val fragment = WatingOrderFragment()
            val args = Bundle()
            args.putString("order_name", order_name)
            args.putInt("order_id", order_id)
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var btnGoMap: Button
    private var order_name = ""
    private var order_id = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getString("order_name")?.let {
            order_name = it
        }
        arguments?.getInt("order_id")?.let {
            order_id = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_success_order, container, false)
        btnGoMap = view.findViewById(R.id.btnSuccessOrder)

        btnGoMap.setOnClickListener {
            val watingFragment = WatingOrderFragment.newInstance(order_id, order_name)
            val transaction = fragmentManager
            transaction?.beginTransaction()
                ?.replace(R.id.contentContainer, watingFragment)
                ?.commit()
        }

        return view
    }
}