package gltest;

public class Face {
    private int[] vertexIndices;
    private int[] vertexNormalIndices;

    public Face(int[] vertexIndices, int[] vertexNormalIndices) {
        this.vertexIndices = new int[vertexIndices.length];
        this.vertexNormalIndices = new int[vertexNormalIndices.length];
        System.arraycopy(vertexIndices, 0, this.vertexIndices, 0, vertexIndices.length);
        System.arraycopy(vertexNormalIndices, 0, this.vertexNormalIndices, 0, vertexNormalIndices.length);
    }

    public int[] getVertexIndices() {
        return vertexIndices;
    }

    public int[] getVertexNormalIndices() {
        return vertexNormalIndices;
    }

    public int sizeVertex() {
        return vertexIndices.length;
    }

    public int sizeVertexNormal() {
        return vertexNormalIndices.length;
    }
}
