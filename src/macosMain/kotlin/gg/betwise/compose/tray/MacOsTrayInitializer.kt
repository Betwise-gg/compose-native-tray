package gg.betwise.compose.tray

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AppKit.NSStatusBar
import platform.AppKit.NSStatusItem

object MacOsTrayInitializer {

    private lateinit var item: NSStatusItem

    fun dispose() {

    }

    fun initialize() {
        val bar = NSStatusBar.systemStatusBar
        item = bar.statusItemWithLength(length = 24.0)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun getItemPosition(): Pair<Double, Double> {
        val button = item.button?: error("Button is null")
        val buttonRect = button.convertRect(button.bounds(), null)
        val screenRect = button.window?.convertRectToScreen(buttonRect)?: error("Screen rect is null")
        return screenRect.useContents {
            origin.x to origin.y
        }
    }
}