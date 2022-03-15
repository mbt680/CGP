package gltest;

import org.joml.Vector3f;

public class Light {
    public Vector3f position = new Vector3f(-1, 2, 50);
    public int shininess = 1;

    public final Vector3f ambient = new Vector3f( 0.7f, 0.7f, 0.7f  );
    public final Vector3f diffuse = new Vector3f( 0.08f, 0.08f, 0.08f );
    public final Vector3f specular = new Vector3f( 0.8f, 0.8f, 0.8f );

    public int vertexLevels = 8;
    public int fragLevels = 8;
}
