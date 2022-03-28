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
    private int[] vao;
    /* Maps names of meshes to the location of their textures */
    private Map<String, Integer> textureMap;

    private boolean hasVertexNormals;
    private int vnOffset;

    private boolean hasUvs;
    private int uvOffset;

    private static int defaultTexture;

    private Model() {
        meshMap = new HashMap<String, Mesh>();
        textureMap = new HashMap<>();
    }

    public static void setDefaultTexture(int defaultTexture) {
        Model.defaultTexture = defaultTexture;
    }

    /**
     * draw this model.
     */
    public void draw(Matrix4f viewMatrix, int replacementTexture) {
        for (int i = 0; i < meshes.length; i++) {
            Mesh tmp = meshes[i];
            Shader program = tmp.getProgram();
            program.use();
            program.setUniform("viewMatrix", viewMatrix);
            int nVertices = tmp.getVertexIndices().length;

            Integer texture = replacementTexture < 0 ? textureMap.get(tmp.getName()) : replacementTexture;
            if (texture != null)
                glBindTexture(GL_TEXTURE_2D, texture);
            else
                glBindTexture(GL_TEXTURE_2D, defaultTexture);
            glBindVertexArray(vao[i]);
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
        for (Mesh mesh : meshes) {
            if (tmpMesh == mesh) {
                mesh.setProgram(program);
                break;
            }
        }
        tmpMesh.setProgram(program);
    }

    /**
     * setProgramForAllKeys set shader program for all named meshes in the model
     * @param program Shader program to use for each mesh
     */
    public void setProgramForAllKeys(Shader program) {
        for (String key : meshMap.keySet()) {
            setProgramForKey(key, program);
        }
    }

    /**
     * getName of model
     * @return
     */
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
        
        FloatBuffer verticesBuffer = null;
        for (int i = 0; i < vao.length; i++) {
            Mesh tmpMesh = meshes[i];
            System.out.printf("Setting up Mesh %s\n", tmpMesh.getName());
            int[] indices = tmpMesh.getVertexIndices();
            for (int z = 0; z < indices.length; z += 3) {
                // System.out.printf("v %d %d %d\n", indices[z], indices[z+1], indices[z+2]);
            }
            if (hasUvs && hasVertexNormals) {
                int[] vnIndices = tmpMesh.getVertexNormalIndices();
                int[] uvIndices = tmpMesh.getVertexUVIndices();
                
                float[] buffer = new float[(indices.length + vnIndices.length + uvIndices.length)*3];
                for (int j = 0; j < buffer.length; j+=3) {
                    int content = j % 9;
                    int c1 = j / 9;

                    if (content == 0) {
                        int k = (indices[c1])*3;
                        // System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
                        System.arraycopy(vertices, k, buffer, j, 3);
                    } else if (content == 3) {
                        int k = (vnIndices[c1]+vnOffset)*3;
                        // if (k >= vertices.length) {
                        //     System.out.println(k);
                        // } else {
                        // System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
                        System.arraycopy(vertices, k, buffer, j, 3);
                        // }
                    } else if (content == 6) {
                        int k = (uvIndices[c1]+uvOffset)*3;
                        // System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
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
            } else if (hasVertexNormals) {
                int[] vnIndices = tmpMesh.getVertexNormalIndices();
                int[] uvIndices = new int[vnIndices.length];
                
                float[] buffer = new float[(indices.length + vnIndices.length + uvIndices.length)*3];
                for (int j = 0; j < buffer.length; j+=3) {
                    int content = j % 9;
                    int c1 = j / 9;

                    if (content == 0) {
                        int k = (indices[c1])*3;
                        // System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
                        System.arraycopy(vertices, k, buffer, j, 3);
                    } else if (content == 3) {
                        int k = (vnIndices[c1]+vnOffset)*3;
                        // if (k >= vertices.length) {
                        //     System.out.println(k);
                        // } else {
                        // System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
                        System.arraycopy(vertices, k, buffer, j, 3);
                        // }
                    } /*else if (content == 6) {
                        int k = (uvIndices[c1]+uvOffset)*3;
                        // System.out.printf("%d %.4f %d %.4f %d %.4f \n", k, vertices[k], k+1, vertices[k+1], k+2, vertices[k+2]);
                        System.arraycopy(vertices, k, buffer, j, 3);
                    }*/
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
            }
            
            vao[i] = glGenVertexArrays();
            vbo[i] = glGenBuffers();

            glBindVertexArray(vao[i]);

            glBindBuffer(GL_ARRAY_BUFFER, vbo[i]);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            glVertexAttribPointer(0, 3, GL_FLOAT, false, 36, 0);
            glEnableVertexAttribArray(0);

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

        /**
         * addVertexUV to the model
         * @param uv uv coordinates to add
         * @throws IllegalArgumentException if size of uv is not 2 or 3
         */
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

        /**
         * addVertexUV to the model
         * @param x
         * @param y
         * @param z
         */
        public void addVertexUV(float x, float y, float z) {
            vertexUVs.add(new float[]{x, y, z});
        }

        /**
         * setMaterialLibrary used by this model.
         */
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

            // System.out.printf("nVert: %d nVn: %d nUV: %d\n", nVertices, nVn, nUv);

            int requiredSize = (nVertices + nVn) * 3 + nUv * 3;
            float[] modelVertices = new float[requiredSize];

            // System.out.printf("required size: %d\n", requiredSize);
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

            System.out.printf("Built model %s with\n  vertices: %d\n  normals: %d\n  texture vertices: %d\n", 
                    model.getName(), vertices.size(), vertexNormals.size(), vertexUVs.size());

            return model;
        }

        /**
         * getMeshForKey search list of meshes for mesh matching key
         * @param key
         * @return
         */
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
