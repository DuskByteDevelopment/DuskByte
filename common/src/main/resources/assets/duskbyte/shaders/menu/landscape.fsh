#version 410 core

layout(std140) uniform GlslSandboxInfo {
    vec4 SandboxResolutionTime;
    vec4 SandboxMouse;
};

layout(location = 0) out vec4 fragColor;

#define resolution SandboxResolutionTime.xy
#define time SandboxResolutionTime.z
#define mouse SandboxMouse.xy

#define iResolution resolution
#define iTime time
#define iMouse mouse

void mainImage(out vec4 fragColor, in vec2 fragCoord);

void main(void) {
    mainImage(fragColor, gl_FragCoord.xy);
}

const vec3 uCameraOrigin = vec3(656.882, 346.103, 833.989);
const vec3 uCameraTarget = vec3(714.486, 343.834, 764.875);

const int kMaxTerrainMarch = 300;
const int kMaxCloudMarch = 53;
const int kMaxGrassMarch = 64;
const int kMaxTerrainShadow = 4;
const int kMaxGrassNormal = 4;
const int kMaxGrassShadow = 2;
const int kMaxGrassBlades = 3;
const float kRoseMarchMaxDist = 120.0;
const float kRoseLodDist = 120.0;
const float kRoseSpawnThreshold = 0.23;
const float kRoseCell = 6.0;

const float kSunIntensity = 0.35;
const float kSunElevation = 0.4;
const vec3 kFogColor = vec3(0.55, 0.55, 0.58);
const vec3 kSkyColor = vec3(0.42, 0.62, 1.1);
const vec3 kSkySunGlareColor = vec3(1.0, 0.6, 0.3);
const vec3 kCloudSkyLight = vec3(0.7, 0.8, 1.0);
const vec3 kCloudGroundLight = vec3(0.1, 0.4, 0.2);
const vec3 kCloudSunLight = vec3(1.0, 0.95, 0.85);
const vec3 kCloudBaseColor = vec3(0.8, 0.8, 0.8);
const vec3 kCloudScatterColor = vec3(1.0, 0.6, 0.4);
const vec3 kTerrainBaseColor = vec3(0.12, 0.09, 0.035);
const vec3 kTerrainSlopeColor = vec3(0.07, 0.055, 0.015);
const vec3 kTerrainAmbientColor = vec3(0.1, 0.2, 0.1);
const vec3 kTerrainSkyAmbient = vec3(0.7, 0.9, 1.5);
const vec3 kTerrainSunColor = vec3(1.0, 0.9, 0.8);
const vec3 kTerrainBackColor = vec3(1.1, 1.0, 0.9);
const vec3 kGrassDryLo = vec3(0.28, 0.22, 0.07);
const vec3 kGrassDryHi = vec3(0.38, 0.3, 0.09);
const vec3 kGrassMidLo = vec3(0.48, 0.38, 0.1);
const vec3 kGrassMidHi = vec3(0.58, 0.46, 0.13);
const vec3 kGrassTipLo = vec3(0.68, 0.55, 0.16);
const vec3 kGrassTipHi = vec3(0.78, 0.64, 0.2);
const vec3 kGrassSunColor = vec3(1.15, 1.0, 0.55);
const vec3 kGrassAmbientLo = vec3(0.18, 0.14, 0.04);
const vec3 kGrassAmbientHi = vec3(0.75, 0.68, 0.35);
const vec3 kGrassBackColor = vec3(1.0, 0.92, 0.65);
const vec3 kGrassFreColor = vec3(1.0, 0.9, 0.55);
const vec3 kGrassSpecColor = vec3(1.0, 0.95, 0.55);
const vec3 kFinalSunGlareColor = vec3(0.8, 0.4, 0.2);
const vec3 kGradeGamma = vec3(1.0, 0.92, 1.0);
const vec3 kGradeTint = vec3(1.02, 0.99, 0.9);
const float kFogIntensity = 0.00025;
const float kGrassWindSpeed = 1.0;
const float kCloudWindSpeed = 1.0;
const float kTerrainShadowK = 32.0;
const float kGrassShadowK = 52.0;
const float kGrassShadowFloor = 0.48;
const float kCloudShadowIntensity = 1.5;
const float kCloudShadowSunDist = 70.0;
const float kSkyGradient = 0.4;
const float kSkySunGlare = 0.2;
const float kSkyCloudMix = 0.12;
const float kCloudSunStrength = 3.0;
const float kCloudDiffuseBase = 0.4;
const float kCloudDiffuseScale = 0.6;
const float kCloudBaseScale = 0.45;
const float kCloudScatterStrength = 0.3;
const float kTerrainSunStrength = 8.5;
const float kTerrainAmbientStrength = 0.2;
const float kTerrainBackStrength = 0.27;
const float kTerrainSpecStrength = 4.0;
const float kGrassSunStrength = 10.0;
const float kGrassAmbientStrength = 0.6;
const float kGrassBackStrength = 0.1;
const float kGrassFreStrength = 0.8;
const float kGrassAlbedoScale = 0.88;
const float kSpecPower = 9.0;
const float kSpecStrength = 3.0;
const float kFinalSunGlare = 0.25;
const float kPostExposure = 1.1;
const float kPostOffset = -0.02;
const float kGradeBlueBias = 0.1;
const float kTemporalBlend = 0.1;
const float kTemporalCloudBlend = 0.8;

