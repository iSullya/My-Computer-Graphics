#ifdef GL_ES
precision mediump float;
#endif

#define RAND_SEED 954.212


// SPHERE STRUCTURES
struct Sphere {
    vec3 center;
    float radius;
    vec3 color;     // base color [0..1]
    float specular; // shininess exponent
    float reflective; 
    int material;   // 0=diffuse, 1=metal, 2=dielectric
    float refrIndex; // for dielectric
};


// Light data
struct Light {
    vec3 position;
    float intensity;
};

// SCENE DEFINITION
const int MAX_SPHERES = 100; // the higher, the longer to compile


// A small 2D hash function for static pseudo-random [0..1] values.
float hash12(vec2 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.yx + 31.32);
    return fract(p.x * p.y * 19.19);
}

// Filling the scene with the spheres 
void sceneSpheres(out Sphere spheres[MAX_SPHERES], out int sphereCount)
{
    sphereCount = 0;

    // Ground sphere
    spheres[sphereCount++] = Sphere(
        vec3(0.0, -1000.0, 0.0),
        1000.0,
        vec3(0.5, 0.5, 0.5),
        100.0,
        0.0,
        0,  // material=diffuse
        1.0
    );

    // Three main spheres
    spheres[sphereCount++] = Sphere(
        vec3(0.0, 1.0, 0.0),
        1.0,
        vec3(1.0),
        500.0,
        0.0,
        2,   // dielectric
        1.5
    );
    spheres[sphereCount++] = Sphere(
        vec3(-4.0, 1.0, 0.0),
        1.0,
        vec3(0.6, 0.3, 0.1),
        50.0,
        0.0,
        0,   // diffuse
        1.0
    );
    spheres[sphereCount++] = Sphere(
        vec3(4.0, 1.0, 0.0),
        1.0,
        vec3(0.7),
        500.0,
        1.0,
        1,   // metal
        1.0
    );

    // Many small random spheres
    for (int a = -5; a <= 5; a++) {
        for (int b = -5; b <= 5; b++) {
            float af = float(a);
            float bf = float(b);

            //float rx = hash12(vec2(af, bf));
            //float rz = hash12(vec2(af + 13.1, bf + 17.7));
            float rx = hash12(vec2(af + RAND_SEED, bf + RAND_SEED + 13.1));
            float rz = hash12(vec2(af + RAND_SEED + 25.7, bf + RAND_SEED + 17.7));


            //vec3 center = vec3(af + 0.9*rx, 0.2, bf + 0.9*rz - 2.0);
            //vec3 center = vec3((af + 0.9 * rx) * 0.6, 0.2, bf + 0.9 * rz - 2.0);
            //vec3 center = vec3(((af + 0.9 * rx) * 0.6) + 2.0, 0.2, bf + 0.9 * rz - 2.0);
            vec3 center = vec3(
                ((af + 0.9 * rx) * 1.5) + 2.0, // scale up x
                0.2,
                ((bf + 0.9 * rz) * 1.5) - 2.0  // scale up z
            );

            // Avoid placing small spheres too close to the big center sphere (0,1,0).
            float dist = length(center - vec3(0.0, 1.0, 0.0));
            if (dist <= 1.2) {
                continue;
            }

            //float chooseMat = hash12(vec2(af + 5.6, bf + 9.2));
            //float rcol = hash12(vec2(af + 2.3, bf + 4.1));
            //float gcol = hash12(vec2(af + 7.1, bf + 3.3));
            //float bcol = hash12(vec2(af + 8.5, bf + 1.2));
            
            float chooseMat = hash12(vec2(af + 5.6 + RAND_SEED, bf + 9.2 + RAND_SEED));
            float rcol = hash12(vec2(af + 2.3 + RAND_SEED, bf + 4.1 + RAND_SEED));
            float gcol = hash12(vec2(af + 7.1 + RAND_SEED, bf + 3.3 + RAND_SEED));
            float bcol = hash12(vec2(af + 8.5 + RAND_SEED, bf + 1.2 + RAND_SEED));

            if (sphereCount < MAX_SPHERES) {
                if (chooseMat < 0.7) {
                    // Diffuse
                    spheres[sphereCount++] = Sphere(
                        center, 0.2,
                        vec3(rcol, gcol, bcol),
                        50.0, 0.0,
                        0, // diffuse
                        1.0
                    );
                } else if (chooseMat < 0.9) {
                    // Metal
                    vec3 metalColor = 0.5 + 0.5 * vec3(rcol, gcol, bcol);
                    spheres[sphereCount++] = Sphere(
                        center, 0.2,
                        metalColor,
                        500.0, 1.0,
                        1, // metal
                        1.0
                    );
                } else {
                    // Glass
                    spheres[sphereCount++] = Sphere(
                        center, 0.2,
                        vec3(1.0),
                        500.0, 0.0,
                        2, // dielectric
                        1.5
                    );
                }
            }
        }
    }
}


