package code;

import java.util.ArrayList;

import android.util.Pair;

import code.Constants;
import code.GL20Renderer;
import code.GameObject;
import code.Vec2f;
import code.PhysicsWorld;

import menuitems.*;
import objects.*;

public class Game {
	static String selectedObjectOrigin; //GRID or INVENTORY or NULL
	static Pair<Integer, Integer> selectedObjectIndex;//x,y indices
	
	private static int state;
	private static boolean levelIsLoaded = false;
	private static GameObject[][] grid;
	private static GameObject[][] inventory;
	private static ArrayList<GameObject> objects; //Other objects than grid objects or balls
	private static ArrayList<GameObject> balls;
	private static ArrayList<GameObject> inGameButtons;
	private static ArrayList<GameObject> mainMenuButtons;
	private static ArrayList<GameObject> levelEditorButtons;
	
	private static Vec2f touch_position;//What point was touched last
	
	public static void init()
	{
		setState(Constants.STATE_MENU);
		selectedObjectOrigin = "NULL";
		selectedObjectIndex = new Pair<Integer, Integer>(0, 0);
		touch_position = new Vec2f(0.0f, 0.0f);
		
		inGameButtons = new ArrayList<GameObject>();
		mainMenuButtons = new ArrayList<GameObject>();
		levelEditorButtons = new ArrayList<GameObject>();
		
		inGameButtons.add(new ReturnToMenuButton());
		inGameButtons.add(new DropButton());
		
		mainMenuButtons.add(new PlayButton());
		mainMenuButtons.add(new QuitGameButton());
		mainMenuButtons.add(new LevelEditorButton());
		
		levelEditorButtons.add(new ReturnToMenuButton());
		levelEditorButtons.add(new DropButton());
		
		PhysicsWorld.init();
	}
	public static void updateGameState()
	{
		PhysicsWorld.update();
		//Call update on every object
		for (GameObject o : objects)
		{
			o.update();
		}
		//Call update on every ball
		for (GameObject o : balls)
		{
			o.update();
		}
		for (int i = 0; i < Constants.GRID_HEIGHT; i++)
    	{
    		for (int j = 0; j < Constants.GRID_WIDTH; j++)
        	{
    			GameObject block = Game.getBlock(j, i);

    	        if (block != null && block != Game.getSelectedObject() )//Draw if block is not being moved
    	        {
    	        	block.update();
    	        }
        	}
    	}
	}
	public static void addObjectToGrid(int x_index, int y_index, GameObject object)
	{
		grid[y_index][x_index] = object;
	}
	public static void addObjectToInventory(int x_index, int y_index, GameObject object)
	{
		inventory[y_index][x_index] = object;
	}
	public static Vec2f getGridBlockCenter(int x_index, int y_index)
	{
    	float block_x = -Constants.OPENGL_SCREEN_HALF_WIDTH + (0.5f+(float)x_index)*Constants.BLOCK_SCALE;
    	float block_y = GL20Renderer.getRatio() * Constants.OPENGL_SCREEN_HALF_WIDTH - (0.5f+(float)y_index)*Constants.BLOCK_SCALE;
        
		return new Vec2f(block_x, block_y);
	}
	public static Vec2f getInventoryBlockCenter(int x_index, int y_index)
	{
		float block_x =  -Constants.OPENGL_SCREEN_HALF_WIDTH + Constants.BLOCK_SCALE/2.f + x_index * Constants.BLOCK_SCALE;
		//float block_y = -GL20Renderer.getRatio() * Constants.OPENGL_SCREEN_HALF_WIDTH + Constants.BLOCK_SCALE/2.f +
		//		(Constants.INVENTORY_HEIGHT-1-y_index) * Constants.BLOCK_SCALE;
		float block_y = GL20Renderer.getRatio()*Constants.OPENGL_SCREEN_HALF_WIDTH
						- Constants.GRID_HEIGHT*Constants.BLOCK_SCALE
						- (0.5f+(float)y_index)*Constants.BLOCK_SCALE;
				
		return new Vec2f(block_x, block_y);
	}
	public static void loadLevel(int levelId)
	{
		clearGameState();//If another level is currently loaded well remove everything from it
		
		objects = new ArrayList<GameObject>();
		balls = new ArrayList<GameObject>();
		
		//Init grid to empty
		grid = new GameObject[Constants.GRID_HEIGHT][Constants.GRID_WIDTH];
		for (int i = 0; i < Constants.GRID_HEIGHT; i++)
		{
			for (int j = 0; j < Constants.GRID_WIDTH; j++)
			{
				grid[i][j] = null;
			}
		}
		//Init inventory blocks to empty
		inventory = new GameObject[Constants.GRID_HEIGHT][Constants.GRID_WIDTH];
		for (int i = 0; i < Constants.INVENTORY_HEIGHT; i++)
		{
			for (int j = 0; j < Constants.INVENTORY_WIDTH; j++)
			{
				inventory[i][j] = null;
			}
		}
		
		//Level specific code
		if (levelId == Constants.LEVEL_EDITOR_LEVEL_ID)
		{
			//Add one instance of every available grid object to inventory
			addObjectToInventory(0, 0, new Box(getInventoryBlockCenter(0, 0)));
			addObjectToInventory(1, 0, new Triangle(getInventoryBlockCenter(1, 0)));
			
			addObjectToInventory(0, 1, new Wall(getInventoryBlockCenter(0, 1)));
			
			//TMP
			addObjectToGrid(5, 6, new Wall(getGridBlockCenter(5, 6)));
			addObjectToGrid(3, 5, new Magnet(getGridBlockCenter(3, 5), 0.01f, 1.2f));
			addObjectToGrid(4, 5, new Magnet(getGridBlockCenter(4, 5), 0.01f, 1.2f));
		}
		else
		{
			//LOAD LEVEL FROM FILE TODO
			addObjectToInventory(0, 0, new Box(getInventoryBlockCenter(0, 0)));
			addObjectToInventory(1, 1, new Box(getInventoryBlockCenter(1, 1)));
			addObjectToInventory(5, 1, new Box(getInventoryBlockCenter(5, 1)));
			addObjectToInventory(7, 1, new Triangle(getInventoryBlockCenter(7, 1)));
			
			addObjectToGrid(4, 0, new Start(getGridBlockCenter(4, 0)));
			addObjectToGrid(4, 11, new End(getGridBlockCenter(4, 11)));
			
			addObjectToGrid(0, 8, new Wall(getGridBlockCenter(0, 8)));
			addObjectToGrid(1, 8, new Wall(getGridBlockCenter(1, 8)));
			addObjectToGrid(4, 4, new Wall(getGridBlockCenter(4, 4)));
			addObjectToGrid(2, 8, new Wall(getGridBlockCenter(2, 8)));
			addObjectToGrid(4, 7, new Wall(getGridBlockCenter(4, 7)));
			addObjectToGrid(6, 6, new Wall(getGridBlockCenter(6, 6)));
			addObjectToGrid(3, 5, new Magnet(getGridBlockCenter(3, 5), 0.8f, 1.0f));
			//addObjectToGrid(4, 5, new Magnet(getGridBlockCenter(4, 5), 0.8f, 1.0f));
			Triangle rotated = new Triangle(getGridBlockCenter(2, 2));
			rotated.rotateClockWise();
			addObjectToGrid(2, 2, rotated);
			
			addObjectToGrid(5, 4, new Wall(getGridBlockCenter(5, 4)));
			addObjectToGrid(5, 5, new Wall(getGridBlockCenter(5, 5)));
			addObjectToGrid(5, 6, new Wall(getGridBlockCenter(5, 6)));
			addObjectToGrid(5, 7, new Wall(getGridBlockCenter(5, 7)));
			addObjectToGrid(5, 0, new Wall(getGridBlockCenter(5, 0)));
			addObjectToGrid(5, 1, new Wall(getGridBlockCenter(5, 1)));
			addObjectToGrid(5, 2, new Wall(getGridBlockCenter(5, 2)));
			addObjectToGrid(4, 8, new Triangle(getGridBlockCenter(4, 8)));
			addObjectToGrid(3, 7, new Wall(getGridBlockCenter(3, 7)));
		}
		levelIsLoaded = true;
	}
	public static void clearGameState()
	{
		if (levelIsLoaded)
		{
			clearBalls();
			
			for (int i = 0; i < Constants.GRID_HEIGHT; i++)
			{
				for (int j = 0; j < Constants.GRID_WIDTH; j++)
				{
					if (grid[i][j] != null)
					{
						grid[i][j].destroy();
					}
				}
			}
			for (int i = 0; i < Constants.INVENTORY_HEIGHT; i++)
			{
				for (int j = 0; j < Constants.INVENTORY_WIDTH; j++)
				{
					if (inventory[i][j] != null)
					{
						inventory[i][j].destroy();
					}
				}
			}
			for (GameObject o : objects)
			{
				o.destroy();
			}
		}
		levelIsLoaded = false;
	}
	public static GameObject getBlock(int x_index, int y_index)
	{
		return grid[y_index][x_index];
	}
	public static GameObject getInventoryBlock(int x_index, int y_index)
	{
		return inventory[y_index][x_index];
	}
	public static ArrayList<GameObject> getInGameButtons()
	{
		return inGameButtons;
	}
	public static ArrayList<GameObject> getMenuButtons()
	{
		return mainMenuButtons;
	}
	public static ArrayList<GameObject> getLevelEditorButtons()
	{
		return levelEditorButtons;
	}
	public static void setSelectedObjectIndex(Pair<Integer, Integer> index)
	{
		selectedObjectIndex = index;
	}
	public static void moveBlockToInventory(int to_x, int to_y)//Move selected object to inventory to parameter index
	{
		if (Game.state == Constants.STATE_IN_GAME)
		{
			if (selectedObjectOrigin == "INVENTORY")
			{
				inventory[to_y][to_x] = inventory[selectedObjectIndex.second][selectedObjectIndex.first];
				inventory[selectedObjectIndex.second][selectedObjectIndex.first] = null;
			}
			else if (selectedObjectOrigin == "GRID")
			{
				inventory[to_y][to_x] = grid[selectedObjectIndex.second][selectedObjectIndex.first];
				grid[selectedObjectIndex.second][selectedObjectIndex.first] = null;
			}
			inventory[to_y][to_x].setPosition(getInventoryBlockCenter(to_x, to_y));
		}
	}
	public static void moveBlockToGrid(int to_x, int to_y)//Move selected object to grid to parameter index
	{
		if (selectedObjectOrigin == "INVENTORY")
		{
			if (state == Constants.STATE_IN_GAME)
			{
				grid[to_y][to_x] = inventory[selectedObjectIndex.second][selectedObjectIndex.first];
				inventory[selectedObjectIndex.second][selectedObjectIndex.first] = null;
			}
			else if (state == Constants.STATE_IN_LEVEL_EDITOR) //Copy object to grid
			{
				grid[to_y][to_x] = inventory[selectedObjectIndex.second][selectedObjectIndex.first].copy();
			}
		}
		else if (selectedObjectOrigin == "GRID")
		{
			grid[to_y][to_x] = grid[selectedObjectIndex.second][selectedObjectIndex.first];
			grid[selectedObjectIndex.second][selectedObjectIndex.first] = null;
		}
		grid[to_y][to_x].setPosition(getGridBlockCenter(to_x, to_y));
	}
	public static Pair<Integer, Integer> getSelectedObjectIndex()
	{
		return selectedObjectIndex;
	}
	public static void setSelectedObjectOrigin(String origin)
	{
		selectedObjectOrigin = origin;
	}
	public static String getSelectedObjectOrigin()
	{
		return selectedObjectOrigin;
	}
	public static GameObject getSelectedObject()
	{
		if (selectedObjectOrigin == "GRID")
		{
			return grid[selectedObjectIndex.second][selectedObjectIndex.first];
		}
		else if (selectedObjectOrigin == "INVENTORY")
		{
			return inventory[selectedObjectIndex.second][selectedObjectIndex.first];
		}
		else
		{
			return null;
		}
	}
	public static void setState(int new_state)
	{
		state = new_state;
		if (state == Constants.STATE_IN_GAME)
		{
			loadLevel(1);//TMP
		}
		else if (state == Constants.STATE_IN_LEVEL_EDITOR)
		{
			loadLevel(0);
		}
	}
	public static int getState()
	{
		return state;
	}
	public static ArrayList<GameObject> getObjects()
	{
		return objects;
	}
	public static void addObject(GameObject object)
	{
		objects.add(object);
	}
	public static ArrayList<GameObject> getBalls()
	{
		return balls;
	}
	public static void addBall(Vec2f position)
	{
		balls.add(new Ball(position));
	}
	public static void clearBalls()
	{
		for (GameObject ball : balls)
		{
			ball.destroy();//Destroy physics
		}
		balls.clear();
	}
	public static void setTouchPosition(Vec2f position)
	{
		touch_position = position;
	}
	public static Vec2f getTouchPosition()
	{
		return touch_position;
	}
	
}
