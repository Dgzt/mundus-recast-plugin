package com.github.dgzt.mundus.plugin.recast.converter;

import com.badlogic.gdx.utils.OrderedMap;
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent;
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class RecastNavMesComponentConverter implements CustomComponentConverter {

    @Override
    public Component.Type getComponentType() {
        return Component.Type.NAVMESH;
    }

    @Override
    public OrderedMap<String, String> convert(final Component component) {
        final OrderedMap<String, String> map = new OrderedMap<>();
        return map;
    }

    @Override
    public Component convert(final GameObject gameObject, final OrderedMap<String, String> orderedMap) {
        return new RecastNavMeshComponent(gameObject);
    }
}
