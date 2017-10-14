import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public Animation parseBVH(String fileName)
{
  //Try to open the file
  try {  
    BufferedReader br = new BufferedReader(new FileReader(dataPath("") + "/" + fileName));
    
    if (br.ready())
    {
      if (root == null)//Parse skeleton only once
      {
        parseSkeleton(br);
      }
      Animation animation = parseMotionData(br);
      br.close();
      return animation;
    }
  }
  catch (FileNotFoundException e) {
    System.out.println("Opening file failed. File not found!");
    System.out.println("Looked for " + dataPath("") + "/" + fileName);
    exit();
  }
  catch (IOException e) {
    System.out.println("Opening file failed. IOException!");
    exit();
  }
  return null;
}

public void parseSkeleton(BufferedReader br)
{
    
    Joint joint;
    Joint predecessor;
    
    //Root
    joint = getNextJoint(br);
    joint.setPredecessor(null);
    joint.setType(UPPER_BODY);

    this.root = joint;
    
    predecessor = joint;
    
      //Ab
      joint = getNextJoint(br);
      joint.setPredecessor(predecessor);
      joint.getPredecessor().addChild(joint);
      joint.setType(UPPER_BODY);
      predecessor = joint;
    
        //Chest
        joint = getNextJoint(br);
        joint.setPredecessor(predecessor);
        joint.getPredecessor().addChild(joint);
        joint.setType(UPPER_BODY);
        predecessor = joint;
        
          //Neck
          joint = getNextJoint(br);
          joint.setPredecessor(predecessor);
          joint.getPredecessor().addChild(joint);
          joint.setType(UPPER_BODY);
          predecessor = joint;
          
            //Head
            joint = getNextJoint(br);
            joint.setPredecessor(predecessor);
            joint.getPredecessor().addChild(joint);
            joint.setType(UPPER_BODY);
            predecessor = joint;  
            
              //End site
              joint = getNextJoint(br);
              joint.setEnd(true);
              joint.setPredecessor(predecessor);
              joint.getPredecessor().addChild(joint);
              joint.setType(UPPER_BODY);
              predecessor = joint.getPredecessor().getPredecessor().getPredecessor();
              
          //LeftCollar
          joint = getNextJoint(br);
          joint.setPredecessor(predecessor);
          joint.getPredecessor().addChild(joint);
          joint.setType(UPPER_BODY);
          predecessor = joint;
          
            //LeftShoulder
            joint = getNextJoint(br);
            joint.setPredecessor(predecessor);
            joint.getPredecessor().addChild(joint);
            joint.setType(UPPER_BODY);
            predecessor = joint;
            
              //LeftElbow
              joint = getNextJoint(br);
              joint.setPredecessor(predecessor);
              joint.getPredecessor().addChild(joint);
              joint.setType(UPPER_BODY);
              predecessor = joint;
              
                //LeftWrist
                joint = getNextJoint(br);
                joint.setPredecessor(predecessor);
                joint.getPredecessor().addChild(joint);
                joint.setType(UPPER_BODY);
                predecessor = joint;
                
                  //End site
                  joint = getNextJoint(br);
                  joint.setEnd(true);
                  joint.setPredecessor(predecessor);
                  joint.getPredecessor().addChild(joint);
                  joint.setType(UPPER_BODY);
                  predecessor = joint.getPredecessor().getPredecessor().getPredecessor().getPredecessor().getPredecessor();
                  
          //RightCollar
          joint = getNextJoint(br);
          joint.setPredecessor(predecessor);
          joint.getPredecessor().addChild(joint);
          joint.setType(UPPER_BODY);
          predecessor = joint;
          
            //RightShoulder
            joint = getNextJoint(br);
            joint.setPredecessor(predecessor);
            joint.getPredecessor().addChild(joint);
            joint.setType(UPPER_BODY);
            predecessor = joint;
            
              //RightElbow
              joint = getNextJoint(br);
              joint.setPredecessor(predecessor);
              joint.getPredecessor().addChild(joint);
              joint.setType(UPPER_BODY);
              predecessor = joint;
              
                //RightWrist
                joint = getNextJoint(br);
                joint.setPredecessor(predecessor);
                joint.getPredecessor().addChild(joint);
                joint.setType(UPPER_BODY);
                predecessor = joint;
                
                  //End site
                  joint = getNextJoint(br);
                  joint.setEnd(true);
                  joint.setPredecessor(predecessor);
                  joint.setType(UPPER_BODY);
                  joint.getPredecessor().addChild(joint);
                  predecessor = joint.getPredecessor().getPredecessor().getPredecessor().getPredecessor().getPredecessor();
                  
      //LeftHip
      joint = getNextJoint(br);
      joint.setPredecessor(this.root);
      joint.getPredecessor().addChild(joint);
      joint.setType(LOWER_BODY);
      predecessor = joint;
      
        //LeftKnee
        joint = getNextJoint(br);
        joint.setPredecessor(predecessor);
        joint.getPredecessor().addChild(joint);
        joint.setType(LOWER_BODY);
        predecessor = joint;
        
          //LeftAnkle
          joint = getNextJoint(br);
          joint.setPredecessor(predecessor);
          joint.getPredecessor().addChild(joint);
          joint.setType(LOWER_BODY);
          predecessor = joint;
          
            //End site
            joint = getNextJoint(br);
            joint.setEnd(true);
            joint.setPredecessor(predecessor);
            joint.setType(LOWER_BODY);
            joint.getPredecessor().addChild(joint);
  
      //RightHip
      joint = getNextJoint(br);
      joint.setPredecessor(this.root);
      joint.getPredecessor().addChild(joint);
      joint.setType(LOWER_BODY);
      predecessor = joint;
      
        //RightKnee
        joint = getNextJoint(br);
        joint.setPredecessor(predecessor);
        joint.getPredecessor().addChild(joint);
        joint.setType(LOWER_BODY);
        predecessor = joint;
        
          //RightAnkle
          joint = getNextJoint(br);
          joint.setPredecessor(predecessor);
          joint.getPredecessor().addChild(joint);
          joint.setType(LOWER_BODY);
          predecessor = joint;
          
            //End site
            joint = getNextJoint(br);
            joint.setEnd(true);
            joint.setPredecessor(predecessor);
            joint.setType(LOWER_BODY);
            joint.getPredecessor().addChild(joint);   
  
}

