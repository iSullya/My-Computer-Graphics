In this assignment, we added more depth and reality to the scene, kind of from 2D to 3D scene using lightning,
previously we relied on shooting a ray and returning the color of the object that it hits. However that will 
return a plain image with no depth. In this assignment we introduced two features, Lighting and object specular (Shininess).

to deal with lighting -in a very simple words- we check if the ray hits an object that is exposed to light directly
if so, then the returned color will be more intense, the less the object exposed to light, the less color intensity
that pixel will have. For shininess, if the object's specular is high, then areas highly exposed to lighting will have even higher intensity.

note that result1 is the rendering with lighting and no specular, and result_final is with both lighting and specular

to run the program on your machine all you need to do is pull this repo (or download it as a ZIP file), then run the main class RayTracer.java

and the output image will be created in the root folder a.k.a Assignment_2 folder named "Assignment_2_result".

