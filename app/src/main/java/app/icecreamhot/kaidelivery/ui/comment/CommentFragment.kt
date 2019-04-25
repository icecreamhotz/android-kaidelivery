package app.icecreamhot.kaidelivery.ui.comment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.icecreamhot.kaidelivery.R
import app.icecreamhot.kaidelivery.model.Employee.EmployeeTotal
import app.icecreamhot.kaidelivery.model.RateAndComment.EmployeeScore
import app.icecreamhot.kaidelivery.network.EmployeeAPI
import app.icecreamhot.kaidelivery.ui.Adapter.EmployeeCommentAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CommentFragment: Fragment() {

    private val employeeAPI by lazy {
        EmployeeAPI.create()
    }

    private var empId: Int? = null
    private lateinit var listEmployeeComment: RecyclerView

    private var disposable: Disposable? = null

    companion object {
        fun newInstance(empId: Int) = CommentFragment().apply {
            arguments = Bundle().apply {
                putInt("emp_id", empId)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.getInt("emp_id")?.let {
            empId = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment_layout, container, false)
        listEmployeeComment = view.findViewById(R.id.listEmployeeComment)

        loadEmployeeCommentData()

        return view
    }

    private fun loadEmployeeCommentData() {
        disposable = employeeAPI.getEmployeeScoreAndComment(empId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loadingOrder.visibility = View.VISIBLE }
//            .doOnTerminate { loadingOrder.visibility = View.GONE }
            .subscribe(
                {
                        result -> setDataToEmployeeCommentList(result.data)
                },
                {
                        err -> Log.d("err", err.message)
                }
            )

    }

    private fun setDataToEmployeeCommentList(result: ArrayList<EmployeeScore>) {
        val scoreCommentEmployeeAdapter = EmployeeCommentAdapter(result)

        listEmployeeComment.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = scoreCommentEmployeeAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }

}