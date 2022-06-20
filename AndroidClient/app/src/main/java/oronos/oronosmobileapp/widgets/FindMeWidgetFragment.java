package oronos.oronosmobileapp.widgets;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.joml.Vector3f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import oronos.oronosmobileapp.MainApplication;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.treeBuilder.LayoutNode;
import oronos.oronosmobileapp.widgets.openGLUtils.ObjLoader;
import oronos.oronosmobileapp.widgets.openGLUtils.Skybox;
import oronos.oronosmobileapp.widgets.openGLUtils.UtilsOpenGL;
import oronos.oronosmobileapp.dataUpdator.DataEventsManagerService;
import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.utilities.Message;

/**
 * Created by beast on 3/21/18.
 */
/**
 * FindMeWidgetFragment.java
 * Fragment qui permet l'affichage et la gestion de la flèche pour le Tag FindMe.
 */
public class FindMeWidgetFragment extends LayoutNode implements DataEventsManagerService.OnCanDataReceivedListener{
	int mProgramHandle;
	private GLSurfaceView mGLSurfaceView;
	private final Vector3f mServerPosition;
	private Vector3f mRocketPosition;
	private RocketArrowRenderer mMyRenderer;

	/**
	 * Register to GPS can data
	 */
	public FindMeWidgetFragment(){
		MainApplication.getInstance().getService().addListener(this,
				"GPS1_LATITUDE",
				"GPS1_LONGITUDE",
				"GPS1_ALT_MSL",
				"GPS2_LATITUDE",
				"GPS2_LONGITUDE",
				"GPS2_ALT_MSL");
		mServerPosition = new Vector3f(Configuration.rocketsConfig.map.SERVER_LONG, Configuration.rocketsConfig.map.SERVER_LAT, 0.0f);
		mRocketPosition = new Vector3f(Configuration.rocketsConfig.map.SERVER_LONG, Configuration.rocketsConfig.map.SERVER_LAT, 0.0f);
		mMyRenderer = new RocketArrowRenderer();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mGLSurfaceView = new GLSurfaceView(getContext());

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

		if (supportsEs2)
		{
			mGLSurfaceView.setEGLContextClientVersion(2);
			mMyRenderer.init(getContext());
			mGLSurfaceView.setRenderer(mMyRenderer);
		}

		return mGLSurfaceView;
	}
	@Override
	public void onResume()
	{
		// The activity must call the GL surface view's onResume() on activity onResume().
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	public void onPause()
	{
		// The activity must call the GL surface view's onPause() on activity onPause().
		super.onPause();
		mGLSurfaceView.onPause();
	}
	@Override
	protected void addChildrenFrag(int container_id) {

	}

	/**
	 * Is called by DataEventsManagerService
	 * Is called as soon as we receive a log
	 * @param protoMessage
	 */
	@Override
	public void onReceivedCanData(Message.CanData protoMessage) {
		switch (protoMessage.getId()) {
			case "GPS1_LATITUDE":
				updateLatitude(protoMessage.getData1().getDoubleType());
				break;
			case "GPS1_LONGITUDE":
				updateLongitude(-protoMessage.getData1().getDoubleType());
				break;
			case "GPS1_ALT_MSL":
				updateAltitude(protoMessage.getData1().getDoubleType());
				break;
			case "GPS2_LATITUDE":
				updateLatitude(protoMessage.getData1().getDoubleType());
				break;
			case "GPS2_LONGITUDE":
				updateLongitude(-protoMessage.getData1().getDoubleType());
				break;
			case "GPS2_ALT_MSL":
				updateAltitude(protoMessage.getData1().getDoubleType());
				break;
		}
	}
	private void updateLongitude(double newLongitude) {
		if((float) newLongitude != mRocketPosition.x){
			mRocketPosition =  new Vector3f((float) (newLongitude), mRocketPosition.y, mRocketPosition.z);
			mMyRenderer.pointingDirection = false;
		}
	}

	private void updateLatitude(double newLatitude) {
		if((float) newLatitude != mRocketPosition.y) {
			mRocketPosition =  new Vector3f(mRocketPosition.x, (float) newLatitude, mRocketPosition.z);
			mMyRenderer.pointingDirection = false;
		}
	}

	private void updateAltitude(double newAltitude) {
		if((float) newAltitude != mRocketPosition.z){
			mRocketPosition =  new Vector3f(mRocketPosition.x,mRocketPosition.y,(float) newAltitude);
			mMyRenderer.pointingDirection = false;
		}
	}

	/**
	 * inner RocketArrowRenderer private class
	 * Renderer of the surfaceView
	 * draws the rocket
	 */
	private class RocketArrowRenderer implements GLSurfaceView.Renderer {
		private Context mContext;

		private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
		private final float[] mLightPosInEyeSpace = new float[4];
		private final float[] mLightPosInWorldSpace = new float[4];
		private static final int COORDS_PER_VERTEX = 3;

		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 80.0f;
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = 0.0f;
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		private float[] mViewMatrix = new float[16];
		private float[] mModelMatrix = new float[16];
		private float[] mProjectionMatrix = new float[16];
		private float[] mMVPMatrix = new float[16];
		private float[] mSkyViewMatrix = new float[16];
		private float[] mLightModelMatrix = new float[16];

		private boolean mObjLoaded = false;
		private boolean pointingDirection = false;

		private FloatBuffer mVertices;
		private FloatBuffer mObjTexCoords;
		private FloatBuffer mObjNormals;

		/*
        TEXTURE ET LUMIERE
        */
		private Skybox skybox;

		private int mMVPMatrixHandle;
		private int mPositionHandle;
		private int mMVMatrixHandle;
		private int mLightPosHandle;
		private int mNormalHandle;
		private int mTextureUniformHandle;
		private int mTextureCoordinateHandle;

		public void init(Context context) {
			mContext = context;
			new Thread(new Runnable() {
				public void run() {
					// Read manually the proto_rocket_obj with ObjLoader class
					ObjLoader objLoader = new ObjLoader(mContext, R.raw.apolo_obj);

					// Initialize the buffers.
					mVertices = ByteBuffer.allocateDirect(objLoader.positions.length * 4)
							.order(ByteOrder.nativeOrder()).asFloatBuffer();
					mVertices.put(objLoader.positions).position(0);

					mObjNormals = ByteBuffer.allocateDirect(objLoader.normals.length * 4)
							.order(ByteOrder.nativeOrder()).asFloatBuffer();
					mObjNormals.put(objLoader.normals).position(0);

					mObjTexCoords = ByteBuffer.allocateDirect(objLoader.textureCoordinates.length * 4)
							.order(ByteOrder.nativeOrder()).asFloatBuffer();
					mObjTexCoords.put(objLoader.textureCoordinates).position(0);

					mObjLoaded = true;
				}
			}).start();
		}

		@Override
		public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
			// Clear the scene : clear it in black
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

			GLES20.glEnable(GLES20.GL_DEPTH_TEST);

			Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

			// call UtilsOpenGL to load the shaders from their txt files into String
			int vertexShaderHandle = UtilsOpenGL.loadFragment(mContext, GLES20.GL_VERTEX_SHADER, R.raw.vertex_shader);
			int fragmentShaderHandle = UtilsOpenGL.loadFragment(mContext, GLES20.GL_FRAGMENT_SHADER, R.raw.fragment_shader);

			mProgramHandle =  UtilsOpenGL.createProgram(new int[]{vertexShaderHandle, fragmentShaderHandle});

			// Bind shaders attributes with the program
			GLES20.glBindAttribLocation(mProgramHandle, 0, "a_Position");
			GLES20.glBindAttribLocation(mProgramHandle, 1, "a_Normal");
			GLES20.glBindAttribLocation(mProgramHandle, 2, "a_Texture");

			//Attributes
			mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
			mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
			mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Texture");

			//Unoforms
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
			mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
			mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
			mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");

			// Set the active texture unit to texture unit 0.
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			// Load the texture to the Rocket
			loadRocketTexture(mContext, R.raw.redrocket);
			// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
			GLES20.glUniform1i(mTextureUniformHandle, 0);

			// SKYBOX : assign an image for each face of the skybox
			// See res/raw for the images, shaders.txt ...
			skybox = new Skybox(mContext,
							new int[] { R.raw.purplenebula_ft, R.raw.purplenebula_ft,
							R.raw.purplenebula_ft, R.raw.purplenebula_ft,
							R.raw.purplenebula_ft, R.raw.purplenebula_ft});
		}

		/**
		 * Is called to update the view
		 */
		@Override
		public void onSurfaceChanged(GL10 glUnused, int width, int height)
		{
			GLES20.glViewport(0, 0, width, height);
			final float ratio = (float) width / height;
			final float left = -ratio;
			final float right = ratio;
			final float bottom = -1.0f;
			final float top = 1.0f;
			final float near = 5.0f;
			final float far = 2000.0f;

			Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		}

		/**
		 * To draw the scene
		 */
		@Override
		public void onDrawFrame(GL10 glUnused)
		{
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			drawSkybox();
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
			Vector3f directionToTheRocket = new Vector3f(mRocketPosition).sub(mServerPosition);

			directionToTheRocket = directionToTheRocket.normalize();
			boolean flag = true;
			float first = mModelMatrix[0];
			for(int i = 1; i < mModelMatrix.length && flag; i++)
			{
				if (mModelMatrix[i] != first) flag = false;
			}

			if(!flag && !Float.isNaN(directionToTheRocket.x ) && !Float.isNaN(directionToTheRocket.y )&& !Float.isNaN(directionToTheRocket.z )){
				Matrix.setLookAtM(mModelMatrix, 0, 0, 0, 0, directionToTheRocket.x, directionToTheRocket.y, directionToTheRocket.z, 0, 0, -1);
			}
			else{
				Matrix.setIdentityM(mModelMatrix, 0);
			}

			if(mObjLoaded){
				draw();
			}
		}


		/**
		 * To draw the skybox
		 */
		private void drawSkybox()
		{
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
			skybox.draw(mMVPMatrix);
		}

		private void draw() {
			GLES20.glUseProgram(mProgramHandle);

			// Adding light to the scene
			Matrix.setIdentityM(mLightModelMatrix, 0);
			// Set Light position
			Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 7.0f);
			Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
			Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
			GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

			mVertices.position(0);
			GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mVertices);
			GLES20.glEnableVertexAttribArray(mPositionHandle);

			mObjNormals.position(0);
			GLES20.glVertexAttribPointer(mNormalHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mObjNormals);
			GLES20.glEnableVertexAttribArray(mNormalHandle);

			// Pass in the texture coordinate information
			mObjTexCoords.position(0);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,0, mObjTexCoords);
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

			Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

			// Pass in the modelview matrix.
			GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

			int numberOfTriangles = mVertices.remaining() / 3;
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numberOfTriangles);
		}

		private void loadRocketTexture(final Context context, final int resourceId)
		{
			final int[] textureHandle = new int[1];

			GLES20.glGenTextures(1, textureHandle, 0);

			if (textureHandle[0] != 0)
			{
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inScaled = false;   // No pre-scaling

				// Read in the resource
				final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

				// Bind to the texture in OpenGL
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

				// Load the bitmap into the bound texture.
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

				// Recycle the bitmap, since its data has been loaded into OpenGL.
				bitmap.recycle();
			}

			if (textureHandle[0] == 0)
			{
				throw new RuntimeException("Error loading texture.");
			}
		}

	}
}