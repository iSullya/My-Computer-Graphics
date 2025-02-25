In this assignment, we introduced a new concept to the scene, which is Super Sampling.

super sampling is an anti-aliasing method, it makes the picture a lot more crisp and sharp
however it degrade the performance. To simlpy put that, instead of shooting one ray for each pixel,
we will shoot multiple ones based on the sampling factor. For instance, if we have a 600 by 600 pixel 
rendering, and a sampling factor of 4 (4by4 rays for each pixel > 16 rays per pixel) there will be 
a total of 5,760,000 rays instead of only 360,000.

note that results are saved in the folder Result, each name reflect what is this result for.

to run the program on your machine all you need to do is pull this repo (or download it as a ZIP file), then run the main class RayTracer.java

and the output image will be created in the root folder a.k.a Assignment_4/Result folder named "Assignment_4_result".