package com.zengyu.sample.fps

import android.util.Log
import android.view.Choreographer

object FpsMonitor : Choreographer.FrameCallback {
    private val TAG = FpsMonitor::class.java.simpleName

    private var frameStartTime = 0L
    private var frameCount = 0

    fun start() {
        frameStartTime = 0L
        frameCount = 0
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun stop() {
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (frameStartTime == 0L) {
            frameStartTime = frameTimeNanos
        }

        frameCount++
        val duration = frameTimeNanos - frameStartTime

        if (duration >= 1_000_000_000L) { // 1 second in nanoseconds
            val fps = frameCount * 1_000_000_000.0 / duration
            Log.d(TAG, "FPS: %.2f".format(fps))
            frameStartTime = frameTimeNanos
            frameCount = 0
        }

        Choreographer.getInstance().postFrameCallback(this)
    }
}
