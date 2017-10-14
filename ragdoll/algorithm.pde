
int rot_counter;
public void initRagdoll(Joint root)
{
  //initRagdollJoint(root, current_animation.getCurrentFrame(), current_animation.getNextFrame());
  rot_counter = 0;
  initGlobalPositions(root, new PMatrix3D(), new PMatrix3D(), current_animation.getCurrentFrame().getRotations());
}
public void updateDistances(Joint joint)
{
  while (joint != root && getDistance(joint.getGlobalPosition(), joint.getPredecessor().getGlobalPosition()) >  (1.f+stretch) *joint.getLength())
  {
    Vec3 v = new Vec3(joint.getPredecessor().getGlobalPosition().x-joint.getGlobalPosition().x,
                      joint.getPredecessor().getGlobalPosition().y-joint.getGlobalPosition().y,
                      joint.getPredecessor().getGlobalPosition().z-joint.getGlobalPosition().z);

    float dist = getDistance(joint.getGlobalPosition(), joint.getPredecessor().getGlobalPosition()) - joint.getLength();
    float v_dist = getDistance(new Vec3(0.f,0.f,0.f), v);
    v.x = v.x / v_dist;
    v.y = v.y / v_dist;
    v.z = v.z / v_dist;
    v.x = dist * v.x;
    v.y = dist * v.y;
    v.z = dist * v.z;
    
    joint.setGlobalPosition(new Vec3(joint.getGlobalPosition().x + offset_constant*v.x, 
                                     joint.getGlobalPosition().y + offset_constant*v.y,
                                     joint.getGlobalPosition().z + offset_constant*v.z));
                                     
  } 
  while (joint != root && getDistance(joint.getGlobalPosition(), joint.getPredecessor().getGlobalPosition()) <  (1.f-stretch) * joint.getLength())
  {
    Vec3 v = new Vec3(joint.getGlobalPosition().x-joint.getPredecessor().getGlobalPosition().x,
                      joint.getGlobalPosition().y-joint.getPredecessor().getGlobalPosition().y,
                      joint.getGlobalPosition().z-joint.getPredecessor().getGlobalPosition().z);
                      
    float dist = getDistance(joint.getGlobalPosition(), joint.getPredecessor().getGlobalPosition()) - joint.getLength();
    float v_dist = getDistance(new Vec3(0.f,0.f,0.f), v);
    v.x = v.x / v_dist;
    v.y = v.y / v_dist;
    v.z = v.z / v_dist;
    v.x = dist*v.x;
    v.y = dist*v.y;
    v.z = dist*v.z;
    
    joint.setGlobalPosition(new Vec3(joint.getGlobalPosition().x - offset_constant*v.x, 
                                     joint.getGlobalPosition().y - offset_constant*v.y,
                                     joint.getGlobalPosition().z - offset_constant*v.z));
  }
  for (Joint j : joint.getChildren())
  {
    updateRagdoll(j);
  }
}

