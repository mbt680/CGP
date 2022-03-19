package gltest;

public class Face {
    private int[] vertexIndices;
    private int[] vertexNormalIndices;
    private int[] vertexUVIndices;

    public Face(int[] vertexIndices, int[] vertexNormalIndices, int[] vertexUVIndices) {
        this.vertexIndices = new int[vertexIndices.length];
        this.vertexNormalIndices = new int[vertexNormalIndices.length];
        this.vertexUVIndices = new int[vertexUVIndices.length];
        System.arraycopy(vertexIndices, 0, this.vertexIndices, 0, vertexIndices.length);
        System.arraycopy(vertexNormalIndices, 0, this.vertexNormalIndices, 0, vertexNormalIndices.length);
        System.arraycopy(vertexUVIndices, 0, this.vertexUVIndices, 0, vertexUVIndices.length);
    }

    public int[] getVertexIndices() {
        return vertexIndices;
    }

    public int[] getVertexNormalIndices() {
        return vertexNormalIndices;
    }

    public int[] getVertexUVIndices() {
        return vertexUVIndices;
    }

    public int sizeVertex() {
        return vertexIndices.length;
    }

    public int sizeVertexNormal() {
        return vertexNormalIndices.length;
    }

    public int sizeVertexUV() {
        return vertexUVIndices.length;   
    }
}