// Single point light
Light sceneLight()
{
    Light l;
    l.position = vec3(2.0, 4.0, 2.0);
    l.intensity = 0.6; // Increase if scene is too dark
    return l;
}

// RAY-SPHERE INTERSECTION
// Returns (t, index) for the closest intersection
struct Hit {
    float t;
    int index;
};

Hit closestIntersection(vec3 ro, vec3 rd, Sphere spheres[MAX_SPHERES], int sphereCount, float tMin, float tMax)
{
    Hit hit;
    hit.t = -1.0;
    hit.index = -1;
    
    float closestT = tMax;
    for(int i=0; i<sphereCount; i++)
    {
        // Solve quadratic for (ro + rd*t - center)^2 = radius^2
        vec3 oc = ro - spheres[i].center;
        float a = dot(rd, rd);
        float b = 2.0 * dot(oc, rd);
        float c = dot(oc, oc) - spheres[i].radius*spheres[i].radius;
        float disc = b*b - 4.0*a*c;
        if(disc < 0.0) continue;
        float sqrtD = sqrt(disc);
        float t1 = (-b - sqrtD)/(2.0*a);
        float t2 = (-b + sqrtD)/(2.0*a);
        
        if(t1>tMin && t1<closestT) {
            closestT = t1;
            hit.t = t1;
            hit.index = i;
        }
        if(t2>tMin && t2<closestT) {
            closestT = t2;
            hit.t = t2;
            hit.index = i;
        }
    }
    
    return hit;
}


// COMPUTE LIGHTING
float computeLighting(vec3 p, vec3 n, vec3 v, float spec, Sphere spheres[MAX_SPHERES], int sphereCount)
{
    // ambient
    float intensity = 0.1;
    Light light = sceneLight();
    
    // direction to light
    vec3 L = normalize(light.position - p);
    
    // check shadow
    float tMax = length(light.position - p);
    Hit shadowHit = closestIntersection(p + n*0.001, L, spheres, sphereCount, 0.001, tMax);
    if(shadowHit.index >= 0) {
        // in shadow
        return intensity;
    }
    
    // diffuse
    float nDotL = dot(n, L);
    if(nDotL > 0.0) {
        intensity += light.intensity * nDotL / (length(n)*length(L));
    }
    
    // specular
    if(spec > 0.0) {
        vec3 r = reflect(-L, n);
        float rDotV = dot(r, v);
        if(rDotV>0.0) {
            intensity += light.intensity * pow(rDotV/(length(r)*length(v)), spec);
        }
    }
    return intensity;
}


// REFRACTION (Snell's law) + Schlick
float schlick(float cosi, float n1, float n2)
{
    float r0 = (n1 - n2)/(n1 + n2);
    r0 = r0*r0;
    return r0 + (1.0 - r0)*pow((1.0 - cosi), 5.0);
}

// A simple pseudo-random function
float randomVal() {
    return 0.3;
}


