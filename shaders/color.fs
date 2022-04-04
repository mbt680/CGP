#version 330 core

out vec4 FragColor;

uniform bool applySolidColour;

in vec3 ptColor;
in vec2 ptTex;
in vec3 ptNorm;

flat in int lightLevels, textureLevels;

in vec3 ptLightNorm, ptAmbientLight, ptSpecularLight, ptDiffuseLight, ptApplyLight;

uniform sampler2D ourTexture;

vec4 applyCelShading(vec4 colour, int levels);
vec4 getLightingColor();

void main()
{
    vec4 texColor = texture(ourTexture, ptTex);
    // variables that do nothing can be optimized out in GLSL by default
    if (applySolidColour)
        FragColor = vec4(ptColor, 0);
    else
        FragColor = applyCelShading(getLightingColor(), lightLevels) * applyCelShading(texColor, textureLevels);
}

vec4 applyCelShading(vec4 colour, int levels) {
    return round(colour * levels) / levels;
}


// Lighting effects
vec4 getLightingColor() 
{ 
    vec4 ambient, diffuse, specular, rim, ltColor;
    float d = dot(ptLightNorm, ptNorm);

    if (ptApplyLight.y > 0) {
        ambient = vec4(ptAmbientLight, 1);
        // add diffuse lighting
        diffuse = vec4(ptDiffuseLight, 1).xyzw * d;
        // add specualar lighting back, small effect with corrected norms
        vec3 halfway = normalize(ptLightNorm - ptNorm);
        float s = max(pow(max(dot(ptNorm, halfway), 0.0), .5), 0.0);
        specular = vec4(ptSpecularLight * s, 1);
    } else {
        ambient = vec4(1, 1, 1, 1);
    }

    // add rim lighting around edge of shadow
    if (ptApplyLight.z > 0) {
        if (d > 0 && d < 0.16)
            rim = vec4(0.4, 0.4, 0.4, 1);
    }

    // Apply cel shading
    ltColor = ambient.xyzw + specular.xyzw + diffuse.xyzw + rim.xyzw;

    return ltColor;
}

