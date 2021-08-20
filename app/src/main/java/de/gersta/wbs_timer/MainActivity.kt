package de.gersta.wbs_timer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val chronometerService: ChronometerService

    init {
        chronometerService = ChronometerService(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialisePersistedState()

        val stopButton = findViewById<Button>(R.id.stop_button)
        stopButton.setOnClickListener { chronometerService.onStopButtonClicked() }

        val addWbsButton = findViewById<Button>(R.id.add_new_wbs)
        addWbsButton.setOnClickListener {
            val newWbsName = findViewById<TextView>(R.id.new_wbs_name)
            val wbsElements = findViewById<LinearLayout>(R.id.wbs_elements)
            val newWbsElement = Button(ContextThemeWrapper(this, R.style.wbsElement))

            newWbsElement.id = View.generateViewId()
            newWbsElement.text = newWbsName.text.toString()
            newWbsElement.setOnClickListener(this)

            wbsElements.addView(newWbsElement)
        }
    }

    override fun onPause() {
        super.onPause()

        val wbsRepository = WbsRepository(this)
        wbsRepository.persistWbsElements(chronometerService.chronosPerWbsElement.entries)
    }

    @SuppressLint("NewApi")
    override fun onClick(view: View) {
        chronometerService.onWbsClicked(view)
    }

    private fun initialisePersistedState() {
        val wbsRepository = WbsRepository(this)
        val existingWbsElements = wbsRepository.extractWbsElements()
        chronometerService.chronosPerWbsElement = existingWbsElements

        existingWbsElements.entries.forEach { entry ->
            val wbsElementLayout = findViewById<LinearLayout>(R.id.wbs_elements)
            val newWbsElement = Button(ContextThemeWrapper(this, R.style.wbsElement))

            newWbsElement.id = View.generateViewId()
            newWbsElement.text = entry.key
            newWbsElement.setOnClickListener(this)

            wbsElementLayout.addView(newWbsElement)
        }
    }


}
