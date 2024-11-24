package com.kdroid.composetray.tray.impl

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import com.kdroid.composetray.menu.api.TrayMenuBuilder
import com.kdroid.composetray.utils.AwtScreenUtils
import java.awt.*

object MacOsTrayInitializer {

    private var roughTrayPosition = Point(0, 0)
    private var roughScreenBounds = Rectangle(0, 0)

    /**
     * Initializes the system tray with the specified parameters.
     *
     * @param iconPath Path to the tray icon.
     * @param tooltip Tooltip text for the icon.
     * @param onLeftPress Action to execute on left-click on the icon.
     * @param menuContent Content of the tray menu.
     * @throws IllegalStateException If the system does not support the tray.
     */
    fun initialize(
        iconPath: String,
        tooltip: String,
        onLeftPress: (() -> Unit)?,
        onScreenResize: (() -> Unit)?,
        menuContent: (TrayMenuBuilder.() -> Unit)?
    ) {
        AwtTrayInitializer.initialize(
            iconPath,
            tooltip,
            onLeftPress = { point ->
                roughTrayPosition = point
                onLeftPress?.invoke()
            },
            onScreenResize,
            menuContent
        )


        // screen resize listener
        Toolkit.getDefaultToolkit().addAWTEventListener({ event ->
            onScreenResize?.invoke()
        }, AWTEvent.PAINT_EVENT_MASK)
    }

    fun getWindowPosition(windowWidth: Int, windowHeight: Int): WindowPosition {
        var x = roughTrayPosition.x
        var y = roughTrayPosition.y
        val bounds: Rectangle = getSafeScreenBounds(roughTrayPosition)
        when {
            y < bounds.y -> y = bounds.y
            y > bounds.y + bounds.height -> y = bounds.y + bounds.height
        }
        when {
            x < bounds.x -> x = bounds.x
            x > bounds.x + bounds.width -> x = bounds.x + bounds.width
        }
        if (x + windowWidth > bounds.x + bounds.width)
            x = (bounds.x + bounds.width) - windowWidth
        if (y + windowHeight > bounds.y + bounds.height)
            y = (bounds.y + bounds.height) - windowHeight
        return WindowPosition(x = x.dp, y = y.dp)
    }

    private fun getSafeScreenBounds(pos: Point?): Rectangle {
        roughScreenBounds = AwtScreenUtils.getScreenBoundsAt(pos)?: error("No screen found at $pos")
        val insets: Insets = AwtScreenUtils.getScreenInsetsAt(pos)?: error("No screen found at $pos")
        roughScreenBounds.x += insets.left
        roughScreenBounds.y += insets.top
        roughScreenBounds.width -= (insets.left + insets.right)
        roughScreenBounds.height -= (insets.top + insets.bottom)
        return roughScreenBounds
    }

}