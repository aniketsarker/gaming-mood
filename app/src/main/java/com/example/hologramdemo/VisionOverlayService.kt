package com.example.hologramdemo

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class VisionOverlayService : Service() {

    private var overlayView: View? = null
    private var leftEdgeBlocker: View? = null
    private var rightEdgeBlocker: View? = null
    private var windowManager: WindowManager? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // 1. Main Color Filter (Green Tint) - Touch pass through
        overlayView = View(this).apply { 
            setBackgroundColor(Color.argb(35, 0, 255, 0)) // Halka green
        }

        val mainParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or // Touch game e pass korbe
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        // 2. Edge Blockers (Left & Right) - Touch BLOCK korbe
        setupEdgeBlockers(layoutFlag)

        try {
            windowManager?.addView(overlayView, mainParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupEdgeBlockers(layoutFlag: Int) {
        // Edge er width (pixels). Tumi chaile 60 er jaygay 80-100 korte paro jodi beshi area block korte chao.
        val edgeWidth = 60 
        
        // Left Edge Blocker
        leftEdgeBlocker = View(this).apply { setBackgroundColor(Color.TRANSPARENT) }
        val leftParams = WindowManager.LayoutParams(
            edgeWidth,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            // FLAG_NOT_TOUCHABLE dilam na, karon amra chai eta touch block koruk!
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }

        // Right Edge Blocker
        rightEdgeBlocker = View(this).apply { setBackgroundColor(Color.TRANSPARENT) }
        val rightParams = WindowManager.LayoutParams(
            edgeWidth,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 0
        }

        try {
            windowManager?.addView(leftEdgeBlocker, leftParams)
            windowManager?.addView(rightEdgeBlocker, rightParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (overlayView != null) { 
                windowManager?.removeView(overlayView)
                overlayView = null 
            }
            if (leftEdgeBlocker != null) { 
                windowManager?.removeView(leftEdgeBlocker)
                leftEdgeBlocker = null 
            }
            if (rightEdgeBlocker != null) { 
                windowManager?.removeView(rightEdgeBlocker)
                rightEdgeBlocker = null 
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
