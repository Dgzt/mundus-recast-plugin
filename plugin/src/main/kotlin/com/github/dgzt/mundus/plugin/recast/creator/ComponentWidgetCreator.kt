package com.github.dgzt.mundus.plugin.recast.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.utils.Array
import com.github.dgzt.mundus.plugin.recast.PropertyManager
import com.github.dgzt.mundus.plugin.recast.component.NavMeshAsset
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.github.dgzt.mundus.plugin.recast.consant.AssetPropertyConstants
import com.github.dgzt.mundus.plugin.recast.model.NavMeshGeneratingModel
import com.github.jamestkhan.recast.NavMeshGenSettings
import com.github.jamestkhan.recast.utils.NavMeshGenerator
import com.github.jamestkhan.recast.utils.NavMeshIO
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent
import com.mbrlabs.mundus.editorcommons.exceptions.AssetAlreadyExistsException
import com.mbrlabs.mundus.editorcommons.types.ToastType
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.RootWidgetCell
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign
import com.mbrlabs.mundus.commons.scene3d.GameObject

object ComponentWidgetCreator {

    private const val RIGHT_PAD = 3f
    private const val BOTTOM_PAD = 3f
    private const val TILE_SIZE_X_DEFAULT_VALUE = 128
    private const val TILE_SIZE_Y_DEFAULT_VALUE = 128


    private var runningNavMeshGenerating: NavMeshGeneratingModel? = null

    fun setup(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        var newNavMeshWidgetRootCell: RootWidgetCell? = null
        val alreadyNavMeshesWidgetRootCell = rootWidget.addEmptyWidget()
        alreadyNavMeshesWidgetRootCell.grow()
        val alreadyNavMeshesWidgetRoot = alreadyNavMeshesWidgetRootCell.rootWidget
        addAlreadyNavMeshes(component, alreadyNavMeshesWidgetRoot)
        rootWidget.addRow()
        rootWidget.addTextButton("New NavMesh") {
            if (runningNavMeshGenerating != null) {
                return@addTextButton
            }

            val newNavMeshWidgetRoot = newNavMeshWidgetRootCell!!.rootWidget
            newNavMeshWidgetRoot.clearWidgets()
            addNewNavMeshWidget(component, newNavMeshWidgetRoot, alreadyNavMeshesWidgetRoot)
        }.setPad(0f, 0f, BOTTOM_PAD, 0f).setAlign(WidgetAlign.CENTER)
        rootWidget.addRow()
        newNavMeshWidgetRootCell = rootWidget.addEmptyWidget()
        newNavMeshWidgetRootCell.grow()

        if (runningNavMeshGenerating?.component == component) {
            runningNavMeshGenerating?.newNavMeshWidgetRoot = newNavMeshWidgetRootCell!!.rootWidget
            runningNavMeshGenerating?.alreadyNavMeshesWidgetRoot = alreadyNavMeshesWidgetRoot
            runningNavMeshGenerating?.generatingLabel = null
        }
    }

    private fun addAlreadyNavMeshes(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        val navMeshAssets = component.navMeshAssets
        for (navMeshAsset in navMeshAssets) {
            println(navMeshAsset.asset.properties)
            rootWidget.addLabel(navMeshAsset.asset.properties.get(AssetPropertyConstants.NAVMEESH_NAME))
            rootWidget.addEmptyWidget().grow()
            rootWidget.addTextButton("X") {
                PropertyManager.assetManager.deleteAsset(navMeshAsset.asset)
                navMeshAssets.removeValue(navMeshAsset, true)
                rootWidget.clearWidgets()
                addAlreadyNavMeshes(component, rootWidget)
            }
            rootWidget.addRow()
        }
    }

