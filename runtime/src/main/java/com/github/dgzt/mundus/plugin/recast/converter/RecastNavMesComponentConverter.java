package com.github.dgzt.mundus.plugin.recast.converter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent;
import com.github.jamestkhan.recast.NavMeshData;
import com.github.jamestkhan.recast.utils.NavMeshIO;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.CustomAsset;
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import org.recast4j.detour.NavMesh;

import java.io.IOException;

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
    public Array<String> getAssetIds(Component component) {
        if (!(component instanceof RecastNavMeshComponent)) {
            return new Array<>();
        }

        final RecastNavMeshComponent recastNavMeshComponent = (RecastNavMeshComponent) component;

        final Array<String> assetIds = new Array<>();
        assetIds.add(recastNavMeshComponent.getAsset().getID());

        return assetIds;
    }

    @Override
    public Component convert(final GameObject gameObject, final OrderedMap<String, String> orderedMap, final ObjectMap<String, Asset> objectMap) {
        final RecastNavMeshComponent component = new RecastNavMeshComponent(gameObject);
        Gdx.app.log("objectMap", "" + objectMap.size);
        if (objectMap.entries().hasNext) {
            component.setAsset((CustomAsset) objectMap.entries().next().value);

            try {
                final NavMesh navMesh = NavMeshIO.load(component.getAsset().getFile());
                final NavMeshData navMeshData = new NavMeshData(navMesh);
                component.setNavMeshData(navMeshData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return component;
    }
}
