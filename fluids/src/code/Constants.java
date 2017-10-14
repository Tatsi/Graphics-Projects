package code;

public interface Constants{
	//OpenGL
	public static float OPENGL_SCREEN_HALF_WIDTH = 1.0f;//Half width of screen in GL coordinates
	public static float OPENGL_SCREEN_WIDTH = 2.f * OPENGL_SCREEN_HALF_WIDTH;
	public static int POSITION_COORDINATES_PER_VERTEX = 3;
	public static int TEXTURE_COORDINATES_PER_VERTEX = 2;
	public static final int CHANNELS_PER_PIXEL = 3;//RGB
	public static final int BYTES_IN_FLOAT = 4;
	public static final int BYTES_IN_INT = 4;
	
	//Game
	public static final int GAME_UPDATES_PER_SECOND = 50;
	public static final int GAME_UPDATE_INTERVAL_MS = 1000 / GAME_UPDATES_PER_SECOND;//In milliseconds
	public static final float GAME_UPDATE_INTERVAL_S = 1.0f / ((float) GAME_UPDATES_PER_SECOND);//In seconds
	public static final int MAX_UPDATES_SKIPPED = 5;
	
	public static final int STATE_MENU = 0;
	public static final int STATE_IN_GAME = 1;
	public static final int STATE_ERROR = 2;
	public static final int STATE_IN_LEVEL_EDITOR = 3;
	
	public static final int LEVEL_EDITOR_LEVEL_ID = 0;

	public static final int GRID_WIDTH = 10; //must be divisible by 2
	public static final int GRID_HALF_WIDTH = GRID_WIDTH / 2;
	public static final int GRID_HEIGHT = 12; //Doesn't have to be power of 2
	public static final int INVENTORY_WIDTH = GRID_WIDTH;
	public static final int INVENTORY_HEIGHT = 3;
	public static final float BLOCK_SCALE = OPENGL_SCREEN_WIDTH / (float)GRID_WIDTH;
	public static final float BLOCK_HALF_SCALE = BLOCK_SCALE / 2.f;
	public static final float BALL_SCALE = BLOCK_SCALE / 2.f;
	
	//Model IDs. Integers 0-N
	public static final int MODEL_GRID_BLOCK_BACKGROUND = 0;
	public static final int MODEL_INVENTORY_BLOCK_BACKGROUND = 1;
	public static final int MODEL_WALL = 2;
	public static final int MODEL_TRIANGLE = 3;
	public static final int MODEL_BOX = 4;
	public static final int MODEL_BALL = 5;
	public static final int MODEL_START = 6;
	public static final int MODEL_END = 7;
	
	public static final int MODEL_MENU_BACKGROUND = 19;
	public static final int MODEL_RETURN_BUTTON = 20;
	public static final int MODEL_PLAY_BUTTON = 21;
	public static final int MODEL_QUIT_GAME_BUTTON = 22;
	public static final int MODEL_DROP_BUTTON = 23;
	public static final int MODEL_LEVEL_EDITOR_BUTTON = 24;
	
	//Buttons
	public static final float BUTTON_MENU_WIDTH = 0.8f * OPENGL_SCREEN_WIDTH; 
	
	//Physics
	public static final float GRAVITY = -7.0f;
}