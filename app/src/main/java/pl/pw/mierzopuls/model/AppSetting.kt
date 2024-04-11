package pl.pw.mierzopuls.model

import android.content.Context
import androidx.core.content.edit

class AppSetting(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var showInstructionOnStart: Boolean
        get() = sharedPreferences.getBoolean("show_instruction_on_start", true)
        set(value) = sharedPreferences.edit {
            putBoolean("show_instruction_on_start", value)
            apply()
        }
}