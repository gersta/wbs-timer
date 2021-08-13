package de.gersta.wbs_timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.Chronometer

class MainActivity : AppCompatActivity() {
    var isChronoRunning = false
    var stoppedTimer: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val chronometer: Chronometer = findViewById(R.id.timer)
        val startStopButton: Button = findViewById(R.id.start_stop_button)

        startStopButton.setOnClickListener {
            if ( isChronoRunning ) {
                chronometer.stop()
                stoppedTimer = SystemClock.elapsedRealtime() - chronometer.base
                Log.d("Stopped chronometer", "Elapsed time %d".format(stoppedTimer))

                isChronoRunning = false
                startStopButton.setText("Continue")
            } else {
                val start = SystemClock.elapsedRealtime() - stoppedTimer
                Log.d("Starting chronometer", "Starting time %d".format(start))
                chronometer.base = start
                chronometer.start()

                isChronoRunning = true
                startStopButton.setText("Stop")
            }
        }
    }
}