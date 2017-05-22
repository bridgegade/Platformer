import com.jogamp.opengl.GL2;

public class AnimationData {
	SpriteDef sprite;
 AnimationDef def;
 int curFrame;
 float secsUntilNextFrame;
 int index;
 
 public AnimationData(SpriteDef s,AnimationDef d, float sUNF){
	 sprite = s;
	 def = d;
	 curFrame = def.frames[index].image;
	 secsUntilNextFrame = sUNF;
	 index = 0;
 }
 public void setDef(AnimationDef d){
	 if(def.frames.length > d.frames.length){
	 index =0;
	 }
	 def = d;
 }
 public AnimationDef getDef(){
	 return def;
 }
 public void update(float deltaTime){
	 //System.out.printf("%.6f",secsUntilNextFrame - deltaTime);
	 secsUntilNextFrame = secsUntilNextFrame- deltaTime;
	 if(secsUntilNextFrame - deltaTime <0){
		 secsUntilNextFrame = def.frames[index].frameTimeSecs;
		 int prevIndex=1;
		 if(index == def.frames.length-1){
			 prevIndex = index;
			 index = 0;
			 }
			 else{
				 prevIndex = index;
				 index++;
			 }
	
		 //sprite.spriteActualPos[0]+= (def.frames[prevIndex].spriteSize[0]-def.frames[index].spriteSize[0]);
		 sprite.spriteActualPos[1]+= (def.frames[prevIndex].spriteSize[1]-def.frames[index].spriteSize[1]);

		 //sprite.spritePos[0]+= (def.frames[prevIndex].spriteSize[0]-def.frames[index].spriteSize[0]);
		 sprite.spritePos[1]=(int) sprite.spriteActualPos[1];
		 sprite.spriteSize = def.frames[index].spriteSize;

	 }

 }
 public void draw(int x, int y, GL2 gl){
	 JavaTemplate.glDrawSprite(gl, def.frames[index].image, x, y, def.frames[index].spriteSize[0], def.frames[index].spriteSize[1]);
	
	 //System.out.println("middle");

	 //System.out.println(index);
 }

 
 
// public void idle(SpriteDef sprite){
//		 int prevIndex=1;
//		 if(index == def.frames.length-1){
//			 prevIndex = index;
//			 index = 2;
//			 }
//			 else{
//				 prevIndex = index;
//				 index++;
//			 }
//	
//	 index = 0;
//	 sprite.spriteActualPos[0]+= (def.frames[prevIndex].spriteSize[0]-def.frames[index].spriteSize[0]);
//	 sprite.spriteActualPos[1]+= (def.frames[prevIndex].spriteSize[1]-def.frames[index].spriteSize[1]);
//
//	 sprite.spritePos[0]+= (def.frames[prevIndex].spriteSize[0]-def.frames[index].spriteSize[0]);
//	 sprite.spritePos[1]+= (def.frames[prevIndex].spriteSize[1]-def.frames[index].spriteSize[1]);
//	 sprite.spriteSize = def.frames[index].spriteSize;
// }
 public void setDefAnimationFix( AnimationDef d){
	 if(d!=def){

	 int i =0;
	 //sprite.spriteActualPos[0]+= (def.frames[index].spriteSize[0]-d.frames[0].spriteSize[0]);
	 sprite.spriteActualPos[1]+= (def.frames[index].spriteSize[1]-d.frames[i].spriteSize[1]);

	 //sprite.spritePos[0]+= (def.frames[index].spriteSize[0]-d.frames[0].spriteSize[0]);
	 sprite.spritePos[1]= (int)sprite.spriteActualPos[1];
	 
	 sprite.spriteSize = d.frames[i].spriteSize;
	 index = 0;
	 setDef(d);
	 }


 }
}