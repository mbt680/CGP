package gltest;

import java.util.List;

import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * View 3D models 
 */
public class ModelViewer {
    private static Camera camera = new Camera();
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;


    private static final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_Q) {
                glfwSetWindowShouldClose(window, true);
            } else if (key == GLFW_KEY_LEFT) {
                camera.rotateLeft();
            } else if (key == GLFW_KEY_RIGHT) {
                camera.rotateRight();
            } else if (key == GLFW_KEY_UP) {
                camera.rotateUp();
            } else if (key == GLFW_KEY_DOWN) {
                camera.rotateDown();
            } else if (key == GLFW_KEY_W) {
                camera.moveForward();
            } else if (key == GLFW_KEY_A) {
                camera.moveLeft();
            } else if (key == GLFW_KEY_S) {
                camera.moveBack();
            } else if (key == GLFW_KEY_D) {
                camera.moveRight();
            } else if (key == GLFW_KEY_Z) {
                camera.moveDown();
            } else if (key == GLFW_KEY_X) {
                camera.moveUp();
            }else if (key == GLFW_KEY_EQUAL) {
                camera.changeScale(1);
            } else if (key == GLFW_KEY_MINUS) {
                camera.changeScale(-1);
            }
        }
    };

    private static final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            camera.changeScale((int)yoffset);      
        }
    };

    private static final GLFWFramebufferSizeCallback bufferSizeCallback = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            glfwSetWindowSize(window, width, height);
        }   
    };

    public static void main(String[] args) throws Exception {
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
        glfwSetScrollCallback(window, scrollCallback);
        glfwSetFramebufferSizeCallback(window, bufferSizeCallback);

        
        // Get the current directory
        String dir = System.getProperty("user.dir");
        Shader shader = new Shader(dir + "/shaders/basic.vs", dir + "/shaders/basic.fs");

        //crashes
        shader.createUniform("viewMatrix");
        shader.setUniform("viewMatrix", camera.viewMatrix);

        // Load models.
        List<Model> modelList = WavefrontParser.parse(dir + "/data/cube.obj", shader);
        
        glEnable(GL_DEPTH_TEST);
        // draw a wireframe
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        while (!glfwWindowShouldClose(window)) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.setViewMatrix(WIDTH, HEIGHT);
            shader.setUniform("viewMatrix", camera.viewMatrix);

            // Main draw loop
            for (Model model : modelList) {
                model.draw();
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }        
        glfwTerminate();
    }
}
