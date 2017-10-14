package menuitems;

import code.GameObject;
import code.Constants;
import code.Game;
import code.Vec2f;

public class PlayButton extends GameObject {

	public PlayButton()
	{
		super(new Vec2f(0.0f,
				0.7f), false, 
				Constants.MODEL_PLAY_BUTTON, 
				new Vec2f(Constants.BUTTON_MENU_WIDTH, 
						0.4f));
	}
	@Override
	public void onClick() {
		Game.setState(Constants.STATE_IN_GAME); // TMP
	}

	@Override
	public void update() { }
}
