#version 330 core

layout (location = 0) in vec3 aPos; // position variable w attribute position 0
layout (location = 1) in vec3 aNorm; // normal vector

uniform mat4 viewMatrix;
uniform vec3 ourColor; // specify a color output to the fragment shader

// Lighting
uniform vec3 lightPos;
uniform vec3 ambientLight, specularLight, diffuseLight;

out vec3 ptColor;
out vec3 ptNorm;

void main()
{
    gl_Position = viewMatrix * vec4(aPos, 1.0); // convert aPos to homogoneous coordinates
    ptNorm = vec3(aNorm);
    float shininess = 1;
    
    vec3 N = ptNorm;
    vec3 L = normalize(lightPos.xyz - gl_Position.xyz);
    vec3 E = -normalize(aPos);
    vec3 H = normalize(L+E);

    float d = max(dot(L, N), 0.0);
    float s = pow(max(dot(N, H), 0.0), shininess);
    vec3 ambient = ambientLight;
    vec3 diffuse = d*diffuseLight;
    vec3 specular = max(pow(max(dot(N, H), 0.0), shininess) * specularLight, 0.0);
    ptColor = vec3( ambient + diffuse + specular ).xyz;
   
    // ptColor = N;
}