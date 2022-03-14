package gltest;

import org.joml.Vector3f;

public class Light {
    final Vector3f position = new Vector3f(-10000, 25000, 500000);
    float shininess;

    private final Vector3f lightAmbient = new Vector3f( 0.2f, 0.2f, 0.2f );
    private final Vector3f lightDiffuse = new Vector3f( 1.0f, 0.8f, 0.0f );
    private final Vector3f lightSpecular = new Vector3f( 1.0f, 1.0f, 1.0f );

    private final Vector3f materialAmbient = new Vector3f( 0.33f, 0.22f, 0.03f );
    private final Vector3f materialDiffuse = new Vector3f( 0.78f, 0.57f, 0.11f);
    private final Vector3f materialSpecular = new Vector3f( 0.99f, 0.91f, 0.81f );

    public final Vector3f ambient = lightAmbient.cross(materialAmbient);
    public final Vector3f diffuse = lightDiffuse.cross(materialDiffuse);
    public final Vector3f specular = lightSpecular.cross(materialSpecular);

    private static final float TRANSLATE_DELTA = 10000f;
    public void moveForward() {
        position.z -= TRANSLATE_DELTA;
    }
    
    public void moveBack() {
        position.z += TRANSLATE_DELTA;
    }
    
    public void moveLeft() {	
        position.x -= TRANSLATE_DELTA;
    }
    
    public void moveRight() {
        position.x += TRANSLATE_DELTA;
    }
    
    public void moveUp() { 
        position.y += TRANSLATE_DELTA; 
    }
    
    public void moveDown() { 
        position.y -= TRANSLATE_DELTA; 
    }
}
