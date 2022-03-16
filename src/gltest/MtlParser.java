package gltest;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * MtlParser is responsible for parsing mtl files and returning key-value
 * associations of textures.
 */
public class MtlParser {
    private static final String NEWMTL = "newmtl";
    private static final String MAP_KD = "map_Kd";
    private static final String COMMENT = "#";

    /**
     * parse mtlPath and return textures in a map.
     * @param mtlPath Path to mtl file to parse
     * @return key-value pairs of texture name and texture data
     */
    public static Map<String, Image> parse(String mtlPath) {
        Map<String, Image> materialMap = new HashMap<>();
        Scanner mtlFile;
        System.out.println(mtlPath);
        String parentDir = mtlPath.substring(0, mtlPath.lastIndexOf('/', mtlPath.length()-1));

        
        try {
            mtlFile = new Scanner(new FileInputStream(mtlPath));
        } catch (FileNotFoundException fe) {
            return materialMap;
        }

        int lineCount = 0;
        String nextKey = null;
        while (mtlFile.hasNextLine()) {
            lineCount++;
            String line = mtlFile.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            
            if (line.startsWith(COMMENT)) {
                continue;
            } else if (line.startsWith(NEWMTL)) {
                if (nextKey == null) {
                    String[] tokens = line.split(" ");
                    if (tokens.length >= 2) {
                        nextKey = tokens[1];
                    } else {
                        System.out.printf("Line %d: newmtl is missing a name", lineCount);
                        System.exit(1);
                    }
                } else {
                    System.out.printf("Line %d: expected to see %s before seeing another %s", lineCount, MAP_KD, NEWMTL);
                    System.exit(1);
                }
            } else if (line.startsWith(MAP_KD)) {
                if (nextKey != null) {
                    String texFileName = null;

                    String[] tokens = line.split(" ");
                    if (tokens.length >= 2) {
                        texFileName = tokens[tokens.length-1];
                    } else {
                        System.out.printf("Line %d: %s is missing a filename", lineCount, MAP_KD);
                        System.exit(1);
                    }
                    Image image = null;
                    String texFullPath = parentDir + File.separatorChar + texFileName;
                    try {
                        image = ImageIO.read(new File(texFullPath));
                    } catch (IOException ie) {
                        System.out.printf("Line %d: %s threw exception\n", lineCount, texFullPath);
                        System.out.println(ie);
                        System.exit(1);
                    }

                    if (image == null) {
                        System.out.printf("Line %d: %s is not an encoding supported by ImageIO", lineCount, texFileName);
                        System.exit(1);
                    }

                    materialMap.put(nextKey, image);
                    nextKey = null;
                } else {
                    System.out.printf("Line %d: nextKey is unexpectedly null", lineCount);
                    System.exit(1);
                }
            }
        }

        return materialMap;
    }

    private static void testNormal() {
        String path = System.getProperty("user.dir") + "/data/engineer9/engineer.mtl";
        Map<String, Image> texMap = parse(path);

        System.out.printf("File %s with keys:\n", path);
        for (String key : texMap.keySet()) {
            System.out.println("\t" + key);
        }
    }

    public static void main(String[] args) {
        testNormal();
    }
}
