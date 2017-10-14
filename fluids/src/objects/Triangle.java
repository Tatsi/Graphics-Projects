package objects;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import code.Constants;
import code.Game;
import code.GameObject;
import code.PhysicsWorld;
import code.Vec2f;

public class Triangle extends GameObject {
	
	Body body;
	
	public Triangle(Vec2f position)
	{
		super(position, true, Constants.MODEL_TRIANGLE, new Vec2f(Constants.BLOCK_SCALE, Constants.BLOCK_SCALE));
		createPhysics();
	}
	public void createPhysics()
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.position.set(new Vec2(position.x, position.y));
		bodyDef.angle = 0.0f;
		body = PhysicsWorld.createBody(bodyDef);
		
		if (body == null)
		{
			Game.setState(Constants.STATE_ERROR);
			return;
		}
		
		Vec2 vertices[] = new Vec2[3]; //CCW order
		vertices[0] = new Vec2(-Constants.BLOCK_HALF_SCALE, Constants.BLOCK_HALF_SCALE);
		vertices[1] = new Vec2(-Constants.BLOCK_HALF_SCALE, -Constants.BLOCK_HALF_SCALE);
		vertices[2] = new Vec2(Constants.BLOCK_HALF_SCALE, -Constants.BLOCK_HALF_SCALE);
		PolygonShape triangle = new PolygonShape();
		triangle.set(vertices, 3);
		body.createFixture(triangle, 0.0f);
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
	@Override
	public Triangle copy () 
	{
		return new Triangle(this.position);
	}
}
