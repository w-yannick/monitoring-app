package oronos.oronosmobileapp.widgets.openGLUtils;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * UtilsOpenGL Class
 */

public class UtilsOpenGL {
    /**
     * Takes a txt file(shaders) as an input
     * Returns the output with String format
     * @param resourceId referes to the resource to open (shader.txt)
     */
    public static String RawResourceToString(final Context context, final int resourceId)
    {
        final InputStreamReader inputStreamReader = new InputStreamReader(context.getResources().openRawResource(resourceId));
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        final StringBuilder stringBuilder = new StringBuilder();

        try
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Loads the shader and compiles it
     * @param context
     * @param shaderType
     * @param resourceID referes to the resource to open (shader.txt)
     */
    public static int loadFragment(Context context, int shaderType, int resourceID)
    {
        // Load in the fragment shader.
        int handle = GLES20.glCreateShader(shaderType);
        String shader = RawResourceToString(context, resourceID);
        if(shader != null){
            GLES20.glShaderSource(handle, shader);
            GLES20.glCompileShader(handle);
            final int[] compileStatus2 = new int[1];
            GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, compileStatus2, 0);
            if(compileStatus2[0] == 0){
                GLES20.glDeleteShader(handle);
                handle = 0;
            }
        }
        if(shader == null){
            throw new RuntimeException("Error creating fragment shader");
        }

        return  handle;
    }

    /**
     * Creates the programm and links it to OpenGL
     * with the list of shaders received in parameter
     * @param shaders Table of shaders
     */
    public static int createProgram(int[] shaders)
    {
        int program = GLES20.glCreateProgram();

        if (program != 0)
        {
            for(int i = 0; i < shaders.length; i++)
            {
                GLES20.glAttachShader(program, shaders[i]);
            }

            GLES20.glLinkProgram(program);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        if (program == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return program;
    }
}
