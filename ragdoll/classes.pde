//import toxi.geom.Quartenion;
public class Vec3
{
 public float x;
 public float y;
 public float z; 
 
 public Vec3(float x, float y, float z)
 {
    this.x = x;
    this.y = y;
    this.z = z; 
 }
}

public class EulerRotation
{
  public float x_rad;
  public float y_rad;
  public float z_rad;
  
  public EulerRotation(Vec3 value)//In degrees
  {
    this.z_rad = radians(value.x);
    this.x_rad = radians(value.y);
    this.y_rad = radians(value.z);
  } 
}

public class Animation
{
  private ArrayList<Frame> frames;
  private int frame_count;
  private int fps;
  private int current_frame;
  
  public Animation(int frame_count, int fps, ArrayList<Frame> frames)
  {
    this.frame_count = frame_count;
    this.frames = frames; 
    this.fps = fps;
    this.current_frame = 0;
  }
  public int getFPS()
  {
    return this.fps; 
  }
  public boolean isAtEnd()//Are we in the last frame
  {
    return current_frame == frame_count - 1; 
  }
  public int getFrameCount()
  {
    return this.frame_count; 
  }
  public void reset()
  {
    this.current_frame = 0; 
  }
  public void advance()
  {
    current_frame++;
    if (current_frame >= frame_count)
    {
      this.current_frame = 0;
    }
  }
  public Frame getCurrentFrame()
  {
    return this.frames.get(current_frame);
  }
  public int getCurrentFrameNumber()
  {
    return this.current_frame;
  }
  public Frame getNextFrame()
  {
    if (this.current_frame + 1 >= frame_count)
    {
      return this.frames.get(0);
    }
    else
    {
      return frames.get(current_frame+1);
    }
  }
}

public class Frame//Root transformation + 19 rotations for bones
{
  private Vec3 root_translation;
  private Vec3 toe_position;//TODO calculate toe position for each frame
  private ArrayList<EulerRotation> rotations;
  private ArrayList<Quaternion> rotationsInQuaternions;
  private ArrayList<Vec3> positions;
  
  
  public Frame(ArrayList<Vec3> data)
  {
    this.rotations = new ArrayList<EulerRotation>();
    this.rotationsInQuaternions = new ArrayList<Quaternion>();
    this.root_translation = data.get(0);
    
    for (int i = 1; i < data.size(); i++)
    {
      EulerRotation e = new EulerRotation(data.get(i));
      this.rotations.add(e);
      this.rotationsInQuaternions.add(eulerToQuaternion(e));
    }
    
    this.positions = new ArrayList<Vec3>();
    
    toe_position = new Vec3(100,0,0);
  }
  public ArrayList<EulerRotation> getRotations()
  {
    return this.rotations;
  }
  public Vec3 getRootTranslation()
  {
    return this.root_translation; 
  }
}

public class Joint
{
  private boolean end;//Is this End site or joint
  private Vec3 offset;
  private ArrayList<Joint> children;
  private Joint predecessor = null;
  private int type = 0;
  private Vec3 velocity;
  private float mass = 1.0f;
  private Vec3 globalPosition;
  
  public Joint(boolean end, Vec3 offset)
  {
    children = new ArrayList<Joint>();
    this.offset = offset;
    this.end = end;
  }
  public void setType(int type)
  {
    this.type = type;
  }
  public int getType()
  {
    return this.type;
  }
  public float getLength()
  {
    return sqrt( pow(this.offset.x, 2.f) + pow(this.offset.y, 2.f) + pow(this.offset.z, 2.f) ); 
  }
  public void addChild(Joint child)
  {
    this.children.add(child);
  }
  public ArrayList<Joint> getChildren()
  {
    return this.children;
  }
  public Vec3 getOffset()
  {
    return this.offset; 
  }
  public void setEnd(boolean end)
  {
    this.end = end; 
  }
  public Vec3 getGlobalPosition()
  {
    return this.globalPosition; 
  }
  public void setGlobalPosition(Vec3 position)
  {
    this.globalPosition = position; 
  }
  public void setVelocity(Vec3 velocity)
  {
    this.velocity = velocity; 
  }
  public Vec3 getVelocity()
  {
    return this.velocity; 
  }
  public boolean isEnd()
  {
    return this.end; 
  }
  public void setPredecessor(Joint j)
  {
    this.predecessor = j; 
  }
  public Joint getPredecessor()
  {
    return this.predecessor; 
  }
}
