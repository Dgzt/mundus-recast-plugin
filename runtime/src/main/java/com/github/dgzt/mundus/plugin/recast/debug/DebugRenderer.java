package com.github.dgzt.mundus.plugin.recast.debug;

import com.badlogic.gdx.graphics.Camera;
import com.github.dgzt.mundus.plugin.recast.component.RecastNavMeshComponent;
import com.github.jamestkhan.recast.debug.RecastDebugDraw;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Component;

public class DebugRenderer {

    private final RecastDebugDraw recastDebugDraw;

    private boolean enabled;

    public DebugRenderer(final Camera camera) {
        recastDebugDraw = new RecastDebugDraw(camera);
        enabled = false;
    }

    public void render(final SceneGraph sceneGraph) {
        if (!enabled) {
            return;
        }

        render(sceneGraph.getRoot());
    }

    private void render(final GameObject gameObject) {
        final RecastNavMeshComponent component = gameObject.findComponentByType(Component.Type.NAVMESH);

        if (component != null) {
            recastDebugDraw.debugDrawNavMeshPolysWithFlags(null, 1); // TODO pass navmesh
        }

        if (gameObject.getChildren() != null) {
            for (int i = 0; i < gameObject.getChildren().size; ++i) {
                final GameObject child = gameObject.getChildren().get(i);
                render(child);
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