    private fun addNewNavMeshWidget(component: RecastNavMeshComponent, newNavMeshWidgetRoot: RootWidget, alreadyNavMeshesWidgetRoot: RootWidget) {
        var tileSizeX = TILE_SIZE_X_DEFAULT_VALUE
        var tileSizeY = TILE_SIZE_Y_DEFAULT_VALUE
        var agentRadius = 0.6f
        var agentHeight = 2.0f
        var agentMaxClimb = 0.89f
        var agentMaxSlope = 45.0f
        var name = ""

        val nameWidget = newNavMeshWidgetRoot.addEmptyWidget()
        nameWidget.grow().setPad(0f, 0f, BOTTOM_PAD, 0f).setAlign(WidgetAlign.LEFT)
        nameWidget.rootWidget.addLabel("Name").setPad(0f, RIGHT_PAD, 0f, 0f)
        nameWidget.rootWidget.addTextField { name = it }.setAlign(WidgetAlign.LEFT)
        nameWidget.rootWidget.addEmptyWidget().grow()

        newNavMeshWidgetRoot.addRow()
        newNavMeshWidgetRoot.addSpinner("Tile Size X", 8, Int.MAX_VALUE, tileSizeX) { tileSizeX = it }.setPad(0f, 0f, BOTTOM_PAD, 0f).setAlign(WidgetAlign.LEFT)
        newNavMeshWidgetRoot.addRow()
        newNavMeshWidgetRoot.addSpinner("Tile Size Y", 8, Int.MAX_VALUE, tileSizeY) { tileSizeY = it }.setPad(0f, 0f, BOTTOM_PAD, 0f).setAlign(WidgetAlign.LEFT)
        newNavMeshWidgetRoot.addRow()
        newNavMeshWidgetRoot.addSpinner("Agent radius", 0.1f, Float.MAX_VALUE, agentRadius) { agentRadius = it }.setPad(0f, 0f, BOTTOM_PAD, 0f).setAlign(WidgetAlign.LEFT)
        newNavMeshWidgetRoot.addRow()
        newNavMeshWidgetRoot.addSpinner("Agent height", 0.1f, Float.MAX_VALUE, agentHeight) { agentHeight = it}.setPad(0f, 0f, BOTTOM_PAD, 0f).setAlign(WidgetAlign.LEFT)
        newNavMeshWidgetRoot.addRow()
        newNavMeshWidgetRoot.addSpinner("Agent max climb", 0.1f, Float.MAX_VALUE, agentMaxClimb) { agentMaxClimb = it }.setPad(0f, 0f, BOTTOM_PAD, 0f).setAlign(WidgetAlign.LEFT)
        newNavMeshWidgetRoot.addRow()
        newNavMeshWidgetRoot.addSpinner("Agent max slope", 0.1f, Float.MAX_VALUE, agentMaxSlope) { agentMaxSlope = it }.setPad(0f, 0f, BOTTOM_PAD, 0f).setAlign(WidgetAlign.LEFT)
        newNavMeshWidgetRoot.addRow()
        newNavMeshWidgetRoot.addTextButton("Generate NavMesh") {
            val terrainComponents = findTerrainComponents(component.gameObject)
            if (terrainComponents.isEmpty) {
                // TODO text
                return@addTextButton
            }

            if (name == "") {
                // TODO text
                return@addTextButton
            }

            // TODO check name already exists

            val navMeshGeneratorThread = createNavMeshGeneratorThread(component, terrainComponents, tileSizeX, tileSizeY,
                agentRadius, agentHeight, agentMaxClimb, agentMaxSlope, name)
            val uiUpdaterThread = createUiUpdaterThread(navMeshGeneratorThread)

            runningNavMeshGenerating = NavMeshGeneratingModel(component, navMeshGeneratorThread, uiUpdaterThread, newNavMeshWidgetRoot, alreadyNavMeshesWidgetRoot)
            runningNavMeshGenerating!!.navMeshGenerator.start()
            runningNavMeshGenerating!!.uiUpdater.start()
        }
    }

