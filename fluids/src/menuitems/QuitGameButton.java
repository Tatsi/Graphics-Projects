package menuitems;

import code.Constants;
import code.Fluids;
import code.GameObject;
import code.Vec2f;

public class QuitGameButton extends GameObject {

	public QuitGameButton()
	{
		super(new Vec2f(0.0f,
				-0.7f),//TMP Hard coded
				false, Constants.MODEL_QUIT_GAME_BUTTON, 
				new Vec2f(Constants.BUTTON_MENU_WIDTH, 0.4f ));
	}
	@Override
	public void onClick() {
		Fluids.quit();
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
