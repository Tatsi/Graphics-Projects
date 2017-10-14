// ####################################
// Multithreaded raytracing algorithm 
// ####################################

#pragma omp parallel for 
for ( int j = 0; j < height; ++j )
{
    // Each thread must have its own random generator
    Random rnd;

    for ( int i = 0; i < width; ++i )
    {
        // generate ray through pixel
            float x = (i + 0.5f) / image->getSize().x *  2.0f - 1.0f;
            float y = (j + 0.5f) / image->getSize().y * -2.0f + 1.0f;
            // point on front plane in homogeneous coordinates
            Vec4f P0( x, y, 0.0f, 1.0f );
            // point on back plane in homogeneous coordinates
            Vec4f P1( x, y, 1.0f, 1.0f );

            // apply inverse projection, divide by w to get object-space points
            Vec4f Roh = (invP * P0);
            Vec3f Ro = (Roh * (1.0f / Roh.w)).getXYZ();
            Vec4f Rdh = (invP * P1);
            Vec3f Rd = (Rdh * (1.0f / Rdh.w)).getXYZ();

            // Subtract front plane point from back plane point,
            // yields ray direction.
            // NOTE that it's not normalized; the direction Rd is defined
            // so that the segment to be traced is [Ro, Ro+Rd], i.e.,
            // intersections that come _after_ the point Ro+Rd are to be discarded.
            Rd = Rd - Ro;

            // trace!
            RaycastResult hit = rt->raycast( Ro, Rd );

            // if we hit something, fetch a color and insert into image
            Vec4f color(0,0,0,1);
            if ( hit.tri != nullptr )
            {
                switch( mode )
                {
                case ShadingMode_Headlight:
                    color = computeShadingHeadlight( hit, cameraCtrl);
                    break;
                case ShadingMode_AmbientOcclusion:
                    color = computeShadingAmbientOcclusion( rt, hit, cameraCtrl, rnd );
                    break;
                case ShadingMode_Whitted:
                    color = computeShadingWhitted( rt, hit, cameraCtrl, rnd, 0 );
                    break;
                }
            }
            // put pixel.
            image->setVec4f(Vec2i(i, j), color);
    }

    // Print progress info
    ++lines_done;
    ::printf("%.2f%% \r", lines_done * 100.0f / height);
}

// #######################################################
// Sections of renderer.cpp where BVH acceleration is used
// #######################################################

RaycastResult RayTracer::raycast(const Vec3f& orig, const Vec3f& dir) const {
	++m_rayCount;
	float tmin = 1.0f, umin = 0.0f, vmin = 0.0f;
	int imin = -1;

	RaycastResult castresult;

	int element_count = 1;
	BvhNode* list[50];//Local stack, supported tree heigth is currently 50.
	list[0] = bvh->root;

	while (element_count > 0)
	{
		element_count--;
		int start, end; BvhNode* left; BvhNode* right;  AABB bb; //IntersectionData intersection;

		left = list[element_count]->leftChild;
		right = list[element_count]->rightChild;
		bb = list[element_count]->bb;
		bool intersection = rayboxintersection(orig, dir, bb);

		if (intersection)
		{
			//Check if this is a leaf = its children are NULL
			if (left == NULL) //Its enough to check one child
			{
				start = list[element_count]->startIndex;
				end = list[element_count]->endIndex;
				//Intersect triangles of this leaf
				for (size_t i = start; i < end + 1; i++)
				{
					float t, u, v;
					int tri_index = (*indexList)[i];
					//Original if: if ( (*m_triangles)[i].intersect_woop( orig, dir, t, u, v ) )
					if ((*m_triangles)[tri_index].intersect_woop(orig, dir, t, u, v))
					{
						if (t > 0.0f && t < tmin)//T=scaled hit distance. Only take intersection into account if this one is closer and on positive side
						{
							imin = tri_index;//we needed to update so we use the correct index in RayCastResult. Original row: imin = i;
							tmin = t;
							umin = u;
							vmin = v;
						}
					}
				}
			}
			else //This is not a leaf, but there was an intersection with this BV so add these to stack
			{
				list[element_count] = left;
				list[element_count + 1] = right;
				element_count += 2;
			}
		}
	}

	if (imin != -1) {
	castresult = RaycastResult(&(*m_triangles)[imin], tmin, umin, vmin, orig + tmin*dir, orig, dir);
	}
	return castresult;
}

