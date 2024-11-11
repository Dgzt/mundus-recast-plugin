package com.github.dgzt.mundus.plugin.recast.creator

import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component

object ComponentCreator {

    fun create(gameObject: GameObject): Component {
        return RecastNavMeshComponent(gameObject)
    }
}
