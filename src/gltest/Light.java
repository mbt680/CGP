package gltest;

import org.joml.Vector3f;

public class Light {
    final Vector3f position = new Vector3f(-10000, 25000, 500000);
    public int shininess = 1;

    public final Vector3f ambient = new Vector3f( 0.4f, 0.2f, 0.0f );
    public final Vector3f diffuse = new Vector3f( 0.05f, 0.06f, 0.05f );
    public final Vector3f specular = new Vector3f( 0.8f, 0.8f, 0.8f );

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