void RayTracer::loadHierarchy(const char* filename, std::vector<RTTriangle>& triangles)
{
    indexList = new std::vector<int>(triangles.size());
    bvh = new Bvh();
    std::ifstream infile(filename, std::ios::binary);
    
    //Read tree
	readBinaryTree(infile, &(bvh->root));

	//Read index list
	int tmp;
	for (int i = 0; i < triangles.size(); i++)
	{
		read(infile, (*indexList)[i]);
	}
	//printSubTree(bvh->root, "");//TEMP
    //Given code:
    m_triangles = &triangles;
}

void RayTracer::saveHierarchy(const char* filename, const std::vector<RTTriangle>& triangles) {
	//printSubTree(bvh->root, "");//TEMP
    std::ofstream outfile(filename, std::ios::binary);
    
    //Store the tree
	writeBinaryTree(outfile, bvh->root);
	//Store indexList
	for (int i = 0; i < triangles.size(); i++)
	{
		write(outfile, (*indexList)[i]);
	}
}

void RayTracer::writeBinaryTree(std::ofstream& outfile, BvhNode* node)
{
    if (node != NULL)
    {
        //Write indices
        write(outfile, node->startIndex);
        write(outfile, node->endIndex);
        //Write AABB
		//write(outfile, node->bb);
		write(outfile, node->bb.min.x);//min_x
		write(outfile, node->bb.min.y);//min_y
		write(outfile, node->bb.min.z);//min_z
		write(outfile, node->bb.max.x);//max_x
		write(outfile, node->bb.max.y);//max_y
		write(outfile, node->bb.max.z);//max_z

        writeBinaryTree(outfile, node->leftChild);
        writeBinaryTree(outfile, node->rightChild);
    } else //If this node is null write -1, -1 and empty AABB. Terminate recursion on this branch
    {
        int terminator = -1;
        write(outfile, terminator);
        write(outfile, terminator);
        AABB aabb;
		write(outfile, -1.0f);//min_x
		write(outfile, -1.0f);//min_y
		write(outfile, -1.0f);//min_z
		write(outfile, -1.0f);//max_x
		write(outfile, -1.0f);//max_y
		write(outfile, -1.0f);//max_z
    }
}
    
void RayTracer::readBinaryTree(std::ifstream& infile, BvhNode** node)
{
    int start, end; AABB aabb;
    //Read indices
    read(infile, start);
    read(infile, end);
    //Read AABB
	read(infile, aabb.min.x);//min_x
	read(infile, aabb.min.y);//min_y
	read(infile, aabb.min.z);//min_z
	read(infile, aabb.max.x);//max_x
	read(infile, aabb.max.y);//max_y
	read(infile, aabb.max.z);//max_z
    
    if (start != -1) {
        *node = new BvhNode();
        (*node)->leftChild = (*node)->rightChild = NULL;
        (*node)->startIndex = start;
        (*node)->endIndex = end;
        (*node)->bb = aabb;
        
        readBinaryTree(infile, &(*node)->leftChild);
        readBinaryTree(infile, &(*node)->rightChild);
    }
}
   