public void updateRagdoll(Joint joint)
{
  float deltaTime = 1.f / (float) current_animation.getFPS();
  Vec3 delta = new Vec3(0.f,0.f,0.f);
  
  joint.setVelocity(new Vec3(joint.getVelocity().x, 
                             joint.getVelocity().y + deltaTime * gravity.y, 
                             joint.getVelocity().z ));
  delta.y = deltaTime * gravity.y;
  
  
  joint.setGlobalPosition(new Vec3(joint.getGlobalPosition().x + deltaTime*joint.getVelocity().x, 
                                   joint.getGlobalPosition().y + deltaTime*joint.getVelocity().y,
                                   joint.getGlobalPosition().z + deltaTime*joint.getVelocity().z));
  
  delta.x = deltaTime*joint.getVelocity().x;
  delta.y += deltaTime*joint.getVelocity().y;
  delta.z = deltaTime*joint.getVelocity().z;
  
  while (joint != root && getDistance(joint.getGlobalPosition(), joint.getPredecessor().getGlobalPosition()) >  (1.f+stretch) *joint.getLength())
  {
    Vec3 v = new Vec3(joint.getPredecessor().getGlobalPosition().x-joint.getGlobalPosition().x,
                      joint.getPredecessor().getGlobalPosition().y-joint.getGlobalPosition().y,
                      joint.getPredecessor().getGlobalPosition().z-joint.getGlobalPosition().z);

    float dist = getDistance(joint.getGlobalPosition(), joint.getPredecessor().getGlobalPosition()) - joint.getLength();
    float v_dist = getDistance(new Vec3(0.f,0.f,0.f), v);
    v.x = v.x / v_dist;
    v.y = v.y / v_dist;
    v.z = v.z / v_dist;
    v.x = dist*v.x;
    v.y = dist*v.y;
    v.z = dist*v.z;
    
    joint.setGlobalPosition(new Vec3(joint.getGlobalPosition().x + offset_constant*v.x, 
                                     joint.getGlobalPosition().y + offset_constant*v.y,
                                     joint.getGlobalPosition().z + offset_constant*v.z));
                                     
  } 
  while (joint != root && getDistance(joint.getGlobalPosition(), joint.getPredecessor().getGlobalPosition()) <  (1.f-stretch) * joint.getLength())
  {
    Vec3 v = new Vec3(joint.getGlobalPosition().x-joint.getPredecessor().getGlobalPosition().x,
                      joint.getGlobalPosition().y-joint.getPredecessor().getGlobalPosition().y,
                      joint.getGlobalPosition().z-joint.getPredecessor().getGlobalPosition().z);
                      
    float dist = getDistance(joint.getGlobalPosition(), joint.getPredecessor().getGlobalPosition()) - joint.getLength();
    float v_dist = getDistance(new Vec3(0.f,0.f,0.f), v);
    v.x = v.x / v_dist;
    v.y = v.y / v_dist;
    v.z = v.z / v_dist;
    v.x = dist*v.x;
    v.y = dist*v.y;
    v.z = dist*v.z;
    
    joint.setGlobalPosition(new Vec3(joint.getGlobalPosition().x - offset_constant*v.x, 
                                     joint.getGlobalPosition().y - offset_constant*v.y,
                                     joint.getGlobalPosition().z - offset_constant*v.z));
  }

  for (Joint j : joint.getChildren())
  {
    updateRagdoll(j);
  }
}

public void handleCollisions(Joint joint)
{
  if (abs(joint.getGlobalPosition().y) < 0.05f)//Set y to 0
  {
    joint.setVelocity(new Vec3(friction * joint.getVelocity().x, 
                               0.f, 
                               friction * joint.getVelocity().z ));
  }
  else if (joint.getGlobalPosition().y <= 0.f)
  {
    joint.setGlobalPosition(new Vec3(joint.getGlobalPosition().x, 
                                     0.0f,
                                     joint.getGlobalPosition().z));
    //Bounce + apply friction
    joint.setVelocity(new Vec3(friction * joint.getVelocity().x, 
                                 - bounce_damping * joint.getVelocity().y, 
                               friction * joint.getVelocity().z ));
  }

  for (Joint child : joint.getChildren())
  {
    handleCollisions(child);
  }
  
}

