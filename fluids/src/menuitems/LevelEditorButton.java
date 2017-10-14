package menuitems;

import code.Constants;
import code.Game;
import code.GameObject;
import code.Vec2f;

public class LevelEditorButton extends GameObject {
	public LevelEditorButton()
	{
		super(new Vec2f(0.0f,
				0.0f), false, 
				Constants.MODEL_LEVEL_EDITOR_BUTTON, 
				new Vec2f(Constants.BUTTON_MENU_WIDTH, 
						0.4f));
	}
	@Override
	public void onClick() {
		Game.setState(Constants.STATE_IN_LEVEL_EDITOR);
	}

	@Override
	public void update() { }
}
