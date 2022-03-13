package gltest;

import org.joml.Vector3f;

public class Light {
    final Vector3f position = new Vector3f(20, 20, 20);
    float shininess;

    Vector3f lightAmbient = new Vector3f( 0.2f, 0.2f, 0.2f );
    Vector3f lightDiffuse = new Vector3f( 1.0f, 1.0f, 1.0f );
    Vector3f lightSpecular = new Vector3f( 1.0f, 1.0f, 1.0f );

    Vector3f materialAmbient = new Vector3f( 1f, 0.933f, 0.345f );
    Vector3f materialDiffuse = new Vector3f( 1f, 0.933f, 0.345f );
    Vector3f materialSpecular = new Vector3f( 1f, 0.933f, 0.345f );

    Vector3f ambient = lightAmbient.cross(materialAmbient);
    Vector3f diffuse = lightDiffuse.cross(materialDiffuse);
    Vector3f specular = lightSpecular.cross(materialSpecular);
}