public Animation parseMotionData(BufferedReader br)
{
  
  String line = new String();
  do {
    try {
      line = br.readLine();
    }
    catch (IOException e) {
      System.out.println("Reading a line failed. IOException!");
      exit();
    }
  } while (!line.contains("Frames:"));
  
  String tokens[] = line.split(":");
  line = tokens[1];
  while (line.charAt(0) == ' ')//Scroll until begin of word OFFSET
  {
    line = line.substring(1);
  }
  
  int frame_count = Integer.parseInt(line);
  ArrayList<Frame> frames = new ArrayList<Frame>();
  
  try {
      line = br.readLine();
  }
  catch (IOException e) {
    System.out.println("Reading a line failed. IOException!");
    exit();
  }
  tokens = line.split(":");
  line = tokens[1];
  while (line.charAt(0) == ' ')//Scroll until begin of word OFFSET
  {
    line = line.substring(1);
  }
  
  int fps = (int) (1.0f / Float.parseFloat(line));
  System.out.println(fps);
  
  //Loop through frames
  for (int i = 0; i < frame_count; i++)
  {
    try {
      line = br.readLine();
    }
    catch (IOException e) {
      System.out.println("Reading a line failed. IOException!");
      exit();
    }
    if (frame_count == 269 || frame_count == 11995) //Diffent amount of spaces with these animations
    {
      tokens = line.split("    ");
    }
    else
    {
      tokens = line.split(" ");
    }
    //System.out.println(tokens[0] + "/" + tokens[1] + "/" + tokens[2]);
    
    ArrayList<Vec3> data = new ArrayList<Vec3>();
    
    for (int j = 0; j < CHANNEL_COUNT; j+=3)
    {
      data.add( new Vec3(Float.parseFloat(tokens[j]),Float.parseFloat(tokens[j+1]),Float.parseFloat(tokens[j+2])) );
    }
    
    frames.add(new Frame(data));
  }
  return new Animation(frame_count, fps, frames);
}

public Joint getNextJoint(BufferedReader br)
{
  String line = new String();
  do {
    try {
      line = br.readLine();
      
    }
    catch (IOException e) {
      System.out.println("Reading a line failed. IOException!");
      exit();
    }
  } while (!line.contains("OFFSET"));
  
  
  while (line.charAt(0) != 'O')//Scroll until begin of word OFFSET
  {
    line = line.substring(1);
  }
  //System.out.println(line);
  String tokens[] = line.split(" ");
  //System.out.println("tokens: " + tokens[0] +  ", " + tokens[1] + ", " + tokens[2]+ ", " + tokens[3]);
  return new Joint(false, new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
}
