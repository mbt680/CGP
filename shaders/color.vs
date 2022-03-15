#version 330 core

layout (location = 0) in vec3 aPos; // position variable w attribute position 0
layout (location = 1) in vec3 aNorm; // normal vector

uniform mat4 viewMatrix;
uniform vec3 ourColor; // specify a color output to the fragment shader

// Lighting
uniform vec3 lightPos;
uniform vec3 ambientLight, specularLight, diffuseLight;
uniform int shininess;
uniform int vertexLevels;
uniform int fragLevels;
uniform bool applyLighting;

//in  vec2 textureCoord;

out vec4 ltColor;
out vec3 ptColor;
out vec3 ptNorm;
out int levels;

uniform sampler2D ourTexture;

void main()
{
    gl_Position = viewMatrix * vec4(aPos, 1.0); // convert aPos to homogoneous coordinates
    ptNorm = vec3(aNorm);
    levels = fragLevels;

    // Select initial color from texture, currently just samples aPos for all points
    ptColor = texture(ourTexture, aPos.xy).xyz;

    // Lighting effects
    if (applyLighting) {
        int shininess = 1;

        vec4 ambient = vec4(ambientLight, 0);
        vec3 lightNorm = normalize(lightPos.xyz);
        float d = max(dot(lightNorm, ptNorm), 0);
        vec4 diffuse = round(vec4(diffuseLight, 0).xyzw * d * vertexLevels) / vertexLevels ;
        //vec4 diffuse = vec4(diffuseLight, 0)*d;

        // TODO: no effect from specular light
        vec3 halfway = normalize(lightNorm - normalize(aPos));
        float s = max(-pow(max(dot(ptNorm, halfway), 0.0), shininess), 0.0);
        vec4 specular = vec4(specularLight * s, 0);

        ltColor = ambient.xyzw + diffuse.xyzw + specular.xyzw;
    }
    else
        ltColor = vec4(1,1,1,1);
}