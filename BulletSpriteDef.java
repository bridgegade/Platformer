
public class BulletSpriteDef extends SpriteDef {
	float rise;
	float run;
	boolean toBeRemoved;
	String type;
	float currentTime;
	public  BulletSpriteDef(int[] p, float[] aP, int t, int[] s, float rise, float run,boolean hasG, String type){
		super(p,aP,t,s,hasG);
		toBeRemoved = false;
		this.rise = rise;
		this.run = run;
		this.type =type;
	}
}
