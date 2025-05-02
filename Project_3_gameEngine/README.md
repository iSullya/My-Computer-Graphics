# 3D GAME ENGINE

A Java‐based 3D “Minecraft-like” engine built on LWJGL and JOML. It demonstrates loading OBJ models, multi‐textured terrain with blend maps, dynamic lighting, and a first‐person camera.

## Overview

- **Core Modules**  
  – **EngineManager**: initializes GLFW/OpenGL, runs the game loop (update + render)  
  – **WindowManager**: creates and manages the GLFW window and input callbacks  
  – **RenderManager**: batches and dispatches entity and terrain rendering  
  – **ObjectLoader**: loads VAOs/VBOs for meshes and textures  
  – **ShaderManager**: compiles, links, and binds GLSL shaders  

- **Models & Textures**  
  – Loads OBJ files from `/resources/models` (e.g. `cube3.obj`, `bunny2.obj`)  
  – Applies diffuse textures via the `Texture` and `Material` classes  

- **Terrain**  
  – Generates a grid mesh (`Terrain.VERTEX_COUNT × VERTEX_COUNT`) of size 800×800 units  
  – Supports four‐way texture blending with a blend map (`BlendMapTerrrain`)  

- **Lighting**  
  – **DirectionalLight** for sun/ambient illumination  
  – **PointLight** for local light sources  
  – **SpotLight** for cone‐shaped effects  

- **Camera & Controls**  
  – First‐person camera (`Camera`) with WASD movement and mouse look  
  – Configurable via `Constants.CAMERA_MOVE_SPEED` and `Constants.MOUSE_SENSITIVITY`  

- **Demo Scene**  
  – `TestGame` sets up a simple scene: textured cube(s), blend‐mapped terrain, and moving lights  

## Customization

| Constant                                | Purpose                                    |
|-----------------------------------------|--------------------------------------------|
| `Constants.TITLE`                       | Window title                               |
| `Constants.FOV`, `Z_NEAR`, `Z_FAR`      | Projection parameters                      |
| `Constants.CAMERA_MOVE_SPEED`           | Camera movement speed                      |
| `Constants.MOUSE_SENSITIVITY`           | Mouse look sensitivity                     |
| `EngineManager.FRAMERATE`               | Game loop target FPS                       |
| `Terrain.VERTEX_COUNT`, `Terrain.SIZE`  | Terrain resolution & world scale           |
| `Constants.MAX_POINT_LIGHTS`, `MAX_SPOT_LIGHTS` | Maximum simultaneous lights       |

Swap out or add textures in `/app/src/main/resources/textures` and models in `/app/src/main/resources/models` to experiment.  
Modify GLSL files under `/app/src/main/resources/shaders` to tweak vertex/fragment logic.

## Usage

1. **Prerequisites**  
   – Java 21+  
   – Gradle (wrapper included)  
   – A GPU with OpenGL 3.2+ support  

2. **Build**  
   ```bash
   ./gradlew build
   ```

3. **Run**  
   ```bash
   ./gradlew run
   ```

4. **Controls**  
   - Move: `W` / `A` / `S` / `D`  
   - Look: mouse movement  
   - Exit: `ESC`  

## Results and Observations

- Terrain with `VERTEX_COUNT=128` yields ~16 K vertices; bump to 256 for finer detail (at a performance cost). 
- Separate shader programs for entities vs. terrain simplify material and lighting logic.
- OBJ loader supports multiple meshes—try `bunny2.obj` or custom models.

Feel free to fork, extend lighting and physics, or integrate networking for a true multiplayer experience!

## Important Note

- This whole project was built on the work of James from [the DevGenie Academy](https://www.youtube.com/@devgenieacademy8889) YouTube channel.
