import com.jogamp.opengl.GL2;

public class AnimationData {
 AnimationDef def;
 int curFrame;
 float secsUntilNextFrame;
 int index;
 
 public AnimationData(AnimationDef d, float sUNF){
	 def = d;
	 curFrame = def.frames[index].image;
	 secsUntilNextFrame = sUNF;
	 index = 0;
 }
 public void setDef(AnimationDef d){
	 def = d;
 }
 public AnimationDef getDef(){
	 return def;
 }
 public void update(float deltaTime, SpriteDef sprite){
	 //System.out.printf("%.6f",secsUntilNextFrame - deltaTime);
	 secsUntilNextFrame = secsUntilNextFrame- deltaTime;
	 if(secsUntilNextFrame - deltaTime <0){
		 secsUntilNextFrame = def.frames[index].frameTimeSecs;
		 int prevIndex=0;
		 if(index == def.frames.length-1){
			 prevIndex = index;
			 index = 1;
			 }
			 else{
				 prevIndex = index;
				 index++;
			 }
	
		 sprite.spriteActualPos[0]+= (def.frames[prevIndex].spriteSize[0]-def.frames[index].spriteSize[0]);
		 sprite.spriteActualPos[1]+= (def.frames[prevIndex].spriteSize[1]-def.frames[index].spriteSize[1]);

		 sprite.spritePos[0]+= (def.frames[prevIndex].spriteSize[0]-def.frames[index].spriteSize[0]);
		 sprite.spritePos[1]+= (def.frames[prevIndex].spriteSize[1]-def.frames[index].spriteSize[1]);
		 sprite.spriteSize = def.frames[index].spriteSize;

	 }
	
 }
 public void draw(int x, int y, GL2 gl){
	 JavaTemplate.glDrawSprite(gl, def.frames[index].image, x, y, def.frames[index].spriteSize[0], def.frames[index].spriteSize[1]);
	
	 //System.out.println("middle");

	 //System.out.println(index);
 }
 public void idle(){
	 index = 0;
 }
}