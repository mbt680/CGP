package gltest;

import org.joml.Vector3f;

public class Light {
    public Vector3f position = new Vector3f(0, 15, -1);
    public int shininess = 1;

    public final Vector3f ambient = new Vector3f( 0.65f, 0.65f, 0.65f  );
    public final Vector3f diffuse = new Vector3f( 0.7f, 0.7f, 0.7f );
    public final Vector3f specular = new Vector3f( 0.5f, 0.5f, 0.5f );

    public int lightingLevels = 32;
    public int textureLevels = 6;
}
