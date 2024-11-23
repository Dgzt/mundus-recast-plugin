package com.github.dgzt.mundus.plugin.recast.listener

import com.github.dgzt.mundus.plugin.recast.PropertyManager
import com.mbrlabs.mundus.editorcommons.events.ProjectChangedEvent

class ProjectChangedListener : ProjectChangedEvent.ProjectChangedListener {
    override fun onProjectChanged(event: ProjectChangedEvent) {
        PropertyManager.sceneGraph = event.sceneGraph
    }
}
