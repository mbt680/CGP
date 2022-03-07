package gltest;

import java.util.ArrayList;
import java.util.List;

/**
 * Mesh - several faces of a model that share the same shader
 */
public class Mesh {
    private Shader program;
    private int[] vertexIndices;
    private int[] vertexNormalIndices;
    private String name;

    /**
     * getName return the name of this shader
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        // System.out.println("Set name for mesh to " + name);
        this.name = name;
    }

    /**
     * setProgram to a new shader. The shader should have a viewMatrix uniform.
     * @param program
     */
    public void setProgram(Shader program) {
        // System.out.println("Set program for " + name + " to " + program.id + " " + this);
        this.program = program;
    }

    private void setVertexIndices(int[] vertexIndices) {
        // System.out.println("Length of mesh is " + indices.length);
        this.vertexIndices = vertexIndices;
    }

    /**
     * getProgram gets the shader program being used
     * @return
     */
    public Shader getProgram() {
        return program;
    }

    /**
     * getVertexIndices of vertices of the model that compose this Mesh
     * @return
     */
    public int[] getVertexIndices() {
        return vertexIndices;
    }

    /**
     * delete the mesh's shader
     */
    public void delete() {
        program.delete();
    }

    /**
     * Builder for constructing Mesh
     */
    public static class Builder {
        private Mesh mesh;
        private List<Face> faces;

        /**
         * Constructor
         * @param program Shader to use with the mesh
         */
        public Builder(String name) {
            mesh = new Mesh();
            //mesh.setProgram(program);
            mesh.setName(name);
            faces = new ArrayList<>();
        }

        /**
         * addFace to the model
         * @param face
         */
        public void addFace(Face face) {
            faces.add(face);
        }

        /**
         * getMesh constructs the mesh and returns it
         * @return
         */
        public Mesh getMesh() {
            int requiredSpace = 0;
            for (Face face : faces) {
                requiredSpace += face.size();
            }

            int[] indices = new int[requiredSpace];
            int i = 0;
            for (Face face : faces) {
                int nToCopy = face.size();
                System.arraycopy(face.getVertexIndices(), 0, indices, i, nToCopy);
                i += nToCopy;
            }

            mesh.setVertexIndices(indices);

            return mesh;
        }
    }
}
