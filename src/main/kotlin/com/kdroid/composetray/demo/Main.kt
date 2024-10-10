package com.kdroid.composetray.demo

import com.kdroid.composetray.state.rememberNotification
import com.kdroid.composetray.state.rememberTrayState
import com.kdroid.composetray.tray.TrayMenuBuilder
import com.kdroid.kmplog.Log
import com.kdroid.kmplog.i

fun main() {
    val trayState = rememberTrayState()
    val notification = rememberNotification("Notification", "Message from MyApp!")
    val trayIconPath = "C:\\Users\\Eyahou Gambache\\CLionProjects\\tray\\icon.ico"

     TrayMenuBuilder(
        state = trayState,
        icon = trayIconPath,
        menuContent = {
            Item("Increment value") {
                Log.i("Increment","Increment clicked")
            }
            Item("Send notification") {
                trayState.sendNotification(notification)
            }
            Item("Disabled Item", isEnabled = false) {}
            CheckableItem("Toggle me") {
                println("Toggle me clicked")
            }
            Divider()
            SubMenu("SubMenu") {
                Item("Option 1") {
                    // Action
                }
                Item("Option 2") {
                    // Action
                }
                SubMenu("SubSubMenu") {
                    Item("Option 3") {}
                }
            }
            Item("Exit") {
                println("Exit clicked")
                dispose()
            }
        }

         
    )
}