package objects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import code.Constants;
import code.GameObject;
import code.PhysicsWorld;
import code.Vec2f;

public class Ball extends GameObject {
	
	Body body;
	
	public Ball(Vec2f position)
	{
		super(position, false, Constants.MODEL_BALL, new Vec2f(Constants.BALL_SCALE, Constants.BALL_SCALE));
		
		//Create physics
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(new Vec2(position.x, position.y));
		body = null;
		
		while (body == null)//Why is this while required? bug?
		{
			body = PhysicsWorld.createBody(bodyDef);
		}
		
		CircleShape ball = new CircleShape();
		ball.m_radius = Constants.BALL_SCALE / 2.f;
			
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = ball;
		fixtureDef.density = 1.5f;
		fixtureDef.friction = 5.0f;
		fixtureDef.restitution = 0.3f;
			
		body.createFixture(fixtureDef);
	}
	@Override
	public void update()
	{
		Vec2 new_pos = body.getPosition();
		this.position.x = new_pos.x;
		this.position.y = new_pos.y;
	}
	public void applyForce(Vec2 force)
	{
		this.body.applyForceToCenter(force);
	}
	@Override
	public void destroy()
	{
		Fixture fixtures = body.getFixtureList();
		body.destroyFixture(fixtures);
		PhysicsWorld.destroyBody(body);
	}
}
