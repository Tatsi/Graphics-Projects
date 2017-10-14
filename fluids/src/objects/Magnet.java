package objects;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import code.Constants;
import code.Game;
import code.GameObject;
import code.PhysicsWorld;
import code.Vec2f;
import objects.Ball;;

public class Magnet extends GameObject {
	
	Body body;
	float force;
	float areaOfEffect;
	
	public Magnet(Vec2f position, float force, float areaOfEffect)
	{
		super(position, true, Constants.MODEL_BALL, new Vec2f(2*Constants.BALL_SCALE, 2*Constants.BALL_SCALE));
		//Create physics
		this.force = force;
		this.areaOfEffect = areaOfEffect;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.position.set(new Vec2(position.x, position.y));
		body = PhysicsWorld.createBody(bodyDef);
		
		if (body == null)
		{
			Game.setState(Constants.STATE_ERROR);
		}
		else
		{
			CircleShape ball = new CircleShape();
			ball.m_radius = Constants.BALL_SCALE;
			
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = ball;
			
			body.createFixture(fixtureDef);
		}
	}
	public void update()
	{
		for (GameObject ball : Game.getBalls())
		{
			float r = (float) Math.sqrt(Math.pow((double)this.position.x-(double)ball.getPosition().x, 2.0)+Math.pow((double)this.position.y-(double)ball.getPosition().y, 2.0));
			
			if (r < this.areaOfEffect)
			{	
				Vec2 unitVector = new Vec2((this.position.x-ball.getPosition().x)/r, (this.position.y-ball.getPosition().y)/r );
				((Ball)ball).applyForce(
						new Vec2(
								this.force*(1.f/Math.abs(r)) * 0.01f * unitVector.x, 
								this.force*(1.f/Math.abs(r)) * 0.01f * unitVector.y)
						);
			}
		}
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
	public Box copy () 
	{
		return new Box(this.position);
	}
}