const float kMaxGrassHeight = 5.2;
const float kGrassHeightMax = 1.35;
const float kGrassEnvelopeScale = 1.48;
const float kGrassCell = 0.9;
const float kMaxRoseHeight = 3.0;
const vec3 kRoseAmbientLo = vec3(0.14, 0.08, 0.10);
const vec3 kRoseAmbientHi = vec3(0.58, 0.38, 0.48);
const float kMaxElevation = 600.0;

const mat3 rotation3D = mat3(0.00, 0.80, 0.60, -0.80, 0.36, -0.48, -0.60, -0.48, 0.64);
const mat3 inverseRotation3D = mat3(0.00, -0.80, -0.60, 0.80, 0.36, -0.48, 0.60, -0.48, 0.64);
const mat2 rotation2D = mat2(0.80, 0.60, -0.60, 0.80);
const mat2 inverseRotation2D = mat2(0.80, -0.60, 0.60, 0.80);


float sdEllipsoidY(in vec3 p, in vec2 r) {
    float k0 = length(p / r.xyx);
    float k1 = length(p / (r.xyx * r.xyx));
    return k0 * (k0 - 1.0) / k1;
}

float sdEllipsoid(in vec3 p, in vec3 r) {
    float k0 = length(p / r);
    float k1 = length(p / (r * r));
    return k0 * (k0 - 1.0) / k1;
}

float sdCapsule(vec3 p, vec3 a, vec3 b, float r) {
    vec3 pa = p - a;
    vec3 ba = b - a;
    float h = clamp(dot(pa, ba) / max(dot(ba, ba), 1e-6), 0.0, 1.0);
    return length(pa - ba * h) - r;
}

float smoothstepWithDerivative(float a, float b, float x, out float der) {
    der = 0.0;
    if(x < a)
        return 0.0;
    if(x > b)
        return 1.0;
    float ir = 1.0 / (b - a);
    x = (x - a) * ir;
    der =  6.0 * x * (1.0 - x) * ir;
    return x * x * (3.0 - 2.0 * x);
}

mat3 setCamera(in vec3 ro, in vec3 ta, float cr) {
    vec3 cw = normalize(ta - ro);
    vec3 cp = vec3(sin(cr), cos(cr), 0.0);
    vec3 cu = normalize(cross(cw, cp));
    vec3 cv = normalize(cross(cu, cw));
    return mat3(cu, cv, cw);
}

float hash1D(vec2 p) {
    p = 50.0 * fract(p * 0.3183099);
    return fract(p.x * p.y * (p.x + p.y));
}

float hash1D(float n) {
    return fract(n * 17.0 * fract(n * 0.3183099));
}

vec2 hash2D(vec2 p) {
    const vec2 k = vec2(0.3183099, 0.3678794);
    float n = 111.0 * p.x + 113.0 * p.y;
    return fract(n * fract(k * n));
}

float noise(in vec2 x) {
    vec2 p = floor(x);
    vec2 w = fract(x);
    #if 1
    vec2 u = w * w * w * (w * (w * 6.0 - 15.0) + 10.0);
    #else
    vec2 u = w * w * (3.0 - 2.0 * w);
    #endif

    float a = hash1D(p + vec2(0, 0));
    float b = hash1D(p + vec2(1, 0));
    float c = hash1D(p + vec2(0, 1));
    float d = hash1D(p + vec2(1, 1));

    return -1.0 + 2.0 * (a + (b - a) * u.x + (c - a) * u.y + (a - b - c + d) * u.x * u.y);
}

float noise(in vec2 x, out vec2 der) {
    der = vec2(0.0);
    vec2 p = floor(x);
    vec2 w = fract(x);
    #if 1
    vec2 u = w * w * w * (w * (w * 6.0 - 15.0) + 10.0);
    vec2 du = 30.0 * w * w * (w * (w - 2.0) + 1.0);
    #else
    vec2 u = w * w * (3.0 - 2.0 * w);
    vec2 du = 6.0 * w * (1.0 - w);
    #endif

    float a = hash1D(p + vec2(0, 0));
    float b = hash1D(p + vec2(1, 0));
    float c = hash1D(p + vec2(0, 1));
    float d = hash1D(p + vec2(1, 1));

    float k0 = a;
    float k1 = b - a;
    float k2 = c - a;
    float k4 = a - b - c + d;

    der = 2.0 * du * vec2(k1 + k4 * u.y, k2 + k4 * u.x);
    return -1.0 + 2.0 * (k0 + k1 * u.x + k2 * u.y + k4 * u.x * u.y);
}

