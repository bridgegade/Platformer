import com.jogamp.opengl.GL2;

public class AnimationDef {
 public FrameDef[] frames;

 public AnimationDef(FrameDef[] f){
	 frames = f;
	
 }
 public AnimationDef(int frameCount, String baseFileNamePath, float frameTimes, GL2 gl){
	 FrameDef[] framesStore = new FrameDef[frameCount];
		for (int i = 0; i < framesStore.length; i++) {
			int[] gunManSize = new int[2];
			int image = JavaTemplate.glTexImageTGAFile(gl, JavaTemplate.class.getResource(baseFileNamePath + i + ".tga"),
					gunManSize);
			
			framesStore[i] = new FrameDef(image, (float) 0.05, gunManSize);
			
		}
		frames =framesStore;
 }
}

class FrameDef {
 public int image;
 public float frameTimeSecs;
 int[] spriteSize;
 public FrameDef(int i, float fts, int[] sSize){
	 image = i;
	 frameTimeSecs=fts;
	 spriteSize = sSize;
 }
}