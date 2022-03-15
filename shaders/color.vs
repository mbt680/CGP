#version 330 core

layout (location = 0) in vec3 aPos; // position variable w attribute position 0
layout (location = 1) in vec3 aNorm; // normal vector

uniform mat4 viewMatrix;
uniform vec3 ourColor; // specify a color output to the fragment shader

// Lighting
uniform vec3 lightPos;
uniform vec3 ambientLight, specularLight, diffuseLight;
uniform int shininess;

out vec3 ltColor;
out vec3 ptColor;
out vec3 ptNorm;

const int numColors = 17;

void main()
{
    gl_Position = viewMatrix * vec4(aPos, 1.0); // convert aPos to homogoneous coordinates
    ptColor = ourColor;
    ptNorm = vec3(aNorm);

    // Lighting effects
    int shininess = 1;

    vec3 ambient = ambientLight;
    vec3 lightNorm = normalize(lightPos.xyz - aPos.xyz);
    float d = max(dot(lightNorm, ptNorm), 0);
    vec3 diffuse = floor(diffuseLight * d * numColors) / numColors;

    // TODO: no effect from specular light
    vec3 halfway = normalize(lightNorm - normalize(aPos));
    float s = max(-pow(max(dot(ptNorm, halfway), 0.0), shininess), 0.0);
    vec3 specular = s * specularLight;

    ltColor = ambient.xyz + diffuse.xyz + specular.xyz;
}