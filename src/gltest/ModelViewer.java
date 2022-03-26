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
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;


    private static final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_Q) {
                glfwSetWindowShouldClose(window, true);
<<<<<<< Updated upstream
            } else if (key == GLFW_KEY_LEFT) {
                camera.rotateLeft();
            } else if (key == GLFW_KEY_RIGHT) {
                camera.rotateRight();
            } else if (key == GLFW_KEY_UP) {
                camera.rotateUp();
            } else if (key == GLFW_KEY_DOWN) {
                camera.rotateDown();
            } else if (key == GLFW_KEY_W) {
=======
            }
            if (key == GLFW_KEY_LEFT) {
                camera.rotateLeft();
            } else if (key == GLFW_KEY_RIGHT) {
                camera.rotateRight();
            }
            if (key == GLFW_KEY_UP) {
                camera.rotateUp();
            } else if (key == GLFW_KEY_DOWN) {
                camera.rotateDown();
            }
            if (key == GLFW_KEY_W) {
>>>>>>> Stashed changes
                camera.moveForward();
            } else if (key == GLFW_KEY_A) {
                camera.moveLeft();
            } else if (key == GLFW_KEY_S) {
                camera.moveBack();
<<<<<<< Updated upstream
            } else if (key == GLFW_KEY_D) {
                camera.moveRight();
            } else if (key == GLFW_KEY_Z) {
                camera.moveDown();
            } else if (key == GLFW_KEY_X) {
                camera.moveUp();
            }else if (key == GLFW_KEY_EQUAL) {
=======
            }
            if (key == GLFW_KEY_A) {
                camera.moveLeft();
            } else if (key == GLFW_KEY_D) {
                camera.moveRight();
            }
            if (key == GLFW_KEY_Z) {
                camera.moveDown();
            } else if (key == GLFW_KEY_X) {
                camera.moveUp();
            }
            if (key == GLFW_KEY_EQUAL) {
>>>>>>> Stashed changes
                camera.changeScale(1);
            } else if (key == GLFW_KEY_MINUS) {
                camera.changeScale(-1);
            }
        }
    };

    private static final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
<<<<<<< Updated upstream
            camera.changeScale((int)yoffset);      
=======
            Settings.camera.changeScale((int) yoffset);
>>>>>>> Stashed changes
        }
    };

    private static final GLFWFramebufferSizeCallback bufferSizeCallback = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            glfwSetWindowSize(window, width, height);
            // Needs to change if initial aspect ratio is changed and no longer 1
<<<<<<< Updated upstream
            int imageSize = Math.min(width, height);
            glViewport((width - imageSize)/2, (height - imageSize)/2, imageSize, imageSize);
        }   
=======
            int imageSize = Math.max(width, height);
            glViewport((width - imageSize) / 2, (height - imageSize) / 2, imageSize, imageSize);
        }
>>>>>>> Stashed changes
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
        Shader redShader = new Shader(dir + "/shaders/color.vs", dir + "/shaders/color.fs");

        // create uniform for camera view
        yellowShader.createUniform("viewMatrix");
        // set uniform containing colour that the shader uses
<<<<<<< Updated upstream
        yellowShader.setConstantUniform3fv("ourColor", new Vector3f(1f, 1f, 0f));
        redShader.createUniform("viewMatrix");
        redShader.setConstantUniform3fv("ourColor", new Vector3f(1f, 0f, 0f));
=======
        yellowShader.setConstantUniform3fv("ourColor", new Vector3f(1f, 0.933f, 0.345f));
        blackShader.createUniform("viewMatrix");
        blackShader.setConstantUniform3fv("ourColor", new Vector3f(0f, 0f, 0f));

        SettingsDialog dialog = new SettingsDialog();
        loadLighting(yellowShader);
>>>>>>> Stashed changes

        List<Shader> shaderList = new ArrayList<>();
        shaderList.add(yellowShader);
        shaderList.add(redShader);

        // Load models.
        WavefrontParser.setDefaultShader(yellowShader);
        Map<String, Model> modelMap = WavefrontParser.parse(dir + "/data/teddy.obj.txt");
        for (String key : modelMap.keySet()) {
            Model model = modelMap.get(key);
            System.out.println("Model: " + model.getName());
            for (String meshKey : model.getMeshNames()) {
                Mesh mesh = model.getMeshForKey(meshKey);
                System.out.println("  has mesh: " + mesh.getName());
            }
        }

        // Get model by the name of "Teddy"
        Model teddy = modelMap.get("Teddy");
        // Set shader program for Mesh "None" to redShader
        teddy.setProgramForKey("None", redShader);
        
        glEnable(GL_DEPTH_TEST);
