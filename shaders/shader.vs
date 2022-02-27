#version 330 core
layout (location = 0) in vec3 aPos; // position variable w attribute position 0
layout (location = 1) in vec3 aColor; // the colour variable with attrib pos 1
out vec3 ourColor; // specify a color output to the fragment shader

void main()
{
    gl_Position = vec4(aPos, 1.0); // convert aPos to homogoneous coordinates
    ourColor = vec3(aColor); // set the output color to the input color
}
