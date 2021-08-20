package de.gersta.wbs_timer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener {
    object ChronoConstants {
        const val INITIAL_VALUE: Long = 0
    }

    var chronoPerType: HashMap<String, Long> = HashMap()
    var currentButtonText = ""
    var isChronometerRunning = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialisePersistedState()

        val stopButton = findViewById<Button>(R.id.stop_button)
        stopButton.setOnClickListener {
            val chronometer: Chronometer = findViewById(R.id.timer)

            stopChronometerForCurrentWbs(chronometer)
        }

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
        wbsRepository.persistWbsElements(chronoPerType.entries)
    }

    @SuppressLint("NewApi")
    override fun onClick(view: View) {
        val currentWbs = findViewById<TextView>(R.id.current_wbs)
        Log.d("onClick", "Current WBS is %s".format(currentWbs.text))
        val chronometer: Chronometer = findViewById(R.id.timer)

        val clickedButton = findViewById<Button>(view.id)
        currentWbs.text = clickedButton.text as String
        Log.d("onClick", "Setting WBS to %s".format(currentWbs.text))

        chronoPerType.putIfAbsent(clickedButton.text.toString(), ChronoConstants.INITIAL_VALUE)

        if ( isDifferentWbsClicked(clickedButton) ) {
            stopChronometerForCurrentWbs(chronometer)

            startChronometerForClickedWbs(chronometer, clickedButton)
        } else {
            if ( isChronometerRunning ) {
                stopChronometerForCurrentWbs(chronometer)
            } else {
                startChronometerForClickedWbs(chronometer, clickedButton)
            }
        }
    }

    private fun initialisePersistedState() {
        val wbsRepository = WbsRepository(this)
        val existingWbsElements = wbsRepository.extractWbsElements()
        chronoPerType = existingWbsElements
        existingWbsElements.entries.forEach { entry ->
            val wbsElementLayout = findViewById<LinearLayout>(R.id.wbs_elements)
            val newWbsElement = Button(ContextThemeWrapper(this, R.style.wbsElement))

            newWbsElement.id = View.generateViewId()
            newWbsElement.text = entry.key
            newWbsElement.setOnClickListener(this)

            wbsElementLayout.addView(newWbsElement)
        }
    }

    private fun stopChronometerForCurrentWbs(chronometer: Chronometer) {
        if ( !currentButtonText.isEmpty() ) {
            chronometer.stop()
            isChronometerRunning = false

            val elapsedTime = SystemClock.elapsedRealtime() - chronometer.base

            chronoPerType.put(currentButtonText, elapsedTime)
            Log.d("Stopped chronometer", "Elapsed time of %s: %d seconds".format(currentButtonText, millisToSeconds(elapsedTime)))
        }
    }

    private fun startChronometerForClickedWbs(chronometer: Chronometer, clickedButton: Button) {
        currentButtonText = clickedButton.text as String


        val currentWbsValue = chronoPerType.getOrElse(currentButtonText, { ChronoConstants.INITIAL_VALUE })
        val start = SystemClock.elapsedRealtime() - currentWbsValue
        Log.d("Starting chronometer", "Starting time for %s: %d seconds".format(currentButtonText, millisToSeconds(currentWbsValue)))
        chronometer.base = start

        chronometer.start()
        isChronometerRunning = true
    }


    private fun isDifferentWbsClicked(clickedButton: Button): Boolean {
        return currentButtonText != clickedButton.text
    }

    private fun millisToSeconds(timeInMillis: Long): Long {
        return timeInMillis / 1000
    }
}
