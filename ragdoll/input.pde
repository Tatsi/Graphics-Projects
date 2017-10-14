void keyPressed() {
    if (key == '1') {
      STATE = STATE_WALK;
      current_animation = walk;
    } 
    else if (key == '2') {
      STATE = STATE_TURN;
      current_animation = turn;
    } 
    else if (key == '3') {
      STATE = STATE_VARIOUS;
      current_animation = various;
    } 
    else if (key == ENTER) {
      LIMP = !LIMP;
      if (LIMP)
      {
        //Calculate velocities for joints
        initRagdoll(root);
        
        //Calculate initial distances for constraints
        
        //Hip
        Joint left_hip = root.getChildren().get(1);
        Joint right_hip = root.getChildren().get(2);
        hip_distance = getDistance(left_hip.getGlobalPosition(), right_hip.getGlobalPosition());
        
        //Back
        Joint upper_back = root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0);
        head_distance = getDistance(root.getGlobalPosition(), upper_back.getGlobalPosition());
        Joint neck = root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0);
        neck_distance = getDistance(root.getGlobalPosition(), neck.getGlobalPosition());
        Joint shoulder_center = root.getChildren().get(0).getChildren().get(0).getChildren().get(0);
        shoulder_center_distance = getDistance(root.getGlobalPosition(), shoulder_center.getGlobalPosition());
        Joint chest = root.getChildren().get(0).getChildren().get(0);
        chest_distance = getDistance(root.getGlobalPosition(), chest.getGlobalPosition());
        
        //Shoulders
        Joint left_shoulder = root.getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().get(0);
        Joint right_shoulder = root.getChildren().get(0).getChildren().get(0).getChildren().get(2).getChildren().get(0);
        shoulder_distance = getDistance(left_shoulder.getGlobalPosition(), right_shoulder.getGlobalPosition());

        //Elbows
        Joint left_elbow = root.getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(0);
        Joint right_elbow = root.getChildren().get(0).getChildren().get(0).getChildren().get(2).getChildren().get(0).getChildren().get(0);
        elbow_distance = getDistance(left_elbow.getGlobalPosition(), right_elbow.getGlobalPosition());
        
        //Wrists
        Joint left_wrist = root.getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0);
        Joint right_wrist = root.getChildren().get(0).getChildren().get(0).getChildren().get(2).getChildren().get(0).getChildren().get(0).getChildren().get(0);
        wrist_distance = getDistance(left_wrist.getGlobalPosition(), right_wrist.getGlobalPosition());
        
        

        //Hips to ab
        Joint ab = root.getChildren().get(0).getChildren().get(0); //NOTE: chest not ab
        left_hip_ab_distance = getDistance(left_hip.getGlobalPosition(), ab.getGlobalPosition());
        right_hip_ab_distance = getDistance(right_hip.getGlobalPosition(), ab.getGlobalPosition());
        
        //Knees to ab
        Joint left_knee = left_hip.getChildren().get(0);
        Joint right_knee = right_hip.getChildren().get(0);
        left_knee_ab_distance = getDistance(left_knee.getGlobalPosition(), ab.getGlobalPosition());
        right_knee_ab_distance = getDistance(right_knee.getGlobalPosition(), ab.getGlobalPosition());
        
        //Ankles to ab
        Joint left_ankle = left_hip.getChildren().get(0).getChildren().get(0);
        Joint right_ankle = right_hip.getChildren().get(0).getChildren().get(0);
        left_ankle_ab_distance = getDistance(left_ankle.getGlobalPosition(), ab.getGlobalPosition());
        right_ankle_ab_distance = getDistance(right_ankle.getGlobalPosition(), ab.getGlobalPosition());
        
        //Knees
        knee_distance = getDistance(left_knee.getGlobalPosition(), right_knee.getGlobalPosition());
        
        //Ankle distance
        ankle_distance = getDistance(left_ankle.getGlobalPosition(), right_ankle.getGlobalPosition());
        
      }
    } 
    else if (key == CODED) {
      if (keyCode == LEFT) {
        rotation+=0.1f;
        if (rotation > 2.f * PI)
        {
          rotation = 0.0f;
        }
      } else if (keyCode == RIGHT) {
        rotation-=0.1f;
        if (rotation < 0.0f)
        {
          rotation += 2.f * PI;
        }
      }
      else if (keyCode == UP) {
        constraints_on = !constraints_on;
      } 
    }
}
