import java.util.ArrayList;

public class ShootingSpriteDef extends SpriteDef {
	boolean canShoot;
	float shootDelay;
	// 0 means not slashing, -1 means left, 1 means right
	int slashing;
	float slashDelay;
	float slashCooldown;
	float currentTime;
	float slashCurrentTime;

	int bulletDamage = 1;
	float slashDamage = (float)2.5;

	ArrayList<String> powerUps = new ArrayList<>();
public ShootingSpriteDef(boolean hasGravity){
		super(hasGravity);
		canShoot = true;
		slashing = 0;

		shootDelay = 250;
		slashDelay = 400;

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
