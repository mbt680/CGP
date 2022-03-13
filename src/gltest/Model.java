package gltest;

import java.nio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL33.*;

/**
 * Model class that can draw a model with several meshes.
 */
public class Model {
    private String name;
    private float[] vertices;
    private Mesh[] meshes;
    private Map<String, Mesh> meshMap;
    private int[] vbo;
    private int[] ebo;
    private int[] vao;

    private boolean hasVertexNormals;
    private int vnOffset;

    private Model() {
        meshMap = new HashMap<String, Mesh>();
    }

    /**
     * draw this model.
     */
    public void draw(Matrix4f viewMatrix) {
        for (int i = 0; i < meshes.length; i++) {
            Mesh tmp = meshes[i];
            Shader program = tmp.getProgram();
            program.use();
            program.setUniform("viewMatrix", viewMatrix);
            int nVertices = tmp.getVertexIndices().length;
        
            glBindVertexArray(vao[i]);
            glDrawElements(GL_TRIANGLES, nVertices, GL_UNSIGNED_INT, 0);
        }
    }

    /**
     * getMeshNames of this model. 
     * @return A Set of names of each mesh
     */
    public Set<String> getMeshNames() {
        return meshMap.keySet();
    }

    /**
     * getMeshForKey returns the mesh mapped to key.
     * @param key to query map with
     * @return Mesh or null if key isn't in the map
     */
    public Mesh getMeshForKey(String key) {
        return meshMap.get(key);
    }

    /**
     * setProgramForkey sets the program of the Mesh in this model matching key.
     * If key does not exist in the map, will return.
     * @param key to find matching Mesh for
     * @param program Shader to set on the Mesh
     */
    public void setProgramForKey(String key, Shader program) {
        Mesh tmpMesh = meshMap.get(key);
        if (tmpMesh == null) return;
        // System.out.println(this + " " + program + " " + key);
        for (Mesh mesh : meshes) {
            if (tmpMesh == mesh) {
                mesh.setProgram(program);
                break;
            }
        }
        tmpMesh.setProgram(program);
    }

    public void setProgramForAllKeys(Shader program) {
        for (String key : meshMap.keySet()) {
            setProgramForKey(key, program);
        }
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    private void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
        for (Mesh mesh : meshes) {
            meshMap.put(mesh.getName(), mesh);
        }
    }

    private void setHasVertexNormals(boolean hasVertexNormals, int offset) {
        this.hasVertexNormals = hasVertexNormals;
        this.vnOffset = offset;
    }

    private void setupBuffers() {
        int nMesh = meshes.length;

        vao = new int[nMesh];
        vbo = new int[nMesh];
        ebo = new int[nMesh];

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();
        for (int i = 0; i < vao.length; i++) {
            Mesh tmpMesh = meshes[i];
            int[] indices = tmpMesh.getVertexIndices();

            IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
            indicesBuffer.put(indices);
            indicesBuffer.flip();

            vao[i] = glGenVertexArrays();
            vbo[i] = glGenBuffers();
            ebo[i] = glGenBuffers();

            glBindVertexArray(vao[i]);

            glBindBuffer(GL_ARRAY_BUFFER, vbo[i]);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[i]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(0);

            if (hasVertexNormals) {
                glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, vnOffset);
                glEnableVertexAttribArray(1);
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    /**
     * delete destroys all buffers of the model.
     */
    public void delete() {
        int nMesh = meshes.length;
        for (int i = 0; i < nMesh; i++) {
            meshes[i].delete();
            glDeleteVertexArrays(vao[i]);
            glDeleteBuffers(vbo[i]);
            glDeleteBuffers(ebo[i]);
        }
    }

    /**
     * Builder is a factory for a model that compresses
     * vertices and meshes into their own arrays and 
     * does contruction of model.
     */
    public static class Builder {
        private List<float[]> vertices;
        private List<float[]> vertexNormals;
        private List<Mesh> meshes;
        private String name;

        /**
         * Constructor
         */
        public Builder(String name) {
            this.name = name;
            vertices = new ArrayList<>();
            vertexNormals = new ArrayList<>();
            meshes = new ArrayList<>();
        }

        /**
         * addMesh to this builder
         * @param mesh Any non-empty mesh
         */
        public void addMesh(Mesh mesh) {
            meshes.add(mesh);
        }

        /**
         * addVertex to the model
         * @param vertex Vertex of points to send to the vertex shader
         */
        public void addVertex(float[] vertex) {
            vertices.add(vertex);
        }

        /**
         * addVertex to the model
         * @param x x coord of vertex to send to vs
         * @param y y coord
         * @param z z coord
         */
        public void addVertex(float x, float y, float z) {
            vertices.add(new float[]{x, y, z});
        }

        /**
         * addVertexNormal to model
         * @param vertexNormal Array of points to add
         */
        public void addVertexNormal(float[] vertexNormal) {
            vertexNormals.add(vertexNormal);
        }

        /**
         * addVertexNormal to model
         * @param x xc of normal
         * @param y yc of normal
         * @param z zc of normal
         */
        public void addVertexNormal(float x, float y, float z) {
            vertexNormals.add(new float[]{x, y, z});
        }

        /**
         * getModel constructs and returns the model.
         * @return Constructed model
         */
        public Model getModel() {
            Model model = new Model();
            model.setName(name);
            // convert list of meshes to an array
            int nMesh = meshes.size();
            Mesh[] arrMesh = new Mesh[nMesh];
            for (int i = 0; i < nMesh; i++) {
                arrMesh[i] = meshes.get(i);
            }
            model.setMeshes(arrMesh);

            // convert list of vertices to an array
            int nVertices = vertices.size();
            int nVn = vertexNormals.size();

            /*
            if (nVn != nVertices && nVn > 0) {
                System.err.printf("nVertices is size %d while nVn is size %d\n", nVertices, nVn);
                System.exit(1);
            }*/

            int requiredSize = (nVertices + nVn) * 3;
            float[] modelVertices = new float[requiredSize];

            for (int i = 0; i < nVertices; i++) {
                float[] tmp = vertices.get(i);
                //float[] tmpVn = vertexNormals.get(i+1);
                System.arraycopy(tmp, 0, modelVertices, i*3, 3);
                //System.arraycopy(tmpVn, 0, modelVertices, (i+1)*3, 3);
            }
            int start = nVertices;
            int end = nVertices + nVn;
            for (int i = start; i < end; i++) {
                float[] tmpVn = vertexNormals.get(i-start);
                System.arraycopy(tmpVn, 0, modelVertices, i*3, 3);
            }  

            model.setVertices(modelVertices);
            if (nVn > 0) {
                model.setHasVertexNormals(true, start);
            }

            model.setupBuffers();


            return model;
        }
    }
}
