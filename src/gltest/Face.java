package gltest;

public class Face {
    private int[] indices;

    public Face(int[] indices) {
        this.indices = new int[indices.length];
        System.arraycopy(indices, 0, this.indices, 0, indices.length);
    }

    public int[] getIndices() {
        return indices;
    }

    public int size() {
        return indices.length;
    }
}
