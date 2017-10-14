package objects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import code.GameObject;
import code.PhysicsWorld;
import code.Vec2f;
import code.Constants;

public class Box extends GameObject {

	Body body;
	
	public Box(Vec2f position)
	{
		super(position, true, Constants.MODEL_BOX, new Vec2f(Constants.BLOCK_SCALE, Constants.BLOCK_SCALE));
		createPhysics();
	}
	private void createPhysics()
	{
		//Create physics
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.position.set(new Vec2(position.x, position.y));
		body = PhysicsWorld.createBody(bodyDef);
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(Constants.BLOCK_HALF_SCALE, Constants.BLOCK_HALF_SCALE);
		body.createFixture(polygonShape, 0.0f);
	}
	@Override
	public Box copy () 
	{
		return new Box(this.position);
	}
	@Override
	public void updatePhysicsShape() {
		body.setTransform(new Vec2(this.position.x, this.position.y), this.getRotationInRadians());
	}
	
	@Override
	public void destroy()
	{
		PhysicsWorld.destroyBody(body);
	}
}
