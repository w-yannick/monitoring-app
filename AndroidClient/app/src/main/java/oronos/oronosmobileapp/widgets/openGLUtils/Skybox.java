package oronos.oronosmobileapp.widgets.openGLUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import oronos.oronosmobileapp.R;

/**
 * Skybox class
 * contains its own shaders and program
 * Uses UtilsOpenGL
 */
public class Skybox {
    final int[] textureObjectIds = new int[1];
    FloatBuffer vertexBuffer;
    private ByteBuffer indexArray;

    private SkyboxProgram mSkyboxProgram;

    public Skybox(final Context context, int resources[]) {
        if(resources.length != 6) return;

        Bitmap texturesBmp[] = new Bitmap[resources.length];
        GLES20.glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) return;

        for(int i = 0; i < resources.length; i++)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            texturesBmp[i] = BitmapFactory.decodeResource(context.getResources(), resources[i], options);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, texturesBmp[0], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, texturesBmp[1], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, texturesBmp[2], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, texturesBmp[3], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, texturesBmp[4], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, texturesBmp[5], 0);

        for (Bitmap bitmap : texturesBmp) {
            bitmap.recycle();
        }

        // Create a unit cube.
         float vertexArray[] = new float[] {
                 -1,  1,  1,//0
                 1,  1,  1,//1
                 -1, -1,  1,//2
                 1, -1,  1,//3
                 -1,  1, -1,//4
                 1,  1, -1,//5
                 -1, -1, -1,//6
                 1, -1, -1 //7
        };

        vertexBuffer = ByteBuffer.allocateDirect(vertexArray.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexArray);
        vertexBuffer.position(0);

        indexArray = ByteBuffer.allocateDirect(6 * 6)
                .put(new byte[] {
                        1, 3, 0,
                        0, 3, 2,
                        4, 6, 5,
                        5, 6, 7,
                        0, 2, 4,
                        4, 2, 6,
                        5, 7, 1,
                        1, 7, 3,
                        5, 1, 4,
                        4, 1, 0,
                        6, 2, 7,
                        7, 2, 3
                });
        indexArray.position(0);

        mSkyboxProgram = new SkyboxProgram(context);
    }

    // Draw the Skybox
    public void draw(float[] matrix) {
        mSkyboxProgram.useProgram();
        mSkyboxProgram.setConfiguration(matrix, textureObjectIds[0]);
        GLES20.glVertexAttribPointer(mSkyboxProgram.getPositionAttributeLocation(), 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mSkyboxProgram.getPositionAttributeLocation());
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray);
    }

    /**
     * SkyboxProgram class
     * Loads the Skybox shaders
     * Creates the Skybox program with them
     * Compiles the program
     */
    final class SkyboxProgram{
        protected static final String U_MATRIX = "u_Matrix";
        protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
        protected static final String A_POSITION = "a_Position";

        private final int uMatrix;
        private final int uTextureUnit;
        private final int aPosition;

        protected int program;

        public SkyboxProgram(Context context) {

            int vertexShaderHandle = UtilsOpenGL.loadFragment(context, GLES20.GL_VERTEX_SHADER, R.raw.skybox_vertex_shader);
            int fragmentShaderHandle = UtilsOpenGL.loadFragment(context, GLES20.GL_FRAGMENT_SHADER, R.raw.skybox_fragment_shader);

            program= UtilsOpenGL.createProgram(new int[]{vertexShaderHandle, fragmentShaderHandle});

            uMatrix = GLES20.glGetUniformLocation(program, U_MATRIX);
            uTextureUnit = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
            aPosition = GLES20.glGetAttribLocation(program, A_POSITION);
        }
        // Bind the texture to fragment shader
        public void setConfiguration(float[] matrix, int textureId) {
            GLES20.glUniformMatrix4fv(uMatrix, 1, false, matrix, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId);
            GLES20.glUniform1i(uTextureUnit, 0);
        }
        public int getPositionAttributeLocation() {
            return aPosition;
        }

        public void useProgram() {
            GLES20.glUseProgram(program);
        }
    }
}