float noise(in vec3 x) {
    vec3 p = floor(x);
    vec3 w = fract(x);

    #if 1
    vec3 u = w * w * w * (w * (w * 6.0 - 15.0) + 10.0);
    #else
    vec3 u = w * w * (3.0 - 2.0 * w);
    #endif

    float n = p.x + 317.0 * p.y + 157.0 * p.z;

    float a = hash1D(n + 0.0);
    float b = hash1D(n + 1.0);
    float c = hash1D(n + 317.0);
    float d = hash1D(n + 318.0);
    float e = hash1D(n + 157.0);
    float f = hash1D(n + 158.0);
    float g = hash1D(n + 474.0);
    float h = hash1D(n + 475.0);

    float k0 = a;
    float k1 = b - a;
    float k2 = c - a;
    float k3 = e - a;
    float k4 = a - b - c + d;
    float k5 = a - c - e + g;
    float k6 = a - b - e + f;
    float k7 = -a + b + c - d + e - f - g + h;

    return -1.0 + 2.0 * (k0 + k1 * u.x + k2 * u.y + k3 * u.z + k4 * u.x * u.y + k5 * u.y * u.z + k6 * u.z * u.x + k7 * u.x * u.y * u.z);
}

float noise(in vec3 x, out vec3 der) {
    der = vec3(0.0);
    vec3 p = floor(x);
    vec3 w = fract(x);
    #if 1
    vec3 u = w * w * w * (w * (w * 6.0 - 15.0) + 10.0);
    vec3 du = 30.0 * w * w * (w * (w - 2.0) + 1.0);
    #else
    vec3 u = w * w * (3.0 - 2.0 * w);
    vec3 du = 6.0 * w * (1.0 - w);
    #endif

    float n = p.x + 317.0 * p.y + 157.0 * p.z;

    float a = hash1D(n + 0.0);
    float b = hash1D(n + 1.0);
    float c = hash1D(n + 317.0);
    float d = hash1D(n + 318.0);
    float e = hash1D(n + 157.0);
    float f = hash1D(n + 158.0);
    float g = hash1D(n + 474.0);
    float h = hash1D(n + 475.0);

    float k0 = a;
    float k1 = b - a;
    float k2 = c - a;
    float k3 = e - a;
    float k4 = a - b - c + d;
    float k5 = a - c - e + g;
    float k6 = a - b - e + f;
    float k7 = -a + b + c - d + e - f - g + h;
    der = 2.0 * du * vec3(k1 + k4 * u.y + k6 * u.z + k7 * u.y * u.z, k2 + k5 * u.z + k4 * u.x + k7 * u.z * u.x, k3 + k6 * u.x + k5 * u.y + k7 * u.x * u.y);
    return -1.0 + 2.0 * (k0 + k1 * u.x + k2 * u.y + k3 * u.z + k4 * u.x * u.y + k5 * u.y * u.z + k6 * u.z * u.x + k7 * u.x * u.y * u.z);
}

float fbm3(in vec2 coord, in float frequency, in float lacunarity, in float persistence) {
    float retVal = 0.0;
    for(int i = 0; i < 3; i++) {
        float n = noise(coord);
        retVal += persistence * n;
        persistence *= lacunarity;
        coord = frequency * rotation2D * coord;
    }
    return retVal;
}

float fbm3(in vec2 coord, in float frequency, in float lacunarity, in float persistence, out vec2 der) {
    float retVal = 0.0;
    der = vec2(0.0);
    mat2 m = mat2(1.0, 0.0, 0.0, 1.0);
    for(int i = 0; i < 3; i++) {
        vec2 d = vec2(0.0);
        float n = noise(coord, d);
        retVal += persistence * n;
        der += persistence * m * d;
        persistence *= lacunarity;
        coord = frequency * rotation2D * coord;
        m = frequency * inverseRotation2D * m;
    }
    return retVal;
}

float fbm4(in vec3 coord, in float frequency, in float lacunarity, in float persistence) {
    float retVal = 0.0;
    for(int i = 0; i < 4; i++) {
        float n = noise(coord);
        retVal += persistence * n;
        persistence *= lacunarity;
        coord = frequency * rotation3D * coord;
    }
    return retVal;
}

float fbm4(in vec2 coord, in float frequency, in float lacunarity, in float persistence) {
    float retVal = 0.0;
    for(int i = 0; i < 4; i++) {
        float n = noise(coord);
        retVal += persistence * n;
        persistence *= lacunarity;
        coord = frequency * rotation2D * coord;
    }
    return retVal;
}

float fbm4(in vec2 coord, in float frequency, in float lacunarity, in float persistence, out vec2 der) {
    float retVal = 0.0;
    der = vec2(0.0);
    mat2 m = mat2(1.0, 0.0, 0.0, 1.0);
    for(int i = 0; i < 3; i++) {
        vec2 d = vec2(0.0);
        float n = noise(coord, d);
        retVal += persistence * n;
        der += persistence * m * d;
        persistence *= lacunarity;
        coord = frequency * rotation2D * coord;
        m = frequency * inverseRotation2D * m;
    }
    return retVal;
}


float fbm7(in vec3 coord, in float frequency, in float lacunarity, in float persistence, out vec3 der) {
    float retVal = 0.0;
    der = vec3(0.0);
    mat3 m = mat3(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0);
    for(int i = 0; i < 7; i++) {
        vec3 d = vec3(0.0);
        float n = noise(coord, d);
        retVal += persistence * n;
        der += persistence * m * der;
        persistence *= lacunarity;
        coord = frequency * rotation3D * coord;
        m = frequency * inverseRotation3D * m;
    }
    return retVal;
}

