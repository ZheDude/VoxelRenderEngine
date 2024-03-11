#version 400 core

in vec2 fragTextureCoords;

out vec4 outColour;

uniform sampler2D textureSampler;

void main(){
    outColour = texture(textureSampler, fragTextureCoords);
//     outColour = vec4(1.0, 1.0, 1.0, 0.2);
}