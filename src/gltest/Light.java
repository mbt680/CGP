package gltest;

import org.joml.Vector3f;

public class Light {
    public Vector3f position = new Vector3f(0, 0, -1);
    public int shininess = 1;

    public final Vector3f ambient = new Vector3f( 0.5f, 0.5f, 0.5f  );
    public final Vector3f diffuse = new Vector3f( 0.5f, 0.5f, 0.5f );
    public final Vector3f specular = new Vector3f( 0.5f, 0.5f, 0.5f );

    public int vertexLevels = 32;
    public int fragLevels = 18;
}
