package gltest;

import java.nio.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// Shows how switch between VAO, and to switch between programs.
public class GLWindow4 {
    private static final String vertexShaderSource = "#version 330 core\n"
        +"layout (location = 0) in vec3 aPos;\n"
        +"void main()\n"
        +"{\n"
        +"  gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n"
        +"}";
    private static final String fragmentShaderSource1 = "#version 330 core\n"
        +"out vec4 FragColor;\n"
        +"void main()\n"
        +"{\n"
        +"  FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n"
        +"}";
    private static final String fragmentShaderSource2 = "#version 330 core\n"
    +"out vec4 FragColor;\n"
    +"void main()\n"
    +"{\n"
    +"  FragColor = vec4(1.0f, 0.0f, 0.0f, 1.0f);\n"
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

        int fragmentShader1 = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader1, fragmentShaderSource1);
        glCompileShader(fragmentShader1);

        if (glGetShaderi(fragmentShader1, GL_COMPILE_STATUS) == GL_FALSE) {
            String message = glGetShaderInfoLog(fragmentShader1);
            System.err.println("Compiling fragment shader failed with message: " + message);
            System.exit(1);
        }

        int shaderProgram1 = glCreateProgram();
        glAttachShader(shaderProgram1, vertexShader);
        glAttachShader(shaderProgram1, fragmentShader1);
        glLinkProgram(shaderProgram1);

        if (glGetProgrami(shaderProgram1, GL_LINK_STATUS) == GL_FALSE) {
            String message = glGetProgramInfoLog(shaderProgram1);
            System.err.println("Linking shaders to program failed with message: " + message);
            System.exit(1);
        }

        int fragmentShader2 = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader2, fragmentShaderSource2);
        glCompileShader(fragmentShader2);

        if (glGetShaderi(fragmentShader2, GL_COMPILE_STATUS) == GL_FALSE) {
            String message = glGetShaderInfoLog(fragmentShader2);
            System.err.println("Compiling fragment shader failed with message: " + message);
            System.exit(1);
        }

        int shaderProgram2 = glCreateProgram();
        glAttachShader(shaderProgram2, vertexShader);
        glAttachShader(shaderProgram2, fragmentShader2);
        glLinkProgram(shaderProgram2);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader1);
        glDeleteShader(fragmentShader2);

        int vaoId1, vaoId2;
        int vboId1, vboId2;
        float vertices1[] = {
            -0.5f, -0.5f, 0.0f,  // bottom left
             0.5f, -0.5f, 0.0f,  // bottom right
            -0.5f,  0.5f, 0.0f   // top left 
        };
        FloatBuffer verticesBuffer1 = BufferUtils.createFloatBuffer(vertices1.length);
        verticesBuffer1.put(vertices1).flip();

        vaoId1 = glGenVertexArrays();
        vboId1 = glGenBuffers();

        float vertices2[] = {
            -0.5f,  0.5f,  0.0f, //top left
             0.5f,  0.5f,  0.0f, // top right
             0.5f, -0.5f,  0.0f  // bottom right
        };
        FloatBuffer verticesBuffer2 = BufferUtils.createFloatBuffer(vertices2.length);
        verticesBuffer2.put(vertices2).flip();

        vaoId2 = glGenVertexArrays();
        vboId2 = glGenBuffers();

        glBindVertexArray(vaoId1);
        glBindBuffer(GL_ARRAY_BUFFER, vboId1);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer1, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(vaoId2);
        glBindBuffer(GL_ARRAY_BUFFER, vboId2);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer2, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        while (!glfwWindowShouldClose(window)) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glUseProgram(shaderProgram1);
            glBindVertexArray(vaoId1);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glUseProgram(shaderProgram2);
            glBindVertexArray(vaoId2);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glDeleteVertexArrays(vaoId1);
        glDeleteBuffers(vboId1);
        glDeleteProgram(shaderProgram1);
        glDeleteProgram(shaderProgram2);
        glfwTerminate();
    }
}
