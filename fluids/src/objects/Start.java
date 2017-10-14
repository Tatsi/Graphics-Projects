package objects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import code.Constants;
import code.GameObject;
import code.PhysicsWorld;
import code.Vec2f;

public class Start extends GameObject {
	Body body;
	Body body2;
	
	public Start(Vec2f position)
	{
		super(position, false, Constants.MODEL_START, new Vec2f(Constants.BLOCK_SCALE, Constants.BLOCK_SCALE));
		createPhysics();
	}
	private void createPhysics()
	{
		//Left
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.position.set(new Vec2(position.x-Constants.BLOCK_HALF_SCALE+0.01f, position.y));
		body = PhysicsWorld.createBody(bodyDef);
		
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(0.005f, Constants.BLOCK_HALF_SCALE);
		body.createFixture(polygonShape, 0.0f);
		
		//Right
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyType.STATIC;
		bodyDef2.position.set(new Vec2(position.x+Constants.BLOCK_HALF_SCALE-0.01f, position.y));
		body2 = PhysicsWorld.createBody(bodyDef2);
		
		PolygonShape polygonShape2 = new PolygonShape();
		polygonShape2.setAsBox(0.005f, Constants.BLOCK_HALF_SCALE);
		body2.createFixture(polygonShape2, 0.0f);
	}
	
	@Override
	public void destroy()
	{
		PhysicsWorld.destroyBody(body);
		PhysicsWorld.destroyBody(body2);
	}
}
