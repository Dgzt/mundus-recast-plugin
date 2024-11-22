package com.github.dgzt.mundus.plugin.recast.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.github.dgzt.mundus.plugin.recast.PropertyManager
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.github.jamestkhan.recast.NavMeshGenSettings
import com.github.jamestkhan.recast.utils.NavMeshGenerator
import com.github.jamestkhan.recast.utils.NavMeshIO
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign

object ComponentWidgetCreator {

    fun setup(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        rootWidget.addTextButton("Generate NavMesh") {
            Gdx.app.log("", "Click")

            val terrainComponent = findTerrainComponent(component)
            if (terrainComponent == null) {
                // TODO text
                return@addTextButton
            }

            // TODO generate navmesh in thread

            // TODO Set parameters on UI
            val settings = NavMeshGenSettings.Builder.SettingsBuilder()
                .useTiles(true)
                .tileSizeX(128)
                .tileSizeZ(128)
                .build()

            val navMeshGenerator = NavMeshGenerator(terrainComponent.modelInstance)
            val navMeshData = navMeshGenerator.build(settings)

            val tmpDir = System.getProperty("java.io.tmpdir")
            val tmpFile = FileHandle("$tmpDir/${terrainComponent.gameObject.name}.navmesh")
            NavMeshIO.save(navMeshData.navMesh, tmpFile)

            // TODO handle asset already exception
            var asset = PropertyManager.assetManager.createNewAsset(tmpFile)
            component.asset = asset

            tmpFile.delete()
        }.setAlign(WidgetAlign.CENTER)
    }

    private fun findTerrainComponent(component: Component): TerrainComponent? {
        val gameObject = component.gameObject
        return gameObject.findComponentByType(Component.Type.TERRAIN)
    }
}
