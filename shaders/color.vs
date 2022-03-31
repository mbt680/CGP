#version 330 core

layout (location = 0) in vec3 aPos; // position variable w attribute position 0
layout (location = 1) in vec3 aNorm; // normal vector
layout (location = 2) in vec3 aTex; //texture coordinates

uniform mat4 viewMatrix;
uniform vec3 ourColor; // specify a color output to the fragment shader

// Lighting
uniform vec3 lightPos;
uniform vec3 ambientLight, specularLight, diffuseLight;
uniform int shininess;
uniform int vertexLevels;
uniform int fragLevels;
uniform bool applyLighting;
uniform bool applyRimLighting;

//in  vec2 textureCoord;

out float ndotv;
out float t_kr;
out float t_dwkr;

out vec3 ptColor;
out vec3 ptNorm;
out vec2 ptTex;
flat out int levels;

out vec3 ptLightNorm, ptAmbientLight, ptSpecularLight, ptDiffuseLight, ptApplyLight;

void main()
{
    // Calculate position based on view matrix
    gl_Position = viewMatrix * vec4(aPos, 1.0); // convert aPos to homogoneous coordinates

    vec3 pdir1 = aTex;
	vec3 pdir2 = aTex;
	float curv1 = 1;
	float curv2 = 1;
	vec4 dcurv = vec4(0,0,0,0);

    ndotv = (1.0f / length(aPos)) * dot(aNorm, aPos);

    if(!(ndotv < 0.0f)){
		vec3 w = normalize(aPos - aNorm * dot(aPos, aNorm));
  		float u = dot(w, pdir1);
  		float v = dot(w, pdir2);
  		float u2 = u*u;
    	float v2 = v*v;
  		t_kr = (curv1*u2) + (curv2*v2);
  		float uv = u*v;
  		float dwII = (u2*u*dcurv.x) + (3.0*u*uv*dcurv.y) + (3.0*uv*v*dcurv.z) + (v*v2*dcurv.w);
  		t_dwkr = dwII + 2.0 * curv1 * curv2 * ndotv/sqrt((1.0 - pow(ndotv, 2.0)));
  	}

    // Select initial color from texture, currently just samples aPos for all points
    if (ptColor == vec3(0,0,0)) {
        ptColor = ourColor;
    }
    ptColor = vec3(0.0, 0.0, 0.0);
    if (aTex.y < 0) {
        ptTex = vec2(aTex.x, -aTex.y);
    } else {
        ptTex = vec2(aTex.x, 1.0 - aTex.y);
    }

    // uncomment me to test if aTex, aPos, and aNorm are the same. Paints the whole model red
    // if (aTex == aPos && aTex == aNorm) {
    //     ptColor = vec3(1, 0, 0);
    // }

    // Set up lighting values for fragment shader
    ptNorm = vec3(aNorm);
    levels = fragLevels;
    ptLightNorm = normalize(viewMatrix * vec4(lightPos, 0)).xyz;
    ptAmbientLight = ambientLight; 
    ptSpecularLight = specularLight;
    ptDiffuseLight = diffuseLight;
    ptApplyLight = vec3(1, applyLighting, applyRimLighting);

}