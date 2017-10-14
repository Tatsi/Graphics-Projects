package code;

import code.Game;
import code.GameGLSurfaceView;
//import com.example.fluids.Constants;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
	/**
	 * @param args
	 */
	public class Fluids extends Activity {

	    private static GLSurfaceView mGLView;
	    public static Game game;
	    public static Activity mainActivity;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        mainActivity = this;
	        mGLView = new GameGLSurfaceView(this);
	        setContentView(mGLView);
	        game = new Game();
	        GameGLSurfaceView.startGameLoop();
	    }
	    
	    @Override
	    protected void onPause() {
	        super.onPause();
	        mGLView.onPause();
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	        mGLView.onResume();
	    }
	    public static void drawGame()
	    {
	    	mGLView.requestRender();
	    }
	    public static void quit()
	    {
	    	GameGLSurfaceView.endGameLoop();
	    	GL20Renderer.clear();
	    	
	    	Game.clearGameState();
	    	//Finish activity TODO
	    	mainActivity.finish();
	    }
	    public void onDestroy() {
	        super.onDestroy();

	    }
	}

