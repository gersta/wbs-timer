package de.gersta.wbs_timer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener {
    object ChronoConstants {
        const val INITIAL_VALUE: Long = 0
    }

    var stoppedTimer: Long = 0
    var chronoPerType: HashMap<String, Long> = HashMap()

    var currentButtonText = "NONE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sport = findViewById<Button>(R.id.sport)
        val gaming = findViewById<Button>(R.id.gaming)

        sport.setOnClickListener(this)
        gaming.setOnClickListener(this)

        val stopButton = findViewById<Button>(R.id.stop_button)
        stopButton.setOnClickListener {
            val chronometer: Chronometer = findViewById(R.id.timer)
            chronometer.stop()
            chronoPerType.put(currentButtonText, SystemClock.elapsedRealtime() - chronometer.base)
            Log.d("Stopped chronometer", "Elapsed time of %s: %d".format(currentButtonText, stoppedTimer))
        }
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
            chronometer.stop()
            chronoPerType.put(currentButtonText, SystemClock.elapsedRealtime() - chronometer.base)
            Log.d("Stopped chronometer", "Elapsed time of %s: %d".format(currentButtonText, stoppedTimer))

            currentButtonText = clickedButton.text as String
        }


        val start = SystemClock.elapsedRealtime() - chronoPerType.getOrElse(currentButtonText, { ChronoConstants.INITIAL_VALUE })
        Log.d("Starting chronometer", "Starting time for %s: %d".format(currentButtonText, start))
        chronometer.base = start
        chronometer.start()
    }


    fun isDifferentWbsClicked(clickedButton: Button): Boolean {
        return currentButtonText != clickedButton.text
    }
}
