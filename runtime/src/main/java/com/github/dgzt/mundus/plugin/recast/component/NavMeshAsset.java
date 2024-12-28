package com.github.dgzt.mundus.plugin.recast.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.github.jamestkhan.recast.NavMeshData;
import com.github.jamestkhan.recast.Pathfinder;
import com.mbrlabs.mundus.commons.assets.CustomAsset;

public class NavMeshAsset {

    private final CustomAsset asset;

    private final NavMeshData navMeshData;

    private final Pathfinder pathfinder;

    public NavMeshAsset(final CustomAsset asset, final NavMeshData navMeshData) {
        this.asset = asset;
        this.navMeshData = navMeshData;

        pathfinder = new Pathfinder(navMeshData);
    }

    /**
     * Finds the path between <code>start</code> and <code>end</code> path.
     * Uploads the <code>path</code> array.
     *
     * @param start The start point.
     * @param end The end point.
     * @param path The uploading array.
     */
    public void getPath(final Vector3 start, final Vector3 end, final Array<float[]> path) {
        pathfinder.getPath(start, end, path);
    }

    public CustomAsset getAsset() {
        return asset;
    }

    public NavMeshData getNavMeshData() {
        return navMeshData;
    }
}
