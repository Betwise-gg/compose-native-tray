package com.kdroid.composetray.tray.impl

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import com.kdroid.composetray.menu.api.TrayMenuBuilder
import com.kdroid.composetray.menu.impl.AwtTrayMenuBuilderImpl
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent


object AwtTrayInitializer {
    // Stores the reference to the current TrayIcon
    private var trayIcon: TrayIcon? = null


    private var roughTrayPosition = Point(0, 0)
    private var roughScreenBounds = Point(0, 0)

    /**
     * Initializes the system tray with the specified parameters.
     *
     * @param iconPath Path to the tray icon.
     * @param tooltip Tooltip text for the icon.
     * @param onLeftClick Action to execute on left-click on the icon.
     * @param menuContent Content of the tray menu.
     * @throws IllegalStateException If the system does not support the tray.
     */
    fun initialize(
        iconPath: String,
        tooltip: String,
        onLeftClick: (() -> Unit)?,
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
            onLeftClick?.let { clickAction ->
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        if (e.button == MouseEvent.BUTTON1) {

                            val point: Point = e.point

                            roughTrayPosition = point
                            clickAction()
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

    fun getWindowPosition(windowWidth: Int, windowHeight: Int): WindowPosition{
        var x = roughTrayPosition.x
        var y = roughTrayPosition.y
        val bounds: Rectangle = getSafeScreenBounds(roughTrayPosition)
        if (y < bounds.y) {
            y = bounds.y
        } else if (y > bounds.y + bounds.height) {
            y = bounds.y + bounds.height
        }
        if (x < bounds.x) {
            x = bounds.x
        } else if (x > bounds.x + bounds.width) {
            x = bounds.x + bounds.width
        }
        if (x + windowWidth > bounds.x + bounds.width) {
            x = (bounds.x + bounds.width) - windowWidth
        }
        if (y + windowHeight > bounds.y + bounds.height) {
            y = (bounds.y + bounds.height) - windowHeight
        }
        return WindowPosition(x = x.dp, y = y.dp)
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


    fun getSafeScreenBounds(pos: Point?): Rectangle {
        val bounds = getScreenBoundsAt(pos)?: error("No screen found at $pos")
        val insets: Insets = getScreenInsetsAt(pos)?: error("No screen found at $pos")
        bounds.x += insets.left
        bounds.y += insets.top
        bounds.width -= (insets.left + insets.right)
        bounds.height -= (insets.top + insets.bottom)
        return bounds
    }

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