//AABB of triangles, not their centroids
AABB RayTracer::computeAABB(int firstIndex, int lastIndex)
{
    bool initialized = false;
    
    Vec3f min, max;
    //Loop through index list and handle the triangle from triangles from index of indexList.
    for (int i = firstIndex; i < lastIndex+1; i++)
    {
		RTTriangle& current_triangle = (*m_triangles)[ (*indexList)[i] ];
        //Loop through corners of triangles
        for (int j = 0; j < 3; j++)
        {

			Vec3f current = current_triangle.m_vertices[j].p;
            if (!initialized)
            {
                min = max = current;
                initialized = true;
            }
            else
            {
                if (current.x < min.x) { min.x = current.x; }
                if (current.x > max.x) { max.x = current.x; }
                
                if (current.y < min.y) { min.y = current.y; }
                if (current.y > max.y) { max.y = current.y; }
                
                if (current.z < min.z) { min.z = current.z; }
                if (current.z > max.z) { max.z = current.z; }
            }
        }
    }
    return AABB(min,max);
}

//Compute AABB of triangles with their centroids, not the corner points
AABB RayTracer::computeAABBCentroids(int firstIndex, int lastIndex)
{
	bool initialized = false;

	Vec3f min, max;
	//Loop through index list and handle the triangle from triangles from index of indexList.
	for (int i = firstIndex; i < lastIndex + 1; i++)
	{
		Vec3f& current_centroid = (*m_triangles)[(*indexList)[i]].centroid();

		if (!initialized)
		{
			min = max = current_centroid;
			initialized = true;
		}
		else
		{
			if (current_centroid.x < min.x) { min.x = current_centroid.x; }
			if (current_centroid.x > max.x) { max.x = current_centroid.x; }

			if (current_centroid.y < min.y) { min.y = current_centroid.y; }
			if (current_centroid.y > max.y) { max.y = current_centroid.y; }

			if (current_centroid.z < min.z) { min.z = current_centroid.z; }
			if (current_centroid.z > max.z) { max.z = current_centroid.z; }
		}
	}
	return AABB(min, max);
}

Plane RayTracer::spatialMedianSplit(int firstIndex, int lastIndex)
{
	//Spatial median uses the bounding box created from triangles centroids, not corner points as you could think
	AABB bb = computeAABBCentroids(firstIndex, lastIndex);

	Plane plane; plane.setZero();//Hyper plane that divides 3d space to two areas
    //The plane consists of a unit vector xyz and multiplier offset from origo z. If we want a plane
    //that is perpendicular to X and located at -7 we would get a vector (1,0,0,-7)
	//RTTriangle& tri1 = (*m_triangles)[(*indexList)[firstIndex]];//TMP
	//RTTriangle& tri2 = (*m_triangles)[(*indexList)[lastIndex]];//TMP
    //Create a plane that is perpendicular to longest axis and lays at the middle of
    //min and max of the longest axis
    float x_length, y_length, z_length;
    x_length = bb.max.x - bb.min.x; y_length = bb.max.y - bb.min.y; z_length = bb.max.z - bb.min.z;
    //Find longest axis of bb
    if (x_length >= y_length && x_length >= z_length) {//X >= Y and X >= Z
		plane.x = 1.0f;
		plane.w = -(bb.min.x + 0.5f * x_length); //Minus sign because we need distance from plane origo, not origo to plane
    } else if (y_length >= x_length && y_length >= z_length)//Y >= X and Y >= Z
    {
		plane.y = 1.0f;
		plane.w = -(bb.min.y + 0.5f * y_length);
    } else //Z longest or some equal
    {
		plane.z = 1.0f;
		plane.w = -(bb.min.z + 0.5f * z_length);
    }
    return plane;
}

Plane RayTracer::split(int firstIndex, int lastIndex, SplitMode splitMode)
{
	return spatialMedianSplit(firstIndex, lastIndex);//Dont use triangle AABB, use the one generated from centroids
}

