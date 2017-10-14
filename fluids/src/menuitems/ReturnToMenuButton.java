package menuitems;

import code.Constants;
//import code.GameGLSurfaceView;
import code.GameObject;
import code.Vec2f;
import code.Game;

public class ReturnToMenuButton extends GameObject {

	public ReturnToMenuButton()
	{
		super(new Vec2f(Constants.OPENGL_SCREEN_HALF_WIDTH/2.0f, 
				-1.5f),//TMP Hard coded
				false, Constants.MODEL_RETURN_BUTTON, 
				new Vec2f(Constants.OPENGL_SCREEN_HALF_WIDTH / 2.0f, 0.5f));
	}
	
	public void update() { }

	public void onClick()
	{
		Game.setState(Constants.STATE_MENU);
	}
}
