#version 330 core

out vec4 FragColor;

in vec3 ptColor;
in vec2 ptTex;
in vec3 ptNorm;
flat in int levels;

in vec3 ptLightNorm, ptAmbientLight, ptSpecularLight, ptDiffuseLight, ptApplyLight;

uniform sampler2D ourTexture;

vec4 applyCelShading(vec4 colour, int levels);
vec4 getLightingColor();

void main()
{
    vec4 texColor = texture(ourTexture, ptTex);
    // variables that do nothing can be optimized out in GLSL by default
    FragColor = applyCelShading(getLightingColor(), levels) * texColor + vec4(ptColor,0)*0;
}

vec4 applyCelShading(vec4 colour, int levels) {
    return round(colour * levels) / levels;
}


// Lighting effects
vec4 getLightingColor() 
{ 
    vec4 ambient, diffuse, specular, ltColor;

    ambient = vec4(ptAmbientLight, 0);

    if (ptApplyLight.y > 0) {
        // add diffuse lighting
        float d = dot(ptLightNorm, ptNorm);
        diffuse = vec4(ptDiffuseLight, 0).xyzw * d;
    }

    // add rim lighting around edge of shadow
    if (ptApplyLight.z > 0) {
        float threshold = 0.26;
        float s = 1 - dot(ptNorm, ptLightNorm);
        specular = vec4((s < 0 ? 0 : s) * ptSpecularLight.xyz, 0);
        // Dont draw on parts that should be completely in shadow
        if (specular.x > threshold)
            specular = vec4(0,0,0,0);
    }

    // Apply cel shading
    specular = applyCelShading(specular, 2);
    diffuse = round(diffuse * levels) / levels;
    ltColor = ambient.xyzw + specular.xyzw + diffuse.xyzw;

    // Reduce extreme black/white lighting values so color preserved
    ltColor = max(ltColor.xyzw, vec4(0.2,0.2,0.2,0));
    ltColor = min(ltColor.xyzw, vec4(1.1,1.1,1.1,1));

    return ltColor;
}

