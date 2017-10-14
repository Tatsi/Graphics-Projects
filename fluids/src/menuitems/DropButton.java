package menuitems;

import code.Constants;
import code.Game;
import code.GameObject;
import code.Vec2f;

public class DropButton extends GameObject {

	public DropButton()
	{
		super(new Vec2f(-Constants.OPENGL_SCREEN_HALF_WIDTH/2.0f,
				-1.5f),//TMP Hard coded
				false, Constants.MODEL_DROP_BUTTON, 
				new Vec2f(Constants.OPENGL_SCREEN_HALF_WIDTH / 2.0f, 0.5f));
	}
	@Override
	public void onClick() {
		if (Game.getBalls().size() != 0)
		{
			Game.clearBalls();
		}
		else
		{
			//Drop ball
			Game.addBall(Game.getGridBlockCenter(4, 0));
		}
	}
	
	@Override
	public void update() { }
}
