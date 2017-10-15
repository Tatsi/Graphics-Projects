# Graphics Projects

Graphics programming related projects that I've done on my spare time and on school courses.

# OpenGL 3D engine prototype

![alt text](/images/engine.png "Screen shot of my OpenGL engine")

This is a 3D engine for Windows that uses WIN32 API to create a window, rendering context and handle user input. It uses OpenGL 3.3 API to render 3D models and also includes simple collision detection. It uses Phong shading to shade the models.

I did this program on my spare time in learning purposes. At first I was using GLUT to do some graphics programming, but soon found it very limited and decided to build my own engine as I found great intrest in computer graphics and knew that this would be a very educational project. It is programmed in C++ and for development I used Visual studio IDE. I use GLSL for shaders and they are read from my shader files from Shaders directory. The used OpenGL version is 3.3.

The engine uses a custom model format which is a slightly modified version of common ply format. The models can be done and textured in Autodesk 3DS Max and then exported to the used format with a customized script. The engine also includes collision detection.

The code of this project can be found in [this GitHub repo](https://github.com/Tatsi/OpenGL3DEngine).

# Computer graphics algorithms

Here are some algoritms that I've developed in Computer graphics courses.

## Bounding volume hierarchy

![alt text](/images/raytrace.png "Raytrace result of Sponza scene that uses my BVH algorithms")

I've implemented a BVH acceleration structure to accelerate raytracing. When compared to basic ray-triangle intersection algorithm the performance improvement
is substantial. I also made the raytracing loop multithreaded to further accelerate the raytracing loop. The relevant parts of the code are gathered in [this file](raycast.cpp). My implementation uses spatial median split to split the primitives.

## Ambient oclusion

![alt text](/images/ao.png "Ambient oclusion result of a box scene")

The code for my implementation of ambient oclusion is [here](/ao.cpp).

## Indirect lightning (not real time radiosity)

![alt text](/images/indirect_lightning.png "The result of indirect lightning algorithm")

The image shows the result of my radiosity algorithm after three bounces. In addition to total random sampling I implemented the algorithm with Quasi Monte Carlo sampling using Halton sequence. The relevant code is [here](/radiosity.cpp).

## Other algorithms 

I've also implemented for example instant radiosity (real time radiosity) algorithm, spotlight shading and shadow mapping. 

On the basic Computer Graphics course I did all 16 assignments and the project work. The programming was done with Processing.

# OpenGL physics game engine prototype for Android 

I was intrested what it's like to create an OpenGL rendering engine for Android. As a test program I created an Android application that uses Android framework 
to create an application that uses OpenGL ES API to perform rendering. I also included `box2d` physics library to make a simple physics base game engine 
prototype. Before I started with Android I made a test program with C++, OpenGL and box2d.

In this prototype user can move and rotate different shaped 2D objects and when he presses a button marbles are released and roll down screen and collision with 
the user placed objects. The idea was to create a game where user has to direct the flow of marbles to certain place in the level by placing objects on the screen.

The code of this program can be found [here](/fluids).

# Computer Animation

For Computer Animation course I was provided with large data set of motion capture data in [BVH format](https://research.cs.wisc.edu/graphics/Courses/cs-838-1999/Jeff/BVH.html). The goal was to implement different animation and physics algorithms by using this data as the base. Programming was done with [Processing](https://processing.org/). For a single task several methods were implemented to illustrate their benefits and pitfalls. The functions that I implemented include:

* BVH parser and showing the motion on a skeleton
* Linear interpolation and linear interpolation of Euler angles of coordinates
* Interpolation with with [quaternion slerp algorithm](https://en.wikipedia.org/wiki/Slerp#Quaternion_Slerp)
* Inverse kinematics to solve joint rotations when root joint (hip position) and toe positions are given. I implemented this also with support to adjust bone 
lengths. With this algorithm it was possible to for example transform the original walking animation to walking on toes or crouching
* Ragdoll animation with point masses, gravity, friction and bone constraints. I also implemented additional constraints that keep the body stiffer and prevent the body from going completely flat when it hits the ground. The ragdoll could be activated during the animation by pressing space key.
* Finding similar poses within long animation data and creating a motion graphs from these. White paper that I used as information can be found [here](https://dl-acm-org.libproxy.aalto.fi/citation.cfm?id=566605)

The ragdoll part of the code can be found [here](/ragdoll).

# Unity projects

I've made a few small unity projects at school and on my spare time. 

## Lootgrabber game

![alt text](/images/lootgrabber2.png "Lootgrabber Android game")

During my studies I applied to game development module that included game development projects in collaboration projects Aalto School of Arts. I was selected to work as a programmer in two unity games as one of the two programmers. One of these projects, called Lootgrabber, was a 2D platformer game for Android. The art were made by two Arts students and the audio by one Arts student. The Android package to run the game can be found [here](/lootgrabber.apk).

![alt text](/images/lootgrabber.png "Lootgrabber Android game")

## Cancer game

As the project of game development module I was in another group that implemented a RTS game where human body fights cancer cells. In this game player controls different types of cells that each have their own abilities. The different types of cells we had in the game are rendered below.

![alt text](/images/cancer.png "Cell types in our cancer game")

## Virtual Reality game

On Virtual Reality course me and two fellow students made a virtual reality game that was played on a dual 3D projector setup using Play station move props and Kinect. I was responsible for rendering, lightning, texturing and finding suitable models. Mostly I used pre made models, but modelled and textured some myself. The aim of the game was to progress through a level by shooting the zombies that appear on your path. A ray was drawn from the players weapon prop to the game scene to help players aim. The player was also able to pick up more ammo by crouching near ammo crates. This was implemented using a Kinect sensor that was tracking players 
pose. The game was developed with Unity and we had a RUIS framework in use to help us integrate Move props and kinect into our game.

![alt text](/images/vr.jpg "Our VR application in use")

# WebGL test game

Quickly done interactive application to test WebGL performance across different mobile devices. It uses [Three.js](https://threejs.org/) library to make scene
setup easier. This was done for WWW Applications course to perform a benchmark on various mobile devices. The aim was to find out how well mobile browsers support
WebGL and how well they perform. The source code can be found in [this GitHub repo](https://github.com/Tatsi/WebGLGame).

# Image processing

## Color production capabilities of displays

I've done a few small projects related to image processing. One of them was comparison of two displays color producing abilities. These displays were Eizo ColorEdge CG242W ja Samsung SyncMaster 2443BW. Both of them were profiled to use Adobe RGB color space. I calculated the gamuts of both displays in Matlab and drew an image 
that shows Eizo displays gamut area in red, Samsungs displays gamut in blue and Adobe RGB in grey. I also calculated dE2000 values for both displays to compare their 
precision. As results I got that Eizo can produce a wider area, approximately 97% of Adobe RGB color space, but Samsung displays colors more precisely.

![alt text](/images/colorspace.png "Adobe RGB, CG242W and 244BW gamuts")

## Feature extraction methods in content based image retrieval

I have also made a Matlab test program that compares the usability of different feature extraction methods in content based image retrieval. I used both local and global features and compared them. In my tests I used 300 training pictures and 100 test pictures. Features were extracted from training pictures and the test pictures were used to perform content based image retrieval. Mehods that I used in my tests were:

* Color histograms
* Bag of features (vlsift-vlsift)
* Bag of features (heslap-sift)
* Bag of features (harlap-sift)
* Bag of features (mser-sift)

It seemed (as expected) that color histograms worked well in situations where most of the image was of certain color. For example it worked well in airplane pictures 
where most of the picture is covered by blue sky. The local features worked very well in for example face recognition, but it required that the features are in a 
certain area of the image. In facial images the face was usually in the center of the image and it worked well.