int RayTracer::partitionPrimitives(int firstIndex, int lastIndex, Plane split_plane)
{
    //Keep two pointers: leftPutPointer and rightPutPointer
    int leftPutPointer = firstIndex;
    int rightPutPointer = lastIndex;
	std::vector<int> tmpIndexList(m_triangles->size());
    
    //The triangles that lay on the otherside of the plane go left and the others to the right
    //Loop though all triangles.
    for (int i = firstIndex; i < lastIndex+1; i++)
    {
		RTTriangle& current = (*m_triangles)[(*indexList)[i]];
		Vec3f centroid = current.centroid();
		//i=7 on different side, negative side -0.58. THE PROBLEM IS HERE, it gets on the wrong side trorlolol
        //If on other side
        if (split_plane.dot( centroid ) > 0.0f ) //If dot product of centroid with plane is positive. CHECK THIS
        {
            tmpIndexList[rightPutPointer] = (*indexList)[i];
            rightPutPointer--;
        } else
        {
            tmpIndexList[leftPutPointer] = (*indexList)[i];
            leftPutPointer++;
        }
    }
    //Copy part of temp to indexList.
    for (int i = firstIndex; i < lastIndex+1; i++)
    {
        (*indexList)[i] = tmpIndexList[i];
    }
	//Handle special case in scene 3 with MAX_TRIS_LEAF=1 where there are 2 (or more) identical triangles and they go to the same side
	//This would cause an infinite loop even when the spatial median split works correctly.
	//We can fix it by splitting at first index, then first triangle goes to left and rest to right
	if (leftPutPointer == lastIndex+1 || rightPutPointer == firstIndex-1)
	{
		rightPutPointer = firstIndex;
	}

    //NOTE right putPointer should now be one less than leftPutPointer and left one greater than right
    //the split_index belongs to left so return rightPutPointer as split_index
    return rightPutPointer;
}

void RayTracer::createTree(BvhNode* N, int firstIndex, int lastIndex, SplitMode splitMode)
{
    N->bb = computeAABB(firstIndex, lastIndex);
	N->leftChild = NULL;
	N->rightChild = NULL;
    
    if (lastIndex-firstIndex+1 > MAX_TRIANGLES_PER_LEAF) //Continue recursion
    {
        Plane split_plane = split(firstIndex, lastIndex, splitMode);
        int split_index = partitionPrimitives(firstIndex, lastIndex, split_plane);//split_index belongs to left
		if (split_index == lastIndex && lastIndex-firstIndex>0)//All triangles are on the same side
		{
			printf("All triangles ended up on the left side. This should never happen.");
		}

        //Left
        N->leftChild = new BvhNode();
        int leftChildStartIndex = firstIndex;
        int leftChildEndIndex = split_index;
        createTree(N->leftChild, leftChildStartIndex, leftChildEndIndex, splitMode);
        //Right
        N->rightChild = new BvhNode();
        int rightChildStartIndex = split_index+1;
        int rightChildEndIndex = lastIndex;
        createTree(N->rightChild, rightChildStartIndex, rightChildEndIndex, splitMode);
        
    } else // Only add info of triangles to LEAFS so we can traverse them with given code
    {
        N->startIndex = firstIndex;
        N->endIndex = lastIndex;
    }
}
    
void RayTracer::constructHierarchy(std::vector<RTTriangle>& triangles, SplitMode splitMode) {
	//Given code (moved from the end of this function to the start so m_triangles can be used):
	m_triangles = &triangles;

    //Init index list with 0,1,2,3,...
    //If indexlist is in the end [3,1,0,2] It means that left childs (indices 0-1) triangles are at indices
    //3 and 1 at the m_triangles list.
    indexList = new std::vector<int>(triangles.size());

    for (int i = 0; i < indexList->size(); i ++)
    {
        (*indexList)[i] = i;
    }
    //First start with range [0, triangles.size()-1] and create a node. Then split it and...
    bvh = new Bvh();
    int indexOfFirst = 0;
    int indexOfLast = triangles.size()-1; //max() required?
    splitMode = SplitMode_SpatialMedian; //TODO
	bvh->root = new BvhNode(); //Add root node
    createTree(bvh->root, indexOfFirst, indexOfLast, splitMode);
	
}
    //Returns whether intersection was found and if it was found the distance to intersection point
