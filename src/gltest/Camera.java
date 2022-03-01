package gltest;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    Matrix4f viewMatrix = new Matrix4f();
    private int scale = 1;
    private Vector3f translation = new Vector3f();
    private Vector3f rotation = new Vector3f();

    void moveForward() {
        ++translation.z;
    }
    
    void moveBack() {
        --translation.z;
    }
    
    void moveLeft() {	
        --translation.x;
    }
    
    void moveRight() {
        ++translation.x;
    }
    
    void moveUp() { 
        ++translation.y; 
    }
    
    void moveDown() { 
        --translation.y; 
    }
    
    void rotateLeft() { 
        rotation.y = (float) ((rotation.y + Math.PI/8) % (2 * Math.PI)); 
    }
    
    void rotateRight() { 
        rotation.y = (float) ((rotation.y - Math.PI/8) % (2 * Math.PI)); 
    }
    
    void rotateUp() { 
        rotation.x = (float) ((rotation.x + Math.PI/8) % (2 * Math.PI)); 
    }
    
    void rotateDown() { 
        rotation.x = (float) ((rotation.x - Math.PI/8) % (2 * Math.PI));
    }
    
    void changeScale(int change) {
        scale += change * 10;
        scale = Math.min(Math.max(scale, 400), 800);
    }

    public void setViewMatrix(float width, float height) {
        viewMatrix.identity();
        
        // add perspective
        viewMatrix.perspective(90f, width / height, 0.1f, scale);

        //scale to zoom, translate to move, and rotate on x/y axis to tilt camera
        Vector3f translationInverse = new Vector3f();
        translationInverse.x = -translation.x;
        translationInverse.y = -translation.y;
        translationInverse.z = -translation.z;

        viewMatrix.scale(scale).
                    translate(translationInverse).
                    rotateX(-rotation.x).
                    rotateY(-rotation.y);
    }
}
