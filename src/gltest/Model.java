package gltest;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
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
    // private int[] ebo;
    private int[] vao;
    /* Maps names of meshes to the location of their textures */
    private Map<String, Integer> textureMap;

    private boolean hasVertexNormals;
    private int vnOffset;

    private boolean hasUvs;
    private int uvOffset;

    private Model() {
        meshMap = new HashMap<String, Mesh>();
        textureMap = new HashMap<>();
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
            int texture = textureMap.get(tmp.getName());
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            glBindVertexArray(vao[i]);
            // glDrawElements(GL_TRIANGLES, nVertices, GL_UNSIGNED_INT, 0);
            // System.out.println(nVertices+"");
            glDrawArrays(GL_TRIANGLES, 0, nVertices);
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

    private void setHasUVs(boolean hasVertexUvs, int offset) {
        this.hasUvs = hasVertexUvs;
        this.uvOffset = offset;
    }

    private void setupTextures(Map<String, Image> materialMap) {
        for (String key : materialMap.keySet()) {
            Image tmpImage = materialMap.get(key);
            int loc = TextureLoader.loadTexture((BufferedImage)tmpImage);
            textureMap.put(key, loc);
        }
    }

    private void setupBuffers() {
        int nMesh = meshes.length;

        vao = new int[nMesh];
        vbo = new int[nMesh];
        // ebo = new int[nMesh];

        /* FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip(); */
        FloatBuffer verticesBuffer = null;
        for (int i = 0; i < vao.length; i++) {
            Mesh tmpMesh = meshes[i];
            System.out.printf("Setting up Mesh %s\n", tmpMesh.getName());
            int[] indices = tmpMesh.getVertexIndices();
            for (int z = 0; z < indices.length; z += 3) {
                System.out.printf("v %d %d %d\n", indices[z], indices[z+1], indices[z+2]);
            }
            //int nvertices = indices.length;
            if (hasUvs && hasVertexNormals) {
                int[] vnIndices = tmpMesh.getVertexNormalIndices();
                int[] uvIndices = tmpMesh.getVertexUVIndices();
                System.out.printf("indices %d vnIndices %d vtIndices %d\n", indices.length, vnIndices.length, uvIndices.length);
                // int[] tmpIndices = new int[indices.length + vnIndices.length + uvIndices.length];
                
                float[] buffer = new float[(indices.length + vnIndices.length + uvIndices.length)*3];
                System.out.printf("vnOffset %d\nuvOffset %d\n buffer %d\n vertices %d\n", vnOffset, uvOffset, buffer.length, vertices.length);
                for (int j = 0; j < buffer.length; j+=3) {
                    int content = j % 9;
                    int c1 = j / 9;
                    int index = j / 3;
                    // System.out.printf("j %d indices[] %d vnIndices[] %d uvIndices[] %d\n", j, indices[index], (vnIndices[index]+vnOffset), (uvIndices[index]+uvOffset));
                    System.out.printf("j %d c1 %d indices[] %d vnIndices[] %d uvIndices[] %d\n", j, c1, indices[c1], (vnIndices[c1]), (uvIndices[c1]));

                    if (content == 0) {
                        // System.arraycopy(vertices, indices[c1], buffer, j, 3);
                        int k = (indices[c1])*3;
                        System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
                        System.arraycopy(vertices, k, buffer, j, 3);
                    } else if (content == 3) {
                        // System.arraycopy(vertices, (vnIndices[index]+vnOffset)*3, buffer, j, 3);
                        int k = (vnIndices[c1]+vnOffset)*3;
                        System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
                        System.arraycopy(vertices, k, buffer, j, 3);
                    } else if (content == 6) {
                        // System.arraycopy(vertices, (uvIndices[index]+uvOffset)*3, buffer, j, 3);
                        int k = (uvIndices[c1]+uvOffset)*3;
                        System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
                        System.arraycopy(vertices, k, buffer, j, 3);
                    }
                } 

                verticesBuffer = BufferUtils.createFloatBuffer(buffer.length);
                verticesBuffer.put(buffer);
                verticesBuffer.flip();

                // log vertices contents
                PrintWriter logFile = null;
                try {
                    logFile = new PrintWriter(i + "vertices.txt");
                    
                    for (int j = 0; j < vertices.length; j+=3) {
                        logFile.printf("%.4f %.4f %.4f\n",
                            vertices[j], vertices[j+1], vertices[j+2] 
                        );
                    }
                    logFile.close();
                } catch (IOException ie) {
                    
                }

                // log buffer contents
                logFile = null;
                try {
                    logFile = new PrintWriter(i + "logfile.txt");
                    
                    for (int j = 0; j < buffer.length; j+=3) {
                        logFile.printf("%.4f %.4f %.4f\n",
                            buffer[j], buffer[j+1], buffer[j+2]
                        );
                    }
                    logFile.close();
                } catch (IOException ie) {
                    
                }

                // log vertices contents
                logFile = null;
                try {
                    logFile = new PrintWriter(i + "_logfile.txt");
                    
                    for (int j = 0; j < buffer.length; j+=3) {
                        int content = j % 9;
                        if (content == 0) {
                            logFile.printf("v  %d %.4f %.4f %.4f\n",
                                j, buffer[j], buffer[j+1], buffer[j+2]
                            );
                        } else if (content == 3) {
                        logFile.printf("vn %d %.4f %.4f %.4f\n",
                            j, buffer[j], buffer[j+1], buffer[j+2]
                        );
                        } else 
                        logFile.printf("vt %d %.4f %.4f %.4f\n",
                            j, buffer[j], buffer[j+1], buffer[j+2]
                        );
                    }
                    logFile.close();
                } catch (IOException ie) {
                    
                }
                /*
                System.arraycopy(indices, 0, tmpIndices, 0, indices.length);
                int sj = indices.length;
                int ej = sj + vnIndices.length;
                System.out.printf("Copying vertex normals from %d to %d\n", sj, ej);
                for (int j = sj; j < ej; j++) {
                    tmpIndices[j] = vnIndices[j-sj] + vnOffset;
                }

                sj = ej;
                ej = sj + uvIndices.length;
                // System.out.printf("tmpIndices.len: %d uvIndices.len: %d sj: %d ej: %d\n", tmpIndices.length, uvIndices.length, sj, ej);
                System.out.printf("Copying vertex textures from %d to %d\n", sj, ej);
                for (int j = sj; j < ej; j++) {
                    tmpIndices[j] = uvIndices[j-sj] + uvOffset;
                }
                int nVert = indices.length;
                indices = tmpIndices;
                
                /*PrintWriter vnFile = null;
                try {
                    vnFile = new PrintWriter(i+ "vnfile.txt");
                    int hx = nVert;
                    int ex = hx + vnIndices.length;
                    for (int j = hx; j < ex; j+=3) {
                        vnFile.printf("f %d %d %d\n", 
                            indices[j]-vnOffset, indices[j+1]-vnOffset, indices[j+2]-vnOffset
                        );
                    }
                    vnFile.close();
                } catch (IOException ie) {
                    
                }

                PrintWriter vtFile = null;
                try {
                    vtFile = new PrintWriter(i+ "vtfile.txt");
                    int hx = nVert + vnIndices.length;
                    int ex = hx + uvIndices.length;
                    for (int j = hx; j < ex; j+=3) {
                        vtFile.printf("f %d %d %d\n", 
                            indices[j]-uvOffset, indices[j+1]-uvOffset, indices[j+2]-uvOffset
                        );
                    }
                    vtFile.close();
                } catch (IOException ie) {
                    
                }

                // System.out.printf("tmpIndices.len: %d uvIndices.len: %d sj: %d ej: %d\n", tmpIndices.length, uvIndices.length, sj, ej);
                PrintWriter logFile = null;
                try {
                    logFile = new PrintWriter(i+ "logfile.txt");
                    int hx = nVert;
                    int thx = hx + vnIndices.length;
                    for (int j = 0; j < hx; j+=3) {
                        logFile.printf("f %d/%d/%d %d/%d/%d %d/%d/%d\n", 
                            indices[j], indices[j+hx]-vnOffset,  indices[j+thx]-uvOffset,
                            indices[j+1], indices[j+hx+1]-vnOffset, indices[j+thx+1]-uvOffset,
                            indices[j+2], indices[j+hx+2]-vnOffset, indices[j+thx+2]-uvOffset
                        );
                    }
                } catch (IOException ie) {
                    
                } finally {
                    logFile.close();
                }*/
            }
             /*else if (hasVertexNormals) {
                int[] vnIndices = tmpMesh.getVertexNormalIndices();
                int[] tmpIndices = new int[indices.length + vnIndices.length];
                
                System.arraycopy(indices, 0, tmpIndices, 0, indices.length);
                int sj = indices.length;
                for (int j = indices.length; j < tmpIndices.length; j++) {
                    tmpIndices[j] = vnIndices[j-sj] + vnOffset;
                }
                indices = tmpIndices;
                
                PrintWriter logFile = null;
                try {
                    logFile = new PrintWriter(i+ "logfile.txt");
                    int hx = indices.length/3;
                    for (int j = 0; j < hx; j+=3)
                    {
                        logFile.printf("f %d//%d %d//%d %d//%d\n", indices[j], indices[j+hx], indices[j+1], indices[j+hx+1], indices[j+2], indices[j+hx+2]);
                    }
                } catch (IOException ie) {
                    
                } finally {
                    logFile.close();
                }
            }*/

            /*
            IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
            indicesBuffer.put(indices);
            indicesBuffer.flip();
            */
            vao[i] = glGenVertexArrays();
            vbo[i] = glGenBuffers();
            // ebo[i] = glGenBuffers();

            glBindVertexArray(vao[i]);

            glBindBuffer(GL_ARRAY_BUFFER, vbo[i]);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            // glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[i]);
            // glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            // int stride = hasVertexNormals?24:0;
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 36, 0);
            glEnableVertexAttribArray(0);

            // System.out.println(vnOffset);
            // System.out.println(vnOffset * 12);
            if (hasVertexNormals) {
                glVertexAttribPointer(1, 3, GL_FLOAT, false, 36, 12);
                glEnableVertexAttribArray(1);
            }
            if (hasUvs) {
                glVertexAttribPointer(2, 3, GL_FLOAT, false, 36, 24);
                glEnableVertexAttribArray(2);
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
            // glDeleteBuffers(ebo[i]);
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
        private List<float[]> vertexUVs;
        private List<Mesh> meshes;
        private Map<String, Image> materialMap;
        private String name;

        /**
         * Constructor
         */
        public Builder(String name) {
            this.name = name;
            vertices = new ArrayList<>();
            vertexNormals = new ArrayList<>();
            vertexUVs = new ArrayList<>();
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

        public void addVertexUV(float[] uv) {
            if (uv.length == 2) {
                float[] tmp = new float[]{uv[0], uv[1], 0f};
                vertexUVs.add(tmp);
            } else if (uv.length == 3) {
                vertexUVs.add(uv);
            } else {
                throw new IllegalArgumentException("addVertexUV should be called with length 2 or 3, not len " + uv.length);
            }
        }

        public void addVertexUV(float x, float y, float z) {
            vertexUVs.add(new float[]{x, y, z});
        }

        public void setMaterialLib(Map<String, Image> materialMap) {
            this.materialMap = materialMap;
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
            int nUv = vertexUVs.size();

            System.out.printf("nVert: %d nVn: %d nUV: %d\n", nVertices, nVn, nUv);

            /*
            if (nVn != nVertices && nVn > 0) {
                System.err.printf("nVertices is size %d while nVn is size %d\n", nVertices, nVn);
                System.exit(1);
            }*/

            int requiredSize = (nVertices + nVn) * 3 + nUv * 3;
            float[] modelVertices = new float[requiredSize];

            System.out.printf("required size: %d\n", requiredSize);
            for (int i = 0; i < nVertices; i++) {
                float[] tmp = vertices.get(i);
                //float[] tmpVn = vertexNormals.get(i+1);
                System.arraycopy(tmp, 0, modelVertices, i*3, 3);
                //System.arraycopy(tmpVn, 0, modelVertices, (i+1)*3, 3);
            }
            int start = nVertices;
            int end = nVertices + nUv;
            for (int i = start; i < end; i++) {
                // System.out.printf("i-start: %d, i*3: %d\n", i-start, i*3);
                float[] tmpUv = vertexUVs.get(i-start);
                System.arraycopy(tmpUv, 0, modelVertices, i*3, 3);
            }  
            //System.out.printf("Number of norms: %d\n Number of vertices: %d\n", nVn, nVertices);

            int uvStart = start;
            start = end;
            end = start + nVn;
            for (int i = start; i < end; i++) {
                float[] tmpVn = vertexNormals.get(i-start);
                System.arraycopy(tmpVn, 0, modelVertices, i*3, 3);
            }
            model.setVertices(modelVertices);
            if (nVn > 0) {
                model.setHasVertexNormals(true, start);
            }
            if (nUv > 0) {
                model.setHasUVs(true, uvStart);
            }
            model.setupTextures(materialMap);
            model.setupBuffers();

            System.out.printf("Built model %s with\n  vertices: %d\n  normals: %d\n  texture vertices: %d\n", model.getName(), vertices.size(), vertexNormals.size(), vertexUVs.size());

            return model;
        }

        public Mesh getMeshForKey(String key) {
            for (Mesh mesh : meshes) {
                if (mesh.getName().equals(key)) {
                    return mesh;
                }
            }
            return null;
        }
    }
}
