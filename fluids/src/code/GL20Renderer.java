package code;

import java.io.IOException;
//import java.util.HashMap;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLES20;
import android.util.SparseArray;

import code.Constants;
import code.Game;
import code.IO;

public class GL20Renderer implements GLSurfaceView.Renderer {

	//private static float ratio;//How many times higher the screen is compared to width
	private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private static float ratio;
    private static boolean gameHasBeenInited;
	//GLSL parameter locations
	public static class GLSL_Locations
	{
		public static int location_position;
		public static int location_texture_coordinates;
		public static int location_model_view_matrix;
		public static int location_rotation;
		public static int location_translation;
		public static int location_scale;
		public static int location_texture_sampler_0;
	}

	//private static HashMap<Integer, Model> models;
	private static SparseArray<Model> models;
	
	
	private static int shaderProgram;
	private static int vertex_shader;
	private static int fragment_shader;
	
	public static float red = 0.0f;
	
	@Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		gameHasBeenInited = false;
        GLES20.glClearColor(0.1f, 0.2f, 0.1f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);//Enable culling. If nothing is drawn we are propably
        									 //looking objects from behind and back sides aren't drawn
        models = new SparseArray<Model>();
        try {
			initShaders();
			//add models TMP. Move to level loader
			models.put(Constants.MODEL_BALL, new Model(fluids.generated.R.raw.ball16_mod, fluids.generated.R.raw.ball16_tco, fluids.generated.R.raw.tmp, 64));
			
			models.put(Constants.MODEL_INVENTORY_BLOCK_BACKGROUND, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.inventory_background_texture, 128));
			models.put(Constants.MODEL_GRID_BLOCK_BACKGROUND, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.grid_texture, 128));
			models.put(Constants.MODEL_MENU_BACKGROUND, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.menu_background, 2048));
			

			models.put(Constants.MODEL_BOX, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.box_texture, 128));
			models.put(Constants.MODEL_START, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.start_texture, 128));
			models.put(Constants.MODEL_END, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.end_texture, 128));
			models.put(Constants.MODEL_TRIANGLE, new Model(fluids.generated.R.raw.triangle_mod, fluids.generated.R.raw.triangle_tco, fluids.generated.R.raw.triangle_texture, 128));
			models.put(Constants.MODEL_WALL, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.wall_texture, 128));
			
			models.put(Constants.MODEL_RETURN_BUTTON, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.return_button_texture, 512));
			models.put(Constants.MODEL_PLAY_BUTTON, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.play_button_texture, 512));
			models.put(Constants.MODEL_QUIT_GAME_BUTTON, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.quit_button_texture, 512));
			models.put(Constants.MODEL_DROP_BUTTON, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.drop_button_texture, 512));
			models.put(Constants.MODEL_LEVEL_EDITOR_BUTTON, new Model(fluids.generated.R.raw.box_mod, fluids.generated.R.raw.box_tco, fluids.generated.R.raw.level_editor_button_texture, 256));
        } catch (IOException e) {
			e.printStackTrace();
			Fluids.quit();
		}
    }
	@Override
    public void onDrawFrame(GL10 unused) {
    	if (Game.getState() == Constants.STATE_MENU)
    	{
    		drawMenu();
    	}
    	else if (Game.getState() == Constants.STATE_IN_GAME)
    	{
        	GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        	drawGame();
        	for (GameObject b : Game.getInGameButtons()) //Draw in game buttons
        	{
        		models.get(b.getModelId()).draw(mMVPMatrix, b.getPosition(), b.getModelScale(), b.getRotation());
        	}
    	}
    	else if (Game.getState() == Constants.STATE_IN_LEVEL_EDITOR)
    	{
        	GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        	drawGame();
        	for (GameObject b : Game.getLevelEditorButtons()) //Draw level editor buttons
        	{
        		models.get(b.getModelId()).draw(mMVPMatrix, b.getPosition(), b.getModelScale(), b.getRotation());
        	}
    	}
    	else if (Game.getState() == Constants.STATE_ERROR)
    	{
    		GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    	}
    	models.get(Constants.MODEL_RETURN_BUTTON).draw(mMVPMatrix, //TMP draw touch position
    			Game.getTouchPosition(), new Vec2f(Constants.BLOCK_SCALE*0.1f,Constants.BLOCK_SCALE*0.1f), 0.0f);
    	
    	checkForGLErrors("onDrawFrame()");
    }
	@Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
    	GLES20.glViewport(0, 0, width, height);
        //For some reason left and right have to be switched. Otherwise we end up looking triangles
        //from behind.
    	ratio = ((float)height) / ((float)width);
    	//We call the games init function now that we know the ratio
    	if (!gameHasBeenInited)
    	{
    		Game.init();
    		gameHasBeenInited = true;
    	}
        Matrix.frustumM(mProjMatrix, 0,
        		Constants.OPENGL_SCREEN_HALF_WIDTH, -Constants.OPENGL_SCREEN_HALF_WIDTH,
        		-ratio*Constants.OPENGL_SCREEN_HALF_WIDTH, ratio*Constants.OPENGL_SCREEN_HALF_WIDTH,
        		3, 3.1f);
    }	
    
    private void initShaders() throws IOException
    {
    	String vertex_shader_string = IO.readShader(fluids.generated.R.raw.vertex_shader);
    	String fragment_shader_string = IO.readShader(fluids.generated.R.raw.fragment_shader);
    	
    	vertex_shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);//Tämä failaa!!!
    	fragment_shader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
    	
    	GLES20.glShaderSource(vertex_shader, vertex_shader_string);
    	GLES20.glShaderSource(fragment_shader, fragment_shader_string);
    	
    	GLES20.glCompileShader(vertex_shader);
    	GLES20.glCompileShader(fragment_shader);
    	
    	//Check if shaders were compiled
    	int[] result = new int[1];
    	GLES20.glGetShaderiv(vertex_shader, GLES20.GL_COMPILE_STATUS, result, 0);
    	if (result[0] == GLES20.GL_FALSE)
    	{
    		String log = GLES20.glGetShaderInfoLog(vertex_shader);//Return empty. Known bug.
    		throw new IOException("Compiling vertex shader failed:\n" + log);
    	}
    	GLES20.glGetShaderiv(fragment_shader, GLES20.GL_COMPILE_STATUS, result, 0);
    	if (result[0] == GLES20.GL_FALSE)
    	{
    		String log = GLES20.glGetShaderInfoLog(fragment_shader);//Return empty. Known bug.
    		throw new IOException("Compiling fragment shader failed:\n" + log);    		
    	}
    	
    	//Create shader program
    	shaderProgram = GLES20.glCreateProgram();
    	GLES20.glAttachShader(shaderProgram, vertex_shader);
    	GLES20.glAttachShader(shaderProgram, fragment_shader);
    	GLES20.glLinkProgram(shaderProgram);
    	GLES20.glUseProgram(shaderProgram);
    	//Link buffers to shaders
    	GLSL_Locations.location_position = GLES20.glGetAttribLocation(shaderProgram, "vertex_position");
    	GLSL_Locations.location_texture_coordinates = GLES20.glGetAttribLocation(shaderProgram, "tex");
    	//Link uniforms to shaders
    	GLSL_Locations.location_texture_sampler_0 = GLES20.glGetUniformLocation(shaderProgram, "sampler");
    	GLSL_Locations.location_model_view_matrix = GLES20.glGetUniformLocation(shaderProgram, "model_view_matrix");
    	GLSL_Locations.location_translation = GLES20.glGetUniformLocation(shaderProgram, "translation");
    	GLSL_Locations.location_scale = GLES20.glGetUniformLocation(shaderProgram, "scale");
    	GLSL_Locations.location_rotation = GLES20.glGetUniformLocation(shaderProgram, "rotation");
    	
		GLES20.glUseProgram(0);
    	
    	checkForGLErrors("initShaders()");
    }

    public static void clear()
    {
    	GLES20.glDetachShader(shaderProgram, vertex_shader);
    	GLES20.glDetachShader(shaderProgram, fragment_shader);
    	GLES20.glDeleteShader(vertex_shader);
    	GLES20.glDeleteShader(fragment_shader);
    	GLES20.glDeleteProgram(shaderProgram);
    	
    	for (int i = 0; i < models.size(); i++)
    	{
    		models.get(i).clear();
    	}
    	checkForGLErrors("Renderer.clear()");
    }
    
    public void drawMenu()
    {
    	GLES20.glClearColor(0.5f, 0.0f, 0.7f, 1.0f);
    	GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		Matrix.setLookAtM(mVMatrix, 0, 0.f, 0.f, -3.f, 0.f, 0.f, 0.f, 0.f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        
        //Draw menu background
        models.get(Constants.MODEL_MENU_BACKGROUND).draw(mMVPMatrix, new Vec2f(0.0f, 0.0f),
        		new Vec2f(Constants.OPENGL_SCREEN_WIDTH, ratio*Constants.OPENGL_SCREEN_WIDTH), 0.0f);
        //Draw buttons
        for (GameObject b : Game.getMenuButtons())
        {
        	models.get(b.getModelId()).draw(mMVPMatrix, b.getPosition(), b.getModelScale(), b.getRotation());
        }
        
        GLES20.glFlush();
    }
    
    public void drawGame()
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        Matrix.setLookAtM(mVMatrix, 0, 0.f, 0.f, -3.f, 0.f, 0.f, 0.f, 0.f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        
        drawGrid();
        drawInventory();
        
        if (Game.getSelectedObjectOrigin() != "NULL") //If there is a object selected or not
        { //We assume that object being moved is block sized (Only blocks can be moved)
        	GameObject selected = Game.getSelectedObject();
        	models.get(selected.getModelId()).draw(mMVPMatrix, 
			Game.getTouchPosition(), selected.getModelScale(), selected.getRotation());
        }
        for (GameObject o : Game.getObjects())//No gameobjects currently
        {
        	models.get(o.getModelId()).draw(mMVPMatrix, o.getPosition(), o.getModelScale(), o.getRotation());
        }
        for (GameObject o : Game.getBalls()) // Draw balls
        {
        	models.get(o.getModelId()).draw(mMVPMatrix, o.getPosition(), o.getModelScale(), o.getRotation());
        }
        GLES20.glFlush();
    }
    private void drawGrid()
    {    	
    	for (int i = 0; i < Constants.GRID_HEIGHT; i++)
    	{
    		for (int j = 0; j < Constants.GRID_WIDTH; j++)
        	{
    			//Draw block background
    	        models.get(Constants.MODEL_GRID_BLOCK_BACKGROUND).draw(mMVPMatrix, 
    	        		Game.getGridBlockCenter(j,i), new Vec2f(Constants.BLOCK_SCALE,Constants.BLOCK_SCALE), 0.0f);
    	        //draw frame around block
    	        models.get(Constants.MODEL_INVENTORY_BLOCK_BACKGROUND).draw(mMVPMatrix,
    	        		new Vec2f(Game.getGridBlockCenter(j,i).x-Constants.BLOCK_HALF_SCALE, 
    	        				Game.getGridBlockCenter(j,i).y), new Vec2f(0.01f, 1.0f), 0.0f);
    	        models.get(Constants.MODEL_INVENTORY_BLOCK_BACKGROUND).draw(mMVPMatrix,
    	        		new Vec2f(Game.getGridBlockCenter(j,i).x+Constants.BLOCK_HALF_SCALE, 
    	        				Game.getGridBlockCenter(j,i).y), new Vec2f(0.01f, 1.0f), 0.0f);
    	        models.get(Constants.MODEL_INVENTORY_BLOCK_BACKGROUND).draw(mMVPMatrix,
    	        		new Vec2f(Game.getGridBlockCenter(j,i).x, 
    	        				Game.getGridBlockCenter(j,i).y-Constants.BLOCK_HALF_SCALE), new Vec2f(1.0f, 0.01f), 0.0f);
    	        models.get(Constants.MODEL_INVENTORY_BLOCK_BACKGROUND).draw(mMVPMatrix,
    	        		new Vec2f(Game.getGridBlockCenter(j,i).x, 
    	        				Game.getGridBlockCenter(j,i).y+Constants.BLOCK_HALF_SCALE), new Vec2f(1.0f, 0.01f), 0.0f);
    	        //TODO more efficient way
    	        /*
    	        for (int x = 0; x < Constants.GRID_WIDTH; x++)
    	        {
    	        	models.get(Constants.MODEL_INVENTORY_BLOCK_BACKGROUND).draw(mMVPMatrix,
        	        		new Vec2f(Game.getGridBlockCenter(j,i).x, 
        	        				Game.getGridBlockCenter(j,i).y+Constants.BLOCK_HALF_SCALE), 
        	        				new Vec2f(0.01f, (float)Constants.GRID_HEIGHT));
    	        }*/
    	        
    			GameObject block = Game.getBlock(j, i);

    	        if (  block != null  && block != Game.getSelectedObject())
    	        {
    	        	models.get(block.getModelId()).draw(mMVPMatrix, block.position, block.getModelScale(), block.getRotation());
    	        }
        	}
    	}
    }
    private void drawInventory()
    {    	
    	for (int i = 0; i < Constants.INVENTORY_HEIGHT; i++)
    	{
    		for (int j = 0; j < Constants.INVENTORY_WIDTH; j++)
        	{
    	        models.get(Constants.MODEL_INVENTORY_BLOCK_BACKGROUND).draw(mMVPMatrix, 
    	        		Game.getInventoryBlockCenter(j,i), new Vec2f(Constants.BLOCK_SCALE,Constants.BLOCK_SCALE), 0.0f);
    	        //draw frame around block
    	        models.get(Constants.MODEL_GRID_BLOCK_BACKGROUND).draw(mMVPMatrix,
    	        		new Vec2f(Game.getGridBlockCenter(j,i).x-Constants.BLOCK_HALF_SCALE, 
    	        				Game.getGridBlockCenter(j,i).y), new Vec2f(0.005f, 1.0f), 0.0f);
    	        models.get(Constants.MODEL_GRID_BLOCK_BACKGROUND).draw(mMVPMatrix,
    	        		new Vec2f(Game.getGridBlockCenter(j,i).x+Constants.BLOCK_HALF_SCALE, 
    	        				Game.getGridBlockCenter(j,i).y), new Vec2f(0.005f, 1.0f), 0.0f);
    	        models.get(Constants.MODEL_GRID_BLOCK_BACKGROUND).draw(mMVPMatrix,
    	        		new Vec2f(Game.getGridBlockCenter(j,i).x, 
    	        				Game.getGridBlockCenter(j,i).y-Constants.BLOCK_HALF_SCALE), new Vec2f(1.0f, 0.005f), 0.0f);
    	        models.get(Constants.MODEL_GRID_BLOCK_BACKGROUND).draw(mMVPMatrix,
    	        		new Vec2f(Game.getGridBlockCenter(j,i).x, 
    	        				Game.getGridBlockCenter(j,i).y+Constants.BLOCK_HALF_SCALE), new Vec2f(1.0f, 0.005f), 0.0f);
    	        
    	        GameObject block = Game.getInventoryBlock(j, i);

    	        if (block != null && //Draw block 
    	        	((block != Game.getSelectedObject() && Game.getState() == Constants.STATE_IN_GAME) ||
    	        	Game.getState() == Constants.STATE_IN_LEVEL_EDITOR))
    	        {
    	        	models.get(block.getModelId()).draw(mMVPMatrix, block.position, block.getModelScale(), block.getRotation());
    	        }
        	}
    	}
    }

    //Check for OpenGL errors
    public static void checkForGLErrors(String loc)
    {
    	int error_code;
        while ((error_code = GLES20.glGetError()) != GLES20.GL_NO_ERROR )
        {
        	String msg = "An OpenGL error occured in " + loc  + ". Error Code:\n" + 
        			Integer.toHexString(error_code);
        	
        	throw new RuntimeException(msg);
        }
    }
    public static float getRatio()
    {
    	if (ratio == 0.0)
    	{
    		Game.setState(Constants.STATE_ERROR);
    	}
    	return ratio;
    }
	public static int getShaderProgram()
	{
		return shaderProgram;
	}

}