    private fun createNavMeshGeneratorThread(component: RecastNavMeshComponent,
                                             terrainComponents: Array<TerrainComponent>,
                                             tileSizeX: Int,
                                             tileSizeY: Int,
                                             agentRadius: Float,
                                             agentHeight: Float,
                                             agentMaxClimb: Float,
                                             agentMaxSlope: Float,
                                             name: String): Thread {
        return Thread {
            val settings = NavMeshGenSettings.Builder.SettingsBuilder()
                .useTiles(true)
                .tileSizeX(tileSizeX)
                .tileSizeZ(tileSizeY)
                .agentRadius(agentRadius)
                .agentHeight(agentHeight)
                .agentMaxClimb(agentMaxClimb)
                .agentMaxSlope(agentMaxSlope)
                .build()

            val modelInstances = Array<ModelInstance>()
            modelInstances.addAll(getTerrainModelInstances(terrainComponents))
            modelInstances.addAll(findAllActiveModelInstances(component))

            val navMeshGenerator = NavMeshGenerator(modelInstances)
            Gdx.app.log("Recast NavMesh Plugin", "Generating...")
            val navMeshData = navMeshGenerator.build(settings)
            Gdx.app.log("Recast NavMesh Plugin", "Generated.")

            val tmpDir = System.getProperty("java.io.tmpdir")
            val tmpFile = FileHandle("$tmpDir/${component.gameObject.name}-$name.navmesh")
            NavMeshIO.save(navMeshData.navMesh, tmpFile)

            Gdx.app.postRunnable {
                try {
                    val asset = PropertyManager.assetManager.createNewAsset(tmpFile)

                    asset.properties.put(AssetPropertyConstants.NAVMEESH_NAME, name)
                    asset.properties.put(AssetPropertyConstants.NAVMEESH_TILE_SIZE_X, tileSizeX.toString())
                    asset.properties.put(AssetPropertyConstants.NAVMEESH_TILE_SIZE_Y, tileSizeY.toString())
                    asset.properties.put(AssetPropertyConstants.NAVMEESH_AGENT_RADIUS, agentRadius.toString())
                    asset.properties.put(AssetPropertyConstants.NAVMEESH_AGENT_HEIGHT, agentHeight.toString())
                    asset.properties.put(AssetPropertyConstants.NAVMEESH_AGENT_MAX_CLIMB, agentMaxClimb.toString())
                    asset.properties.put(AssetPropertyConstants.NAVMEESH_AGENT_MAX_SLOPE, agentMaxSlope.toString())
                    component.navMeshAssets.add(NavMeshAsset(asset, navMeshData))

                    PropertyManager.assetManager.markAsModifiedAsset(asset)

                    PropertyManager.toasterManager.success("NavMesh generated.")
                } catch (ex: AssetAlreadyExistsException) {
                    PropertyManager.toasterManager.sticky(ToastType.ERROR, "NavMesh asset already exist!")
                } finally {
                    tmpFile.delete()
                    runningNavMeshGenerating?.alreadyNavMeshesWidgetRoot?.clearWidgets()
                    addAlreadyNavMeshes(component, runningNavMeshGenerating?.alreadyNavMeshesWidgetRoot!!)
                }
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
                    if (runningNavMeshGenerating?.generatingLabel == null) {
                        rootWidget.clearWidgets()
                        runningNavMeshGenerating?.generatingLabel = rootWidget.addLabel(text).label
                    } else {
                        runningNavMeshGenerating?.generatingLabel?.setText(text)
                    }
                }
            }

            val rootWidget = runningNavMeshGenerating!!.newNavMeshWidgetRoot
            Gdx.app.postRunnable {
                rootWidget.clearWidgets()
            }

            runningNavMeshGenerating = null
        }
    }

    private fun findTerrainComponents(gameObject: GameObject): Array<TerrainComponent> {
        val retArray = Array<TerrainComponent>()

        val terrainComponent = gameObject.findComponentByType<TerrainComponent>(Component.Type.TERRAIN)
        if (terrainComponent != null) {
            retArray.add(terrainComponent)
        } else {
            val children = gameObject.findChildrenByComponent(Component.Type.TERRAIN)
            for (child in children) {
                val childTerrainComponent = child.findComponentByType<TerrainComponent>(Component.Type.TERRAIN)
                retArray.add(childTerrainComponent)
            }
        }

        return retArray
    }

    private fun getTerrainModelInstances(terrainComponents: Array<TerrainComponent>): Array<ModelInstance> {
        val retArray = Array<ModelInstance>()

        for (terrainComponent in terrainComponents) {
            retArray.add(terrainComponent.modelInstance)
        }

        return retArray
    }

    private fun findAllActiveModelInstances(navMeshComponent: RecastNavMeshComponent): Array<ModelInstance> {
        val retArray = Array<ModelInstance>()

        val modelComponents = navMeshComponent.gameObject.sceneGraph.root.findComponentsByType<ModelComponent>(Array(), Component.Type.MODEL, true)

        for (i in 0 until modelComponents.size) {
            val modelComponent = modelComponents.get(i)

            if (modelComponent.gameObject.active) {
                retArray.add(modelComponent.modelInstance)
            }
        }

        return retArray
    }
}
