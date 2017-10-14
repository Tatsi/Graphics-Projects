package code;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;

import code.Constants;
import code.IO;
import code.Texture;

public class Model {
	
	private int vboid; //Vertex buffer object id
	private int tboid; //Texture coordinate buffer object id
	
	private int triangle_count;
	
	FloatBuffer vertexData;
	FloatBuffer textureCoordinateData;
	
	private Texture texture;
	
	public Model(int vertex_resource_number, int texture_coordinate_resource_number, int texture_file_resource_number, int texture_width) throws IOException
	{
		//this.id = model_id;
		texture = new Texture(texture_file_resource_number, texture_width);
		
		//load data
		vertexData = IO.loadData(vertex_resource_number, Constants.POSITION_COORDINATES_PER_VERTEX);
		textureCoordinateData = IO.loadData(texture_coordinate_resource_number, Constants.TEXTURE_COORDINATES_PER_VERTEX);

		triangle_count = vertexData.capacity() / 3; //Store triangle count
		
		IntBuffer a = IntBuffer.allocate(1);
		GLES20.glGenBuffers(1, a);
		vboid = a.get(0); //Assign id
		//Buffer data
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboid);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.capacity() * Constants.BYTES_IN_FLOAT, vertexData, GLES20.GL_STATIC_DRAW);

		//////////////////////////////
		//Create texture coord array//
		GLES20.glGenBuffers(1, a);
		tboid = a.get(0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, tboid);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureCoordinateData.capacity() * Constants.BYTES_IN_FLOAT, textureCoordinateData, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GL20Renderer.checkForGLErrors("Model constructor");
	}
	
	//Not called yet TODO
	public void destroy()
	{
		IntBuffer t = IntBuffer.allocate(1); t.put(texture.getId());
		GLES20.glDeleteTextures(1, t);
		
		IntBuffer v = IntBuffer.allocate(1); v.put(vboid);
		GLES20.glDeleteBuffers(1, v);
		
		IntBuffer tc = IntBuffer.allocate(1); tc.put(tboid);
		GLES20.glDeleteBuffers(1, tc);
		
	}
	public int getVboId()
	{
		return vboid;
	}
	public int getTboId()
	{
		return tboid;
	}
	public void draw(float[] MVPMatrix, Vec2f translation, Vec2f scale, float rotation)
	{

        GLES20.glUseProgram(GL20Renderer.getShaderProgram());

        //Bind vertex coordinate buffer
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboid);
		GLES20.glEnableVertexAttribArray(GL20Renderer.GLSL_Locations.location_position);
		GLES20.glVertexAttribPointer(GL20Renderer.GLSL_Locations.location_position, 3, GLES20.GL_FLOAT, false,
                0, 0);
		//Bind texture coordinate buffer 
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, tboid);
		GLES20.glEnableVertexAttribArray(GL20Renderer.GLSL_Locations.location_texture_coordinates);
		GLES20.glVertexAttribPointer(GL20Renderer.GLSL_Locations.location_texture_coordinates, 2, GLES20.GL_FLOAT, false,
                0, 0);
		
		//Bind texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0); 
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getId());
		
		//Uniforms
		GLES20.glUniformMatrix4fv(GL20Renderer.GLSL_Locations.location_model_view_matrix, 1, false, MVPMatrix, 0);
		GLES20.glUniform2f(GL20Renderer.GLSL_Locations.location_scale, scale.x, scale.y);
		GLES20.glUniform2f(GL20Renderer.GLSL_Locations.location_translation, translation.x, translation.y);
		GLES20.glUniform1f(GL20Renderer.GLSL_Locations.location_rotation, (float) Math.toRadians(rotation));
		
		//Draw
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangle_count * Constants.POSITION_COORDINATES_PER_VERTEX);
		
		//Clear
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		GLES20.glDisableVertexAttribArray(GL20Renderer.GLSL_Locations.location_position);
		GLES20.glDisableVertexAttribArray(GL20Renderer.GLSL_Locations.location_texture_coordinates);
		GLES20.glUseProgram(0);
		
		GL20Renderer.checkForGLErrors("Model.draw()");
	}
	public void clear()//Clear buffered data
	{
		//TODO
	}
}
