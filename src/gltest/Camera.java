package gltest;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    Matrix4f viewMatrix = new Matrix4f();
    private int scale = 10;
    private Vector3f translation = new Vector3f(0,40,100);
    private Vector3f rotation = new Vector3f();

    Camera() {}

    // amount to move by each frame
    private static final float TRANSLATE_DELTA = 0.5f;

    void moveForward() {
        translation.z -= TRANSLATE_DELTA;
    }
    
    void moveBack() {
        translation.z += TRANSLATE_DELTA;
    }
    
    void moveLeft() {	
        translation.x -= TRANSLATE_DELTA;
    }
    
    void moveRight() {
        translation.x += TRANSLATE_DELTA;
    }
    
    void moveUp() { 
        translation.y += TRANSLATE_DELTA; 
    }
    
    void moveDown() { 
        translation.y -= TRANSLATE_DELTA; 
    }
    
    void rotateLeft() { 
        rotation.y = (float) ((rotation.y + Math.PI/128) % (2 * Math.PI)); 
    }
    
    void rotateRight() { 
        rotation.y = (float) ((rotation.y - Math.PI/128) % (2 * Math.PI)); 
    }
    
    void rotateUp() { 
        rotation.x = (float) ((rotation.x + Math.PI/128) % (2 * Math.PI)); 
    }
    
    void rotateDown() { 
        rotation.x = (float) ((rotation.x - Math.PI/128) % (2 * Math.PI));
    }
    
    private final int minScale = 1;
    private final int maxScale = 100;
    void changeScale(int change) {
        scale = Math.min(Math.max(scale + change, minScale), maxScale);
    }

    public void setViewMatrix(float width, float height) {
        viewMatrix.identity();
        
        // add perspective with scale
        float scaledFovy = (maxScale - scale + 1) * (float) Math.PI/(maxScale + 1);
        viewMatrix.perspective(scaledFovy * 0.7f, width / height, 0.1f, 1000f);

        //translate to move, and rotate on x/y axis to tilt camera
        Vector3f translationInverse = new Vector3f();
        translationInverse.x = -translation.x;
        translationInverse.y = -translation.y;
        translationInverse.z = -translation.z;

        viewMatrix.translate(translationInverse).
                    rotateX(-rotation.x).
                    rotateY(-rotation.y);
    }
}
