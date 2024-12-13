package com.github.dgzt.mundus.plugin.recast.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.github.dgzt.mundus.plugin.recast.PropertyManager
import com.github.dgzt.mundus.plugin.recast.component.NavMeshAsset
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.github.dgzt.mundus.plugin.recast.consant.AssetPropertyConstants
import com.github.dgzt.mundus.plugin.recast.model.NavMeshGeneratingModel
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

    private var runningNavMeshGenerating: NavMeshGeneratingModel? = null

    fun setup(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        var newNavMeshWidgetRootCell: RootWidgetCell? = null
        val alreadyNavMeshesWidgetRootCell = rootWidget.addEmptyWidget()
        alreadyNavMeshesWidgetRootCell.grow()
        addAlreadyNavMeshes(component, alreadyNavMeshesWidgetRootCell.rootWidget)
        rootWidget.addRow()
        rootWidget.addTextButton("New NavMesh") {
            if (runningNavMeshGenerating != null) {
                return@addTextButton
            }

            val newNavMeshWidgetRoot = newNavMeshWidgetRootCell!!.rootWidget
            newNavMeshWidgetRoot.clearWidgets()
            addNewNavMeshWidget(component, newNavMeshWidgetRoot)
        }.setAlign(WidgetAlign.CENTER)
        rootWidget.addRow()
        newNavMeshWidgetRootCell = rootWidget.addEmptyWidget()

        if (runningNavMeshGenerating?.component == component) {
            runningNavMeshGenerating?.newNavMeshWidgetRoot = newNavMeshWidgetRootCell!!.rootWidget
        }
    }

    private fun addAlreadyNavMeshes(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        val navMeshAssets = component.navMeshAssets
        for (navMeshAsset in navMeshAssets) {
            println(navMeshAsset.asset.properties)
            rootWidget.addLabel(navMeshAsset.asset.properties.get(AssetPropertyConstants.NAVMEESH_NAME))
            rootWidget.addEmptyWidget().grow()
            rootWidget.addTextButton("X") {
                // TODO
                println("Delete")
            }
            rootWidget.addRow()
        }
    }

    private fun addNewNavMeshWidget(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        var tileSizeX = TILE_SIZE_X_DEFAULT_VALUE
        var tileSizeY = TILE_SIZE_Y_DEFAULT_VALUE
        var name = ""

        rootWidget.addTextField { name = it }.grow().setAlign(WidgetAlign.LEFT)
        rootWidget.addRow()
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

            if (name == "") {
                // TODO text
                return@addTextButton
            }

            val navMeshGeneratorThread = createNavMeshGeneratorThread(component, terrainComponent, tileSizeX, tileSizeY, name)
            val uiUpdaterThread = createUiUpdaterThread(navMeshGeneratorThread)

            runningNavMeshGenerating = NavMeshGeneratingModel(component, navMeshGeneratorThread, uiUpdaterThread, rootWidget)
            runningNavMeshGenerating!!.navMeshGenerator.start()
            runningNavMeshGenerating!!.uiUpdater.start()
        }
    }

    private fun createNavMeshGeneratorThread(component: RecastNavMeshComponent,
                                            terrainComponent: TerrainComponent,
                                            tileSizeX: Int,
                                            tileSizeY: Int,
                                            name: String): Thread {
        return Thread {
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

                asset.properties.put(AssetPropertyConstants.NAVMEESH_NAME, name)
                asset.properties.put(AssetPropertyConstants.NAVMEESH_TILE_SIZE_X, tileSizeX.toString())
                asset.properties.put(AssetPropertyConstants.NAVMEESH_TILE_SIZE_Y, tileSizeY.toString())
                component.navMeshAssets.add(NavMeshAsset(asset, navMeshData))

                PropertyManager.assetManager.markAsModifiedAsset(asset)

                tmpFile.delete()

                // TODO add to UI
            }

        }
    }

    private fun createUiUpdaterThread(navMeshGeneratorThread: Thread): Thread {
        return Thread {
            var count = 0
            while (navMeshGeneratorThread.isAlive) {
                Thread.sleep(1000)
                if (++count > 3) {
                    count = 0
                }
                var text = "Generating"
                for (c in 0 until count) {
                    text += "."
                }

                val rootWidget = runningNavMeshGenerating!!.newNavMeshWidgetRoot
                Gdx.app.postRunnable {
                    rootWidget.clearWidgets()
                    rootWidget.addLabel(text)
                }
            }

            val rootWidget = runningNavMeshGenerating!!.newNavMeshWidgetRoot
            Gdx.app.postRunnable {
                rootWidget.clearWidgets()
            }

            runningNavMeshGenerating = null
        }
    }

    private fun findTerrainComponent(component: Component): TerrainComponent? {
        val gameObject = component.gameObject
        return gameObject.findComponentByType(Component.Type.TERRAIN)
    }
}
