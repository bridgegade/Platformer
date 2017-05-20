public class AnimationDef {
 public FrameDef[] frames;

 public AnimationDef(FrameDef[] f){
	 frames = f;
	
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