package oronos.oronosmobileapp.widgets.openGLUtils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * https://stackoverflow.com/questions/41012719/how-to-load-and-display-obj-file-in-android-with-opengl-es-2
 */

public class ObjLoader {
    public final float[] normals;
    public final float[] textureCoordinates;
    public final float[] positions;

    public ObjLoader(Context context, int objResource) {

        Vector<Float> vertices = new Vector<>();
        Vector<Float> normals = new Vector<>();
        Vector<Float> textures = new Vector<>();
        Vector<String> faces = new Vector<>();

        BufferedReader reader = null;
        try {

            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(objResource));
            reader = new BufferedReader(in);

            // read the object and assign each data (vertices, normals, textures)
            // to the right vector
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitedLine = line.split(" ");
                switch (splitedLine[0]) {
                    case "v":
                        // vertices
                        vertices.add(Float.valueOf(splitedLine[1]));
                        vertices.add(Float.valueOf(splitedLine[2]));
                        vertices.add(Float.valueOf(splitedLine[3]));
                        break;
                    case "vt":
                        // textures
                        textures.add(Float.valueOf(splitedLine[1]));
                        textures.add(Float.valueOf(splitedLine[2]));
                        break;
                    case "vn":
                        // normals
                        normals.add(Float.valueOf(splitedLine[1]));
                        normals.add(Float.valueOf(splitedLine[2]));
                        normals.add(Float.valueOf(splitedLine[3]));
                        break;
                    case "f":
                        // faces: vertex/texture/normal
                        faces.add(splitedLine[1]);
                        faces.add(splitedLine[2]);
                        faces.add(splitedLine[3]);
                        break;
                }
            }
        } catch (IOException e) {
            // cannot load or read file
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        int numberFaces = faces.size();
        this.normals = new float[numberFaces * 3];
        textureCoordinates = new float[numberFaces * 2];
        positions = new float[numberFaces * 3];
        int positionIndex = 0;
        int normalIndex = 0;
        int textureIndex = 0;
        // Fill all the tables variables
        for (String face : faces) {
            String[] parts = face.split("/");

            int index = 3 * (Short.valueOf(parts[0]) - 1);
            positions[positionIndex++] = vertices.get(index++);
            positions[positionIndex++] = vertices.get(index++);
            positions[positionIndex++] = vertices.get(index);

            index = 2 * (Short.valueOf(parts[1]) - 1);
            textureCoordinates[normalIndex++] = textures.get(index++);
            // NOTE: Bitmap gets y-inverted
            textureCoordinates[normalIndex++] = 1 - textures.get(index);

            index = 3 * (Short.valueOf(parts[2]) - 1);
            this.normals[textureIndex++] = normals.get(index++);
            this.normals[textureIndex++] = normals.get(index++);
            this.normals[textureIndex++] = normals.get(index);
        }
    }
}