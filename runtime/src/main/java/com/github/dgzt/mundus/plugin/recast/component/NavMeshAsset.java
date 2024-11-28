package com.github.dgzt.mundus.plugin.recast.component;

import com.github.jamestkhan.recast.NavMeshData;
import com.mbrlabs.mundus.commons.assets.CustomAsset;

public class NavMeshAsset {

    private final CustomAsset asset;

    private final NavMeshData navMeshData;

    public NavMeshAsset(final CustomAsset asset, final NavMeshData navMeshData) {
        this.asset = asset;
        this.navMeshData = navMeshData;
    }

    public CustomAsset getAsset() {
        return asset;
    }

    public NavMeshData getNavMeshData() {
        return navMeshData;
    }
}
