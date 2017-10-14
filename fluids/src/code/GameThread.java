package code;

import code.GL20Renderer;

public class GameThread extends Thread {

	private boolean running = false;
	long cyckleBeginTime;
	long cyckleDuration;
	int timeToSleep;
	int updatesSkipped; 
	
	public void setRunning(boolean r)
	{
		this.running = r;
	}
	
	@Override
    public void run() {
		while (running)
		{	
			cyckleBeginTime = System.currentTimeMillis();
			updatesSkipped = 0;
				if (Game.getState() == Constants.STATE_IN_GAME || Game.getState() == Constants.STATE_IN_LEVEL_EDITOR)
				{
					Game.updateGameState();
				}
				Fluids.drawGame();
				if (GL20Renderer.red < 1.0f)
				{
					GL20Renderer.red += 0.005f;
				}
			cyckleDuration = System.currentTimeMillis() - cyckleBeginTime;
			timeToSleep = (int)(Constants.GAME_UPDATE_INTERVAL_MS - cyckleDuration);
			if (timeToSleep > 0)
			{
				try {
					Thread.sleep(timeToSleep);
				} catch (InterruptedException e) {
					//TODO Raise error
				}
			}
			while (timeToSleep < 0 && updatesSkipped < Constants.MAX_UPDATES_SKIPPED)
			{
				if (Game.getState() == Constants.STATE_IN_GAME || Game.getState() == Constants.STATE_IN_LEVEL_EDITOR)
				{
					Game.updateGameState();
				}
				updatesSkipped++;
				timeToSleep += Constants.GAME_UPDATE_INTERVAL_MS;
			}
		}
	}
}
