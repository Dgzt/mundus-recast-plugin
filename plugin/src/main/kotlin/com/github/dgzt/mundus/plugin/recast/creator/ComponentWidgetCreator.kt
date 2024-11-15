package com.github.dgzt.mundus.plugin.recast.creator

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.github.dgzt.mundus.plugin.recast.PropertyManager
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import com.mbrlabs.mundus.pluginapi.ui.WidgetAlign

object ComponentWidgetCreator {

    fun setup(component: RecastNavMeshComponent, rootWidget: RootWidget) {
        rootWidget.addTextButton("Generate NavMesh") {
            Gdx.app.log("", "Click")

            val tmpDir = System.getProperty("java.io.tmpdir")
            val file = FileHandle("$tmpDir/tmp.navmesh")
            file.writeString("Hi!", true)

            PropertyManager.assetManager.createNewAsset(file)

            file.delete()
        }.setAlign(WidgetAlign.CENTER)
    }
}
