import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.opengl.*;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class JavaTemplate {
	// Set this to true to make the game loop exit.
	private static boolean shouldExit;

	// The previous frame's keyboard state.
	private static boolean kbPrevState[] = new boolean[256];

	// The current frame's keyboard state.
	private static boolean kbState[] = new boolean[256];

	// Level info
	private static int currentLevel = 0;
	private static boolean changingLevel = false;

	// Textures for background
	private static int grayTileTex;
	private static int pillarTileTex;
	private static int brickTileTex;

	private static int health1Tex;
	private static int health2Tex;
	private static int health3Tex;
	private static int health4Tex;
	private static int health5Tex;
	private static int health6Tex;
	private static int health7Tex;
	private static int health8Tex;
	private static int health9Tex;

	// Texture for mainBackground
	private static int arenaBackTex;

	// Size of the tile
	private static int[] tileSize = new int[2];
	private static int[] healthSize = new int[2];
	private static int[] backgroundSize = new int[2];

	// All bullets in world
	private static ArrayList<BulletSpriteDef> bullets = new ArrayList<BulletSpriteDef>();
	// All actors in world
	private static ArrayList<SpriteDef> actors = new ArrayList<SpriteDef>();
	// All slimes in world
	private static ArrayList<SpriteDef> slimes = new ArrayList<SpriteDef>();

	private static float gravity;

	public static void main(String[] args) {
		GLProfile gl2Profile;

		try {
			// Make sure we have a recent version of OpenGL
			gl2Profile = GLProfile.get(GLProfile.GL2);
		} catch (GLException ex) {
			System.out.println("OpenGL max supported version is too low.");
			System.exit(1);
			return;
		}

		// Create the window and OpenGL context.
		GLWindow window = GLWindow.create(new GLCapabilities(gl2Profile));

		window.setSize(1600, 800);

		window.setTitle("Java Template");
		window.setVisible(true);
		window.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
		window.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.isAutoRepeat()) {
					return;
				}
				kbState[keyEvent.getKeyCode()] = true;
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
				if (keyEvent.isAutoRepeat()) {
					return;
				}
				kbState[keyEvent.getKeyCode()] = false;
			}
		});

		// Setup OpenGL state.
		window.getContext().makeCurrent();
		GL2 gl = window.getGL().getGL2();

		gl.glViewport(0, 0, 1600, 800);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glOrtho(0, 1600, 800, 0, 0, 100);

		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// Game initialization goes here.
		// Create tile textures here
		grayTileTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/tile.tga"), tileSize);
		brickTileTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/brick.tga"), tileSize);
		BackgroundDef bground = new BackgroundDef(80, 40, tileSize[0], tileSize[1]);

		pillarTileTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/pillar.tga"), tileSize);
		BackgroundDef topGroundDef = new BackgroundDef(40, 40, tileSize[0], tileSize[1]);

		// Health textures here
		health1Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health1.tga"), healthSize);
		health2Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health2.tga"), healthSize);
		health3Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health3.tga"), healthSize);
		health4Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health4.tga"), healthSize);
		health5Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health5.tga"), healthSize);
		health6Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health6.tga"), healthSize);
		health7Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health7.tga"), healthSize);
		health8Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health8.tga"), healthSize);
		health9Tex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/health9.tga"), healthSize);

		arenaBackTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/arena_background.tga"),
				backgroundSize);
		backgroundSize[0] = window.getWidth();
		backgroundSize[1] = window.getHeight();

		// set tiles equal to textures here

		// set tiles for middle ground
		for (int x = 0; x < bground.width; x++) {
			for (int y = 0; y < bground.height; y++) {
				if (y > bground.height / 2) {
					bground.setTile(x, y, grayTileTex, true);
				} else {
					bground.setTile(x, y, 0, false);
				}

			}

		}
		for (int x = 5; x < bground.width; x++) {

			for (int y = 0; y < bground.height - 5; y++) {

				if (y > (bground.height / 3 + 6)) {
					bground.setTile(x, y, brickTileTex, true);
				} else if (y == bground.height / 3 + 3 && x > 3 && x < 8) {
					bground.setTile(x, y, brickTileTex, true);
				} else if (y == bground.height / 3 + 4 && x > 10 && x < 14) {
					bground.setTile(x, y, brickTileTex, true);
				} else if (y == bground.height / 3 + 6 && x > 18 && x < 23) {
					bground.setTile(x, y, brickTileTex, true);
				} else {
					bground.setTile(x, y, 0, false);
				}

			}

		}
		// set tiles textures for top ground
		// for (int x = 0; x < topGroundDef.width; x++) {
		// for (int y = 0; y < topGroundDef.height; y++) {
		//
		// topGroundDef.setTile(x, y, 0, false);
		//
		// }
		//
		// }
		// for (int x = 15; x < topGroundDef.width; x++) {
		// topGroundDef.setTile(x, topGroundDef.height - 5, pillarTileTex,
		// false);
		// topGroundDef.setTile(x, topGroundDef.height - 7, pillarTileTex,
		// false);
		//
		// }

		// Initialization of sprites
		SpriteDef camera = new SpriteDef(false);
		camera.spriteSize[0] = window.getWidth();
		camera.spriteSize[1] = window.getHeight();

		camera.spriteActualPos[1] = 9 * tileSize[1];
		camera.spritePos[1] = 9 * tileSize[1];
		camera.spriteActualPos[0] = 5 * tileSize[0];
		camera.spritePos[0] = 5 * tileSize[0];

		SpriteDef bullet = new SpriteDef(true);
		bullet.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/bullet.tga"),
				bullet.spriteSize);

		SpriteDef slimeBullet = new SpriteDef(true);
		slimeBullet.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/slimeBullet.tga"),
				slimeBullet.spriteSize);

		// slime dx dy for chasing
		float dx = 0;
		float dy = 0;
		float xVelocity = 0;
		float yVelocity = 0;
		double velocityTime = 0;

		// item pick ups
		// SOWRD
		SpriteDef sword = new SpriteDef(true);
		sword.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/pickups/sword0.tga"),
				sword.spriteSize);
		actors.add(sword);
		AnimationDef swordAnimation = new AnimationDef(4, "/resources/pickups/sword", (float) 0.05, gl);
		AnimationData swordGlow = new AnimationData(sword, swordAnimation, (float) 0.25);
		sword.animation = swordGlow;

		// REGEN HEART
		SpriteDef regenGlobe = new SpriteDef(true);
		regenGlobe.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/pickups/regen0.tga"),
				regenGlobe.spriteSize);
		actors.add(regenGlobe);
		AnimationDef regenGlobeAnDef = new AnimationDef(4, "/resources/pickups/regen", (float) 0.05, gl);
		AnimationData regenGlobeAnimation = new AnimationData(regenGlobe, regenGlobeAnDef, (float) 0.25);
		regenGlobe.animation = regenGlobeAnimation;

		// set animationDefs for main character
		ShootingSpriteDef mainCharacter = new ShootingSpriteDef(true);
		mainCharacter.spriteActualPos[1] = 16 * tileSize[1];
		mainCharacter.spritePos[1] = 16 * tileSize[1];
		mainCharacter.spriteActualPos[0] = 15 * tileSize[0];
		mainCharacter.spritePos[0] = 15 * tileSize[0];
		mainCharacter.health = 64;
		actors.add(mainCharacter);
		//TESING
		mainCharacter.powerUps.add("sword");
		mainCharacter.powerUps.add("regenGlobe");
		mainCharacter.powerUps.add("enhance");


		mainCharacter.spriteTex = glTexImageTGAFile(gl,

				JavaTemplate.class.getResource("/resources/mainCharacter/gunManWalkRight0.tga"),
				mainCharacter.spriteSize);
		AnimationDef gunDashLeft = new AnimationDef(1, "/resources/mainCharacter/dashLeft", (float) 0.05, gl);
		AnimationDef gunDashRight = new AnimationDef(1, "/resources/mainCharacter/dashRight", (float) 0.05, gl);
		AnimationDef gunManJumpRight = new AnimationDef(1, "/resources/mainCharacter/gunManJumpRight",(float) 0.05, gl );
		AnimationDef gunManJumpLeft = new AnimationDef(1, "/resources/mainCharacter/gunManJumpLeft",(float) 0.05, gl );

		AnimationDef gunThrowLeft = new AnimationDef(5, "/resources/mainCharacter/throwingLeft", (float) 0.05, gl);
		AnimationDef gunThrowRight = new AnimationDef(5, "/resources/mainCharacter/throwingRight", (float) 0.05, gl);
		AnimationDef gunSlashLeft = new AnimationDef(3, "/resources/mainCharacter/gunManSlashLeft", (float) 0.2, gl);
		AnimationDef gunSlashRight = new AnimationDef(3, "/resources/mainCharacter/gunManSlashRight", (float) 0.2, gl);
		AnimationDef gunSuperSlashLeft = new AnimationDef(3, "/resources/mainCharacter/gunManSuperSlashLeft", (float) 0.2, gl);
		AnimationDef gunSuperSlashRight = new AnimationDef(3, "/resources/mainCharacter/gunManSuperSlashRight", (float) 0.2, gl);
		AnimationDef gunManWalkingLeft = new AnimationDef(8, "/resources/mainCharacter/gunManWalkLeft", (float) 0.05,
				gl);
		AnimationDef gunManWalkingRight = new AnimationDef(8, "/resources/mainCharacter/gunManWalkRight", (float) 0.05,
				gl);
		AnimationDef gunManIdleRight = new AnimationDef(1, "/resources/mainCharacter/gunManIdleRight", (float) 0.05,
				gl);
		AnimationDef gunManIdleLeft = new AnimationDef(1, "/resources/mainCharacter/gunManIdleLeft", (float) 0.05, gl);
		AnimationData gunMan = new AnimationData(mainCharacter, gunManWalkingRight, (float) 0.25);
		mainCharacter.animation = gunMan;

		// SLIME animation
		SpriteDef slime = new SpriteDef(true);
		slimes.add(slime);
		slime.spriteActualPos[1] = 16 * tileSize[1];
		slime.spritePos[1] = 16 * tileSize[1];
		slime.spriteActualPos[0] = 35 * tileSize[0];
		slime.spritePos[0] = 35 * tileSize[0];
		slime.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/slime/f1.tga"),
				slime.spriteSize);
		actors.add(slime);
		AnimationDef slimeMoving = new AnimationDef(10, "/resources/slime/f", (float) 0.09, gl);
		AnimationData slimeAnimationData = new AnimationData(slime, slimeMoving, (float) 0.25);
		slimeAnimationData.setDef(slimeMoving);
		slime.animation = slimeAnimationData;

		// ROBOT animation
		AnimationDef robotWalkLeft = new AnimationDef(5, "/resources/robot/robot_walkleft", (float) 0.05, gl);
		AnimationDef robotWalkRight = new AnimationDef(5, "/resources/robot/robot_walkright", (float) 0.05, gl);
		AnimationDef robotAttackRight = new AnimationDef(5, "/resources/robot/robot_attackright", (float) 0.05, gl);
		AnimationDef robotAttackLeft = new AnimationDef(5, "/resources/robot/robot_attackleft", (float) 0.05, gl);
		SpriteDef robot = new SpriteDef(true);
		robot.health = 64;
		robot.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/robot/robot_walkleft0.tga"),
				robot.spriteSize);
		AnimationData robotAnimation = new AnimationData(robot, robotWalkLeft, (float) 0.25);
		robot.animation = robotAnimation;
		actors.add(robot);

		// Sound set
		Sound bgMusic = Sound.loadFromFile("src/resources/sounds/ninja_game_music.wav");
		Clip bgClip = bgMusic.playLooping();

		// The game loop
		long lastFrameNS;
		long curFrameNS = System.nanoTime();
		long curFrameMS;
		long lastFrameMS;
		float physicsDelay = 0;
		gravity = (float) 0.007;
		while (!shouldExit) {

			System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);
			lastFrameNS = curFrameNS;
			curFrameNS = System.nanoTime();

			float deltaTimeMS = (float) (curFrameNS - lastFrameNS) / 1000000;
			curFrameMS = curFrameNS / 1000000;
			lastFrameMS = lastFrameNS / 1000000;

			// Actually, this runs the entire OS message pump.
			window.display();

			if (!window.isVisible()) {
				shouldExit = true;
				bgClip.stop();
				break;
			}

			// Game logic goes here.
			if (kbState[KeyEvent.VK_ESCAPE]) {
				shouldExit = true;
			}

			// Physics
			do {
				// Gravity
				int numActors = actors.size();
				for (int i = 0; i < numActors; i++) {
					if (actors.get(i).hasGravity && !actors.get(i).touchedGround) {
						actors.get(i).fallSpeed += gravity;
						// float jumpForce=0;
						// if(actors.get(i).jumpForce>0){
						// jumpForce= actors.get(i).jumpForce *deltaTimeMS;
						// System.out.println("jumpforce"+jumpForce);
						//
						// }
						actors.get(i).spriteActualPos[1] += actors.get(i).fallSpeed - actors.get(i).jumpForce;
						if (actors.get(i) == mainCharacter) {
							if (collidesWithBackground(actors.get(i), bground) == -1) {
								
								actors.get(i).spriteActualPos[1] -= actors.get(i).fallSpeed + actors.get(i).jumpForce;
								actors.get(i).spritePos[1] = (int) actors.get(i).spriteActualPos[1];
								actors.get(i).touchedGround = true;
								actors.get(i).canJump = true;
								actors.get(i).jumpForce = 0;
								actors.get(i).fallSpeed = 0;

							}

							else if (collidesWithBackground(actors.get(i), bground) == 1) {
								actors.get(i).spriteActualPos[1] += actors.get(i).fallSpeed + actors.get(i).jumpForce;
								actors.get(i).spritePos[1] = (int) actors.get(i).spriteActualPos[1];
								actors.get(i).jumpForce = 0;

							}
							if (camera.spritePos[1] < (tileSize[1] * bground.height - camera.spriteSize[1])
									&& mainCharacter.spriteActualPos[1]
											- camera.spriteActualPos[1] >= window.getHeight() / 2) {
								camera.spriteActualPos[1] += actors.get(i).fallSpeed - actors.get(i).jumpForce;
								camera.spritePos[1] = (int) camera.spriteActualPos[1];
							}

						} else {
							if (collidesWithBackground(actors.get(i), bground) == 1) {

								actors.get(i).spriteActualPos[1] += actors.get(i).fallSpeed + actors.get(i).jumpForce;
								actors.get(i).spritePos[1] = (int) actors.get(i).spriteActualPos[1];
								actors.get(i).jumpForce = 0;
								if (actors.get(i) instanceof BulletSpriteDef) {
									actors.get(i).spriteActualPos[1] += actors.get(i).fallSpeed + (float) 1;
									actors.get(i).spritePos[1] = (int) actors.get(i).spriteActualPos[1];
									BulletSpriteDef bS = (BulletSpriteDef) actors.get(i);
									bS.rise = 0;
									bS.run = 0;

									actors.remove(i);
									numActors--;
									continue;
								}

							} else if (collidesWithBackground(actors.get(i), bground) == -1) {
								actors.get(i).spriteActualPos[1] -= actors.get(i).fallSpeed + actors.get(i).jumpForce;
								actors.get(i).spritePos[1] = (int) actors.get(i).spriteActualPos[1];
								actors.get(i).touchedGround = true;
								actors.get(i).canJump = true;
								actors.get(i).jumpForce = 0;
								actors.get(i).fallSpeed = 0;
								if (actors.get(i) instanceof BulletSpriteDef) {
									actors.get(i).spriteActualPos[1] += actors.get(i).fallSpeed + (float) 6;
									actors.get(i).spritePos[1] = (int) actors.get(i).spriteActualPos[1];
									actors.remove(i);
									numActors--;

									continue;
								}
							}

						}

					}

					actors.get(i).spriteActualPos[1] += gravity;
					if (actors.get(i).hasGravity && collidesWithBackground(actors.get(i), bground) == 0) {
						actors.get(i).touchedGround = false;
						actors.get(i).spritePos[1] = (int) actors.get(i).spriteActualPos[1];
						if (actors.get(i) == mainCharacter) {
							if (camera.spritePos[1] < (tileSize[1] * bground.height - camera.spriteSize[1])
									&& mainCharacter.spriteActualPos[1]
											- camera.spriteActualPos[1] >= window.getHeight() / 2) {
								camera.spriteActualPos[1] += gravity;
								camera.spritePos[1] = (int) camera.spriteActualPos[1];
							}
						}
					} else {
						actors.get(i).spriteActualPos[1] -= gravity;
						actors.get(i).spritePos[1] = (int) actors.get(i).spriteActualPos[1];

						actors.get(i).fallSpeed = 0;
					}

				}

				// projectile physics
				// slimeAnimationData.update((deltaTimeMS / 2000));
				// if (slimeAnimationData.index == 3) {
				// dx = mainCharacter.spriteActualPos[0] -
				// slime.spriteActualPos[0];
				// dy = mainCharacter.spriteActualPos[1] -
				// slime.spriteActualPos[1];
				// velocityTime = (dx / Math.cos(45));
				// }
				// if (slimeAnimationData.index > 3 && slimeAnimationData.index
				// <= 10 ) {
				//
				// float time = (float) Math.sqrt(((velocityTime * Math.sin(45))
				// - dy) / gravity);
				// float velocity = (float) (dx / (time * Math.cos(45)));
				// xVelocity = (float) (velocity * Math.cos(45));
				// yVelocity = (float) (velocity * Math.sin(45));
				// slime.touchedGround = false;
				// System.out.println("v" + velocity);
				// System.out.println("x" + xVelocity);
				// System.out.println("y" + yVelocity);
				// System.out.println("g" + gravity);
				// slime.spriteActualPos[1] -= yVelocity;
				// slime.spriteActualPos[0] += xVelocity;
				//
				// if (collidesWithBackground(slime, bground)) {
				// slime.touchedGround = true;
				// slime.spriteActualPos[1] += yVelocity;
				// slime.spriteActualPos[0] -= xVelocity;
				// }
				//
				// slime.spritePos[0] = (int) slime.spriteActualPos[0];
				// slime.spritePos[1] = (int) slime.spriteActualPos[1];
				//
				// }
				// if (slime.touchedGround) {
				// xVelocity = 0;
				// yVelocity = 0;
				// }

				// if(slimeAnimationData.index>3&&slimeAnimationData.index<=10
				// && slime.touchedGround){
				// slime.canJump= false;
				// System.out.println("touched ground");
				// }
				physicsDelay += 2;
				// System.out.println(deltaTimeMS);
			} while (physicsDelay < deltaTimeMS);
			physicsDelay = 0;

			// Slime actions
			int slimesLength = slimes.size();
			for (int i = 0; i < slimesLength; i++) {
				if (slimes.get(i).touchedGround) {
					slimes.get(i).animation.update((deltaTimeMS / 1000));
				}
				double chanceForAction = Math.random() * 300;

				if (slimes.get(i).animation.index == 3) {
					slimes.get(i).animation.update((deltaTimeMS / 500));
					// chance to jump
					if (chanceForAction >= 100 && chanceForAction < 297) {
						if (slimes.get(i).canJump) {
							if (mainCharacter.spriteActualPos[0] > slimes.get(i).spriteActualPos[0]) {
								dx = 3 * deltaTimeMS / 5;
								// dy = 3 * deltaTimeMS;
							} else {
								dx = -3 * deltaTimeMS / 5;
								// dy = 3 * deltaTimeMS;
							}
							slimes.get(i).jumpForce = (float) 2;
							slimes.get(i).spriteActualPos[1] -= slimes.get(i).jumpForce;
							slimes.get(i).canJump = false;
							slimes.get(i).touchedGround = false;

						}
					}
					// chance to shoot
					else if (chanceForAction >= 1 && chanceForAction < 40) {
						int run;
						if (mainCharacter.spriteActualPos[0] > slimes.get(i).spriteActualPos[0]) {
							run = 1;
						} else {
							run = -1;
						}
						SpriteDef sB = new BulletSpriteDef(
								new int[] { slimes.get(i).spritePos[0] + 16, slimes.get(i).spritePos[1] + 80 },
								new float[] { slimes.get(i).spritePos[0] + 16, slimes.get(i).spritePos[1] + 80 },
								slimeBullet.spriteTex, slimeBullet.spriteSize, 1, run, true, "slime");
						sB.jumpForce = (float) 1;
						bullets.add((BulletSpriteDef) sB);
						actors.add(sB);

					}
					// chance to spawn clone
					else if (chanceForAction >= 297 && slimesLength < 3) {
						SpriteDef slimeClone = new SpriteDef(true);
						slimes.add(slimeClone);
						slimeClone.spriteActualPos[1] = slimes.get(i).spriteActualPos[1];
						slimeClone.spritePos[1] = slimes.get(i).spritePos[1];
						slimeClone.spriteActualPos[0] = slimes.get(i).spriteActualPos[0];
						slimeClone.spritePos[0] = slimes.get(i).spritePos[0];
						slimeClone.spriteTex = glTexImageTGAFile(gl,
								JavaTemplate.class.getResource("/resources/slime/f0.tga"), slimeClone.spriteSize);

						actors.add(slimeClone);

						// slime animation
						AnimationDef slimeMovingClone = new AnimationDef(9, "/resources/slime/f", (float) 0.09, gl);
						AnimationData slimeAnimationDataClone = new AnimationData(slimeClone, slimeMovingClone,
								(float) 0.25);
						slimeAnimationDataClone.setDef(slimeMovingClone);
						slimeClone.animation = slimeAnimationDataClone;

					}
				}

				if (slimeAnimationData.index == 4 && !slimes.get(i).touchedGround) {

					// slime.spriteActualPos[1] -= dy;

					slimes.get(i).spriteActualPos[0] += dx;

					// slime.spriteActualPos[1] -= slime.jumpForce;
					if ((collidesWithBackground(slimes.get(i), bground) == 1
							|| collidesWithBackground(slimes.get(i), bground) == -1)
							|| (slimes.get(i).spriteActualPos[0] + slimes.get(i).spriteSize[0] > tileSize[0]
									* bground.width || slimes.get(i).spriteActualPos[0] <= 0)) {
						// slime.spriteActualPos[1] += dy;
						// slime.spriteActualPos[1] += slime.jumpForce;
						slimes.get(i).spriteActualPos[0] -= dx;

					}
					slimes.get(i).spritePos[0] = (int) slimes.get(i).spriteActualPos[0];
					// slime.spritePos[1] = (int) slime.spriteActualPos[1];

				}
			}

			// ROBOT MOVEMENT
			if (robot.health > 0 && currentLevel == 2) {
				int robotAnimationSpeed = 5000;
				double robotMoveSpeed = 4;
				if (robot.health < 34) {
					robotMoveSpeed = 2.5;
					robotAnimationSpeed = 3000;
				}
				if (intersects(robot, camera) || robot.health < 34) {
					if (Math.abs(robot.spritePos[0] - mainCharacter.spritePos[0]) < 30) {
						if (mainCharacter.spritePos[0] > robot.spritePos[0]) {
							robotAnimation.setDefAnimationFix(robotAttackRight);
							robotAnimation.update(deltaTimeMS / robotAnimationSpeed);
						} else {
							robotAnimation.setDefAnimationFix(robotAttackLeft);
							robotAnimation.update(deltaTimeMS / robotAnimationSpeed);
						}
						if (intersects(robot, mainCharacter)) {
							
							if(robot.health <34){
								mainCharacter.health -= .03;
							}else{
								mainCharacter.health -= .02;
							}
						}
					} else {
						if (mainCharacter.spritePos[0] > robot.spritePos[0]) {
							robotAnimation.setDefAnimationFix(robotWalkRight);
							robotAnimation.update(deltaTimeMS / robotAnimationSpeed);
							robot.spriteActualPos[0] += deltaTimeMS / robotMoveSpeed;
							if (collidesWithBackground(robot, bground) == 1
									|| collidesWithBackground(robot, bground) == -1) {
								robot.spriteActualPos[0] -= (deltaTimeMS) / robotMoveSpeed;
							}
							robot.spritePos[0] = (int) robot.spriteActualPos[0];
						} else if (mainCharacter.spritePos[0] < robot.spritePos[0]) {
							robotAnimation.setDefAnimationFix(robotWalkLeft);
							robotAnimation.update(deltaTimeMS / robotAnimationSpeed);
							robot.spriteActualPos[0] -= deltaTimeMS / robotMoveSpeed;
							if (collidesWithBackground(robot, bground) == 1
									|| collidesWithBackground(robot, bground) == -1) {
								robot.spriteActualPos[0] += (deltaTimeMS) / robotMoveSpeed;
							}
							robot.spritePos[0] = (int) robot.spriteActualPos[0];
						}
					}
				}
			}

			// GUMAN MOVEMENT CONTROLS && movement
			if (mainCharacter.health > 0) {
				if (mainCharacter.slashCurrentTime < mainCharacter.slashDelay) {
					if (mainCharacter.slashing == 1) {
						mainCharacter.spriteActualPos[0] += 2 * deltaTimeMS;
						if (collidesWithBackground(mainCharacter, bground) != 0

								|| mainCharacter.spriteActualPos[0] >= tileSize[0] * bground.width
										- mainCharacter.spriteSize[0]
								|| mainCharacter.spriteActualPos[0] <= 0) {
							mainCharacter.spriteActualPos[0] -= 2 * deltaTimeMS;

						} else {
							gunMan.update((deltaTimeMS / 1000));

							mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
							if (camera.spritePos[0] < (tileSize[0] * bground.width - camera.spriteSize[0])) {
								camera.spriteActualPos[0] += 2 * deltaTimeMS;
								camera.spritePos[0] = (int) camera.spriteActualPos[0];
							}

						}
					} else if (mainCharacter.slashing == -1) {
						mainCharacter.spriteActualPos[0] -= 2 * deltaTimeMS;
						if (collidesWithBackground(mainCharacter, bground) != 0

								|| mainCharacter.spriteActualPos[0] >= tileSize[0] * bground.width
										- mainCharacter.spriteSize[0]
								|| mainCharacter.spriteActualPos[0] <= 0) {
							mainCharacter.spriteActualPos[0] += 2 * deltaTimeMS;
						} else {
							gunMan.update((deltaTimeMS / 1000));

							mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
							if (camera.spritePos[0] > 0 && mainCharacter.spriteActualPos[0]
									- camera.spriteActualPos[0] <= window.getWidth() / 2) {
								camera.spriteActualPos[0] -= 2 * deltaTimeMS;
								camera.spritePos[0] = (int) camera.spriteActualPos[0];
							}
						}

					}
				} else {
					if (gunMan.def == gunSlashLeft) {
						gunMan.setDefAnimationFix(gunManWalkingLeft);
					} else if (gunMan.def == gunSlashRight) {
						gunMan.setDefAnimationFix(gunManWalkingRight);

					}
					mainCharacter.slashing = 0;
				}

				// controlled jump based on gravity

				if ((gunMan.def != gunDashLeft && gunMan.def !=gunDashRight) && kbState[KeyEvent.VK_UP] && mainCharacter.spritePos[1] > 0 && mainCharacter.canJump) {
					if(mainCharacter.facing.equals("left")){
						gunMan.setDefAnimationFix(gunManJumpLeft);
						gunMan.update((deltaTimeMS / 1000));
					}
					else{
						gunMan.setDefAnimationFix(gunManJumpRight);
						gunMan.update((deltaTimeMS / 1000));
					}
					// gunMan.setDef(gunManWalkingUp);
					// gunMan.update((deltaTimeMS / 1000));
					if (kbState[KeyEvent.VK_SHIFT] && mainCharacter.powerUps.contains("enhance")) {
						mainCharacter.health-=.01;
						mainCharacter.jumpForce = (float) 2.5;
					} else {
						mainCharacter.jumpForce = (float) 1.5;
					}
					mainCharacter.spriteActualPos[1] -= mainCharacter.jumpForce;
					mainCharacter.canJump = false;
				}

				if (kbState[KeyEvent.VK_LEFT] && mainCharacter.spritePos[0] > 0 && mainCharacter.slashing == 0) {
					mainCharacter.facing = "left";

					float speed = 0;
					if (kbState[KeyEvent.VK_SHIFT] && mainCharacter.powerUps.contains("enhance")) {
						mainCharacter.health-=.01;
						gunMan.setDefAnimationFix(gunDashLeft);
						gunMan.update((deltaTimeMS / 1000));
						speed = (deltaTimeMS);
					} else {
						gunMan.setDefAnimationFix(gunManWalkingLeft);
						gunMan.update((deltaTimeMS / 1000));
						speed = (deltaTimeMS) / 2;
					}
					mainCharacter.spriteActualPos[0] -= speed;
					if (collidesWithBackground(mainCharacter, bground) == -1
							|| collidesWithBackground(mainCharacter, bground) == 1) {
						mainCharacter.spriteActualPos[0] += speed;
					}

					mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
				}
				if (kbState[KeyEvent.VK_RIGHT]
						&& mainCharacter.spritePos[0] < tileSize[0] * bground.width - mainCharacter.spriteSize[0]
						&& mainCharacter.slashing == 0) {
					mainCharacter.facing = "right";
					float speed = 0;
					if (kbState[KeyEvent.VK_SHIFT] && mainCharacter.powerUps.contains("enhance")) {
						mainCharacter.health-=.01;
						gunMan.setDefAnimationFix(gunDashRight);
						gunMan.update((deltaTimeMS / 1000));
						speed = (deltaTimeMS);
					} else {
						gunMan.setDefAnimationFix(gunManWalkingRight);
						gunMan.update((deltaTimeMS / 1000));
						speed = (deltaTimeMS) / 2;
					}
					mainCharacter.spriteActualPos[0] += speed;

					if (collidesWithBackground(mainCharacter, bground) == 1
							|| collidesWithBackground(mainCharacter, bground) == -1) {
						mainCharacter.spriteActualPos[0] -= speed;
					}

					mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
				}

				// main player attacks
				if (kbState[KeyEvent.VK_SPACE] && !kbState[KeyEvent.VK_RIGHT] && !kbState[KeyEvent.VK_LEFT]) {
					
					// System.out.println("current time" +
					// mainCharacter.currentTime);
					// System.out.println("shoot delay" +
					// mainCharacter.shootDelay);

					Sound shurikenThrow = Sound.loadFromFile("src/resources/sounds/shuriken_throw.wav");
					shurikenThrow.play();
					float speed = 0;
					float animationSpeed = (deltaTimeMS / 1000);
					int randomRange = (int) (Math.random()*10)+35;
					System.out.println(randomRange);
					if (kbState[KeyEvent.VK_SHIFT] && mainCharacter.powerUps.contains("enhance")) {
						mainCharacter.health-=.02;
						animationSpeed = (deltaTimeMS / 500);
						mainCharacter.currentTime +=animationSpeed *500;
						speed = (deltaTimeMS);
						 randomRange = (int) (Math.random()*60)+5;

					} 
					if (mainCharacter.facing.equals("left")) {
						gunMan.setDefAnimationFix(gunThrowLeft);
					} else if (mainCharacter.facing.equals("right")) {
						gunMan.setDefAnimationFix(gunThrowRight);

					}
					gunMan.update(animationSpeed);
					// add to bullets

					if ((mainCharacter.currentTime  > mainCharacter.shootDelay)) {
						mainCharacter.currentTime = 0;
						if (gunMan.getDef() == gunThrowLeft) {
							bullets.add(new BulletSpriteDef(
									new int[] { mainCharacter.spritePos[0] + 20, mainCharacter.spritePos[1] + randomRange },
									new float[] { mainCharacter.spritePos[0] + 20, mainCharacter.spritePos[1] + randomRange },

									bullet.spriteTex, bullet.spriteSize, 0, -1, true, "player"));

						} else if (gunMan.getDef() == gunThrowRight) {
							bullets.add(new BulletSpriteDef(
									new int[] { mainCharacter.spritePos[0] + 70, mainCharacter.spritePos[1] + randomRange },
									new float[] { mainCharacter.spritePos[0] + 70, mainCharacter.spritePos[1] + randomRange },
									bullet.spriteTex, bullet.spriteSize, 0, 1, true, "player"));

						}
					}

				} else if (kbState[KeyEvent.VK_Z] && (mainCharacter.slashCurrentTime > mainCharacter.slashDelay)
						&& (mainCharacter.slashCurrentTime > mainCharacter.slashCooldown)
						&& mainCharacter.powerUps.contains("sword")) {

					Sound swordSlash = Sound.loadFromFile("src/resources/sounds/sword_slash.wav");
					swordSlash.play();
					mainCharacter.slashCurrentTime = 0;
					if (mainCharacter.facing.equals("left")) {
						if (kbState[KeyEvent.VK_SHIFT] && mainCharacter.powerUps.contains("enhance")) {
							mainCharacter.health-=8;

							gunMan.setDefAnimationFix(gunSuperSlashLeft);

						}
						else{
						gunMan.setDefAnimationFix(gunSlashLeft);
						}
						while (collidesWithBackground(mainCharacter, bground) != 0) {
							mainCharacter.spriteActualPos[0]--;
						}
						mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
						mainCharacter.slashing = -1;
					} else if (mainCharacter.facing.equals("right")) {
						if (kbState[KeyEvent.VK_SHIFT] && mainCharacter.powerUps.contains("enhance")) {
							gunMan.setDefAnimationFix(gunSuperSlashRight);

						}
						else{
						gunMan.setDefAnimationFix(gunSlashRight);
						}

						mainCharacter.slashing = 1;

					}
				} else if (!kbState[KeyEvent.VK_RIGHT] && !kbState[KeyEvent.VK_LEFT] && mainCharacter.slashing == 0 && mainCharacter.jumpForce==0) {
					if (mainCharacter.facing.equals("right")) {
						gunMan.setDefAnimationFix(gunManIdleRight);
					} else if (mainCharacter.facing.equals("left")) {
						gunMan.setDefAnimationFix(gunManIdleLeft);
					}

				}

				// Key presses for Camera
				if (camera.spritePos[1] > 0 && mainCharacter.spriteActualPos[1] - camera.spriteActualPos[1] <= 0) {
					if (camera.spritePos[1] > 0
							&& mainCharacter.spriteActualPos[1] - camera.spriteActualPos[1] <= window.getHeight() / 2) {
						camera.spriteActualPos[1] -= (deltaTimeMS);
						camera.spritePos[1] = (int) camera.spriteActualPos[1];
					}
				} else if (kbState[KeyEvent.VK_UP] && camera.spritePos[1] <= 0) {
					camera.spritePos[1] = 0;
				}
				if (kbState[KeyEvent.VK_DOWN]
						&& camera.spritePos[1] < (tileSize[1] * bground.height - camera.spriteSize[1])
						&& mainCharacter.spriteActualPos[1] - camera.spriteActualPos[1] >= window.getHeight() / 2) {
					camera.spriteActualPos[1] += (deltaTimeMS);
					camera.spritePos[1] = (int) camera.spriteActualPos[1];
				} else if (kbState[KeyEvent.VK_DOWN]
						&& camera.spritePos[1] >= (tileSize[1] * bground.height - camera.spriteSize[1])) {
					camera.spritePos[1] = tileSize[1] * bground.height - camera.spriteSize[1];
				}
				if (kbState[KeyEvent.VK_LEFT] && camera.spritePos[0] > 0
						&& mainCharacter.spriteActualPos[0] - camera.spriteActualPos[0] <= window.getWidth() / 2) {
					camera.spriteActualPos[0] -= (deltaTimeMS);
					camera.spritePos[0] = (int) camera.spriteActualPos[0];
				} else if (kbState[KeyEvent.VK_LEFT] && camera.spritePos[0] <= 0) {
					camera.spritePos[0] = 0;
				}
				if (kbState[KeyEvent.VK_RIGHT]
						&& camera.spritePos[0] < (tileSize[0] * bground.width - camera.spriteSize[0])
						&& mainCharacter.spriteActualPos[0] - camera.spriteActualPos[0] >= window.getWidth() / 2) {
					camera.spriteActualPos[0] += (deltaTimeMS);
					camera.spritePos[0] = (int) camera.spriteActualPos[0];
				} else if (kbState[KeyEvent.VK_RIGHT]
						&& camera.spritePos[0] >= (tileSize[0] * bground.width - camera.spriteSize[0])) {
					camera.spritePos[0] = tileSize[0] * bground.width - camera.spriteSize[0];
				}

			}
			gl.glClearColor(0, 0, 0, 1);
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

			glDrawSprite(gl, arenaBackTex, 0, 0, backgroundSize[0], backgroundSize[1]);

			// Draw Background
			// System.out.println("bottom");
			for (int x = (int) Math.floor((camera.spritePos[0]) / tileSize[0]); x <= (int) Math
					.floor((camera.spritePos[0] + camera.spriteSize[0]) / tileSize[0]); x++) {
				for (int y = (int) Math.floor((camera.spritePos[1]) / tileSize[1]); y <= (int) Math
						.floor((camera.spritePos[1] + camera.spriteSize[1]) / tileSize[1]); y++) {
					if (x < bground.width && y < bground.height) {
						glDrawSprite(gl, bground.getTile(x, y).tex, x * bground.tileWidth - camera.spritePos[0],
								y * bground.tileHeight - camera.spritePos[1], tileSize[0], tileSize[1]);
					}
				}
			}

			// Draw pillar
			// System.out.println("bottom");
			// for (int x = (int) Math.floor((camera.spritePos[0]) /
			// tileSize[0]); x <= (int) Math
			// .floor((camera.spritePos[0] + camera.spriteSize[0]) /
			// tileSize[0]); x++) {
			// for (int y = (int) Math.floor((camera.spritePos[1]) /
			// tileSize[1]); y <= (int) Math
			// .floor((camera.spritePos[1] + camera.spriteSize[1]) /
			// tileSize[1]); y++) {
			// if ((x < bground.width && y < bground.height)
			// && (y * topGroundDef.tileHeight - camera.spritePos[1] <=
			// mainCharacter.spritePos[1]
			// + mainCharacter.spriteSize[1] - camera.spritePos[1])) {
			// glDrawSprite(gl, topGroundDef.getTile(x, y).tex,
			// x * topGroundDef.tileWidth - camera.spritePos[0],
			// y * topGroundDef.tileHeight - camera.spritePos[1], tileSize[0],
			// tileSize[1]);
			// }
			// }
			// }

			// System.out.println("middle");
			// System.out.println(deltaTimeMS);

			// Slash damage

			for (SpriteDef s : slimes) {
				if (mainCharacter.slashing != 0) {
					if (intersects(s, mainCharacter) && s.hitDelayCurrent > s.hitDelay) {
						if(gunMan.def == gunSuperSlashLeft||gunMan.def == gunSuperSlashRight)
						{
							s.health -= mainCharacter.slashDamage *2;
						}
						else{
						s.health -= mainCharacter.slashDamage;
						}
						s.hitDelayCurrent = 0;
					}
				}
				s.hitDelayCurrent += deltaTimeMS;
			}

			// Draw Bullets
			int bulletsSize = bullets.size();
			for (int i = 0; i < bulletsSize; i++) {
				if (i >= bulletsSize) {
					// System.out.println(i);
					// System.out.println("ActualSize" + bullets.size());
					// System.out.println("Size" + bulletsSize);
					break;

				}
				if (bullets.get(i).type.equals("player")) {
					if (intersects(bullets.get(i), camera)) {
						glDrawSprite(gl, bullet.spriteTex, bullets.get(i).spritePos[0] - camera.spritePos[0],
								bullets.get(i).spritePos[1] - camera.spritePos[1], bullet.spriteSize[0],
								bullet.spriteSize[1]);
						// System.out.println("drawing bullets");
					}
					if (bullets.get(i).rise > 0) {
						bullets.get(i).spriteActualPos[1] += deltaTimeMS * 2;

					} else if (bullets.get(i).rise < 0) {
						bullets.get(i).spriteActualPos[1] -= deltaTimeMS * 2;

					} else if (bullets.get(i).run > 0) {
						bullets.get(i).spriteActualPos[0] += deltaTimeMS * 2;

					} else if (bullets.get(i).run < 0) {
						bullets.get(i).spriteActualPos[0] -= deltaTimeMS * 2;

					}

					bullets.get(i).spritePos[0] = (int) bullets.get(i).spriteActualPos[0];
					bullets.get(i).spritePos[1] = (int) bullets.get(i).spriteActualPos[1];
					if (bullets.get(i).spritePos[0] < 0 || bullets.get(i).spritePos[0] > bground.width * tileSize[0]) {
						bullets.remove(i);
						bulletsSize--;
					} else if (intersects(bullets.get(i), robot)) {
						robot.health--;
						bullets.remove(i);
						bulletsSize--;
					}

				}
				for (SpriteDef s : slimes) {
					if (i >= bulletsSize) {
						// System.out.println(i);
						// System.out.println("ActualSize" + bullets.size());
						// System.out.println("Size" + bulletsSize);
						break;

					}
					if (bullets.get(i).type.equals("player")) {
						if (intersects(bullets.get(i), s)) {
							bullets.remove(i);
							bulletsSize--;
							s.health -= mainCharacter.bulletDamage;
						}
					} else if (bullets.get(i).type.equals("slime") && s.health > 0) {
						if (intersects(bullets.get(i), mainCharacter)) {
							bullets.remove(i);
							bulletsSize--;
							mainCharacter.health--;
						} else {
							if (intersects(bullets.get(i), camera)) {
								glDrawSprite(gl, bullets.get(i).spriteTex,
										bullets.get(i).spritePos[0] - camera.spritePos[0],
										bullets.get(i).spritePos[1] - camera.spritePos[1], bullets.get(i).spriteSize[0],
										bullets.get(i).spriteSize[1]);
								// System.out.println("drawing bullets");
							}
							if (!bullets.get(i).touchedGround) {
								if (bullets.get(i).run > 0 && bullets.get(i).rise > 0) {
									bullets.get(i).spriteActualPos[0] += deltaTimeMS / 3;

								} else if (bullets.get(i).run < 0 && bullets.get(i).rise > 0) {
									bullets.get(i).spriteActualPos[0] -= deltaTimeMS / 3;

								}
							}

							bullets.get(i).spritePos[0] = (int) bullets.get(i).spriteActualPos[0];
							bullets.get(i).spritePos[1] = (int) bullets.get(i).spriteActualPos[1];
							bullets.get(i).currentTime += deltaTimeMS;

							if (bullets.get(i).spritePos[0] < 0
									|| bullets.get(i).spritePos[0] > bground.width * tileSize[0]
									|| bullets.get(i).currentTime > 15000) {
								bullets.remove(i);
								bulletsSize--;
							}

						}
					}
				}

			}
			// Draw the main character sprite

			if (intersects(mainCharacter, camera) && mainCharacter.health > 0) {
				System.out.println("X " + mainCharacter.spritePos[0]);
				System.out.println("Y " + mainCharacter.spritePos[1]);

				mainCharacter.animation.draw(mainCharacter.spritePos[0] - camera.spritePos[0],
						mainCharacter.spritePos[1] - camera.spritePos[1], gl);

			}
			// Update currentTime for main character sprite
			mainCharacter.currentTime += deltaTimeMS;
			mainCharacter.slashCurrentTime += deltaTimeMS;

			// Draw health bar

			if (mainCharacter.health >= 64) {
				glDrawSprite(gl, health1Tex, 0, 0, healthSize[0], healthSize[1]);
			} else if (mainCharacter.health >= 61) {
				glDrawSprite(gl, health2Tex, 0, 0, healthSize[0], healthSize[1]);
			} else if (mainCharacter.health >= 52) {
				glDrawSprite(gl, health3Tex, 0, 0, healthSize[0], healthSize[1]);
			} else if (mainCharacter.health >= 44) {
				glDrawSprite(gl, health4Tex, 0, 0, healthSize[0], healthSize[1]);
			} else if (mainCharacter.health >= 35) {
				glDrawSprite(gl, health5Tex, 0, 0, healthSize[0], healthSize[1]);
			} else if (mainCharacter.health >= 26) {
				glDrawSprite(gl, health6Tex, 0, 0, healthSize[0], healthSize[1]);
			} else if (mainCharacter.health >= 17) {
				glDrawSprite(gl, health7Tex, 0, 0, healthSize[0], healthSize[1]);
			} else if (mainCharacter.health >= 8) {
				glDrawSprite(gl, health8Tex, 0, 0, healthSize[0], healthSize[1]);
			} else if (mainCharacter.health >= 0) {
				glDrawSprite(gl, health9Tex, 0, 0, healthSize[0], healthSize[1]);
			}

			// // Draw slimes
			int slimesSize = slimes.size();
			for (int i = 0; i < slimesSize; i++) {
				if (intersects(slimes.get(i), camera) && slimes.get(i).health > 0) {
					slimes.get(i).animation.draw(slimes.get(i).spritePos[0] - camera.spritePos[0],
							slimes.get(i).spritePos[1] - camera.spritePos[1], gl);

				} else if (slimes.size() == 1 && slimes.get(i).health <= 0) {
					regenGlobe.spritePos[0] = slimes.get(i).spritePos[0];
					regenGlobe.spritePos[1] = slimes.get(i).spritePos[1];
					regenGlobe.jumpForce = deltaTimeMS / 2;
					// to skip initial impact from gravity
					// regenGlobe.spriteActualPos[1]=slimes.get(i).spritePos[1]-
					// 150;
					// regenGlobe.spriteActualPos[0]=slimes.get(i).spritePos[0];

					slimes.remove(slimes.get(i));
					slimesSize--;

				} else if (slimes.get(i).health <= 0) {
					slimes.remove(slimes.get(i));
					slimesSize--;

				}
			}

			// Change first level after killing last slime and drop power up
			// DRAW GLOBES
			if (intersects(regenGlobe, camera) && slimes.size() == 0
					&& !mainCharacter.powerUps.contains("regenGlobe")) {
				regenGlobeAnimation.update((deltaTimeMS / 1000));
				regenGlobe.animation.draw(regenGlobe.spritePos[0] - camera.spritePos[0],
						regenGlobe.spritePos[1] - camera.spritePos[1], gl);
				// System.out.println("drawing main character");

			}
			if (intersects(sword, camera) && robot.health <= 0 && !mainCharacter.powerUps.contains("sword")) {
				swordGlow.update((deltaTimeMS / 1000));
				sword.animation.draw(sword.spritePos[0] - camera.spritePos[0], sword.spritePos[1] - camera.spritePos[1],
						gl);
				// System.out.println("drawing main character");

			}
			// PICK UP POWER UPS
			if (intersects(regenGlobe, mainCharacter)) {
				mainCharacter.powerUps.add("regenGlobe");
				mainCharacter.powerUps.add("enhance");
			}
			if (mainCharacter.powerUps.contains("regenGlobe") && mainCharacter.health < 64 && mainCharacter.health>0) {
				mainCharacter.health += deltaTimeMS / 500;
			}
			if (intersects(sword, mainCharacter)) {
				mainCharacter.powerUps.add("sword");
			}

			if (slimes.size() == 0 && currentLevel == 0) {
				currentLevel = 1;
				changingLevel = true;
			}
			if (changingLevel == true && currentLevel == 1) {

				currentLevel = 2;
				for (int x = 0; x < bground.width; x++) {
					for (int y = 0; y < bground.height; y++) {
						if (y > 5 * bground.height / 6) {
							bground.setTile(x, y, grayTileTex, true);
						} else {
							bground.setTile(x, y, 0, false);
						}

					}

				}
				for (int x = 5; x < bground.width; x++) {

					for (int y = 0; y < bground.height - 10; y++) {

						if (y > (bground.height / 3 + 6)) {
							bground.setTile(x, y, brickTileTex, true);
						} else if (y == bground.height / 3 + 3 && x > 3 && x < 8) {
							bground.setTile(x, y, brickTileTex, true);
						} else if (y == bground.height / 3 + 4 && x > 10 && x < 14) {
							bground.setTile(x, y, brickTileTex, true);
						} else if (y == bground.height / 3 + 6 && x > 18 && x < 23) {
							bground.setTile(x, y, brickTileTex, true);
						} else {
							bground.setTile(x, y, 0, false);
						}

					}

				}

				robot.spriteActualPos[1] = 1547;
				robot.spritePos[1] = 1547;
				robot.spriteActualPos[0] = 2741;
				robot.spritePos[0] = 2741;
				changingLevel = false;
			}

			// DRAW ROBOT
			if (currentLevel == 2) {
				if (intersects(robot, camera) && robot.health > 0) {

					robot.animation.draw(robot.spritePos[0] - camera.spritePos[0],
							robot.spritePos[1] - camera.spritePos[1], gl);
					// System.out.println("drawing main character");

				} else if (robot.health <= 0) {
					sword.spritePos[0] = robot.spritePos[0];
					sword.spritePos[1] = robot.spritePos[1];
					sword.touchedGround = true;
					sword.jumpForce = deltaTimeMS / 3;
					sword.spriteActualPos[1] -= sword.jumpForce;

				}
			}

			// Draw top layer

			// for (int x = (int) Math.floor((camera.spritePos[0]) /
			// tileSize[0]); x <= (int) Math
			// .floor((camera.spritePos[0] + camera.spriteSize[0]) /
			// tileSize[0]); x++) {
			// for (int y = (int) Math.floor((camera.spritePos[1]) /
			// tileSize[1]); y <= (int) Math
			// .floor((camera.spritePos[1] + camera.spriteSize[1]) /
			// tileSize[1]); y++) {
			// if ((x < bground.width && y < bground.height)
			// && (y * topGroundDef.tileHeight - camera.spritePos[1] >
			// mainCharacter.spritePos[1]
			// + 1 * mainCharacter.spriteSize[1] / 3 - camera.spritePos[1])) {
			// glDrawSprite(gl, topGroundDef.getTile(x, y).tex,
			// x * topGroundDef.tileWidth - camera.spritePos[0],
			// y * topGroundDef.tileHeight - camera.spritePos[1], tileSize[0],
			// tileSize[1]);
			//
			// }
			// }
			// }

		}
	}

	/**
	 * Checks for collision with background tile, returns 1 if collision is from
	 * bottom, return -1 if collision is from top, 0 if no collision
	 * 
	 * @param sprite
	 * @param groundDef
	 * @return
	 */
	private static int collidesWithBackground(SpriteDef sprite, BackgroundDef groundDef) {
		for (int x = 0; x < groundDef.width; x++) {
			for (int y = 0; y < groundDef.height; y++) {
				// System.out.println("x coordinate = "+sprite.spritePos[0]);
				// System.out.println("tile x coordinate = "+x *
				// groundDef.tileWidth);

				if (groundDef.getTile(x, y).collidable
						&& (((sprite.spriteActualPos[0] < x * groundDef.tileWidth + groundDef.tileWidth)
								&& (sprite.spriteActualPos[0] + sprite.spriteSize[0] >= x * groundDef.tileWidth))
								|| ((sprite.spriteActualPos[0] + sprite.spriteSize[0] < x * groundDef.tileWidth
										+ groundDef.tileWidth && sprite.spriteActualPos[0] > x * groundDef.tileWidth)))
						&& (sprite.spriteActualPos[1] + sprite.spriteSize[1] < y * groundDef.tileHeight
								+ groundDef.tileHeight)
						&& (sprite.spriteActualPos[1] + sprite.spriteSize[1] >= y * groundDef.tileHeight)) {
					return -1;
				} else if (groundDef.getTile(x, y).collidable
						&& ((sprite.spriteActualPos[0] + sprite.spriteSize[0] > x * groundDef.tileWidth)
								&& (sprite.spriteActualPos[0] <= x * groundDef.tileWidth + groundDef.tileWidth)
								&& (sprite.spriteActualPos[1] + sprite.spriteSize[1] > y * groundDef.tileHeight
										+ groundDef.tileHeight)
								&& (sprite.spriteActualPos[1] < y * groundDef.tileHeight + groundDef.tileHeight))) {

					return 1;
				}
			}
		}
		return 0;
	}

	// Checks if two sprites intersects
	private static boolean intersects(SpriteDef s1, SpriteDef s2) {
		if ((s1.spritePos[0] < s2.spritePos[0] + s2.spriteSize[0])
				&& (s1.spritePos[0] + s1.spriteSize[0] >= s2.spritePos[0])
				&& (s1.spritePos[1] < s2.spritePos[1] + s2.spriteSize[1])
				&& (s1.spritePos[1] + s1.spriteSize[1] >= s2.spritePos[1])) {
			return true;
		}

		return false;
	}

	// Load a file into an OpenGL texture and return that texture.
	public static int glTexImageTGAFile(GL2 gl, URL filename, int[] out_size) {
		final int BPP = 4;

		DataInputStream file = null;
		try {
			// Open the file.
			file = new DataInputStream(filename.openStream());
		} catch (FileNotFoundException ex) {
			System.err.format("File: %s -- Could not open for reading.", filename);
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// Skip first two bytes of data we don't need.
			file.skipBytes(2);

			// Read in the image type. For our purposes the image type
			// should be either a 2 or a 3.
			int imageTypeCode = file.readByte();
			if (imageTypeCode != 2 && imageTypeCode != 3) {
				file.close();
				System.err.format("File: %s -- Unsupported TGA type: %d", filename, imageTypeCode);
				return 0;
			}

			// Skip 9 bytes of data we don't need.
			file.skipBytes(9);

			int imageWidth = Short.reverseBytes(file.readShort());
			int imageHeight = Short.reverseBytes(file.readShort());
			int bitCount = file.readByte();
			file.skipBytes(1);

			// Allocate space for the image data and read it in.
			byte[] bytes = new byte[imageWidth * imageHeight * BPP];

			// Read in data.
			if (bitCount == 32) {
				for (int it = 0; it < imageWidth * imageHeight; ++it) {
					bytes[it * BPP + 0] = file.readByte();
					bytes[it * BPP + 1] = file.readByte();
					bytes[it * BPP + 2] = file.readByte();
					bytes[it * BPP + 3] = file.readByte();
				}
			} else {
				for (int it = 0; it < imageWidth * imageHeight; ++it) {
					bytes[it * BPP + 0] = file.readByte();
					bytes[it * BPP + 1] = file.readByte();
					bytes[it * BPP + 2] = file.readByte();
					bytes[it * BPP + 3] = -1;
				}
			}

			file.close();

			// Load into OpenGL
			int[] texArray = new int[1];
			gl.glGenTextures(1, texArray, 0);
			int tex = texArray[0];
			gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
			gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, imageWidth, imageHeight, 0, GL2.GL_BGRA,
					GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes));
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

			out_size[0] = imageWidth;
			out_size[1] = imageHeight;
			return tex;
		} catch (IOException ex) {
			System.err.format("File: %s -- Unexpected end of file.", filename);
			return 0;
		}
	}

	public static void glDrawSprite(GL2 gl, int tex, int x, int y, int w, int h) {
		if (tex != 0) {
			gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
			gl.glBegin(GL2.GL_QUADS);
			{
				gl.glColor3ub((byte) -1, (byte) -1, (byte) -1);
				gl.glTexCoord2f(0, 1);
				gl.glVertex2i(x, y);
				gl.glTexCoord2f(1, 1);
				gl.glVertex2i(x + w, y);
				gl.glTexCoord2f(1, 0);
				gl.glVertex2i(x + w, y + h);
				gl.glTexCoord2f(0, 0);
				gl.glVertex2i(x, y + h);
			}
			gl.glEnd();
		}
	}
}