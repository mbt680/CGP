package gltest;

import java.nio.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// Shows how to render with shaders as a separate program
public class GLWindow5 {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;


    private static final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_Q) {
                glfwSetWindowShouldClose(window, true);
            }
        }
    };

    private static final GLFWFramebufferSizeCallback bufferSizeCallback = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            glfwSetWindowSize(window, width, height);
        }   
    };

    public static void main(String[] args) {
        long window;

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = glfwCreateWindow(WIDTH, HEIGHT, "Basic example", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new IllegalStateException("Unable to create window");
        }
        
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSetKeyCallback(window, keyCallback);
        glfwSetFramebufferSizeCallback(window, bufferSizeCallback);

        // Get the current directory
        String dir = System.getProperty("user.dir");

        // Set up the shader program
        Shader shader = new Shader(dir + "/shaders/shader.vs", dir + "/shaders/shader.fs");

        int vaoId;
        int vboId;
        float vertices[] = {
            // first three are vertex, second are color
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.0f,  0.0f, 1.0f, 0.0f,
            0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f
        };
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();

        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        // vertex attribute 0 takes 3 floats, which are non normalized, skipping every 24 bytes starting at 0
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 24, 0);
        glEnableVertexAttribArray(0);

        // vertex attribute 1 takes 3 floats, which are non normalized, skipping every 24 bytes starting at 12
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 12);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        while (!glfwWindowShouldClose(window)) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            shader.use();
            glBindVertexArray(vaoId);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        shader.delete();
        glfwTerminate();
    }
}