<<<<<<< Updated upstream
        // draw a wireframe
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
=======
        glDepthFunc(GL_LESS);

>>>>>>> Stashed changes
        while (!glfwWindowShouldClose(window)) {
            glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
            glEnable(GL_STENCIL_TEST);

            glClearColor(0, 0, 0, 1);
            glClearStencil(0);
            glStencilMask(0xFF);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

<<<<<<< Updated upstream
            camera.setViewMatrix(WIDTH, HEIGHT);
            // Main draw loop
=======
            applySettings(yellowShader);

            glEnable(GL_STENCIL_TEST);
            glStencilFunc(GL_ALWAYS, 1, 0xFF);
            glStencilMask(0xFF);
            glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);

            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glLineWidth(1.0f);
            teddy.setProgramForAllKeys(yellowShader);
>>>>>>> Stashed changes
            for (String key : modelMap.keySet()) {
                Model model = modelMap.get(key);
                model.draw(camera.viewMatrix);
            }
<<<<<<< Updated upstream
=======

            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP); 
            glStencilFunc(GL_EQUAL, Settings.hasContours ? 1 : 0, 0xFF);
            glStencilMask(0x00);
            glDisable(GL_DEPTH_TEST);

            // Ouline draw loop
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glLineWidth(1.0f);
            teddy.setProgramForAllKeys(blackShader);
            for (String key : modelMap.keySet()) {
                Model model = modelMap.get(key);
                model.drawCountour(Settings.camera.viewMatrix, textureID);
            }

            glDisable(GL_STENCIL_TEST);
            glEnable(GL_DEPTH_TEST);
>>>>>>> Stashed changes

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        glfwTerminate();
<<<<<<< Updated upstream
=======
        dialog.dispose();
    }

    static void loadLighting(Shader shader) throws Exception {
        shader.createUniform("applyLighting");
        shader.createUniform("applyRimLighting");
        shader.createUniform("lightPos");
        shader.createUniform("shininess");
        shader.setInt("shininess", Settings.lighting.shininess);
        shader.createUniform("ambientLight");
        shader.createUniform("specularLight");
        shader.createUniform("diffuseLight");
        shader.createUniform("vertexLevels");
        shader.createUniform("fragLevels");
    }

    static String prevFileLocation = "";
    static int textureID = -1;

    static void applySettings(Shader shader) throws Exception {
        Settings.camera.setViewMatrix(WIDTH, HEIGHT);

        shader.setBool("applyLighting", Settings.hasLighting);
        shader.setBool("applyRimLighting", Settings.hasRimLighting);
        shader.setUniform("lightPos", Settings.lighting.position);

        shader.setUniform("ambientLight", Settings.lighting.ambient);
        shader.setUniform("specularLight", Settings.lighting.specular);
        shader.setUniform("diffuseLight", Settings.lighting.diffuse);

        if (Settings.hasCelShading) {
            shader.setInt("vertexLevels", Settings.lighting.vertexLevels);
            shader.setInt("fragLevels", Settings.lighting.fragLevels);
        } else {
            shader.setInt("vertexLevels", Integer.MAX_VALUE);
            shader.setInt("fragLevels", Integer.MAX_VALUE);
        }

        String fileLocation = Settings.materialFileLoc;
        if (fileLocation.isEmpty()) {
            textureID = -1;
        } else if (fileLocation != prevFileLocation) {
            textureID = loadMaterial(shader);
            prevFileLocation = fileLocation;
        }
    }

    static int loadMaterial(Shader shader) throws Exception {
        BufferedImage image = TextureLoader.loadImage(Settings.materialFileLoc);
        int textureID = TextureLoader.loadTexture(image);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glGenerateMipmap(GL_TEXTURE_2D);
        return textureID;
>>>>>>> Stashed changes
    }
}
