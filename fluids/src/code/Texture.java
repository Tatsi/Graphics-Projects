package code;

import java.io.IOException;
import java.io.InputStream;
//import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

//import android.annotation.TargetApi;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.opengl.GLU;

//import javax.microedition.khronos.opengles.GL10;

public class Texture {
	
	private int id;
	
	public Texture(int resource_id, int width) throws IOException
	{
		InputStream in = GameGLSurfaceView.resources.openRawResource(resource_id);
		
		byte[] buffer = new byte[width * width * Constants.CHANNELS_PER_PIXEL];
		in.read(buffer, 0, width*width*Constants.CHANNELS_PER_PIXEL);
		in.close();
		
		IntBuffer a = IntBuffer.allocate(1);
		GLES20.glGenTextures(1, a);
		this.id = a.get(0);			

		ByteBuffer pixels = ByteBuffer.allocateDirect(width*width*Constants.CHANNELS_PER_PIXEL);
	    pixels.order(ByteOrder.BIG_ENDIAN);
		pixels.put(buffer);
		
	    pixels.position(0);
	    
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.id);
	    
	    GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
	    GLES20.glTexParameterf( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );
		
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, width, 0, 
				GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, pixels);
		
		GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, 0);
		
		GL20Renderer.checkForGLErrors("Texture load");
	}
	
	//Destructor not called yet TODO
	public void destroy()
	{
		int[] texture_id = {id};
		GLES20.glDeleteTextures(1, texture_id, 0);
	}
	public int getId()
	{
		return id;
	}

	
}