float fbm8(in vec3 coord, in float frequency, in float lacunarity, in float persistence, out vec3 der) {
    float retVal = 0.0;
    der = vec3(0.0);
    mat3 m = mat3(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0);
    for(int i = 0; i < 8; i++) {
        vec3 d = vec3(0.0);
        float n = noise(coord, d);
        retVal += persistence * n;
        der += persistence * m * der;
        persistence *= lacunarity;
        coord = frequency * rotation3D * coord;
        m = frequency * inverseRotation3D * m;
    }
    return retVal;
}

float fbm9(in vec2 coord, in float frequency, in float lacunarity, in float persistence) {
    float retVal = 0.0;
    for(int i = 0; i < 9; i++) {
        float n = noise(coord);
        retVal += persistence * n;
        persistence *= lacunarity;
        coord = frequency * rotation2D * coord;
    }
    return retVal;
}


vec3 fog(in vec3 col, float t) {
    vec3 ext = exp2(-t * kFogIntensity * vec3(1, 1.5, 4));
    return col * ext + (1.0 - ext) * kFogColor;
}

vec3 computeSunDir() {
    float azimuth = iTime * 0.01;
    return normalize(vec3(cos(azimuth), kSunElevation, sin(azimuth)));
}

vec4 cloudsFbm(in vec3 pos) {
    vec3 der = vec3(0.0);
    float height = fbm8(pos * 0.0015 + vec3(2.0, 1.1, 1.0) + kCloudWindSpeed * 0.07 * vec3(-iTime, 0.5 * iTime, 0.15 * iTime),  2.0, 0.65, 0.5, der);
    return vec4(height, der);
}

vec4 cloudsMap(in vec3 pos, out float nnd) {
    float d = abs(pos.y - 900.0) - 40.0;
    vec3 gra = vec3(0.0, sign(pos.y - 900.0), 0.0);

    vec4 n = cloudsFbm(pos);
    d += 400.0 * n.x * (0.7 + 0.3 * gra.y);

    if(d > 0.0)
        return vec4(-d, 0.0, 0.0, 0.0);

    nnd = -d;
    d = min(-d / 100.0, 0.25);

    return vec4(d, gra);
}

float cloudsShadowFlat(in vec3 ro, in vec3 rd) {
    float t = (900.0 - ro.y) / rd.y;
    if(t < 0.0)
        return 1.0;
    vec3 pos = ro + rd * t;
    return cloudsFbm(pos).x;
}


vec4 renderClouds(in vec3 ro, in vec3 rd, float tmin, float tmax, inout float resT, in vec2 px, in vec3 gSunDir) {
    vec4 sum = vec4(0.0);

    // bounding volume
    float tl = (600.0 - ro.y) / rd.y;
    float th = (1200.0 - ro.y) / rd.y;
    if(tl > 0.0)
        tmin = max(tmin, tl);
    else
        return sum;
    if(th > 0.0)
        tmax = min(tmax, th);

    float t = tmin;
    float lastT = -1.0;
    float thickness = 0.0;
    for(int i = 0; i < kMaxCloudMarch; i++) {
        vec3 pos = ro + t * rd;
        float nnd;
        vec4 denGra = cloudsMap(pos, nnd);
        float den = denGra.x;
        float dt = max(0.2, 0.011 * t);
        if(den > 0.001) {
            float kk;
            cloudsMap(pos + gSunDir * kCloudShadowSunDist, kk);
            float sha = 1.0 - smoothstep(-200.0, 200.0, kk);
            sha *= kCloudShadowIntensity;

            vec3 nor = normalize(denGra.yzw);
            float dif = clamp(kCloudDiffuseBase + kCloudDiffuseScale * dot(nor, gSunDir), 0.0, 1.0) * sha;
            float fre = clamp(1.0 + dot(nor, rd), 0.0, 1.0) * sha;
            float occ = 0.2 + 0.7 * max(1.0 - kk / 200.0, 0.0) + 0.1 * (1.0 - den);
            // lighting
            vec3 lin = vec3(0.0);
            lin += kCloudSkyLight * 1.0 * (0.5 + 0.5 * nor.y) * occ;
            lin += kCloudGroundLight * 1.0 * (0.5 - 0.5 * nor.y) * occ;
            lin += kCloudSunLight * kCloudSunStrength * kSunIntensity * dif * occ + 0.1;

            // color
            vec3 col = kCloudBaseColor * kCloudBaseScale;

            col *= lin;

            col = fog(col, t);

            // front to back blending    
            float alp = clamp(den * 0.5 * 0.125 * dt, 0.0, 1.0);
            col.rgb *= alp;
            sum = sum + vec4(col, alp) * (1.0 - sum.a);

            thickness += dt * den;
            if(lastT < 0.0)
                lastT = t;
        } else {
            dt = abs(den) + 0.2;

        }
        t += dt;
        if(sum.a > 0.995 || t > tmax)
            break;
    }

    if(lastT > 0.0)
        resT = min(resT, lastT);

    sum.xyz += max(0.0, 1.0 - 0.0125 * thickness) * kCloudScatterColor * kCloudScatterStrength * pow(clamp(dot(gSunDir, rd), 0.0, 1.0), 32.0);

    return clamp(sum, 0.0, 1.0);
}

