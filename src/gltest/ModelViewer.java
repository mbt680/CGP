package gltest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL;
import org.joml.Vector3f;
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
    private static Light lighting = new Light();
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;


    private static final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_Q) {
                glfwSetWindowShouldClose(window, true);
            } if (key == GLFW_KEY_LEFT) {
                camera.rotateLeft();
            } else if (key == GLFW_KEY_RIGHT) {
                camera.rotateRight();
            } if (key == GLFW_KEY_UP) {
                camera.rotateUp();
            } else if (key == GLFW_KEY_DOWN) {
                camera.rotateDown();
            } if (key == GLFW_KEY_W) {
                camera.moveForward();
            } else if (key == GLFW_KEY_S) {
                camera.moveBack();
            } if (key == GLFW_KEY_A) {
                camera.moveLeft();
            } else if (key == GLFW_KEY_D) {
                camera.moveRight();
            } if (key == GLFW_KEY_Z) {
                camera.moveDown();
            } else if (key == GLFW_KEY_X) {
                camera.moveUp();
            } if (key == GLFW_KEY_EQUAL) {
                camera.changeScale(1);
            } else if (key == GLFW_KEY_MINUS) {
                camera.changeScale(-1);
            } if (key == GLFW_KEY_T) {
                lighting.moveUp();
            } else if (key == GLFW_KEY_G) {
                lighting.moveDown();
            } if (key == GLFW_KEY_F) {
                lighting.moveBack();
            } else if (key == GLFW_KEY_H) {
                lighting.moveForward();
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
            // Needs to change if initial aspect ratio is changed and no longer 1
            int imageSize = Math.min(width, height);
            glViewport((width - imageSize)/2, (height - imageSize)/2, imageSize, imageSize);
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

        // Setup shaders
        Shader yellowShader = new Shader(dir + "/shaders/color.vs", dir + "/shaders/color.fs");
        Shader blackShader = new Shader(dir + "/shaders/color.vs", dir + "/shaders/color.fs");

        // create uniform for camera view
        yellowShader.createUniform("viewMatrix");
        // set uniform containing colour that the shader uses
        yellowShader.setConstantUniform3fv("ourColor", new Vector3f(1f, 0.933f, 0.345f));
        blackShader.createUniform("viewMatrix");
        blackShader.setConstantUniform3fv("ourColor", new Vector3f(0f, 0f, 0f));
        //create lighting uniforms
        yellowShader.createUniform("lightPos");
        yellowShader.createUniform("ambientLight");
        yellowShader.setConstantUniform3fv("ambientLight", lighting.ambient );
        yellowShader.createUniform("specularLight");
        yellowShader.setConstantUniform3fv("specularLight", lighting.specular );
        yellowShader.createUniform("diffuseLight");
        yellowShader.setConstantUniform3fv("diffuseLight", lighting.diffuse );

        List<Shader> shaderList = new ArrayList<>();
        shaderList.add(yellowShader);
        shaderList.add(blackShader);

        // Load models.
        WavefrontParser.setDefaultShader(yellowShader);
        Map<String, Model> modelMap = WavefrontParser.parse(dir + "/data/engineer8.obj");
        for (String key : modelMap.keySet()) {
            Model model = modelMap.get(key);
            System.out.println("Model: " + model.getName());
            for (String meshKey : model.getMeshNames()) {
                Mesh mesh = model.getMeshForKey(meshKey);
                System.out.println("  has mesh: " + mesh.getName());
            }
        }

        // Get model by the name of "engineer_morphs_low"
        Model teddy = modelMap.get("unnamed");
        
        
        glEnable(GL_DEPTH_TEST);
        // draw a wireframe
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        while (!glfwWindowShouldClose(window)) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            camera.setViewMatrix(WIDTH, HEIGHT);
            yellowShader.setUniform("lightPos", lighting.position );

            // Ouline draw loop
            teddy.setProgramForAllKeys(blackShader);
            glEnable( GL_POLYGON_OFFSET_FILL );
            glPolygonOffset( -2.5f, -2.5f );
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
            glLineWidth(5.0f);
            for (String key : modelMap.keySet()) {
                Model model = modelMap.get(key);
                model.draw(camera.viewMatrix);
            }

            // Main draw loop
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glLineWidth(1.0f);
            teddy.setProgramForAllKeys(yellowShader);
            for (String key : modelMap.keySet()) {
                Model model = modelMap.get(key);
                model.draw(camera.viewMatrix);
            }
            glDisable( GL_POLYGON_OFFSET_FILL);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }        
        glfwTerminate();
    }
}
