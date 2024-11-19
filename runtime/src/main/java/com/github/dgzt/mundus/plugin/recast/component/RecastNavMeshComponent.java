package com.github.dgzt.mundus.plugin.recast.component;

import com.mbrlabs.mundus.commons.assets.CustomAsset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class RecastNavMeshComponent extends AbstractComponent {

    private CustomAsset asset;

    public RecastNavMeshComponent(final GameObject go) {
        super(go);
        type = Type.NAVMESH;
    }

    @Override
    public void update(float delta) {
        // NOOP
    }

    @Override
    public Component clone(final GameObject gameObject) {
        final RecastNavMeshComponent cloned = new RecastNavMeshComponent(gameObject);
        cloned.setType(type);
        return cloned;
    }

    public CustomAsset getAsset() {
        return asset;
    }

    public void setAsset(final CustomAsset asset) {
        this.asset = asset;
    }
}