// TRACE RAY
vec3 traceRay(vec3 ro, vec3 rd, Sphere spheres[MAX_SPHERES], int sphereCount, int maxDepth)
{
    vec3 col = vec3(0.0);        // Final accumulated color
    vec3 attenuation = vec3(1.0); // Multiplicative attenuation factor

    for (int i = 0; i < maxDepth; i++) {
        // Find the closest intersection for the current ray.
        Hit hit = closestIntersection(ro, rd, spheres, sphereCount, 0.001, 1e9);
        if (hit.index < 0) {
            // No hit: add background color (sky gradient)
            float t = 0.5 * (rd.y + 1.0);
            vec3 background = mix(vec3(1.0), vec3(0.5, 0.7, 1.0), t);
            col += attenuation * background;
            break;
        }
        
        Sphere sph = spheres[hit.index];
        vec3 hitPoint = ro + rd * hit.t;
        vec3 normal = normalize(hitPoint - sph.center);
        vec3 viewDir = normalize(ro - hitPoint);
        
        // Compute local illumination using your existing computeLighting:
        float intensity = computeLighting(hitPoint, normal, viewDir, sph.specular, spheres, sphereCount);
        vec3 localColor = sph.color * intensity;
        
        // Handle material behavior based on material type:
        if (sph.material == 0) { // Diffuse
            // For diffuse, if there's reflectivity, we blend with a reflection bounce.
            if (sph.reflective > 0.0) {
                vec3 reflectDir = reflect(rd, normal);
                ro = hitPoint + normal * 0.001;  // offset to avoid self-intersection
                rd = reflectDir;
                attenuation *= mix(localColor, vec3(1.0), sph.reflective);
            } else {
                col += attenuation * localColor;
                break;
            }
        }
        else if (sph.material == 1) { // Metal
            // Introduce a small fuzz factor to the reflection
            float fuzz = 0.03; // Adjust this value for more or less fuzziness
            // Create a simple pseudo-random offset based on hit.t
            vec3 fuzzOffset = fuzz * vec3(
                fract(sin(hit.t * 12.9898) * 43758.5453),
                fract(sin(hit.t * 78.233) * 43758.5453),
                fract(sin(hit.t * 37.719) * 43758.5453)
            );
            vec3 reflectDir = reflect(rd, normal) + fuzzOffset;
            reflectDir = normalize(reflectDir);
            ro = hitPoint + normal * 0.001; // offset to avoid self-intersection
            rd = reflectDir;
            //attenuation *= 1.0;
            attenuation *= sph.color;
        }
        else if (sph.material == 2) { // Dielectric (glass)
            float n1 = 1.0;
            float n2 = sph.refrIndex;
            float cosi = dot(rd, -normal);
            vec3 n = normal;
            if (cosi < 0.0) {
                cosi = -cosi;
                n = -normal;
                float temp = n1; n1 = n2; n2 = temp;
            }
            float eta = n1 / n2;
            float k = 1.0 - eta * eta * (1.0 - cosi * cosi);
            if (k < 0.0) {
                // Total internal reflection
                vec3 reflectDir = reflect(rd, normal);
                ro = hitPoint + n * 0.001;
                rd = reflectDir;
            } else {
                vec3 reflectDir = reflect(rd, normal);
                vec3 refractDir = normalize(rd * eta + n * (eta * cosi - sqrt(k)));
                float reflectProb = schlick(cosi, n1, n2);
                if (randomVal() < reflectProb) {
                    ro = hitPoint + n * 0.001;
                    rd = reflectDir;
                } else {
                    ro = hitPoint + refractDir * 0.001;
                    rd = refractDir;
                }
            }
            attenuation *= 1.0; // For dielectric, assume no color attenuation.
        }
    }
    
    return col;
}


void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    // Set up the scene
    Sphere spheres[MAX_SPHERES];
    int sphereCount;
    sceneSpheres(spheres, sphereCount);
    
    // Camera
    vec2 uv = (fragCoord / iResolution.xy);
    uv = uv * 2.0 - 1.0;
    
    
    float aspect = iResolution.x / iResolution.y;
    //uv.x *= aspect;
    
    // Camera position and orientation
    vec3 lookFrom = vec3(8.0, 1.5, 2.5);
    vec3 lookAt   = vec3(0.0, 1.0, -3.0);
    vec3 vup      = vec3(0.0, 1.0, 0.0);
    float vfov    = 20.0; // in degrees
    
    // build camera coordinate system
    float theta = radians(vfov);
    float halfH = tan(theta*0.5);
    float halfW = aspect*halfH;
    vec3 w = normalize(lookFrom - lookAt);
    vec3 u = normalize(cross(vup, w));
    vec3 v = cross(w, u);
    
    vec3 origin = lookFrom;
    vec3 lowerLeftCorner = origin - u*halfW - v*halfH - w;
    vec3 horizontal = u*(2.0*halfW);
    vec3 vertical   = v*(2.0*halfH);
    
    // Generate ray for this pixel
    vec3 rd = normalize(lowerLeftCorner + uv.x*horizontal + uv.y*vertical - origin);
    
    // Trace the ray (up to some recursion depth)
    vec3 color = traceRay(origin, rd, spheres, sphereCount, 5); // 5 bounces
    
    // Final Output
    fragColor = vec4(color, 1.0);
}
