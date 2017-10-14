package code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class IO {
	/*
	*	Load float buffer with 2-4 values per vertex. 
	*
	*	Parameters: resource_id = 	resource id of the file from R file
	*				elementCountPerVertex = How many float values the are in the file for each vertice.
	*
	*	For example vertex positions (3) and texture coordinates (2.)
	*	Data should be in text file with header row that tells the vertex (row) count. An Example data file with:
	*	2
	*	0.0012 0.001 0.212
	*	0.2123 0.012 0.123
	*/
	public static FloatBuffer loadData(int resource_id ,int elementCountPerVertex) throws IOException
	{
		InputStream in = GameGLSurfaceView.resources.openRawResource(resource_id);

		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

		String element_count = reader.readLine();
		int num_of_elements = Integer.parseInt(element_count);
		
		FloatBuffer circleBuffer = ByteBuffer.allocateDirect(num_of_elements * elementCountPerVertex * Constants.BYTES_IN_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		
		for (int j = 0; j < num_of_elements; j++) {
			String line = reader.readLine();
			String[] parts = line.split(" ");
			
		    circleBuffer.put(Float.parseFloat(parts[0]));
		    circleBuffer.put(Float.parseFloat(parts[1]));
		    
		    if (elementCountPerVertex >= 3)
		    {
		    	circleBuffer.put(Float.parseFloat(parts[2]));
		    }
		    if (elementCountPerVertex == 4)
		    {
		    	circleBuffer.put(Float.parseFloat(parts[3]));
		    }
		}
		reader.close();
		in.close();

		circleBuffer.position(0);
		return circleBuffer;
	}
	
	public static IntBuffer loadIndiceData(int resource_id) throws IOException
	{
		InputStream in = GameGLSurfaceView.resources.openRawResource(resource_id);

		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

		String element_count = reader.readLine();
		int num_of_elements = Integer.parseInt(element_count); 
		 
		IntBuffer circleBuffer = IntBuffer.allocate(num_of_elements * 3);

		for (int j = 0; j < num_of_elements; j++) {
			String line = reader.readLine();
			String[] parts = line.split(" ");
			
		    circleBuffer.put(Integer.parseInt(parts[0]));
		    circleBuffer.put(Integer.parseInt(parts[1]));
		    circleBuffer.put(Integer.parseInt(parts[2]));

		}
		reader.close();
		in.close();
		
		//circleBuffer.flip();
		circleBuffer.position(0);
		
		return circleBuffer;
	}
	
	public static String readShader(int resource_id) throws IOException
	{
		InputStream f = GameGLSurfaceView.resources.openRawResource(resource_id);
		int size = (int)(f.available());
		byte[] source = new byte[size];
		f.read(source,0,size);
		f.close();
		return new String(source,0,size);
	}
	
}
