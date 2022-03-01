package gltest;

import java.util.ArrayList;
import java.util.List;

/**
 * Mesh - several faces of a model that share the same shader
 */
public class Mesh {
    private Shader program;
    private int[] indices;

    private void setProgram(Shader program) {
        this.program = program;
    }

    private void setIndices(int[] indices) {
        System.out.println("Length of mesh is " + indices.length);
        this.indices = indices;
    }

    /**
     * getProgram gets the shader program being used
     * @return
     */
    public Shader getProgram() {
        return program;
    }

    /**
     * getIndices of vertices of the model that compose this Mesh
     * @return
     */
    public int[] getIndices() {
        return indices;
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
        public Builder(Shader program) {
            mesh = new Mesh();
            mesh.setProgram(program);
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
                System.arraycopy(face.getIndices(), 0, indices, i, nToCopy);
                i += nToCopy;
            }

            mesh.setIndices(indices);

            return mesh;
        }
    }
}
