package code;

import code.Vec2f;

public abstract class GameObject {

	protected Vec2f position;
	protected float rotation;
	protected int model_id;
	protected Vec2f model_scale; //Models vertex coordinate values (x,y) should be between [-0.5,0.5] and
								 //its center must be in origo.
								 //model_scale=1.0 keeps the original size, 2.0 doubles..
	protected boolean movable;   //Whether player can move this object
	
	public GameObject(Vec2f position, boolean movable, int model_id, Vec2f model_scale)
	{
		this.position = position;
		this.movable = movable;
		this.model_id = model_id;
		this.model_scale = model_scale;
		this.rotation = 0.0f;
	}

	void checkClick(Vec2f click_position) 
	{
		if ( this.hit(click_position) )
		{
			onClick();
		}
	}
	boolean hit(Vec2f position) // If position is inside this models Bounding Box
	{
		return position.x < this.position.x + this.model_scale.x / 2.0f &&
				position.x > this.position.x - this.model_scale.x / 2.0f &&
				position.y < this.position.y + this.model_scale.y / 2.0f &&
				position.y > this.position.y - this.model_scale.y / 2.0f;
	}
	public float getRotationInRadians()
	{
		return -(this.rotation/360.0f) * 2.f * (float) Math.PI;
	}
	public void rotateClockWise()
	{
		this.rotation += 90.f;
		if (this.rotation == 360.f)
		{
			this.rotation = 0.0f;
		}
		updatePhysicsShape();
	}
	public void rotateCounterClockWise()
	{
		this.rotation -= 90.f;
		if (this.rotation < 0.f)
		{
			this.rotation += 360.f;
		}
		updatePhysicsShape();
	}
	public Vec2f getPosition()
	{
		return position;
	}
	public void setPosition(Vec2f new_position)
	{
		this.position = new_position;
		updatePhysicsShape();
	}
	public int getModelId()
	{
		return model_id;
	}
	public Vec2f getModelScale()
	{
		return model_scale;
	}
	public float getRotation()
	{
		return rotation;
	}
	public boolean isMovable()
	{
		return movable;
	}
	
	/////////////////////////////////
	//Methods that can be overrided//
	/////////////////////////////////
	public void update() { } // Update objects state.
	public void updatePhysicsShape() { } //Move objects physics body if it is necessary after object moves
	public void onClick() { } //Override what happens when this objects Bounding Box is clicked	
	public void destroy() { } //Destroy objects dynamic parts eg. physics body
	public GameObject copy() { return null; } //Make a copy of this object. By default retuns null and the object is not copyable
}
