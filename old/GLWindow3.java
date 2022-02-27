package gltest;

import java.nio.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// Shows how to render with EBO
public class GLWindow3 {
    private static final String vertexShaderSource = "#version 330 core\n"
        +"layout (location = 0) in vec3 aPos;\n"
        +"void main()\n"
        +"{\n"
        +"  gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n"
        +"}";
    private static final String fragmentShaderSource = "#version 330 core\n"
        +"out vec4 FragColor;\n"
        +"void main()\n"
        +"{\n"
        +"  FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n"
        +"}";
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

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE){
            String message = glGetShaderInfoLog(vertexShader);
            System.err.println("Compiling vertex shader failed with message: " + message);
            System.exit(1);
        }

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            String message = glGetShaderInfoLog(fragmentShader);
            System.err.println("Compiling fragment shader failed with message: " + message);
            System.exit(1);
        }

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            String message = glGetProgramInfoLog(shaderProgram);
            System.err.println("Linking shaders to program failed with message: " + message);
            System.exit(1);
        }
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        int vaoId;
        int vboId;
        int eboId;
        float[] vertices = {
             0.5f,  0.5f, 0.0f, // top right
             0.5f, -0.5f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f, // bottom left
        };
        int[] indices = {
            0, 1, 3,    // first triangle
            1, 2, 3     // second
        };
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();

        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();
        eboId = glGenBuffers();

        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        while (!glfwWindowShouldClose(window)) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glUseProgram(shaderProgram);
            glBindVertexArray(vaoId);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            // glDrawArrays(GL_TRIANGLES, 0, 6);
            
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteBuffers(eboId);
        glDeleteProgram(shaderProgram);
        glfwTerminate();
    }
}