// terrain

vec2 terrainMap(in vec2 p) {
    float e = fbm4(p / 2000.0 + vec2(1.0, -2.0), 1.9, 0.55, 0.5);
    float a = 1.0 - smoothstep(0.12, 0.13, abs(e + 0.12));
    e = 0.5 * kMaxElevation * (e + 1.0);

    return vec2(e, a);
}

vec4 terrainMapD(in vec2 p) {
    vec2 der = vec2(0.0);
    float height = fbm4(p / 2000.0 + vec2(1.0, -2.0), 1.9, 0.55, 0.5, der);
    height = 0.5 * kMaxElevation * (height + 1.0);
    der = 0.5 * kMaxElevation * der;
    return vec4(height, normalize(vec3(-der.x, 1.0, -der.y)));
}

vec3 terrainNormal(in vec2 pos) {
    return terrainMapD(pos).yzw; 
}

float terrainShadow(in vec3 ro, in vec3 rd, in float mint) {
    float res = 1.0;
    float t = mint;
    for(int i = 0; i < kMaxTerrainShadow; i++) {
        vec3 pos = ro + t * rd;
        vec2 env = terrainMap(pos.xz);
        float hei = pos.y - env.x;
        res = min(res, kTerrainShadowK * hei / t);
        if(res < 0.0001 || pos.y > kMaxElevation)
            break;
        t += clamp(hei, 0.5 + t * 0.05, 25.0);
    }
    return clamp(res, 0.0, 1.0);
}

