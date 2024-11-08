package com.github.dgzt.mundus.plugin.recast

import com.mbrlabs.mundus.pluginapi.MenuExtension
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import org.pf4j.Extension
import org.pf4j.Plugin

class MundusRecastPlugin : Plugin() {

    @Extension
    class YourMenuExtension : MenuExtension {

        companion object {
            const val PAD = 5f
        }

        override fun getMenuName(): String = "Recast plugin"

        override fun setupDialogRootWidget(root: RootWidget) {
            root.addLabel("TODO").setPad(PAD, PAD, PAD, PAD)
        }

    }

}
