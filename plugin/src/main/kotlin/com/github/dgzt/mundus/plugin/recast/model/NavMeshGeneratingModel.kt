package com.github.dgzt.mundus.plugin.recast.model

import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent
import com.mbrlabs.mundus.pluginapi.ui.RootWidget

class NavMeshGeneratingModel(
    val component: RecastNavMeshComponent,
    val navMeshGenerator: Thread,
    val uiUpdater: Thread,
    var newNavMeshWidgetRoot: RootWidget
)