//Force back, shoulders to stay approximately at shape
public void satisfyConstraints()
{
  float tolerance = 0.01f;
  Joint left_hip = root.getChildren().get(1);
  Joint right_hip = root.getChildren().get(2);
  Joint left_knee = left_hip.getChildren().get(0);
  Joint right_knee = right_hip.getChildren().get(0);
  Joint ab = root.getChildren().get(0).getChildren().get(0); 
 
  boolean change = false;
 
  //Left ankle to ab
  Joint left_ankle = left_hip.getChildren().get(0).getChildren().get(0);
  float x = getDistance(left_ankle.getGlobalPosition(), ab.getGlobalPosition()) - left_ankle_ab_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(ab.getGlobalPosition(), left_ankle.getGlobalPosition());
    v.x = left_ankle.getGlobalPosition().x - ab.getGlobalPosition().x;
    v.y = left_ankle.getGlobalPosition().y - ab.getGlobalPosition().y;
    v.z = left_ankle.getGlobalPosition().z - ab.getGlobalPosition().z;
    v.x = left_ankle_ab_distance * (v.x / v_dist);
    v.y = left_ankle_ab_distance * (v.y / v_dist);
    v.z = left_ankle_ab_distance * (v.z / v_dist);
    left_ankle.setGlobalPosition(new Vec3(ab.getGlobalPosition().x + v.x,
                                          ab.getGlobalPosition().y + v.y,
                                          ab.getGlobalPosition().z + v.z));
    change = true; 
  }
  //right ankle to ab
  Joint right_ankle = right_hip.getChildren().get(0).getChildren().get(0);
  x = getDistance(right_ankle.getGlobalPosition(), ab.getGlobalPosition()) - right_ankle_ab_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(ab.getGlobalPosition(), right_ankle.getGlobalPosition());
    v.x = right_ankle.getGlobalPosition().x - ab.getGlobalPosition().x;
    v.y = right_ankle.getGlobalPosition().y - ab.getGlobalPosition().y;
    v.z = right_ankle.getGlobalPosition().z - ab.getGlobalPosition().z;
    v.x = right_ankle_ab_distance * (v.x / v_dist);
    v.y = right_ankle_ab_distance * (v.y / v_dist);
    v.z = right_ankle_ab_distance * (v.z / v_dist);
    right_ankle.setGlobalPosition(new Vec3(ab.getGlobalPosition().x + v.x,
                                           ab.getGlobalPosition().y + v.y,
                                           ab.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Left knee to ab
  
  x = getDistance(left_knee.getGlobalPosition(), ab.getGlobalPosition()) - left_knee_ab_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(ab.getGlobalPosition(), left_knee.getGlobalPosition());
    v.x = left_knee.getGlobalPosition().x - ab.getGlobalPosition().x;
    v.y = left_knee.getGlobalPosition().y - ab.getGlobalPosition().y;
    v.z = left_knee.getGlobalPosition().z - ab.getGlobalPosition().z;
    v.x = left_knee_ab_distance * (v.x / v_dist);
    v.y = left_knee_ab_distance * (v.y / v_dist);
    v.z = left_knee_ab_distance * (v.z / v_dist);
    left_knee.setGlobalPosition(new Vec3(ab.getGlobalPosition().x + v.x,
                                         ab.getGlobalPosition().y + v.y,
                                         ab.getGlobalPosition().z + v.z));
    change = true; 
  }
  //right knee to ab
  
  x = getDistance(right_knee.getGlobalPosition(), ab.getGlobalPosition()) - right_knee_ab_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(ab.getGlobalPosition(), left_knee.getGlobalPosition());
    v.x = right_knee.getGlobalPosition().x - ab.getGlobalPosition().x;
    v.y = right_knee.getGlobalPosition().y - ab.getGlobalPosition().y;
    v.z = right_knee.getGlobalPosition().z - ab.getGlobalPosition().z;
    v.x = right_knee_ab_distance * (v.x / v_dist);
    v.y = right_knee_ab_distance * (v.y / v_dist);
    v.z = right_knee_ab_distance * (v.z / v_dist);
    right_knee.setGlobalPosition(new Vec3(ab.getGlobalPosition().x + v.x,
                                          ab.getGlobalPosition().y + v.y,
                                          ab.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Left hip to ab

  x = getDistance(left_hip.getGlobalPosition(), ab.getGlobalPosition()) - left_hip_ab_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(ab.getGlobalPosition(), left_hip.getGlobalPosition());
    v.x = left_hip.getGlobalPosition().x - ab.getGlobalPosition().x;
    v.y = left_hip.getGlobalPosition().y - ab.getGlobalPosition().y;
    v.z = left_hip.getGlobalPosition().z - ab.getGlobalPosition().z;
    v.x = left_hip_ab_distance * (v.x / v_dist);
    v.y = left_hip_ab_distance * (v.y / v_dist);
    v.z = left_hip_ab_distance * (v.z / v_dist);
    left_hip.setGlobalPosition(new Vec3(ab.getGlobalPosition().x + v.x,
                                        ab.getGlobalPosition().y + v.y,
                                        ab.getGlobalPosition().z + v.z));
    change = true; 
  }
  //right hip to ab
  x = getDistance(right_hip.getGlobalPosition(), ab.getGlobalPosition()) - right_hip_ab_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(ab.getGlobalPosition(), left_hip.getGlobalPosition());
    v.x = right_hip.getGlobalPosition().x - ab.getGlobalPosition().x;
    v.y = right_hip.getGlobalPosition().y - ab.getGlobalPosition().y;
    v.z = right_hip.getGlobalPosition().z - ab.getGlobalPosition().z;
    v.x = right_hip_ab_distance * (v.x / v_dist);
    v.y = right_hip_ab_distance * (v.y / v_dist);
    v.z = right_hip_ab_distance * (v.z / v_dist);
    right_hip.setGlobalPosition(new Vec3(ab.getGlobalPosition().x + v.x,
                                        ab.getGlobalPosition().y + v.y,
                                        ab.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Root to chest
  Joint upper_back = root.getChildren().get(0).getChildren().get(0);
  
  x = getDistance(root.getGlobalPosition(), upper_back.getGlobalPosition()) - chest_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(upper_back.getGlobalPosition(), root.getGlobalPosition());
    v.x = upper_back.getGlobalPosition().x - root.getGlobalPosition().x;
    v.y = upper_back.getGlobalPosition().y - root.getGlobalPosition().y;
    v.z = upper_back.getGlobalPosition().z - root.getGlobalPosition().z;
    v.x = chest_distance * (v.x / v_dist);
    v.y = chest_distance * (v.y / v_dist);
    v.z = chest_distance * (v.z / v_dist);
    upper_back.setGlobalPosition(new Vec3(root.getGlobalPosition().x + v.x,
                                          root.getGlobalPosition().y + v.y,
                                          root.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Root to shoulder center
  upper_back = root.getChildren().get(0).getChildren().get(0).getChildren().get(0);
  
  x = getDistance(root.getGlobalPosition(), upper_back.getGlobalPosition()) - shoulder_center_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(upper_back.getGlobalPosition(), root.getGlobalPosition());
    v.x = upper_back.getGlobalPosition().x - root.getGlobalPosition().x;
    v.y = upper_back.getGlobalPosition().y - root.getGlobalPosition().y;
    v.z = upper_back.getGlobalPosition().z - root.getGlobalPosition().z;
    v.x = shoulder_center_distance * (v.x / v_dist);
    v.y = shoulder_center_distance * (v.y / v_dist);
    v.z = shoulder_center_distance * (v.z / v_dist);
    upper_back.setGlobalPosition(new Vec3(root.getGlobalPosition().x + v.x,
                                          root.getGlobalPosition().y + v.y,
                                          root.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Root to neck
  upper_back = root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0);
  
  x = getDistance(root.getGlobalPosition(), upper_back.getGlobalPosition()) - neck_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(upper_back.getGlobalPosition(), root.getGlobalPosition());
    v.x = upper_back.getGlobalPosition().x - root.getGlobalPosition().x;
    v.y = upper_back.getGlobalPosition().y - root.getGlobalPosition().y;
    v.z = upper_back.getGlobalPosition().z - root.getGlobalPosition().z;
    v.x = neck_distance * (v.x / v_dist);
    v.y = neck_distance * (v.y / v_dist);
    v.z = neck_distance * (v.z / v_dist);
    upper_back.setGlobalPosition(new Vec3(root.getGlobalPosition().x + v.x,
                                          root.getGlobalPosition().y + v.y,
                                          root.getGlobalPosition().z + v.z));
    //Update collars too, their in the same spot
    root.getChildren().get(0).getChildren().get(0).getChildren().get(1).setGlobalPosition(upper_back.getGlobalPosition());
    root.getChildren().get(0).getChildren().get(0).getChildren().get(2).setGlobalPosition(upper_back.getGlobalPosition());
    change = true; 
    
  }
  //Root to head
  upper_back = root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0);
  
  x = getDistance(root.getGlobalPosition(), upper_back.getGlobalPosition()) - head_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(upper_back.getGlobalPosition(), root.getGlobalPosition());
    v.x = upper_back.getGlobalPosition().x - root.getGlobalPosition().x;
    v.y = upper_back.getGlobalPosition().y - root.getGlobalPosition().y;
    v.z = upper_back.getGlobalPosition().z - root.getGlobalPosition().z;
    v.x = head_distance * (v.x / v_dist);
    v.y = head_distance * (v.y / v_dist);
    v.z = head_distance * (v.z / v_dist);
    upper_back.setGlobalPosition(new Vec3(root.getGlobalPosition().x + v.x,
                                          root.getGlobalPosition().y + v.y,
                                          root.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Ankles
  x = getDistance(left_ankle.getGlobalPosition(), right_ankle.getGlobalPosition()) - ankle_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(left_ankle.getGlobalPosition(), right_ankle.getGlobalPosition());
    v.x = right_ankle.getGlobalPosition().x - left_ankle.getGlobalPosition().x;
    v.y = right_ankle.getGlobalPosition().y - left_ankle.getGlobalPosition().y;
    v.z = right_ankle.getGlobalPosition().z - left_ankle.getGlobalPosition().z;
    v.x = 0.5f*x * (v.x / v_dist);
    v.y = 0.5f*x * (v.y / v_dist);
    v.z = 0.5f*x * (v.z / v_dist);
    right_ankle.setGlobalPosition(new Vec3(right_ankle.getGlobalPosition().x - v.x,
                                          right_ankle.getGlobalPosition().y - v.y,
                                          right_ankle.getGlobalPosition().z - v.z));
                                              
    left_ankle.setGlobalPosition(new Vec3(left_ankle.getGlobalPosition().x + v.x,
                                          left_ankle.getGlobalPosition().y + v.y,
                                          left_ankle.getGlobalPosition().z + v.z));
     change = true; 
  }
  //Knees
  x = getDistance(left_knee.getGlobalPosition(), right_knee.getGlobalPosition()) - knee_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(left_knee.getGlobalPosition(), right_knee.getGlobalPosition());
    v.x = right_knee.getGlobalPosition().x - left_knee.getGlobalPosition().x;
    v.y = right_knee.getGlobalPosition().y - left_knee.getGlobalPosition().y;
    v.z = right_knee.getGlobalPosition().z - left_knee.getGlobalPosition().z;
    v.x = 0.5f*x * (v.x / v_dist);
    v.y = 0.5f*x * (v.y / v_dist);
    v.z = 0.5f*x * (v.z / v_dist);
    right_knee.setGlobalPosition(new Vec3(right_knee.getGlobalPosition().x - v.x,
                                          right_knee.getGlobalPosition().y - v.y,
                                          right_knee.getGlobalPosition().z - v.z));
                                              
    left_knee.setGlobalPosition(new Vec3(left_knee.getGlobalPosition().x + v.x,
                                         left_knee.getGlobalPosition().y + v.y,
                                         left_knee.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Left hip to right hip  
  x = getDistance(left_hip.getGlobalPosition(), right_hip.getGlobalPosition()) - hip_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(left_hip.getGlobalPosition(), right_hip.getGlobalPosition());
    v.x = right_hip.getGlobalPosition().x - left_hip.getGlobalPosition().x;
    v.y = right_hip.getGlobalPosition().y - left_hip.getGlobalPosition().y;
    v.z = right_hip.getGlobalPosition().z - left_hip.getGlobalPosition().z;
    v.x = 0.5f*x * (v.x / v_dist);
    v.y = 0.5f*x * (v.y / v_dist);
    v.z = 0.5f*x * (v.z / v_dist);
    right_hip.setGlobalPosition(new Vec3(right_hip.getGlobalPosition().x - v.x,
                                              right_hip.getGlobalPosition().y - v.y,
                                              right_hip.getGlobalPosition().z - v.z));
                                              
    left_hip.setGlobalPosition(new Vec3(left_hip.getGlobalPosition().x + v.x,
                                             left_hip.getGlobalPosition().y + v.y,
                                             left_hip.getGlobalPosition().z + v.z));
    change = true; 
  }
  
  
  
  //Left shoulder to right shoulder
  Joint left_shoulder = root.getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().get(0);
  Joint right_shoulder = root.getChildren().get(0).getChildren().get(0).getChildren().get(2).getChildren().get(0);
  
  x = getDistance(left_shoulder.getGlobalPosition(), right_shoulder.getGlobalPosition()) - shoulder_distance;//X=positiivinen -> venymistä
  Joint neck = root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0);
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(left_shoulder.getGlobalPosition(), right_shoulder.getGlobalPosition());
    v.x = right_shoulder.getGlobalPosition().x - left_shoulder.getGlobalPosition().x;
    v.y = right_shoulder.getGlobalPosition().y - left_shoulder.getGlobalPosition().y;
    v.z = right_shoulder.getGlobalPosition().z - left_shoulder.getGlobalPosition().z;
    v.x = 0.5f*x * (v.x / v_dist);
    v.y = 0.5f*x * (v.y / v_dist);
    v.z = 0.5f*x * (v.z / v_dist);
    right_shoulder.setGlobalPosition(new Vec3(right_shoulder.getGlobalPosition().x - v.x,
                                              right_shoulder.getGlobalPosition().y - v.y,
                                              right_shoulder.getGlobalPosition().z - v.z));
                                              
    left_shoulder.setGlobalPosition(new Vec3(left_shoulder.getGlobalPosition().x + v.x,
                                             left_shoulder.getGlobalPosition().y + v.y,
                                             left_shoulder.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Left elbow to right elbow
  Joint left_elbow = root.getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(0);
  Joint right_elbow = root.getChildren().get(0).getChildren().get(0).getChildren().get(2).getChildren().get(0).getChildren().get(0);
  x = getDistance(left_elbow.getGlobalPosition(), right_elbow.getGlobalPosition()) - elbow_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(left_elbow.getGlobalPosition(), right_elbow.getGlobalPosition());
    v.x = right_elbow.getGlobalPosition().x - left_elbow.getGlobalPosition().x;
    v.y = right_elbow.getGlobalPosition().y - left_elbow.getGlobalPosition().y;
    v.z = right_elbow.getGlobalPosition().z - left_elbow.getGlobalPosition().z;
    v.x = 0.5f*x * (v.x / v_dist);
    v.y = 0.5f*x * (v.y / v_dist);
    v.z = 0.5f*x * (v.z / v_dist);
    right_elbow.setGlobalPosition(new Vec3(right_elbow.getGlobalPosition().x - v.x,
                                              right_elbow.getGlobalPosition().y - v.y,
                                              right_elbow.getGlobalPosition().z - v.z));
                                              
    left_elbow.setGlobalPosition(new Vec3(left_elbow.getGlobalPosition().x + v.x,
                                             left_elbow.getGlobalPosition().y + v.y,
                                             left_elbow.getGlobalPosition().z + v.z));
    change = true; 
  }
  //Left wrist to right wrist
  Joint left_wrist = root.getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0);
  Joint right_wrist = root.getChildren().get(0).getChildren().get(0).getChildren().get(2).getChildren().get(0).getChildren().get(0).getChildren().get(0);
  x = getDistance(left_wrist.getGlobalPosition(), right_wrist.getGlobalPosition()) - wrist_distance;//X=positiivinen -> venymistä
  
  if (abs(x) > tolerance)
  {
    Vec3 v = new Vec3(0.f,0.f,0.f);
    float v_dist = getDistance(left_wrist.getGlobalPosition(), right_wrist.getGlobalPosition());
    v.x = right_wrist.getGlobalPosition().x - left_wrist.getGlobalPosition().x;
    v.y = right_wrist.getGlobalPosition().y - left_wrist.getGlobalPosition().y;
    v.z = right_wrist.getGlobalPosition().z - left_wrist.getGlobalPosition().z;
    v.x = 0.5f*x * (v.x / v_dist);
    v.y = 0.5f*x * (v.y / v_dist);
    v.z = 0.5f*x * (v.z / v_dist);
    right_wrist.setGlobalPosition(new Vec3(right_wrist.getGlobalPosition().x - v.x,
                                           right_wrist.getGlobalPosition().y - v.y,
                                           right_wrist.getGlobalPosition().z - v.z));
                                              
    left_wrist.setGlobalPosition(new Vec3(left_wrist.getGlobalPosition().x + v.x,
                                          left_wrist.getGlobalPosition().y + v.y,
                                          left_wrist.getGlobalPosition().z + v.z));
    change = true; 
  }
}

public void initGlobalPositions(Joint j, PMatrix3D mat, PMatrix3D mat2, ArrayList<EulerRotation> rotations)
{
  
  if (j == root)
  {
    mat.translate(current_animation.getCurrentFrame().getRootTranslation().x, 
                  current_animation.getCurrentFrame().getRootTranslation().y,
                  current_animation.getCurrentFrame().getRootTranslation().z); 
    mat2.translate(current_animation.getNextFrame().getRootTranslation().x, 
                   current_animation.getNextFrame().getRootTranslation().y,
                   current_animation.getNextFrame().getRootTranslation().z); 
  }
  mat.translate(j.getOffset().x, 
                j.getOffset().y,
                j.getOffset().z); 
  
  mat2.translate(j.getOffset().x, 
                 j.getOffset().y,
                 j.getOffset().z); 
               
  if (!j.isEnd())
  {
    mat.rotateZ(rotations.get(rot_counter).z_rad);
    mat.rotateX(rotations.get(rot_counter).x_rad);
    mat.rotateY(rotations.get(rot_counter).y_rad);
    
    mat2.rotateZ(current_animation.getNextFrame().getRotations().get(rot_counter).z_rad);
    mat2.rotateX(current_animation.getNextFrame().getRotations().get(rot_counter).x_rad);
    mat2.rotateY(current_animation.getNextFrame().getRotations().get(rot_counter).y_rad);
    rot_counter++;
  }

  float[] v = {0.f,0.0f,0.0f,1.0f};
  float[] result = new float[4];
  float[] result2 = new float[4];
  //mat.apply(m);
  result = mat.mult(v, result);
  result2 = mat2.mult(v, result2);
  
  j.setGlobalPosition(new Vec3(result[0], result[1], result[2]));
  //Convert movement speed rougfly to meters with 0.1
  j.setVelocity( new Vec3(0.1f * (result2[0]-result[0]) / (1.f / (float) current_animation.getFPS()),
                          0.1f * (result2[1]-result[1]) / (1.f / (float) current_animation.getFPS()),
                          0.1f * (result2[2]-result[2]) / (1.f / (float) current_animation.getFPS())
                          ));
  
  for (Joint child : j.getChildren())
  {
    initGlobalPositions(child, mat.get(), mat2.get(), rotations);
  }
}
