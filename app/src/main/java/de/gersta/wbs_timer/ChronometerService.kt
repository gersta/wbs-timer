package de.gersta.wbs_timer

import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.RequiresApi

class ChronometerService(private val activity: MainActivity) {
    object ChronoConstants {
        const val INITIAL_VALUE: Long = 0
    }

    private var currentButtonText = ""
    private var isChronometerRunning = false

    var chronosPerWbsElement: HashMap<String, Long> = HashMap()

    @RequiresApi(Build.VERSION_CODES.N)
    fun onWbsClicked(view: View) {
        val chronometer = activity.findViewById<Chronometer>(R.id.timer)

        val currentWbs = activity.findViewById<TextView>(R.id.current_wbs)
        Log.d("onClick", "Current WBS is %s".format(currentWbs.text))

        val clickedButton = activity.findViewById<Button>(view.id)
        currentWbs.text = clickedButton.text as String
        Log.d("onClick", "Setting WBS to %s".format(currentWbs.text))

        chronosPerWbsElement.putIfAbsent(clickedButton.text.toString(), ChronoConstants.INITIAL_VALUE)

        if ( isDifferentWbsClicked(clickedButton) ) {
            stopChronometerForCurrentWbs()

            startChronometerForClickedWbs( clickedButton)
        } else {
            if ( isChronometerRunning ) {
                stopChronometerForCurrentWbs()
            } else {
                startChronometerForClickedWbs(clickedButton)
            }
        }
    }

    fun onStopButtonClicked() {
        stopChronometerForCurrentWbs()
    }

    private fun stopChronometerForCurrentWbs() {
        if ( !currentButtonText.isEmpty() ) {
            val chronometer = activity.findViewById<Chronometer>(R.id.timer)

            chronometer.stop()
            isChronometerRunning = false

            val elapsedTime = SystemClock.elapsedRealtime() - chronometer.base

            chronosPerWbsElement.put(currentButtonText, elapsedTime)
            Log.d("Stopped chronometer", "Elapsed time of %s: %d seconds".format(currentButtonText, millisToSeconds(elapsedTime)))
        }
    }

    private fun startChronometerForClickedWbs(clickedButton: Button) {
        currentButtonText = clickedButton.text as String

        val chronometer = activity.findViewById<Chronometer>(R.id.timer)
        val currentWbsValue = chronosPerWbsElement.getOrElse(currentButtonText, { ChronoConstants.INITIAL_VALUE })
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