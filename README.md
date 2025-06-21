# Recast4j Mundus plugin

This is a Recast and Detour navigation mesh toolset plugin for [Mundus Editor](https://github.com/JamesTKhan/Mundus).

## Setup for Editor

You need to build the plugin from source code:

```shell
mvn clean install
```

Then you need to copy `plugin/build/libs/recasts-plugin.jar` jar file into your `.mundus/plugins/` directory.

## Usage in Editor

Need to add `Recast NavMesh` component to terrain game object:

![image](https://github.com/user-attachments/assets/d5b0bc42-0ac2-4526-8192-d20a10c2be2c)

Add new navmesh:

![image](https://github.com/user-attachments/assets/395a5e94-5a49-4f8b-8a81-7a6131bf6007)

Add name to navhmesh and generate it:

![image](https://github.com/user-attachments/assets/18156904-fc9f-4294-8baa-6aebed8cd2a2)

Wait until the generation will be finished:

![image](https://github.com/user-attachments/assets/5a5290f9-20e6-4709-a7b1-0d31ecfcc48c)

![image](https://github.com/user-attachments/assets/fb0c7721-7f6f-4a3c-bda4-000292fea49d)

And don't forget to save project and you can use it in runtime.

## Setup for Runtime

Add the dependency in your core project:

```groovy
allprojects {
    ext {
        ...
        mundusVersion = 'master-SNAPSHOT'
        recastNavMeshPluginVersion = 'master-SNAPSHOT'
        gwtRecast4jVersion = "gwt_migration_antz-SNAPSHOT"
        gltfVersion = '2.2.1' // Only needed if targeting HTML, version should match what Mundus uses
    }
}

...

project(":core") {
    ...
    dependencies {
        ...
        api "com.github.jamestkhan.mundus:gdx-runtime:$mundusVersion"
        api "com.github.Dgzt:mundus-recast-plugin:$recastNavMeshPluginVersion"
    }
}
```

If you are targeting HTML (GWT) you will also need the following:

```groovy
project(":html") {
    ...
    dependencies {
        ...
        api "com.github.jamestkhan.mundus:gdx-runtime:$mundusVersion:sources"
        api "com.github.jamestkhan.mundus:commons:$mundusVersion:sources"
        api "com.github.mgsx-dev.gdx-gltf:gltf:$gltfVersion:sources"

        api "com.github.Dgzt:mundus-recast-plugin:$recastNavMeshPluginVersion:sources"
        api("com.github.JamesTKhan:gdx-recast:ce15d46:sources")
        api "com.github.antzGames.gwt-recast4j:recast:$gwtRecast4jVersion:sources"
        api "com.github.antzGames.gwt-recast4j:detour:$gwtRecast4jVersion:sources"
        api "com.github.antzGames.gwt-recast4j:detour-crowd:$gwtRecast4jVersion:sources"
    }
}
```

and lastly add this to your GdxDefinition.gwt.xml file:

```xml
<module>
    ...
    <inherits name="recast_navmesh_plugin" />
    <inherits name="gdx_recast" />
</module>
```

## Usage in Runtime

You need to pass the converter to Mundus:

```java
    @Override
    public void create () {
        ...

        mundus = new Mundus(Gdx.files.internal("MundusExampleProject"), config, new RecastNavMesComponentConverter());
        ...
    }
```

You can get the path between two points:

```java
    // Get navhmesh component from scenegraph
    RecastNavMeshComponent navMeshComponent = sceneGraph.findByName("Terrain 2").findComponentByType(Component.Type.NAVMESH);
    // Get navmesh asset from component
    NavMeshAsset navMeshAsset = navMeshComponent.findNavMeshAssetByName("main");

    // Define start and end positions on terrain 
    Ray startRay = sceneGraph.scene.cam.getPickRay(100f, 100f);
    Vector3 start = terrainComponent.getRayIntersection(new Vector3(), startRay);
    
    Ray endRay = sceneGraph.scene.cam.getPickRay(200f, 200f);
    Vector3 end = terrainComponent.getRayIntersection(new Vector3(), endRay);
    
    Array<float[]> path = new Array<>();
    // The `getPath` method will upload the path array
    navMeshAsset.getPath(start, end, path);
```
