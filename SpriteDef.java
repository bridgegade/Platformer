/*
 * Holds sprite data
 */
public class SpriteDef {
	AnimationData animation;
	
	// Position for the sprite
	int[] spritePos = new int[2];
	boolean touchedGround;
	boolean hasGravity;
	float fallSpeed;
	float[] spriteActualPos = new float[2];
	
	// Texture for the sprite.
	int spriteTex;
	
	// Size of the sprite.
	int[] spriteSize = new int[2];
	int health;
	boolean canJump = true;
	float jumpForce = 0;
	public SpriteDef(boolean hasGrav){
		health = 10;
		hasGravity = hasGrav;
		fallSpeed= 0;
	}
	public SpriteDef(int[] p, float[] aP, int t, int[] s, boolean hasGrav){
		health = 10;
		 spritePos = p;
		 spriteActualPos = aP;
		// Texture for the sprite.
		 spriteTex = t;
		// Size of the sprite.
		spriteSize = s;
		touchedGround = false;
		hasGravity = hasGrav;
		fallSpeed =0;
	}
}
