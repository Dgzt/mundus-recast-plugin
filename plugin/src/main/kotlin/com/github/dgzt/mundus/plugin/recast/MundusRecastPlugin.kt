package com.github.dgzt.mundus.plugin.recast

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.utils.Array
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.github.dgzt.mundus.plugin.recast.converter.RecastNavMesComponentConverter
import com.github.dgzt.mundus.plugin.recast.creator.ComponentCreator
import com.github.dgzt.mundus.plugin.recast.creator.ComponentWidgetCreator
import com.github.dgzt.mundus.plugin.recast.creator.MenuCreator
import com.github.dgzt.mundus.plugin.recast.debug.DebugRenderer
import com.github.dgzt.mundus.plugin.recast.listener.ProjectChangedListener
import com.github.dgzt.mundus.plugin.recast.listener.SceneChangedListener
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.pluginapi.AssetExtension
import com.mbrlabs.mundus.pluginapi.ComponentExtension
import com.mbrlabs.mundus.pluginapi.CustomShaderRenderExtension
import com.mbrlabs.mundus.pluginapi.EventExtension
import com.mbrlabs.mundus.pluginapi.MenuExtension
import com.mbrlabs.mundus.pluginapi.ToasterExtension
import com.mbrlabs.mundus.pluginapi.manager.AssetManager
import com.mbrlabs.mundus.pluginapi.manager.PluginEventManager
import com.mbrlabs.mundus.pluginapi.manager.ToasterManager
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import org.pf4j.Extension
import org.pf4j.Plugin

class MundusRecastPlugin : Plugin() {

    @Extension
    class RecastMenuExtension : MenuExtension {

        companion object {
            const val PAD = 5f
        }

        override fun getMenuName(): String = "Recast plugin"

        override fun setupDialogRootWidget(root: RootWidget) {
            MenuCreator.setup(root)
        }
    }

    @Extension
    class RecastComponentExtension : ComponentExtension {
        override fun getSupportedComponentTypes(): Array<Component.Type> {
            val array = Array<Component.Type>()
            array.add(Component.Type.TERRAIN)
            array.add(Component.Type.TERRAIN_MANAGER)
            return array
        }

        override fun getComponentType(): Component.Type = Component.Type.NAVMESH

        override fun getComponentName(): String = "Recast NavMesh"

        override fun createComponent(gameObject: GameObject): Component = ComponentCreator.create(gameObject)

        override fun setupComponentInspectorWidget(component: Component, rootWidget: RootWidget) =
            ComponentWidgetCreator.setup(component as RecastNavMeshComponent, rootWidget)

        override fun getConverter(): CustomComponentConverter = RecastNavMesComponentConverter()
    }

    @Extension
    class RecastAssetExtension : AssetExtension {
        override fun assetManager(assetManager: AssetManager) {
            PropertyManager.assetManager = assetManager
        }
    }

    @Extension
    class RecastToasterExtension : ToasterExtension {
        override fun toasterManager(toasterManager: ToasterManager) {
            PropertyManager.toasterManager = toasterManager
        }
    }

    @Extension
    class RecastNavMeshEventExtension : EventExtension {
        override fun manageEvents(eventManager: PluginEventManager) {
            eventManager.registerEventListener(ProjectChangedListener())
            eventManager.registerEventListener(SceneChangedListener())
        }
    }

    @Extension
    class RecastNavMeshCustomShaderRenderExtension : CustomShaderRenderExtension {
        override fun render(cam: Camera) {
            if (PropertyManager.debugRenderer == null) {
                PropertyManager.debugRenderer = DebugRenderer(cam)
            }

            PropertyManager.debugRenderer!!.render(PropertyManager.sceneGraph)
        }
    }

}
