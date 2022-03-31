#version 330 core

out vec4 FragColor;

in vec3 ptColor;
in vec2 ptTex;
in vec3 ptNorm;
flat in int levels;

in float ndotv;
in float t_kr;
in float t_dwkr;

//in float fz;
//in float c_limit;
//in float sc_limit;
//in float dwkr_limit;

in vec3 ptLightNorm, ptAmbientLight, ptSpecularLight, ptDiffuseLight, ptApplyLight;

uniform sampler2D ourTexture;

vec4 applyCelShading(vec4 colour, int levels);
vec4 getLightingColor();

void main()
{
    vec4 texColor = texture(ourTexture, ptTex);

	vec4 lineColor = vec4(1.0, 1.0, 1.0, 1.0); 

    float fz = 0.1;
    float c_limit = 1.0;
    float sc_limit = 1.0;
    float dwkr_limit = 0.05;

    // use feature size
	float kr = fz*abs(t_kr); // absolute value to use it in limits
	float dwkr = fz*fz*t_dwkr; // two times fz because derivative
	float dwkr2 = (dwkr-dwkr*pow(ndotv, 2.0));

	// compute limits
	float contour_limit = c_limit*(pow(ndotv, 2.0)/kr);
	sc_limit *= (kr/dwkr2);
	// contours
	if(contour_limit<1.0)
	    {lineColor.xyz = min(lineColor.xyz, vec3(contour_limit, contour_limit, contour_limit));}
	// suggestive contours
	else if((sc_limit<1.0) && dwkr2>dwkr_limit)
	    {lineColor.xyz = min(lineColor.xyz, vec3(sc_limit, sc_limit, sc_limit));}

    // variables that do nothing can be optimized out in GLSL by default
    FragColor = min (lineColor, applyCelShading(getLightingColor(), levels) * texColor + vec4(ptColor,0)*0);
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

