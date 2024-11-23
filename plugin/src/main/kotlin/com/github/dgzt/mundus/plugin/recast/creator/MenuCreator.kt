package com.github.dgzt.mundus.plugin.recast.creator

import com.github.dgzt.mundus.plugin.recast.PropertyManager
import com.mbrlabs.mundus.pluginapi.ui.RootWidget

object MenuCreator {

    private const val PAD = 5f

    fun setup(root: RootWidget) {
        root.addCheckbox("Debug render", PropertyManager.debugRenderer!!.isEnabled) {
            PropertyManager.debugRenderer!!.isEnabled = it
        }.setPad(PAD, PAD, PAD, PAD)
    }
}
