package gltest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WavefrontParser {
    private static final String VERTEX = "v";
    private static final String OBJECT = "o";
    private static final String FACE = "f";
    private static final String USEMTL = "usemtl";
    private static final String SURFACE = "s";
    private static final String MTLLIB = "mtllib";
    private static final String COMMENT = "#";

    private static Shader defaultShader;

    public static Map<String, Model> parse(String wavefrontPath) {
        // open a stream
        Scanner wavefrontFile; 
        
        try {
            wavefrontFile = new Scanner(new FileInputStream(wavefrontPath));
        } catch (FileNotFoundException fe) {
            return new HashMap<>();
        }

        // parse the file
        Map<String, Model> modelMap = new HashMap<>();
        Model.Builder currentModel = null;
        Mesh.Builder currentMesh = null;
        while (wavefrontFile.hasNextLine()) {
            String line = wavefrontFile.nextLine();
            if (line.trim().isEmpty())
                continue;
            
            String[] tokens = line.split(" ");
            String lineStart = tokens[0];
            //System.out.println(lineStart);
            if (lineStart.equals(COMMENT)) {
                continue;
            } else if (lineStart.equals(MTLLIB)) {
                continue;
            } else if (lineStart.equals(SURFACE)) {
                continue;
            } else if (lineStart.equals(OBJECT)) {
                System.out.println("Found object");
                // update the current model
                if (currentModel != null) {
                    Model model = currentModel.getModel();
                    if (currentMesh != null) {
                        Mesh mesh = currentMesh.getMesh();
                        if (model.getMeshForKey(mesh.getName()) != null) {
                            // System.out.println("Mesh already exists with name " + mesh.getName() + " in " + model.getName());
                        } else {
                            // System.out.println("NEW_OBJECT: New mesh with name " + mesh.getName() + " " + mesh);
                            mesh.setProgram(defaultShader);
                            currentModel.addMesh(mesh);
                        }
                        model = currentModel.getModel();
                    }
                    currentMesh = null;
                    modelMap.put(model.getName(), model);
                }
                currentModel = new Model.Builder(tokens[1]);
            } else if (lineStart.equals(USEMTL)) {
                System.out.println("Found mesh");
                // create a new mesh
                if (currentMesh != null) {
                    Mesh mesh = currentMesh.getMesh();
                    Model model = currentModel.getModel();
                    if (model.getMeshForKey(mesh.getName()) != null) {
                        // System.out.println("Mesh already exists with name " + mesh.getName() + " in " + model.getName());
                    } else {
                        // System.out.println("New mesh with name " + mesh.getName() + " " + mesh);
                        mesh.setProgram(defaultShader);
                        currentModel.addMesh(mesh);
                    }
                }
                currentMesh = new Mesh.Builder(tokens[1]);
                
            } else if (lineStart.equals(FACE)) {
                // add face to the current mesh
                int[] indices = new int[tokens.length - 1];
                for (int i = 0; i < indices.length; i++) {
                    indices[i] = Integer.parseInt(tokens[i+1]) - 1;
                }
                // Some wavefront files have no material, this handles that
                if (currentMesh == null) {
                    currentMesh = new Mesh.Builder("noname");
                }
                currentMesh.addFace(new Face(indices));
            } else if (lineStart.equals(VERTEX)) {
                // add vertex to the current model
                float[] vertices = new float[tokens.length - 1];
                for (int i = 0; i < vertices.length; i++) {
                    vertices[i] = Float.parseFloat(tokens[i+1]);
                } 
                currentModel.addVertex(vertices);
            } 
        }
        if (currentMesh != null) {
            Model model = currentModel.getModel();
            Mesh mesh = currentMesh.getMesh();
            if (model.getMeshForKey(mesh.getName()) == null) {
                mesh.setProgram(defaultShader);

                currentModel.addMesh(mesh);
            }
        }
        if (currentModel != null) {
            Model model = currentModel.getModel();
            modelMap.put(model.getName(), model);
        }
        return modelMap;
    }

    /**
     * setDefaultShader to be used for meshes
     * @param defaultShader Any shader that can handler Vector3f as input. 
     * Must accept a Matrix4f as a view matrix.
     */
    public static void setDefaultShader(Shader defaultShader) {
        WavefrontParser.defaultShader = defaultShader;
    }
}
