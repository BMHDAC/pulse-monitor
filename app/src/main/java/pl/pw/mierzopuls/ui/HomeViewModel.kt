package pl.pw.mierzopuls.ui

import android.content.Context
import android.widget.Toast

class HomeViewModel(
    private val context: Context) {
    fun onStart() {
        Toast.makeText(context, "Start button clicked !", Toast.LENGTH_SHORT).show()
    }

    fun onHistory() {
        Toast.makeText(context, "History button clicked !", Toast.LENGTH_SHORT).show()
    }

    fun onStudy() {
        Toast.makeText(context, "Study button clicked !", Toast.LENGTH_SHORT).show()
    }
}