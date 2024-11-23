package com.github.dgzt.mundus.plugin.recast.listener

import com.github.dgzt.mundus.plugin.recast.PropertyManager
import com.mbrlabs.mundus.editorcommons.events.SceneChangedEvent

class SceneChangedListener : SceneChangedEvent.SceneChangedListener {
    override fun onSceneChanged(event: SceneChangedEvent) {
        PropertyManager.sceneGraph = event.sceneGraph
    }
}
