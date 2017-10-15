Vec4f Renderer::computeShadingAmbientOcclusion(RayTracer* rt, const RaycastResult& hit, const CameraControls& cameraCtrl, Random& rnd)
{
	Vec4f color;

    //Nudge hitpoint 0.001 towards camera
    Vec3f triangle_to_camera = (cameraCtrl.getPosition() - hit.point).normalized(); // Unit vector
    Vec3f hit_point(hit.point.x + 0.001f*triangle_to_camera.x, hit.point.y + 0.001f*triangle_to_camera.y, 
        hit.point.z + 0.001f*triangle_to_camera.z); // = hit.point + triangle_to_camera;
    
	// Find normal vector of intersection point
	Vec3f tri_normal(hit.tri->normal());
    
	// flip normal if camera is behind triangle
	Vec3f hit_to_camera = (cameraCtrl.getPosition() - hit.point).normalized(); // Move the hit_point to origo
    // Plane tri_plane(tri_normal.x, tri_normal.y, tri_normal.z, 0.0f);
    if (dot(hit_to_camera, tri_normal) < 0.0f) //Camera on back side of triangle
    {
        tri_normal = -1.0f * tri_normal;
    }
    
	int no_hit_count = 0;// How many random rays did not hit anything?

	// Generate AO rays
	for (int i = 0; i < m_aoNumRays; i++)
	{
		// Pick a random direction:
		// Pick a 2D-random point until it is inside x^2+y^2<=1
		Vec3f rnd_point;
		do
		{
			rnd_point.x = rnd.getF32(-1.0f, 1.0f);
			rnd_point.y = rnd.getF32(-1.0f, 1.0f);
		} while (powf(rnd_point.x, 2.0f) + powf(rnd_point.y, 2.0f) > 1.0f);
		rnd_point.z = sqrtf(1.0f - powf(rnd_point.x, 2.0f) - powf(rnd_point.y, 2.0f)); // Calculate z
        rnd_point = rnd_point.normalized();
        
		//Generate rotation matrix:
		Mat3f R = formBasis(tri_normal);

		//Rotate:
		rnd_point = R * rnd_point;
		// Set lenght from 1.0f to m_aoRayLength.
		Vec3f ao_ray = m_aoRayLength * rnd_point;
		// Trace a ray from hit_point to ray direction:
		RaycastResult result = rt->raycast(hit_point, ao_ray);
		if (result.tri == nullptr)//There was no hit
		{
			no_hit_count++;
		}
	}
	// Set color
	float ratio = ((float) no_hit_count) / ((float) m_aoNumRays);
    return Vec4f(ratio, ratio, ratio, 1.0f );
}