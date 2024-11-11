package com.github.dgzt.mundus.plugin.recast

import com.badlogic.gdx.utils.OrderedMap
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component

class RecastNavMeshConverter : CustomComponentConverter {
    override fun getComponentType(): Component.Type = Component.Type.NAVMESH

    override fun convert(component: Component): OrderedMap<String, String> {
        return OrderedMap()
    }

    override fun convert(gameObject: GameObject, orderedMap: OrderedMap<String, String>): Component {
        return RecastNavMeshComponent(gameObject)
    }
}
