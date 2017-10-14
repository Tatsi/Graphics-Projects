//Draw skeleteton (except legs) from original motion data
public void drawEnds(Joint joint, ArrayList<EulerRotation> rotations)
{
  pushMatrix();
  
  stroke(255,255,255);
  
  line(0.0f,0.0f,0.0f, joint.getOffset().x, joint.getOffset().y, joint.getOffset().z);

  translate(joint.getOffset().x, joint.getOffset().y, joint.getOffset().z);  
  
  if (!joint.isEnd())
  {     
    rotateZ(rotations.get(rotation_counter).z_rad);
    rotateX(rotations.get(rotation_counter).x_rad); 
    rotateY(rotations.get(rotation_counter).y_rad);
    rotation_counter++;
  }
  else
  {
    stroke(255,100,100);
    sphere(1); 
  }
  
  for (Joint j : joint.getChildren())
  {
    drawEnds(j, rotations);
  }
  popMatrix();
}

public void drawRagdoll(Joint joint)
{
  drawJoint(joint);
  pushMatrix();
  translate(joint.getGlobalPosition().x,
         joint.getGlobalPosition().y,
         joint.getGlobalPosition().z);
  sphere(1.f);
  popMatrix();

  
}

public void drawJoint(Joint joint)
{
  pushMatrix();
    stroke(0,255,0);  
    translate(joint.getGlobalPosition().x,
              joint.getGlobalPosition().y,
              joint.getGlobalPosition().z);
    sphere(2);//Skeleton root
  popMatrix();
  stroke(0,0,255);
         
  for (Joint j : joint.getChildren())
  {
    line(joint.getGlobalPosition().x,
    joint.getGlobalPosition().y,
    joint.getGlobalPosition().z,
    j.getGlobalPosition().x,
    j.getGlobalPosition().y,
    j.getGlobalPosition().z);
  }
  
  for (Joint j : joint.getChildren())
  {
    drawJoint(j);
  }
  
}
