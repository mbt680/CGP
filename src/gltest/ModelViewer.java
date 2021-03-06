package gltest;

import java.awt.image.BufferedImage;
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
    static class Settings {
        public static Camera camera = new Camera();
        public static Light lighting = new Light();
        public static String materialFileLoc = "";
        public static boolean pointLight = true;
        public static boolean hasCelShading = false;
        public static boolean textureHasCelShading = false;
        public static boolean hasLighting = false;
        public static boolean hasRimLighting = false;
        public static boolean hasContours = false;
        public static boolean hasSugContours = false;
    };

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    private static final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            Camera camera = Settings.camera;
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
            }
        }
    };

    private static final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            Settings.camera.changeScale((int)yoffset);      
        }
    };

    private static final GLFWFramebufferSizeCallback bufferSizeCallback = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            glfwSetWindowSize(window, width, height);
            // Needs to change if initial aspect ratio is changed and no longer 1
            int imageSize = Math.max(width, height);
            glViewport((width - imageSize)/2, (height - imageSize)/2, imageSize, imageSize);
        }   
    };

    public static void main(String[] args) throws Exception {
        long window;
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = glfwCreateWindow(WIDTH, HEIGHT, "Model Viewer", NULL, NULL);
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
        Shader blackShader = new Shader(dir + "/shaders/color.vs", dir + "/shaders/color.fs");

        // create uniform for camera view
        blackShader.createUniform("viewMatrix");
        blackShader.setConstantUniform3fv("ourColor", new Vector3f(0f, 0f, 0f));
        blackShader.createUniform("applySolidColour");
        
        SettingsDialog dialog = new SettingsDialog();
        blackShader.use();
        loadLighting(blackShader);
        
        // Default texture
        Model.setDefaultTexture(TextureLoader.loadTexture(TextureLoader.loadImage(dir + "/data/textures/white.png")));

        // Load model from command line args
        String modelPath = "/data/skull/skull.obj";
        String modelName = "skull";
        if (args.length == 2) {
            modelPath = args[0];
            modelName = args[1];
        } 

        System.out.println("About to load model");
        // Load models.
        WavefrontParser.setDefaultShader(blackShader);
        Map<String, Model> modelMap = WavefrontParser.parse(dir + modelPath);
        for (String key : modelMap.keySet()) {
            Model model = modelMap.get(key);
            System.out.println("Model: " + model.getName());
            for (String meshKey : model.getMeshNames()) {
                Mesh mesh = model.getMeshForKey(meshKey);
                System.out.println("  has mesh: " + mesh.getName());
            }
        }

        // Get model by the name of "engineer_morphs_low"
        Model teddy = modelMap.get(modelName);

        if (teddy == null) {
            System.out.println("Model is null!");
        }

        teddy.setProgramForAllKeys(blackShader);

        glEnable(GL_DEPTH_TEST);
        while (!glfwWindowShouldClose(window)) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            applySettings(blackShader);

            // Ouline draw loop
            if (Settings.hasContours) {
                teddy.setProgramForAllKeys(blackShader);
                blackShader.use();
                // Set the model to draw in just ourColour, with no lighting
                blackShader.setBool("applySolidColour", true);
                glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
                glLineWidth(5.0f);
                for (String key : modelMap.keySet()) {
                    Model model = modelMap.get(key);
                    model.draw(Settings.camera.viewMatrix, textureID);
                }
                glEnable( GL_POLYGON_OFFSET_FILL);
                glPolygonOffset(-2.5f, -2.5f);
            }
            
            // Main draw loop
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glLineWidth(1.0f);
            teddy.setProgramForAllKeys(blackShader);
            blackShader.use();
            // Set the model to draw in the colour of its texture, with lighting
            blackShader.setBool("applySolidColour", false);
            for (String key : modelMap.keySet()) {
                Model model = modelMap.get(key);
                model.draw(Settings.camera.viewMatrix, textureID);
            }
            glDisable( GL_POLYGON_OFFSET_FILL);

            glfwSwapBuffers(window);
            glfwPollEvents();
            
        }        

        // Free memory used by the models
        for (String key : modelMap.keySet()) {
            Model model = modelMap.get(key);
            model.delete();
        }

        glfwTerminate();
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

        shader.use();

        shader.setBool("pointLight", Settings.pointLight);
        shader.setBool("applyLighting", Settings.hasLighting);
        shader.setBool("applyRimLighting", Settings.hasRimLighting);
        shader.setUniform("lightPos", Settings.lighting.position );

        shader.setUniform("ambientLight", Settings.lighting.ambient );
        shader.setUniform("specularLight", Settings.lighting.specular );
        shader.setUniform("diffuseLight", Settings.lighting.diffuse );

        if (Settings.hasCelShading) {
            shader.setInt("vertexLevels", Settings.lighting.lightingLevels );
            shader.setInt("fragLevels", Settings.lighting.textureLevels );
        } else {
            shader.setInt("vertexLevels", Integer.MAX_VALUE );
            shader.setInt("fragLevels", Integer.MAX_VALUE );
        }

        String fileLocation = Settings.materialFileLoc;
        if (fileLocation.isEmpty() ) {
            textureID = -1;
        } else if (fileLocation!= prevFileLocation) {
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
    }
}
