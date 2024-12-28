package com.github.dgzt.mundus.plugin.recast.component;

import com.badlogic.gdx.utils.Array;
import com.github.dgzt.mundus.plugin.recast.consant.AssetPropertyConstants;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class RecastNavMeshComponent extends AbstractComponent {

    private final Array<NavMeshAsset> navMeshAssets;

    public RecastNavMeshComponent(final GameObject go) {
        super(go);
        type = Type.NAVMESH;
        navMeshAssets = new Array<>();
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

    /**
     * Finds the NavMeshAsset by name.
     *
     * @param name The name of NavMesh.
     * @return The found NavMeshAsset
     */
    public NavMeshAsset findNavMeshAssetByName(final String name) {
        for (int i = 0; i < navMeshAssets.size; ++i) {
            final NavMeshAsset navMeshAsset = navMeshAssets.get(i);

            if (name.equals(navMeshAsset.getAsset().getProperties().get(AssetPropertyConstants.NAVMEESH_NAME))) {
                return navMeshAsset;
            }
        }

        return null;
    }

    public Array<NavMeshAsset> getNavMeshAssets() {
        return navMeshAssets;
    }
}
