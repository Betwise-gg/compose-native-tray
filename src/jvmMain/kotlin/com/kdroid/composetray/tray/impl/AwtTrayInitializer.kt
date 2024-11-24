package com.kdroid.composetray.tray.impl

import com.kdroid.composetray.menu.api.TrayMenuBuilder
import com.kdroid.composetray.menu.impl.AwtTrayMenuBuilderImpl
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent


object AwtTrayInitializer {

    // Stores the reference to the current TrayIcon
    private var trayIcon: TrayIcon? = null


    /**
     * Initializes the system tray with the specified parameters.
     *
     * @param iconPath Path to the tray icon.
     * @param tooltip Tooltip text for the icon.
     * @param onLeftPress Action to execute on left-press on the icon.
     * @param menuContent Content of the tray menu.
     * @throws IllegalStateException If the system does not support the tray.
     */
    fun initialize(
        iconPath: String,
        tooltip: String,
        onLeftPress: ((Point) -> Unit)?,
        onScreenResize: (() -> Unit)?,
        menuContent: (TrayMenuBuilder.() -> Unit)?
    ) {
        if (!SystemTray.isSupported()) {
            throw IllegalStateException("System tray is not supported.")
        }

        // If a trayIcon already exists, remove it before creating a new one
        dispose()

        val systemTray = SystemTray.getSystemTray()
        val popupMenu = PopupMenu()

        // Create the tray icon
        val trayImage = Toolkit.getDefaultToolkit().getImage(iconPath)
        val newTrayIcon = TrayIcon(trayImage, tooltip, popupMenu).apply {
            isImageAutoSize = true

            // Handle the left-click if specified
            onLeftPress?.let { pressAction ->
                addMouseListener(object : MouseAdapter() {
                    override fun mousePressed(e: MouseEvent?) {
                        if (e?.button == MouseEvent.BUTTON1) {
                            val point: Point = e.point
                            pressAction(point)
                        }
                    }
                })
            }
        }

        // Add the menu content if specified
        menuContent?.let {
            AwtTrayMenuBuilderImpl(popupMenu, newTrayIcon).apply(it)
        }

        // Add the icon to the system tray
        systemTray.add(newTrayIcon)

        // Store the reference for future use
        trayIcon = newTrayIcon
    }

    /**
     * Disposes of the current tray icon, if it exists.
     */
    fun dispose() {
        trayIcon?.let { icon ->
            SystemTray.getSystemTray().remove(icon)
            trayIcon = null
        }
    }


}