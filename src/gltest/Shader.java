package gltest;

import java.io.*;
import java.util.Scanner;

import static org.lwjgl.opengl.GL33.*;

/**
 * Shader class for creating and using shader programs
 */
public class Shader {
    private int id;

    /**
     * Constructor
     * @param vertexPath Path to vertex shader file
     * @param fragmentPath Path to fragment shader file
     */
    public Shader(String vertexPath, String fragmentPath) {
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
     * Clean up this object
     */
    public void delete() {
        glDeleteProgram(id);
    }
}
