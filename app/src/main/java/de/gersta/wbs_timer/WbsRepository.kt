package de.gersta.wbs_timer

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity


class WbsRepository(context: Context): AppCompatActivity() {
    private var database: SharedPreferences = context.getSharedPreferences("wbs-elements",Context.MODE_PRIVATE)

    fun persistWbsElements(elements: Set<Map.Entry<String, Long>>) {
        val writer = database.edit()

        with(writer) {
            elements.forEach { element ->
                putString(element.key, element.value.toString())
            }

            apply()
        }
    }

    fun extractWbsElements(): HashMap<String, Long> {
        val wbsElements = HashMap<String, Long>()
        database.all.entries.forEach { entry -> wbsElements.put(entry.key, entry.value.toString().toLong()) }

        return wbsElements
    }

}