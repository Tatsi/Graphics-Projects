void Radiosity::vertexTaskFunc( MulticoreLauncher::Task& task )
{
    RadiosityContext& ctx = *(RadiosityContext*)task.data;

    if( ctx.m_bForceExit )
        return;

    // which vertex are we to compute?
    int v = task.idx;

    // fetch vertex and its normal
    Vec3f n = ctx.m_scene->vertex(v).n.normalized();
    Vec3f o = ctx.m_scene->vertex(v).p + 0.01f*n;//Nudged origin

    // direct lighting pass? => integrate direct illumination by shooting shadow rays to light source
    if ( ctx.m_currentBounce == 0 )
    {
        Vec3f E(0);
        for ( int r = 0; r < ctx.m_numDirectRays; ++r )
        {
            // draw sample on light source
            float pdf;
            Vec3f Pl;
			Random rnd(r);//Use r as seed
            //ctx.m_light->sample(pdf, Pl, 0, rnd); // Total random sampling
			ctx.m_light->QMCsample(pdf, Pl, r+1, rnd); // QMC sampling using Halton sequence

            // construct vector from current vertex (o) to light sample
			Vec3f vertex_to_light = Pl - o; //Is there something else?
            
			RaycastResult result = ctx.m_rt->raycast(o, vertex_to_light);
            // trace shadow ray to see if it's blocked
			if (result.tri == nullptr) // Ray did not hit anything = Visibility function
            { // if not, add the appropriate emission, 1/r^2 and clamped cosine terms, accounting for the PDF as well.
                Vec3f vertex_to_light_normalized =  vertex_to_light.normalized();
                
                // Omega (cosine) of "vertex to light" and surface normal
				float O_vl_sn = fastMax(0.0f, vertex_to_light_normalized.dot(n));
                
                // Omega (cosine) of "light to vertex" and light normal. Normalized() might not be required, check it
				float O_lv_ln = fastMax(0.0f, ctx.m_light->getNormal().normalized().dot(-1.0f * vertex_to_light_normalized));
                
                // Emission of light source
                Vec3f emission = ctx.m_light->getEmission();
                
                // rest of the terms inside the sum without emission E(yi)
				float multiplier = (O_lv_ln*O_vl_sn) / (powf(vertex_to_light.length(), 2.0f)*pdf);
                
                // accumulate into E
                E = E + multiplier*emission;
            }
        }
        // Note we are NOT multiplying by PI here;
        // it's implicit in the hemisphere-to-light source area change of variables.
        // The result we are computing is _irradiance_, not radiosity.
        ctx.m_vecCurr[ v ] = E * (1.0f/ctx.m_numDirectRays); // = 1 /N
        ctx.m_vecResult[ v ] = ctx.m_vecCurr[ v ];
	}
    else
    {
        // OK, time for indirect!
        // Implement hemispherical gathering integral for bounces > 1.

        // Get local coordinate system the rays are shot from.
        Mat3f B = formBasis( n );

        Vec3f E(0.0f);
        for ( int r = 0; r < ctx.m_numHemisphereRays; ++r )
        {
            // Draw a cosine weighted direction and find out where it hits (if anywhere)
            // You need to transform it from the local frame to the vertex' hemisphere using B.
			Vec3f d;// = random_ray_in_upper_hemisphere
            
			Random rnd;
			do
			{
				d.x = rnd.getF32(-1.0f, 1.0f);
				d.y = rnd.getF32(-1.0f, 1.0f);
			} while (powf(d.x, 2.0f) + powf(d.y, 2.0f) > 1.0f);
			d.z = sqrtf(1.0f - powf(d.x, 2.0f) - powf(d.y, 2.0f));//Calculate z
			d = d.normalized();

            // Make the direction long but not too long to avoid numerical instability in the ray tracer.
            // For our scenes, 100 is a good length. (I know, this special casing sucks.)
            d = 100.0f * d;
            
            // Transform d to vertices triangles coordinate system
            d = B * d;
            
            // Shoot ray, see where we hit
            const RaycastResult result = ctx.m_rt->raycast( o, d );
            if ( result.tri != nullptr )
            {
                // check for backfaces => don't accumulate if we hit a surface from below!
				float dot_val = dot(result.tri->normal(), d);

                // If dot product of hit triangles normal and ray is positive, we have hit the triangle from below. Then do nothing
                if (dot_val < 0.0f)//We have a hit from above, Ok
                {
                    // interpolate lighting from previous pass
                    const Vec3i& indices = result.tri->m_data.vertex_indices;
                    
                    // fetch barycentric coordinates
                    float alfa = result.u; // alfa = u ?
					float beta = result.v; // beta = v ?
                    
                    // Ei = interpolated irradiance determined by ctx.m_vecPrevBounce from vertices using the barycentric coordinates                
                    // Are we supposed to use vecResult or vecCurr?
					Vec3f Ei = alfa * ctx.m_vecResult[indices[0]] + beta*ctx.m_vecResult[indices[1]] + (1.0f - alfa - beta)*ctx.m_vecResult[indices[2]];
                    

                    // Divide incident irradiance by PI so that we can turn it into outgoing
                    // radiosity by multiplying by the reflectance factor below.
                    Ei *= (1.0f / FW_PI);

                    // check for texture
                    const auto mat = result.tri->m_material;
                    if ( mat->textures[MeshBase::TextureType_Diffuse].exists() )
                    {
						// using the barycentric coordinates of the intersection (hit.u, hit.v) and the
						// vertex texture coordinates (hit.tri->m_vertices[i].t) of the intersected triangle,
						// compute the uv coordinate of the intersection point.
						Vec2f uv = Vec2f(.0f);
						// U
						uv.x = (1 - result.u - result.v) * result.tri->m_vertices[0].t.x +
							result.u * result.tri->m_vertices[1].t.x + result.v * result.tri->m_vertices[2].t.x;
						// V
						uv.y = (1 - result.u - result.v) * result.tri->m_vertices[0].t.y +
							result.u * result.tri->m_vertices[1].t.y + result.v * result.tri->m_vertices[2].t.y;

                        // read diffuse texture like in assignment1
                        const Texture& tex = mat->textures[MeshBase::TextureType_Diffuse];
                        const Image& teximg = *tex.getImage();
                    
						Vec2i texelCoords = getTexelCoords(uv, teximg.getSize());//Get texture coordinates
						Vec3f tex_color = teximg.getVec4f(texelCoords).getXYZ(); //fetch texture color from coordinates
                        Ei *= tex_color;
                    }
                    else
                    {
                        // no texture, use constant albedo from material structure.
						Ei *= mat->diffuse.getXYZ(); // (this is just one line)
                    }
                    E += Ei; // accumulate
                }
            }
        }
        // Store result for this bounce
        // Note that since we are storing irradiance, we multiply by PI(
        // (Remember the slides about cosine weighted importance sampling!)
        ctx.m_vecCurr[ v ] = E * (FW_PI / ctx.m_numHemisphereRays);
        // Also add to the global accumulator.
        ctx.m_vecResult[ v ] = ctx.m_vecResult[ v ] + ctx.m_vecCurr[ v ];

        // uncomment this to visualize only the current bounce
        // ctx.m_vecResult[ v ] = ctx.m_vecCurr[ v ];	
    }
    
}