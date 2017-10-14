import java.util.ArrayList;

import toxi.geom.*;
import papaya.*;

import peasy.*;
PeasyCam cam;

public Joint root;

public Animation walk;
public Animation turn;
public Animation various;
public Animation current_animation;

static final int CHANNEL_COUNT = 60;
public int rotation_counter = 0;
public static final float offset_constant = 0.01f;

public static float hip_distance = 0.0f;

//Back distances
public static float head_distance = 0.0f;
public static float neck_distance = 0.0f;
public static float shoulder_center_distance = 0.0f;
public static float chest_distance = 0.0f;
public static float shoulder_distance = 0.0f;
public static float elbow_distance = 0.0f;
public static float wrist_distance = 0.0f;
public static float left_hip_ab_distance = 0.0f;
public static float right_hip_ab_distance = 0.0f;
public static float left_knee_ab_distance = 0.0f;
public static float right_knee_ab_distance = 0.0f;
public static float left_ankle_ab_distance = 0.0f;
public static float right_ankle_ab_distance = 0.0f;
public static float knee_distance = 0.0f;
public static float ankle_distance = 0.0f;

public static final float bounce_damping = 0.3f;
public static final float friction = 0.8f;
public static final float stretch = 0.02f;//If bones strecth or shrink more than this they are moved back

public static boolean constraints_on = false;
//Joint types
public static final int UPPER_BODY = 0;
public static final int LOWER_BODY = 1;

public static final int STATE_WALK = 0;
public static final int STATE_TURN = 1;
public static final int STATE_VARIOUS = 2;

public static Vec3 gravity;
public static float rotation = 0.0f; 

public static int STATE = STATE_WALK;
public static boolean LIMP = false;

void setup() {
  gravity = new Vec3(0.0f,-9.f,0.0f);
  size(800, 600, OPENGL);
  stroke(255);
  background(0, 0, 0);
  cursor(CROSS);
  frameRate(100);
  root = null;
  walk = parseBVH("walk_01.bvh");
  turn = parseBVH("turn_1.bvh");
  various = parseBVH("various_gestures_01.bvh");
  current_animation = walk;
  //cam = new PeasyCam(this, 100);
  //cam.setMinimumDistance(90);
  //cam.setMaximumDistance(3000);
}

void draw() {
  
  background(0, 0, 0);
  
  //render FPS
  textSize(12);
  fill(255, 0, 0);
  text("Render FPS: " + frameRate, 10, 20);
  switch (STATE)
  {
    case STATE_WALK:
      text("ANIMATION: Walk", 10, 40);
      break;
    case STATE_TURN:
      text("ANIMATION: Turn", 10, 40);
      break;
    case STATE_VARIOUS:
      text("ANIMATION: Various gestures", 10, 40);
      break;
  }
  fill(20, 200, 0);
  fill(200, 100, 20);
  text("Change animation with keys 1, 2 and 3", 10, 110);
  text("Rotate view with LEFT and RIGHT keys", 10, 130);
  text("Press ENTER to activate RAGDOLL. ON="+LIMP, 10, 150);
  text("Press UP to turn off/on constraints that make the body a bit stiff. ON="+constraints_on, 10, 170);
  
  pushMatrix();
    translate(400, 400);//Move origo to center of window
    rotateX(PI);//Turn Y up
    rotation_counter = 0;
    rotateY(rotation);
    //Draw axis
    pushMatrix();
      stroke(0,255,0);
      line(0,0,0,0,350,0);
      fill(0, 255, 0);
      pushMatrix();
        translate(0, 350);
        rotateX(PI);
        text("Y", -3, -10); 
      popMatrix();
      stroke(255,0,0);
      line(-350,0,0,350,0,0);
      fill(255, 0, 0);
      text("X", -370, 4); 
    popMatrix();
    ////////////////
    //Draw objects//
    ////////////////
    pushMatrix();
      //Move to skeleton root
      translate(current_animation.getCurrentFrame().getRootTranslation().x, 
                current_animation.getCurrentFrame().getRootTranslation().y,
                current_animation.getCurrentFrame().getRootTranslation().z);
      stroke(255,100,100);           
      sphere(3);//Skeleton root
      if (!LIMP)
      {
        //Draw original movement for upper body
        ArrayList<EulerRotation> rotations = current_animation.getCurrentFrame().getRotations();
        drawEnds(root, current_animation.getCurrentFrame().getRotations());//Draw upper body & toe positions?
        current_animation.advance();//Advance to next frame
      }
    popMatrix();
    if (LIMP) //Draw ragdoll
    {
      updateRagdoll(root);

      if (constraints_on)
      {
        satisfyConstraints();

        //updateDistances(root);
      }
      handleCollisions(root);
      updateDistances(root);
      drawRagdoll(root);
    }
  popMatrix();
}
