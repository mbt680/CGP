package gltest;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WavefrontParser {
    private static final String VERTEX = "v";
    private static final String OBJECT = "o";
    private static final String VERTEX_NORMAL = "vn";
    private static final String VERTEX_UV = "vt";
    private static final String VERTEX_TEX_SEPERATOR = "/";
    private static final String VINDEX_VNINDEX_SEPERATOR = "//";
    private static final String FACE = "f";
    private static final String USEMTL = "usemtl";
    private static final String MATERIAL_GROUP = "g";
    private static final String SURFACE = "s";
    private static final String MTLLIB = "mtllib";
    private static final String COMMENT = "#";
    private static final String DEFAULT_NAME = "unnamed";
    private static Shader defaultShader;

    public static Map<String, Model> parse(String wavefrontPath) {
        // open a stream
        Scanner wavefrontFile; 
        System.out.println("Trying to load file " + wavefrontPath);
        try {
            wavefrontFile = new Scanner(new FileInputStream(wavefrontPath));
        } catch (FileNotFoundException fe) {
            return new HashMap<>();
        }

        // parse the file
        Map<String, Model> modelMap = new HashMap<>();
        Model.Builder currentModel = null;
        Mesh.Builder currentMesh = null;
        Map<String, Image> currentMtlLib = null;
        String faceType = VERTEX;

        int lineCount = 0;
        while (wavefrontFile.hasNextLine()) {
            lineCount++;
            String line = wavefrontFile.nextLine().trim();
            if (line.isEmpty())
                continue;
            
            String[] tokens = line.split("\\s+");
            String[] vnTokens = line.split(VINDEX_VNINDEX_SEPERATOR);
            String[] vtTokens = line.split(VERTEX_TEX_SEPERATOR);
            
            String lineStart = tokens[0];
            //System.out.println(lineStart);
            if (lineStart.equals(COMMENT)) {
                continue;
            } else if (lineStart.equals(MTLLIB)) {
                System.out.println("Loading material library");
                String filename = tokens[1];
                int i = wavefrontPath.lastIndexOf("/");
                String parentDir = wavefrontPath.substring(0, i+1);
                currentMtlLib = MtlParser.parse(parentDir + File.separator + filename);
            } else if (lineStart.equals(SURFACE)) {
                System.out.println("Found surface");
                continue;
            } else if (lineStart.equals(OBJECT)) {
                System.out.println("Found object");
                // update the current model
                if (currentModel != null) {
                    Model model = currentModel.getModel();
                    if (currentMesh != null) {
                        Mesh mesh = currentMesh.getMesh();
                        if (currentModel.getMeshForKey(mesh.getName()) != null) {
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
            } else if (lineStart.equals(USEMTL) /*|| lineStart.equals(MATERIAL_GROUP)*/) {
                System.out.println("Found mesh");
                // create a new mesh
                if (currentMesh != null) {
                    Mesh mesh = currentMesh.getMesh();
                    if (currentModel == null)
                    {
                        System.out.println("ModelBuilder is null");
                        currentModel = new Model.Builder(DEFAULT_NAME);
                    }
                    // Model model = currentModel.getModel();
                    // if (model == null) System.out.println("Model is null?"); 
                    if (currentModel.getMeshForKey(mesh.getName()) != null) {
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
                int[] vertexIndices = new int[tokens.length - 1];
                int[] vertexNormalIndices = new int[Math.max(0, Math.max(vnTokens.length - 1, vtTokens.length))];
                int[] vertexUVIndices = new int[Math.max(0, vtTokens.length - 1)];
                // System.out.println("Note that face type is currently " + faceType);
                if (faceType.equals(VERTEX)) {
                    for (int i = 0; i < vertexIndices.length; i++) {
                        vertexIndices[i] = Integer.parseInt(tokens[i+1]) - 1;
                    }
                } else if (faceType.equals(VERTEX_NORMAL)) {
                    for (int i = 0; i < vertexIndices.length; i++) {
                        String[] subTokens = tokens[i+1].split(VINDEX_VNINDEX_SEPERATOR);
                        vertexIndices[i] = Integer.parseInt(subTokens[0]) - 1;
                        vertexNormalIndices[i] = Integer.parseInt(subTokens[1]) - 1;
                    }
                } else if (faceType.equals(VERTEX_UV)) {
                    for (int i = 0; i < vertexIndices.length; i++) {
                        // System.out.println(line);
                        // System.out.println(tokens[i+1]);
                        String[] subTokens = tokens[i+1].split(VERTEX_TEX_SEPERATOR);
                        // System.out.println(subTokens.length);
                        vertexIndices[i] = Integer.parseInt(subTokens[0]) - 1;
                        vertexNormalIndices[i] = Integer.parseInt(subTokens[1]) - 1;
                        vertexUVIndices[i] = Integer.parseInt(subTokens[2]) - 1;
                    }
                }
                // Some wavefront files have no material, this handles that
                if (currentMesh == null) {
                    currentMesh = new Mesh.Builder(DEFAULT_NAME);
                }
                Face tmpFace = new Face(vertexIndices, vertexNormalIndices, vertexUVIndices);
                currentMesh.addFace(tmpFace);
            } else if (lineStart.equals(VERTEX)) {
                // add vertex to the current model
                float[] vertices = new float[tokens.length - 1];
                for (int i = 0; i < vertices.length; i++) {
                    try {
                    vertices[i] = Float.parseFloat(tokens[i+1]);
                    } catch (NumberFormatException ne) {
                        System.out.printf("NFE At line: %d\n For %s!!%s\n", lineCount, tokens[i+1], line);
                    }
                } 
                if (currentModel == null) {
                    currentModel = new Model.Builder(DEFAULT_NAME);
                }
                currentModel.addVertex(vertices);
            } else if (lineStart.equals(VERTEX_NORMAL)) {
                // add vertex normal to the current model
                float[] vertices = new float[tokens.length - 1];
                for (int i = 0; i < vertices.length; i++) {
                    vertices[i] = Float.parseFloat(tokens[i+1]);
                } 
                if (currentModel == null) {
                    currentModel = new Model.Builder(DEFAULT_NAME);
                }
                if (faceType.equals(VERTEX)) {
                    System.out.println("Changing face type to " + VERTEX_NORMAL);
                    faceType = VERTEX_NORMAL;
                }
                currentModel.addVertexNormal(vertices);
            } else if (lineStart.equals(VERTEX_UV)) {
                // add vertex uv coordinates to current model
                float[] vertices = new float[tokens.length - 1];
                for (int i = 0; i < vertices.length; i++) {
                    vertices[i] = Float.parseFloat(tokens[i+1]);
                }
                if (currentModel == null) {
                    currentModel = new Model.Builder(DEFAULT_NAME);
                }
                if (faceType.equals(VERTEX) || faceType.equals(VERTEX_NORMAL)) {
                    faceType = VERTEX_UV;
                    System.out.println("Changing face type to " + VERTEX_UV);

                }
                currentModel.addVertexUV(vertices);
            }
        }
        if (currentMesh != null) {
            // Model model = currentModel.getModel();
            Mesh mesh = currentMesh.getMesh();
            if (currentModel.getMeshForKey(mesh.getName()) == null) {
                mesh.setProgram(defaultShader);

                currentModel.addMesh(mesh);
            }
        }
        if (currentModel != null) {
            if (currentMtlLib != null) {
                currentModel.setMaterialLib(currentMtlLib);
            }
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
