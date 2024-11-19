package com.github.dgzt.mundus.plugin.recast

import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.github.dgzt.mundus.plugin.recast.converter.RecastNavMesComponentConverter
import com.github.dgzt.mundus.plugin.recast.creator.ComponentCreator
import com.github.dgzt.mundus.plugin.recast.creator.ComponentWidgetCreator
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.pluginapi.AssetExtension
import com.mbrlabs.mundus.pluginapi.ComponentExtension
import com.mbrlabs.mundus.pluginapi.MenuExtension
import com.mbrlabs.mundus.pluginapi.manager.AssetManager
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
            root.addLabel("TODO").setPad(PAD, PAD, PAD, PAD)
        }

    }

    @Extension
    class RecastComponentExtension : ComponentExtension {
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

}
