package gltest;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL33.*;

/**
 * Shader class for creating and using shader programs
 */
public class Shader {
    public int id;
    private Map<String, Pair<Integer, Vector3f>> persistentUniform;
    private Map<String, Integer> uniforms;

    /**
     * Constructor
     * @param vertexPath Path to vertex shader file
     * @param fragmentPath Path to fragment shader file
     */
    public Shader(String vertexPath, String fragmentPath) {
        persistentUniform = new HashMap<>();
        uniforms = new HashMap<>();
    
        String vertexCode = "";
        String fragmentCode = "";
        Scanner vertexScanner, fragmentScanner;
        
        try {
            vertexScanner = new Scanner(new FileInputStream(vertexPath));
            fragmentScanner = new Scanner(new FileInputStream(fragmentPath));

            StringBuilder vertexBuilder = new StringBuilder();
            while (vertexScanner.hasNextLine()) {
                vertexBuilder.append(vertexScanner.nextLine());
                vertexBuilder.append('\n');
            }
            vertexScanner.close();
            vertexCode = vertexBuilder.toString();

            StringBuilder fragmentBuilder = new StringBuilder();
            while (fragmentScanner.hasNextLine()) {
                fragmentBuilder.append(fragmentScanner.nextLine());
                fragmentBuilder.append('\n');
            }
            fragmentScanner.close();
            fragmentCode = fragmentBuilder.toString();
            
        } catch(Exception ie) {
            System.out.println("Error, shader not successfully read");
            ie.printStackTrace();
        }

        int vertex, fragment;

        // vertex shader
        vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex, vertexCode);
        glCompileShader(vertex);
        checkCompileStatus(vertex, GL_VERTEX_SHADER);

        // fragment 
        fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment, fragmentCode);
        glCompileShader(fragment);
        checkCompileStatus(fragment, GL_FRAGMENT_SHADER);

        // program
        id = glCreateProgram();
        glAttachShader(id, vertex);
        glAttachShader(id, fragment);
        glLinkProgram(id);
        checkLinkStatus(id, GL_LINK_STATUS);

        // delete the shaders as they're linked into our program now and now longer necessary
        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    private void checkCompileStatus(int shader, int type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String message = glGetShaderInfoLog(shader);

            if (type == GL_VERTEX_SHADER) {
                System.out.println("Error compiling vertex shader " + message);
            } else if (type == GL_FRAGMENT_SHADER) {
                System.out.println("Error compiling fragment shader " + message);
            } 
        }
    }

    private void checkLinkStatus(int object, int type) {
        if (glGetProgrami(object, type) == GL_FALSE) {
            String message = glGetProgramInfoLog(id);
            System.out.println("Error linking shader " + message);
        }
    }

    /**
     * use this shader program
     */
    public void use() {
        glUseProgram(id);

        for (String key : persistentUniform.keySet()) {
            Pair<Integer, Vector3f> uniform = persistentUniform.get(key);
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer fb = stack.mallocFloat(3);
                uniform.getValue().get(fb);
                glUniform3fv(uniform.getKey(),  fb);
            }
        }
    }

    /**
     * setBool set a boolean attribute ref'd to by key to value
     * @param name name of the attribute in shader
     * @param value value to set attribute to
     */
    public void setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(id, name), value?1:0);
    }

    /**
     * setInt changes name attribute to value
     * @param name Name of attribute to set
     * @param value Value to change attribute to
     */
    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(id, name), value);
    }

    /**
     * setFloat changes name attribute to value
     * @param name Name of attribute to set
     * @param value Value to change attribute to
     */
    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(id, name), value);
    }

    /**
     * setConstantUniform3fv sets a vector3 for the shader, which is set each time when the program
     * is used.
     * @param uniformName Variable to set
     * @param value Value of variable
     * @throws Exception If uniformName is not valid 
     */
    public void setConstantUniform3fv(String uniformName, Vector3f value) throws Exception {
        // System.out.printf("Setting %s in shader %d\n", uniformName, id);
        int uniformLocation = glGetUniformLocation(id, uniformName);
        if (uniformLocation < 0) {  
            //throw new Exception("Could not find uniform: " + uniformName); 
        } else {
            persistentUniform.put(uniformName, new Pair<>(uniformLocation, value));
        }
    }

    /**
     * Clean up this object
     */
    public void delete() {
        glDeleteProgram(id);
    }

    /**
     * createUniform for the shader, and store its location
     * @param uniformName Name of the uniform in the shader
     * @throws Exception If name is not valid
     */
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(id, uniformName);
        if (uniformLocation < 0) {  
        //    throw new Exception("Could not find uniform: " + uniformName); 
        }
        uniforms.put(uniformName, uniformLocation);
    }

    /**
     * setUniform variable to value (this method only works when the uniform is a Matrix4f.)
     * TODO: Add more setUniform methods for other data types
     * @param <T>
     * @param uniformName Name of the uniform to set
     * @param value Value to set the uniform to
     */

    public <T> void setUniform(String uniformName, T value) {
        // Dump the matrix into a float bufferpublic static <E> 
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int uniformLocation = glGetUniformLocation(id, uniformName);
            if (value instanceof Matrix4f) {
                FloatBuffer fb = stack.mallocFloat(16);
                Matrix4f matrix = (Matrix4f) value;
                matrix.get(fb);
                glUniformMatrix4fv(uniformLocation, false, fb);
            } else if (value instanceof Vector3f) {
                Vector3f vector = (Vector3f) value;
                glUniform3f(uniformLocation, vector.x, vector.y, vector.z);
            }
        }
    }
}
