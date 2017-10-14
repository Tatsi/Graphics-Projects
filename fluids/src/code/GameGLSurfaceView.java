package code;

import code.GL20Renderer;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.util.Pair;
import android.view.MotionEvent;
import android.os.SystemClock;

public class GameGLSurfaceView extends GLSurfaceView {
	
	private GL20Renderer renderer;
	public static Resources resources;
	
	private static GameThread gameThread;
	private static long pressTime = 0;//When screen was pressed last time
	
	public GameGLSurfaceView(Context context){
        super(context);
        
        gameThread = new GameThread();
        
        resources = context.getResources();
        this.setEGLContextClientVersion(2);
        renderer = new GL20Renderer();
        this.setRenderer(renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
	public static void startGameLoop()
	{
		gameThread.setRunning(true);
		gameThread.start();
	}
	public static void endGameLoop()
	{
		gameThread.setRunning(false);
		
		boolean retry = true;
		
		while (retry) {
            try {
                  gameThread.join();
                  retry = false;
            } catch (InterruptedException e) {
            }
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX() / getWidth() * Constants.OPENGL_SCREEN_WIDTH - Constants.OPENGL_SCREEN_HALF_WIDTH;
		float y = (1.0f - e.getY() / getHeight() ) * Constants.OPENGL_SCREEN_WIDTH * GL20Renderer.getRatio() 
				- Constants.OPENGL_SCREEN_HALF_WIDTH * GL20Renderer.getRatio();
		Vec2f clickPosition = new Vec2f(x,y);
		
		Game.setTouchPosition(clickPosition);
		
		if (e.getAction() == MotionEvent.ACTION_DOWN)
		{
			handleTouchPress(clickPosition);
		}
		else if (e.getAction() == MotionEvent.ACTION_UP)
		{
			handleTouchRelease(clickPosition);
		}
	    return true;
	}
	private void handleTouchPress(Vec2f clickPosition)
	{
		pressTime = SystemClock.elapsedRealtime();
		
		if (Game.getState() == Constants.STATE_IN_GAME || Game.getState() == Constants.STATE_IN_LEVEL_EDITOR)
		{
			if (Game.getState() == Constants.STATE_IN_GAME)
			{
				for (GameObject b : Game.getInGameButtons())
				{
					b.checkClick(clickPosition);
				}
			}
			else if (Game.getState() == Constants.STATE_IN_LEVEL_EDITOR)
			{
				for (GameObject b : Game.getLevelEditorButtons())
				{
					b.checkClick(clickPosition);
				}
			}
			
			for (int i = 0; i < Constants.GRID_HEIGHT; i++)
			{
				for (int j = 0; j < Constants.GRID_WIDTH; j++)
				{
					GameObject block = Game.getBlock(j, i);
					if (block != null && (block.isMovable() || Game.getState() == Constants.STATE_IN_LEVEL_EDITOR) 
						&& block.hit(clickPosition) && Game.getBalls().isEmpty())
					{
						Game.setSelectedObjectOrigin("GRID");
						Game.setSelectedObjectIndex(new Pair<Integer, Integer>(j, i));
						i = Constants.GRID_HEIGHT; j = Constants.GRID_WIDTH;//Stop
					}
				}
			}
			for (int i = 0; i < Constants.INVENTORY_HEIGHT; i++)
			{
				for (int j = 0; j < Constants.INVENTORY_WIDTH; j++)
				{
					GameObject block = Game.getInventoryBlock(j, i);
					if (block != null && (block.isMovable() || Game.getState() == Constants.STATE_IN_LEVEL_EDITOR) 
						&& block.hit(clickPosition) && Game.getBalls().isEmpty())
					{
						Game.setSelectedObjectOrigin("INVENTORY");
						Game.setSelectedObjectIndex(new Pair<Integer, Integer>(j, i));
						i = Constants.INVENTORY_HEIGHT; j = Constants.INVENTORY_WIDTH;//Stop
					}
				}
			}
		}
		else if (Game.getState() == Constants.STATE_MENU)
		{
			for (GameObject b : Game.getMenuButtons())
			{
				b.checkClick(clickPosition);
			}
		}
		else if (Game.getState() == Constants.STATE_ERROR)
		{
			Fluids.quit();
		}
	}
	private void handleTouchRelease(Vec2f touchPosition)
	{
		if (Game.getSelectedObjectOrigin() != "NULL")
		{
			if (Game.getState() == Constants.STATE_IN_GAME)
			{
				if (SystemClock.elapsedRealtime() - pressTime < 120) //Tap = Rotate selected object
				{
					Game.getSelectedObject().rotateClockWise();
				}
				else
				{
					for (int i = 0; i < Constants.INVENTORY_HEIGHT; i++)
					{
						for (int j = 0; j < Constants.INVENTORY_WIDTH; j++)
						{					
							if (Game.getInventoryBlock(j, i) == null) //We wont drop the object if this place is reserved
							{
								Vec2f block_position = Game.getInventoryBlockCenter(j, i);
								
								//Check if finger was released on this block
								if (touchPosition.x < block_position.x + Constants.BLOCK_SCALE / 2.0f &&
										touchPosition.x > block_position.x - Constants.BLOCK_SCALE / 2.0f &&
										touchPosition.y < block_position.y + Constants.BLOCK_SCALE / 2.0f &&
										touchPosition.y > block_position.y - Constants.BLOCK_SCALE / 2.0f)
								{
									Game.moveBlockToInventory(j, i);
									i = Constants.GRID_HEIGHT; j = Constants.GRID_WIDTH; //Move
								}
							}
						}
					}
					
				}//ELSE
				
			}
			for (int i = 0; i < Constants.GRID_HEIGHT; i++)
			{
				for (int j = 0; j < Constants.GRID_WIDTH; j++)
				{					
					if (Game.getBlock(j, i) == null) //We wont drop the object if this place is reserved
					{
						Vec2f block_position = Game.getGridBlockCenter(j, i);
						//Check if finger was released on this block
						if (touchPosition.x < block_position.x + Constants.BLOCK_SCALE / 2.0f &&
							touchPosition.x > block_position.x - Constants.BLOCK_SCALE / 2.0f &&
							touchPosition.y < block_position.y + Constants.BLOCK_SCALE / 2.0f &&
							touchPosition.y > block_position.y - Constants.BLOCK_SCALE / 2.0f)
						{
							Game.moveBlockToGrid(j, i);
							i = Constants.GRID_HEIGHT; j = Constants.GRID_WIDTH; //Stop
						}
					}
				}	
			}
			Game.setSelectedObjectOrigin("NULL");
		}
	}
	
}
