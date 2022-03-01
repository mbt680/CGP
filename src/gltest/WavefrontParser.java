package gltest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WavefrontParser {
    private static final String VERTEX = "v";
    private static final String OBJECT = "o";
    private static final String FACE = "f";
    private static final String USEMTL = "usemtl";
    private static final String SURFACE = "s";
    private static final String MTLLIB = "mtllib";
    private static final String COMMENT = "#";

    public static List<Model> parse(String wavefrontPath, Shader defaultShader) {
        // open a stream
        Scanner wavefrontFile; 
        
        try {
            wavefrontFile = new Scanner(new FileInputStream(wavefrontPath));
        } catch (FileNotFoundException fe) {
            return new ArrayList<>();
        }

        // parse the file
        List<Model> modelsList = new ArrayList<>();
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
                // update the current model
                if (currentModel != null) {
                    modelsList.add(currentModel.getModel());
                }
                currentModel = new Model.Builder();
            } else if (lineStart.equals(USEMTL)) {
                // create a new mesh
                if (currentMesh != null) {
                    currentModel.addMesh(currentMesh.getMesh());
                }
                currentMesh = new Mesh.Builder(defaultShader);
            } else if (lineStart.equals(FACE)) {
                // add face to the current mesh
                int[] indices = new int[tokens.length - 1];
                for (int i = 0; i < indices.length; i++) {
                    indices[i] = Integer.parseInt(tokens[i+1]) - 1;
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
            currentModel.addMesh(currentMesh.getMesh());
        }
        if (currentModel != null) {
            modelsList.add(currentModel.getModel());
        }
        return modelsList;
    }
}