bool rayboxintersection(const Vec3f& orig, const Vec3f& dir, const AABB box)
{
    //Check parelleism for each dimension
    for (int i = 0; i < 3; i++)//X, Y and Z
    {
        if (dir[i] == 0.0f)//Ray is parallel to i-axis
        {
            if (orig[i] < box.min[i] || orig[i] > box.max[i]) //And ray is outside dimensions range
            {
                return false; //No intersection
            }
        }
    }
    float t_start = 0.0f; //Init with t1
    float t_end = 0.0f; //Init with t1
    //For each dimension calculate intersection distances t1 and t2
    for (int i = 0; i < 3; i++)
    {
        float t1 = (box.min[i] - orig[i]) / dir[i];
        float t2 = (box.max[i] - orig[i]) / dir[i];
        //If t1 > t2 -> swap
        if (t1 > t2)
        {
            float tmp = t1;
            t1 = t2;
            t2 = tmp;
        }
            
        if (i == 0)//Init with x interval
        {
            t_start = t1; //Init with t1
            t_end = t2; //Init with t1
        }
        else//Y and Z
        {
            if (t1 > t_start) { t_start = t1; }//Update if necessary
            if (t2 < t_end) { t_end = t2; }//Update if necessary
        }
        if (t_start > t_end) { return false; } //Box is missed
        if (t_end < 0.0f) { return false; } //Box is behind
    }
    return true;
}

Vec2f getTexelCoords(Vec2f uv, const Vec2i size)
{
    Vec2i value;//Use int because we use this value in an array and we need discrete values
	if (uv.x < 0.0f) { uv.x = F32(-1.0f) * uv.x; }//u is negative
	if (uv.y < 0.0f) { uv.y = F32(-1.0f) * uv.y; }//v is negative
	value.x = fmod(uv.x,1.0f) * size.x;
    value.y = fmod(uv.y,1.0f) * size.y;
	
	return value;
}

Mat3f formBasis(const Vec3f& n) {
	Mat3f mat;
	
	Vec3f z_vector(0.0f,0.0f,1.0f);
	Vec3f Q(n);
	//Find the component with the smallest absolute value and set it to 1
	Vec3f Q_abs(fabsf(Q.x), fabsf(Q.y), fabsf(Q.z));
	if (Q_abs.x <= Q_abs.y && Q_abs.x <= Q_abs.z)//X is smallest
	{
		Q.x = 1.0f;
	}
	else if (Q_abs.y <= Q_abs.x && Q_abs.y <= Q_abs.z)//Y is smallest
	{
		Q.y = 1.0f;
	}
	else //Z is smallest
	{
		Q.z = 1.0f;
	}
	//B = N x T
	Vec3f B = n.cross(Q); 

	mat.setCol(0, Q);//Q=T
	mat.setCol(1, B);
	mat.setCol(2, n);
    return mat;
}

void bvhclean(BvhNode* node)
{
	if (node != NULL)
	{
		bvhclean(node->leftChild);
		bvhclean(node->rightChild);
		delete node;
	}
}

RayTracer::~RayTracer()
{
	bvhclean(bvh->root);
}

void printSubTree(BvhNode* node, String tabs)
{
	if (node != NULL)
	{
		::printf(tabs.getPtr());
		::printf("[%i,%i]\n", node->startIndex, node->endIndex);
		printSubTree(node->leftChild, tabs);
		printSubTree(node->rightChild, tabs);
	}
}

// Write a simple data type to a stream.
template<class T>
std::ostream& write(std::ostream& stream, const T& x) {
    return stream.write(reinterpret_cast<const char*>(&x), sizeof(x));
}
// Read a simple data type from a stream.
template<class T>
std::istream& read(std::istream& os, T& x) {
    return os.read(reinterpret_cast<char*>(&x), sizeof(x));
}

class Bvh
{
public:
    BvhNode* root;//Root pointer
	Bvh();//Default constructor
};

struct IntersectionData {
	bool intersection;
	float start, end;
};