vec2 raymarchTerrain(in vec3 ro, in vec3 rd, float tmin, float tmax) {
    // bounding plane
    float tp = (kMaxElevation + kMaxGrassHeight * kGrassEnvelopeScale - ro.y) / rd.y;
    if(tp > 0.0)
        tmax = min(tmax, tp);

    // raymarch
    float dis, th;
    float t2 = -1.0;
    float t = tmin;
    float ot = t;
    float odis = 0.0;
    float odis2 = 0.0;
    for(int i = 0; i < kMaxTerrainMarch; i++) {
        th = 0.001 * t;

        vec3 pos = ro + t * rd;
        vec2 env = terrainMap(pos.xz);
        float hei = env.x;

        // grass envelope
        float dis2 = pos.y - (hei + kMaxGrassHeight * kGrassEnvelopeScale);
        if(dis2 < th) {
            if(t2 < 0.0) {
                t2 = ot + (th - odis2) * (t - ot) / (dis2 - odis2);
            }
        }
        odis2 = dis2;

        // terrain
        dis = pos.y - hei;
        if(dis < th)
            break;

        ot = t;
        odis = dis;
        t += dis * 0.8 * (1.0 - 0.75 * env.y);
        if(t > tmax)
            break;
    }

    if(t > tmax)
        t = -1.0;
    else
        t = ot + (th - odis) * (t - ot) / (dis - odis);

// grass

vec3 cloudWindDir() {
    return normalize(vec3(1.0, 0.0, -0.15));
}

float grassSway(in vec2 xz) {
    vec3 pos = vec3(xz.x, 0.0, xz.y) * 0.008 + vec3(2.0, 1.1, 1.0)
        + kGrassWindSpeed * 0.07 * vec3(-iTime, 0.5 * iTime, 0.15 * iTime);
    return fbm4(pos, 2.0, 0.65, 0.5) * 2.0 - 1.0;
}

float grassMap(in vec3 p, in float rt, out float oHei, out float oMat, out float oDis) {
    oHei = 1.0;
    oDis = 0.0;
    oMat = 0.0;

    float base = terrainMap(p.xz).x;
    float swayBase = grassSway(p.xz);

    float d = 20.0;
    vec2 n = floor(p.xz);
    vec2 f = fract(p.xz);
    for(int j = 0; j <= 1; j++) for(int i = 0; i <= 1; i++) {
            vec2 g = vec2(float(i), float(j)) - step(f, vec2(0.5));
            vec2 cell = n + g;

            for(int b = 0; b < kMaxGrassBlades; b++) {
                vec2 bladeCell = cell + vec2(float(b) * 17.3, float(b) * 9.1);
                vec2 v = hash2D(bladeCell + vec2(13.1, 71.7));

                // 4x2 sub-grid; bias each blade toward the parent cell perimeter
                vec2 grid = vec2(float(b & 3), float(b >> 2));
                vec2 subCell = vec2(0.25, 0.5);
                vec2 jitter = hash2D(bladeCell + vec2(41.3, 83.7));
                // Prefer sub-cell edges (pow < 1 pushes uniform samples toward 0/1)
                vec2 edgeJitter = jitter * 2.0 - 1.0;
                edgeJitter = sign(edgeJitter) * pow(abs(edgeJitter), vec2(0.28));
                edgeJitter = edgeJitter * 0.5 + 0.5;
                vec2 local = (grid + edgeJitter) * subCell;
                // Expand from cell center so blades sit nearer the cell boundary
                vec2 anchor = clamp(0.5 + (local - 0.5) * 1.95, 0.02, 0.98);
                vec2 r = g - f + anchor;

                float hSeed = hash1D(bladeCell + 23.7);
                float height = kMaxGrassHeight * mix(0.15, kGrassHeightMax, hSeed);
                float width = (0.022 + 0.030 * v.y) * mix(0.8, 1.2, height / kMaxGrassHeight)
                    + 0.008 * smoothstep(120.0, 500.0, rt);
                float angle = v.y * 2.28318;

                float sway = swayBase * height * 0.42;
                vec3 wind = cloudWindDir();
                vec3 tip = vec3(wind.x * sway, height, wind.z * sway);

                mat2 rot = mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
                vec2 localXZ = rot * (r * kGrassCell);
                vec3 q = vec3(localXZ.x, p.y - base, localXZ.y);

                float k = sdCapsule(q, vec3(0.0), tip, width);

                if(k < d) {
                    d = k;
                    oMat = hash1D(bladeCell + 111.0);
                    oHei = clamp((p.y - base) / height, 0.0, 1.0);
                }
            }
        }

    return d;
}

// wildroses

float rosesMap(in vec3 p, in float rt, out float oHei, out float oMat, out float oDis) {
    oHei = 0.0;
    oDis = 0.0;
    oMat = 0.0;

    float base = terrainMap(p.xz).x;
    float swayBase = grassSway(p.xz);
    float d = 20.0;
    vec2 n = floor(p.xz / kRoseCell);
    vec2 f = fract(p.xz / kRoseCell);
    for(int j = 0; j <= 1; j++) for(int i = 0; i <= 1; i++) {
            vec2 g = vec2(float(i), float(j)) - step(f, vec2(0.5));
            vec2 cell = n + g;
            if(hash1D(cell + 501.0) > kRoseSpawnThreshold)
                continue;

            vec2 o = hash2D(cell);
            vec2 v = hash2D(cell + vec2(13.1, 71.7));
            vec2 r = g - f + o;

            float height = kMaxRoseHeight * (0.55 + 0.45 * v.x);
            float flowerR = 0.18 + 0.20 * v.y;
            float sway = swayBase * height * 0.42;
            vec3 wind = cloudWindDir();
            vec3 tip = vec3(wind.x * sway, height, wind.z * sway);
            vec3 q = vec3(r.x * kRoseCell, p.y - base, r.y * kRoseCell);

            float k;
            if(rt > kRoseLodDist) {
                vec3 blobPos = vec3(tip.x * 0.55, height * 0.55, tip.z * 0.55);
                k = sdEllipsoid(q - blobPos, vec3(flowerR * 1.6, flowerR, flowerR * 1.6));
                if(k < d) {
                    d = k;
                    oMat = hash1D(cell + 777.0);
                    oHei = 1.0;
                }
            } else {
                float stemR = 0.015 + 0.008 * v.y;
                vec3 flowerPos = tip + vec3(0.0, flowerR * 0.5, 0.0);
                float kStem = sdCapsule(q, vec3(0.0), tip, stemR);
                float kFlower = sdEllipsoid(q - flowerPos, vec3(flowerR, flowerR * 0.55, flowerR));
                k = min(kStem, kFlower);
                if(k < d) {
                    d = k;
                    oMat = hash1D(cell + 777.0);
                    oHei = clamp((p.y - base) / height, 0.0, 1.0);
                    if(kFlower <= kStem)
                        oHei = max(oHei, 0.88);
                }
            }
        }

    return d;
}

float grassShadow(in vec3 ro, in vec3 rd) {
    float res = 1.0;
    float t = 0.02;
    float kk1, kk2, kk3;
    for(int i = 0; i < kMaxGrassShadow; i++) {
        float h = grassMap(ro + rd * t, t, kk1, kk2, kk3);
        res = min(res, kGrassShadowK * h / t);
        t += max(h, 0.02);
        if(res < 0.001)
            break;
    }
    return mix(kGrassShadowFloor, 1.0, clamp(res, 0.0, 1.0));
}

vec3 grassNormal(in vec3 pos, in float t) {
    float kk1, kk2, kk3;
    vec3 n = vec3(0.0);
    for(int i = 0; i < kMaxGrassNormal; i++) {
        vec3 e = 0.5773 * (2.0 * vec3((((i + 3) >> 1) & 1), ((i >> 1) & 1), (i & 1)) - 1.0);
        n += e * grassMap(pos + 0.004 * e, t, kk1, kk2, kk3);
    }
    return normalize(n);
}

vec3 roseNormal(in vec3 pos, in float t) {
    float kk1, kk2, kk3;
    vec3 n = vec3(0.0);
    for(int i = 0; i < kMaxGrassNormal; i++) {
        vec3 e = 0.5773 * (2.0 * vec3((((i + 3) >> 1) & 1), ((i >> 1) & 1), (i & 1)) - 1.0);
        n += e * rosesMap(pos + 0.004 * e, t, kk1, kk2, kk3);
    }
    return normalize(n);
}

// sky

vec3 renderSky(in vec3 ro, in vec3 rd, in vec3 gSunDir) {
    // background sky     
    vec3 col = kSkyColor - rd.y * kSkyGradient;

    // clouds
    float t = (2500.0 - ro.y) / rd.y;
    if(t > 0.0) {
        vec2 uv = (ro + t * rd).xz;
        float cl = fbm9(uv * 0.00104, 1.9, 0.55, 0.5);
        float dl = smoothstep(-0.2, 0.6, cl);
        col = mix(col, vec3(1.0), kSkyCloudMix * dl);
    }

    // sun glare    
    float sun = clamp(dot(gSunDir, rd), 0.0, 1.0);
    col += kSkySunGlare * kSunIntensity * kSkySunGlareColor * pow(sun, 32.0);

    return col;
}

// main image

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 p = (2.0 * fragCoord - iResolution.xy) / iResolution.y;

    vec3 gSunDir = computeSunDir();

    vec3 rayOrigin = uCameraOrigin;
    vec3 rayTarget = uCameraTarget;

    mat3 camera = setCamera(rayOrigin, rayTarget, 0.0);
    vec3 rayDirection = camera * normalize(vec3(p, 1.5));

    float resT = 2000.0;

    // sky
    vec3 col = renderSky(rayOrigin, rayDirection, gSunDir);

    // raycast terrain and grass envelope
    {
        const float tmax = 2000.0;
        int obj = 0;
        vec2 t = raymarchTerrain(rayOrigin, rayDirection, 1.0, tmax);
        if(t.x > 0.0) {
            resT = t.x;
            obj = 1;
        }

        // raycast grass and wildroses, if needed
        float hei, mid, displa;
        float rHei, rMat, rDis;
        bool hitRose = false;

        if(t.y > 0.0) {
            float tf = t.y;
            float tfMax = (t.x > 0.0) ? t.x : tmax;
            for(int i = 0; i < kMaxGrassMarch; i++) {
                vec3 pos = rayOrigin + tf * rayDirection;
                float disG = grassMap(pos, tf, hei, mid, displa);
                float disR = 20.0;
                hitRose = false;
                if(tf < kRoseMarchMaxDist) {
                    disR = rosesMap(pos, tf, rHei, rMat, rDis);
                    hitRose = (disR < disG);
                }
                float dis = min(disG, disR);
                if(dis < (0.000125 * tf))
                    break;
                tf += dis;
                if(tf > tfMax)
                    break;
            }
            if(tf < tfMax) {
                resT = tf;
                obj = hitRose ? 3 : 2;
            }
        }

        // shade
        if(obj > 0) {
            vec3 pos = rayOrigin + resT * rayDirection;
            vec3 epos = pos + vec3(0.0, 4.8, 0.0);

            float sha1 = terrainShadow(pos + vec3(0, 0.02, 0), gSunDir, 0.02);
            float cloudAtt = smoothstep(-0.325, -0.075, cloudsShadowFlat(epos, gSunDir));
            sha1 *= mix(1.0, cloudAtt, clamp(kCloudShadowIntensity, 0.0, 1.0));

            float sha2 = grassShadow(pos + vec3(0, 0.02, 0), gSunDir);

            vec3 tnor = terrainNormal(pos.xz);
            vec3 nor;

            vec3 speC = vec3(1.0);

            // terrain
            if(obj == 1) {
                // bump map
                vec3 nder = vec3(0.0);
                fbm7((pos - vec3(0.0, 0.5 * kMaxElevation, 0.0)) * 0.15 * vec3(1.0, 0.2, 1.0), 1.92, 0.5, 0.5, nder);
                nor = normalize(tnor + 0.8 * (1.0 - abs(tnor.y)) * 0.8 * nder);

                col = kTerrainBaseColor * 0.85;

                col = 1.0 * mix(col, kTerrainSlopeColor * 0.2, smoothstep(0.7, 0.9, nor.y));
                float dif = clamp(dot(nor, gSunDir), 0.0, 1.0);
                dif *= sha1;
                dif *= sha2;

                float bac = clamp(dot(normalize(vec3(-gSunDir.x, 0.0, -gSunDir.z)), nor), 0.0, 1.0);
                float foc = clamp((pos.y / 2.0 - 180.0) / 130.0, 0.0, 1.0);
                float dom = clamp(0.5 + 0.5 * nor.y, 0.0, 1.0);
                vec3 lin = 1.0 * kTerrainAmbientStrength * mix(kTerrainAmbientColor, kTerrainSkyAmbient * 3.0, dom) * foc;
                lin += 1.0 * kTerrainSunStrength * kSunIntensity * kTerrainSunColor * dif;
                lin += 1.0 * kTerrainBackStrength * kTerrainBackColor * bac * foc;
                speC = vec3(kTerrainSpecStrength) * dif * smoothstep(20.0, 0.0, abs(pos.y / 2.0 - 310.0) - 20.0);

                col *= lin;
            }
            // grass
            else if(obj == 2) {
                vec3 gnor = grassNormal(pos, resT);
                nor = normalize(gnor + 1.2 * tnor);

                // lighting
                float occ = clamp(hei, 0.0, 1.0);
                float dif = clamp(0.15 + 0.85 * dot(nor, gSunDir), 0.0, 1.0);
                dif *= sha1;
                if(dif > 0.0001) {
                    float a = clamp(0.4 + 0.6 * dot(tnor, gSunDir), 0.0, 1.0);
                    a = a * a;
                    a *= occ;
                    a *= 0.55;
                    a *= smoothstep(40.0, 160.0, resT);
                    float shaG = grassShadow(pos + gSunDir * 0.08, gSunDir);
                    dif *= a + (1.0 - a) * shaG;
                }
                float dom = clamp(0.5 + 0.5 * nor.y, 0.0, 1.0);
                float bac = clamp(0.5 + 0.5 * dot(normalize(vec3(-gSunDir.x, 0.0, -gSunDir.z)), nor), 0.0, 1.0);
                float fre = clamp(1.0 + dot(nor, rayDirection), 0.0, 1.0);

                // lights
                vec3 lin = kGrassSunStrength * kSunIntensity * kGrassSunColor * dif * occ * (2.2 - 1.3 * smoothstep(0.0, 100.0, resT));
                lin += kGrassAmbientStrength * mix(kGrassAmbientLo, kGrassAmbientHi, dom * occ);
                lin += kGrassBackStrength * kGrassBackColor * bac * occ;
                lin += kGrassFreStrength * kGrassFreColor * pow(fre, 5.0) * occ * (1.0 - smoothstep(80.0, 180.0, resT));
                speC = dif * kGrassSpecColor * 0.7;

                // material
                float patchTone = hash1D(floor(pos.xz * 0.04));
                float tone = mix(patchTone, mid, 0.7);

                vec3 dryCol = mix(kGrassDryLo, kGrassDryHi, mid);
                vec3 midCol = mix(kGrassMidLo, kGrassMidHi, mid);
                vec3 tipCol = mix(kGrassTipLo, kGrassTipHi, mid);
                col = mix(dryCol, midCol, smoothstep(0.1, 0.55, hei));
                col = mix(col, tipCol, smoothstep(0.45, 1.0, hei));

                // slight per-blade warmth and soft patch variation
                col *= mix(vec3(0.93, 1.02, 0.88), vec3(1.06, 0.97, 0.82), tone);
                col *= 0.90 + 0.20 * mid;

                col *= kGrassAlbedoScale + 0.12 * smoothstep(0.0, 0.5, mid);
                col *= lin;
            }
            // wildrose
            else if(obj == 3) {
                rosesMap(pos, resT, rHei, rMat, rDis);
                vec3 rnor = roseNormal(pos, resT);
                nor = normalize(rnor + 1.2 * tnor);

                float occ = clamp(rHei, 0.0, 1.0);
                float dif = clamp(0.15 + 0.85 * dot(nor, gSunDir), 0.0, 1.0);
                dif *= sha1;
                if(dif > 0.0001) {
                    float a = clamp(0.4 + 0.6 * dot(tnor, gSunDir), 0.0, 1.0);
                    a = a * a;
                    a *= occ;
                    a *= 0.55;
                    a *= smoothstep(40.0, 160.0, resT);
                    float shaG = grassShadow(pos + gSunDir * 0.08, gSunDir);
                    dif *= a + (1.0 - a) * shaG;
                }
                float dom = clamp(0.5 + 0.5 * nor.y, 0.0, 1.0);
                float bac = clamp(0.5 + 0.5 * dot(normalize(vec3(-gSunDir.x, 0.0, -gSunDir.z)), nor), 0.0, 1.0);
                float bloom = smoothstep(0.72, 0.95, rHei);

                vec3 lin = kGrassSunStrength * kSunIntensity * kGrassSunColor * dif * occ * 1.0;
                lin += kGrassAmbientStrength * mix(kRoseAmbientLo, kRoseAmbientHi, dom * occ);
                lin += kGrassBackStrength * vec3(1.2, 0.5, 0.6) * bac * bloom;
                speC = dif * vec3(1.1, 0.85, 0.95) * (0.5 + 0.5 * bloom);

                vec3 stemCol = mix(vec3(0.12, 0.22, 0.08), vec3(0.18, 0.32, 0.12), rHei);
                vec3 bloomCol = mix(vec3(0.75, 0.25, 0.45), vec3(0.95, 0.55, 0.65), rMat);
                col = mix(stemCol, bloomCol, bloom);
                col *= lin;
            }

            // spec
            vec3 ref = reflect(rayDirection, nor);
            float fre = clamp(1.0 + dot(nor, rayDirection), 0.0, 1.0);
            float spe = kSpecStrength * kSunIntensity * pow(clamp(dot(ref, gSunDir), 0.0, 1.0), kSpecPower) * (0.05 + 0.95 * pow(fre, 5.0));
            col += spe * speC;

            col = fog(col, resT);
        }
    }

    // clouds
    {
        vec4 res = renderClouds(rayOrigin, rayDirection, 0.0, resT, resT, fragCoord, gSunDir);
        col = col * (1.0 - res.w) + res.xyz;
    }

    // final

    // sun glare    
    float sun = clamp(dot(gSunDir, rayDirection), 0.0, 1.0);
    col += kFinalSunGlare * kSunIntensity * kFinalSunGlareColor * pow(sun, 4.0);

    // gamma
    col = pow(clamp(col * kPostExposure + kPostOffset, 0.0, 1.0), vec3(0.4545));

    // contrast
    col = col * col * (3.0 - 2.0 * col);            

    // color grade    
    col = pow(col, kGradeGamma);
    col *= kGradeTint;
    col.z = col.z + kGradeBlueBias;

    fragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
}
