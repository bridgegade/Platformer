
public class ShootingSpriteDef extends SpriteDef {
	boolean canShoot;
	float shootDelay;
	// 0 means not slashing, -1 means left, 1 means right
	int slashing;
	float slashDelay;
	float slashCooldown;
	float currentTime;
	float slashCurrentTime;
public ShootingSpriteDef(boolean hasGravity){
		super(hasGravity);
		canShoot = true;
		slashing = 0;
		shootDelay = 150;
		slashDelay = 150;
		slashCooldown = 1000;
		slashCurrentTime =0;
		currentTime = 0;
	}
	public  ShootingSpriteDef(int[] p, float[] aP, int t, int[] s, boolean hasG){
		super(p,aP,t,s,hasG);
		canShoot = true;
		shootDelay = 40;
		currentTime = 0;
	}
		
	
	

}
