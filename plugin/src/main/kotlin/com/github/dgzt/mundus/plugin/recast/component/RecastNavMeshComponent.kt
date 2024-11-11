package com.github.dgzt.mundus.plugin.recast.component

import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent
import com.mbrlabs.mundus.commons.scene3d.components.Component

class RecastNavMeshComponent(gameObject: GameObject) : AbstractComponent(gameObject) {

    init {
        type = Component.Type.NAVMESH
    }

    override fun update(delta: Float) {
        // NOOP
    }

    override fun clone(gameObject: GameObject): Component {
        val cloned = RecastNavMeshComponent(gameObject)
        cloned.type = this.type
        return cloned
    }
}
