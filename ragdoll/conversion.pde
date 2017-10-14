public Quaternion eulerToQuaternion(EulerRotation a)
{
  float[][] a_rotate_x = { {1.0f,            0.0f,          0.0f,           0.0f}, 
                               {0.0f,            cos(a.x_rad),  -sin(a.x_rad),  0.0f},
                               {0.0f,            sin(a.x_rad),  cos(a.x_rad),   0.0f},
                               {0.0f,            0.0f,          0.0f,           1.0f} };
                                         
  float[][] a_rotate_y = { {cos(a.y_rad),    0.0f,          sin(a.y_rad),   0.0f}, 
                           {0.0f,            1.0f,          0.0f,           0.0f},
                           {-sin(a.y_rad),   0.0f,          cos(a.y_rad),   0.0f},
                           {0.0f,            0.0f,          0.0f,           1.0f} };
      
  float[][] a_rotate_z = { {cos(a.z_rad),    -sin(a.z_rad), 0.0f,           0.0f}, 
                           {sin(a.z_rad),    cos(a.z_rad),  0.0f,           0.0f},
                           {0.0f,            0.0f,          1.0f,           0.0f},
                           {0.0f,            0.0f,          0.0f,           1.0f} };

  float[][] m1 = Mat.identity(4);
      
  m1 = Mat.multiply(m1, a_rotate_z);
  m1 = Mat.multiply(m1, a_rotate_x);
  m1 = Mat.multiply(m1, a_rotate_y);
  
  Matrix4x4 matrix1 = new Matrix4x4(m1[0][0], m1[0][1], m1[0][2], m1[0][3],
                                    m1[1][0], m1[1][1], m1[1][2], m1[1][3], 
                                    m1[2][0], m1[2][1], m1[2][2], m1[2][3],
                                    m1[3][0], m1[3][1], m1[3][2], m1[3][3]);
              
  return Quaternion.createFromMatrix(matrix1);
}


public float getDistance(Vec3 a, Vec3 b)
{
  return sqrt( pow(a.x-b.x, 2.f) + pow(a.y-b.y, 2.f) + pow(a.z-b.z, 2.f) ); 
}
