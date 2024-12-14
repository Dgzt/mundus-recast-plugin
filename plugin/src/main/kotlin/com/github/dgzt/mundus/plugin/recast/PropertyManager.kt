package com.github.dgzt.mundus.plugin.recast

import com.github.dgzt.mundus.plugin.recast.debug.DebugRenderer
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.pluginapi.manager.AssetManager
import com.mbrlabs.mundus.pluginapi.manager.ToasterManager


object PropertyManager {
    lateinit var assetManager: AssetManager
    lateinit var toasterManager: ToasterManager
    lateinit var sceneGraph: SceneGraph
    var debugRenderer: DebugRenderer? = null
}
