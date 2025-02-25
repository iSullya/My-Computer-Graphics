In this assignment, we addded shadows and reflectivity for more realistic and rich image.

First we added shadows, before we compute diffusion or shininess of a pixel is behind an object.
If it is? then we skip those and the result is darker pixels imitating shadows.

Then we added reflectivity, which is an object property that mirror the view reflected on its surface from other objects.
And how did we achieve that? we simply will shoot a ray perpendicular to the point that the original ray (the main one),
then reflect what the sub-ray intersected with (more like a scene within a scene) and bounce this ray based on the recursionDepth.

note that result1-result3 are stages of reaching the final result, result_final

to run the program on your machine all you need to do is pull this repo (or download it as a ZIP file), then run the main class RayTracer.java

and the output image will be created in the root folder a.k.a Assignment_3 folder named "Assignment_3_result".