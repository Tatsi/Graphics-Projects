package code;

import code.Constants;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public class PhysicsWorld {
	private static World world;
	private static Body gridFrameBody; // Surrounds game grid
	
	public static void init()
	{
		world = new World(new Vec2(0.0f, Constants.GRAVITY));
		
		//Create boundaries of game area. This loop surrounds game grid area.
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(new Vec2(0.0f, 0.0f));
		gridFrameBody = world.createBody(bodyDef);
		Vec2 vs[] = new Vec2[4];
		vs[0] = new Vec2(-Constants.OPENGL_SCREEN_HALF_WIDTH, 
				GL20Renderer.getRatio()*Constants.OPENGL_SCREEN_HALF_WIDTH);
		vs[1] = new Vec2(-Constants.OPENGL_SCREEN_HALF_WIDTH, 
				GL20Renderer.getRatio()*Constants.OPENGL_SCREEN_HALF_WIDTH 
				-Constants.GRID_HEIGHT*Constants.BLOCK_SCALE);
		vs[2] = new Vec2(Constants.OPENGL_SCREEN_HALF_WIDTH, 
				GL20Renderer.getRatio()*Constants.OPENGL_SCREEN_HALF_WIDTH 
				-Constants.GRID_HEIGHT*Constants.BLOCK_SCALE);
		vs[3] = new Vec2(Constants.OPENGL_SCREEN_HALF_WIDTH, 
				GL20Renderer.getRatio()*Constants.OPENGL_SCREEN_HALF_WIDTH);
		ChainShape loop = new ChainShape();
		loop.createLoop(vs, 4);
		gridFrameBody.createFixture(loop, 0.0f);
		
		world.setAllowSleep(false);//TMP
	}
	
	public static void update()
	{
		world.step(Constants.GAME_UPDATE_INTERVAL_S, 1, 1);
	}
	public static Body createBody(BodyDef bodyDef)
	{
		return world.createBody(bodyDef);
	}
	public static void destroyBody(Body body)
	{
		world.destroyBody(body);
	}
	
}
