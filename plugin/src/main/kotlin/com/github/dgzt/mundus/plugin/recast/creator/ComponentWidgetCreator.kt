package com.github.dgzt.mundus.plugin.recast.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.github.dgzt.mundus.plugin.recast.PropertyManager
import com.github.dgzt.mundus.plugin.recast.component.NavMeshAsset
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.github.jamestkhan.recast.NavMeshGenSettings
import com.github.jamestkhan.recast.utils.NavMeshGenerator
import com.github.jamestkhan.recast.utils.NavMeshIO
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.RootWidgetCell
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign

object ComponentWidgetCreator {

    private const val TILE_SIZE_X_DEFAULT_VALUE = 128
    private const val TILE_SIZE_Y_DEFAULT_VALUE = 128

    fun setup(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        var newNavMeshWidgetRootCell: RootWidgetCell? = null
        rootWidget.addTextButton("New NavMesh") {
            val newNavMeshWidgetRoot = newNavMeshWidgetRootCell!!.rootWidget
            newNavMeshWidgetRoot.clearWidgets()
            addNewNavMeshWidget(component, newNavMeshWidgetRoot)
        }.setAlign(WidgetAlign.CENTER)
        rootWidget.addRow()
        newNavMeshWidgetRootCell = rootWidget.addEmptyWidget()
    }

    private fun addNewNavMeshWidget(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        var tileSizeX = TILE_SIZE_X_DEFAULT_VALUE
        var tileSizeY = TILE_SIZE_Y_DEFAULT_VALUE

        rootWidget.addSpinner("Tile Size X", 8, Int.MAX_VALUE, tileSizeX) { tileSizeX = it }.setAlign(WidgetAlign.LEFT)
        rootWidget.addRow()
        rootWidget.addSpinner("Tile Size Y", 8, Int.MAX_VALUE, tileSizeY) { tileSizeY = it }.setAlign(WidgetAlign.LEFT)
        rootWidget.addRow()
        rootWidget.addTextButton("Generate NavMesh") {
            val terrainComponent = findTerrainComponent(component)
            if (terrainComponent == null) {
                // TODO text
                return@addTextButton
            }

            Thread {
                val settings = NavMeshGenSettings.Builder.SettingsBuilder()
                    .useTiles(true)
                    .tileSizeX(tileSizeX)
                    .tileSizeZ(tileSizeY)
                    .build()

                val navMeshGenerator = NavMeshGenerator(terrainComponent.modelInstance)
                Gdx.app.log("Recast NavMesh Plugin", "Generating...")
                val navMeshData = navMeshGenerator.build(settings)
                Gdx.app.log("Recast NavMesh Plugin", "Generated.")

                val tmpDir = System.getProperty("java.io.tmpdir")
                val tmpFile = FileHandle("$tmpDir/${terrainComponent.gameObject.name}.navmesh")
                NavMeshIO.save(navMeshData.navMesh, tmpFile)

                Gdx.app.postRunnable {
                    // TODO handle asset already exception
                    val asset = PropertyManager.assetManager.createNewAsset(tmpFile)
                    component.navMeshAssets.add(NavMeshAsset(asset, navMeshData))

                    tmpFile.delete()
                }

            }.start()


        }

    }

    private fun findTerrainComponent(component: Component): TerrainComponent? {
        val gameObject = component.gameObject
        return gameObject.findComponentByType(Component.Type.TERRAIN)
    }
}
