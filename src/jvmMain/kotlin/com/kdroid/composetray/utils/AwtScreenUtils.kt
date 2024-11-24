package com.kdroid.composetray.utils

import java.awt.*

object AwtScreenUtils {

    fun getScreenInsetsAt(pos: Point?): Insets? {
        val gd = getGraphicsDeviceAt(pos)
        var insets: Insets? = null
        if (gd != null) {
            insets = Toolkit.getDefaultToolkit().getScreenInsets(gd.defaultConfiguration)
        }
        return insets
    }

    fun getScreenBoundsAt(pos: Point?): Rectangle? {
        val gd: GraphicsDevice? = getGraphicsDeviceAt(pos)
        var bounds: Rectangle? = null
        if (gd != null) {
            bounds = gd.defaultConfiguration.bounds
        }
        return bounds
    }

    fun getGraphicsDeviceAt(pos: Point?): GraphicsDevice? {
        var device: GraphicsDevice? = null

        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val lstGDs = ge.screenDevices

        val lstDevices = ArrayList<GraphicsDevice>(lstGDs.size)

        for (gd in lstGDs) {
            val gc = gd.defaultConfiguration
            val screenBounds = gc.bounds

            if (screenBounds.contains(pos)) {
                lstDevices.add(gd)
            }
        }

        device = if (lstDevices.size > 0) {
            lstDevices[0]
        } else {
            ge.defaultScreenDevice
        }

        return device
    }
}