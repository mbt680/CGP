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

    private void setVertexNormalIndices(int[] vertexNormalIndices) {
        this.vertexNormalIndices = vertexNormalIndices;
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
     * getVertexNormalIndices
     * @return
     */
    public int[] getVertexNormalIndices() {
        return vertexNormalIndices;
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
            int requiredSpaceV = 0;
            int requiredSpaceVn = 0;
            for (Face face : faces) {
                requiredSpaceV += face.sizeVertex();
                requiredSpaceVn += face.sizeVertexNormal();
            }

            int[] vertexIndices = new int[requiredSpaceV];
            int[] vertexNormalIndices = new int[requiredSpaceVn];
            int i = 0;
            int j = 0;
            for (Face face : faces) {
                int nToCopyV = face.sizeVertex();
                int nToCopyVn = face.sizeVertexNormal();
                System.arraycopy(face.getVertexIndices(), 0, vertexIndices, i, nToCopyV);
                System.arraycopy(face.getVertexNormalIndices(), 0, vertexNormalIndices, j, nToCopyVn);
                i += nToCopyV;
                j += nToCopyVn;
            }

            mesh.setVertexIndices(vertexIndices);
            mesh.setVertexNormalIndices(vertexNormalIndices);

            return mesh;
        }
    }
}
