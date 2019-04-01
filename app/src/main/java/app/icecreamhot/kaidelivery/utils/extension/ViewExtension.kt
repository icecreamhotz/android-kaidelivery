package app.icecreamhot.kaidelivery.utils.extension

import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.fragment.app.Fragment

fun View.getParentActivity(): Fragment? {
    var context = this.context
    while(context is ContextWrapper) {
        if(context is Fragment) {
            return context
        }
        context = context.baseContext
    }
    return null
}