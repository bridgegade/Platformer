import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.opengl.*;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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

	// Texture for mainBackground
	// Size of the tile
	private static int[] tileSize = new int[2];

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
		window.setSize(1000, 800);

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
		gl.glViewport(0, 0, 1000, 800);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glOrtho(0, 1000, 800, 0, 0, 100);
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

		ShootingSpriteDef mainCharacter = new ShootingSpriteDef(true);
		mainCharacter.spriteActualPos[1] = 16 * tileSize[1];
		mainCharacter.spritePos[1] = 16 * tileSize[1];
		mainCharacter.spriteActualPos[0] = 15 * tileSize[0];
		mainCharacter.spritePos[0] = 15 * tileSize[0];
		mainCharacter.health = 50;
		actors.add(mainCharacter);
		mainCharacter.spriteTex = glTexImageTGAFile(gl,

				JavaTemplate.class.getResource("/resources/gunManWalkRight0.tga"), mainCharacter.spriteSize);

		SpriteDef bullet = new SpriteDef(true);
		bullet.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/bullet.tga"),
				bullet.spriteSize);

		SpriteDef slime = new SpriteDef(true);
		slimes.add(slime);
		slime.spriteActualPos[1] = 16 * tileSize[1];
		slime.spritePos[1] = 16 * tileSize[1];
		slime.spriteActualPos[0] = 35 * tileSize[0];
		slime.spritePos[0] = 35 * tileSize[0];
		slime.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/slime/f0.tga"),
				slime.spriteSize);
		actors.add(slime);

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
		SpriteDef sword = new SpriteDef(true);
		sword.spriteTex = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/pickups/sword.tga"),
				slimeBullet.spriteSize);
		actors.add(sword);

		// set animationDefs for main character for each direction
		FrameDef[] gunManWalkingDownFrames = new FrameDef[7];
		for (int i = 0; i < gunManWalkingDownFrames.length; i++) {
			int[] gunManSize = new int[2];
			int image = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/gunManWalkDown" + i + ".tga"),
					gunManSize);
			gunManWalkingDownFrames[i] = new FrameDef(image, (float) 0.05, gunManSize);
		}
		AnimationDef gunManWalkingDown = new AnimationDef(gunManWalkingDownFrames);

		FrameDef[] gunManWalkingUpFrames = new FrameDef[7];
		for (int i = 0; i < gunManWalkingUpFrames.length; i++) {
			int[] gunManSize = new int[2];
			int image = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/gunManWalkUp" + i + ".tga"),
					gunManSize);
			gunManWalkingUpFrames[i] = new FrameDef(image, (float) 0.05, gunManSize);
		}
		AnimationDef gunManWalkingUp = new AnimationDef(gunManWalkingUpFrames);

		FrameDef[] gunManWalkingLeftFrames = new FrameDef[7];
		for (int i = 0; i < gunManWalkingLeftFrames.length; i++) {
			int[] gunManSize = new int[2];
			int image = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/gunManWalkLeft" + i + ".tga"),
					gunManSize);
			gunManWalkingLeftFrames[i] = new FrameDef(image, (float) 0.05, gunManSize);
		}
		AnimationDef gunManWalkingLeft = new AnimationDef(gunManWalkingLeftFrames);

		FrameDef[] gunManWalkingRightFrames = new FrameDef[7];
		for (int i = 0; i < gunManWalkingRightFrames.length; i++) {
			int[] gunManSize = new int[2];
			int image = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/gunManWalkRight" + i + ".tga"),
					gunManSize);
			gunManWalkingRightFrames[i] = new FrameDef(image, (float) 0.05, gunManSize);
		}
		AnimationDef gunManWalkingRight = new AnimationDef(gunManWalkingRightFrames);

		AnimationData gunMan = new AnimationData(gunManWalkingRight, (float) 0.25);
		mainCharacter.animation=gunMan;
		

		// slime animation
		FrameDef[] slimeMovementFrames = new FrameDef[9];
		for (int i = 0; i < slimeMovementFrames.length; i++) {
			int[] slimeSize = new int[2];
			int image = glTexImageTGAFile(gl, JavaTemplate.class.getResource("/resources/slime/f" + i + ".tga"),
					slimeSize);
			slimeMovementFrames[i] = new FrameDef(image, (float) 0.09, slimeSize);
		}
		AnimationDef slimeMoving = new AnimationDef(slimeMovementFrames);
		AnimationData slimeAnimationData = new AnimationData(slimeMoving, (float) 0.25);
		slimeAnimationData.setDef(slimeMoving);
		slime.animation= slimeAnimationData;
		
		
		
		
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
					slimes.get(i).animation.update((deltaTimeMS / 1000), slimes.get(i));
				}
				double chanceForAction = Math.random() * 300;

				if (slimes.get(i).animation.index == 2) {
					slimes.get(i).animation.update((deltaTimeMS / 500), slimes.get(i));
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
					else if (chanceForAction >= 1 && chanceForAction < 15) {
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
						FrameDef[] slimeMovementFramesClone = new FrameDef[9];
						for (int j = 0; j < slimeMovementFramesClone.length; j++) {
							int[] slimeSizeClone = new int[2];
							int imageClone = glTexImageTGAFile(gl,
									JavaTemplate.class.getResource("/resources/slime/f" + j + ".tga"), slimeSizeClone);
							slimeMovementFramesClone[j] = new FrameDef(imageClone, (float) 0.09, slimeSizeClone);
						}
						AnimationDef slimeMovingClone = new AnimationDef(slimeMovementFramesClone);
						AnimationData slimeAnimationDataClone = new AnimationData(slimeMovingClone, (float) 0.25);
						slimeAnimationDataClone.setDef(slimeMovingClone);
						slimeClone.animation = slimeAnimationDataClone;

					}
				}

				if (slimeAnimationData.index == 3 && !slimes.get(i).touchedGround) {

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
			// controlled jump, less realistic
			// if (kbState[KeyEvent.VK_UP] && mainCharacter.spritePos[1] > 0 &&
			// mainCharacter.canJump) {
			// // gunMan.setDef(gunManWalkingUp);
			// // gunMan.update((deltaTimeMS / 1000));
			// mainCharacter.spriteActualPos[1] -= 250 * (gravity);
			//
			// mainCharacter.spritePos[1] = (int)
			// mainCharacter.spriteActualPos[1];
			// } else if (!kbState[KeyEvent.VK_UP] &&
			// !mainCharacter.touchedGround) {
			// mainCharacter.canJump = false;
			// }

			// GUMAN MOVEMENT CONTROLS && movement
			if (mainCharacter.health > 0) {
				if (mainCharacter.slashCurrentTime < mainCharacter.slashDelay && mainCharacter.slashing != 0)
					if (mainCharacter.slashing == 1) {
						mainCharacter.spriteActualPos[0] += 3 * deltaTimeMS;
						if (collidesWithBackground(mainCharacter, bground) == 1
								|| collidesWithBackground(mainCharacter, bground) == -1
								|| mainCharacter.spriteActualPos[0] >= tileSize[0] * bground.width
										- mainCharacter.spriteSize[0]
								|| mainCharacter.spriteActualPos[0] <= 0) {
							mainCharacter.spriteActualPos[0] -= 3 * deltaTimeMS;

						} else {
							mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
							if (camera.spritePos[0] < (tileSize[0] * bground.width - camera.spriteSize[0])) {
								camera.spriteActualPos[0] += 3 * deltaTimeMS;
								camera.spritePos[0] = (int) camera.spriteActualPos[0];
							}

						}
					} else if (mainCharacter.slashing == -1) {
						mainCharacter.spriteActualPos[0] -= 3 * deltaTimeMS;
						if (collidesWithBackground(mainCharacter, bground) == 1
								|| collidesWithBackground(mainCharacter, bground) == -1
								|| mainCharacter.spriteActualPos[0] >= tileSize[0] * bground.width
										- mainCharacter.spriteSize[0]
								|| mainCharacter.spriteActualPos[0] <= 0) {
							mainCharacter.spriteActualPos[0] += 3 * deltaTimeMS;

						} else {
							mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
							if (camera.spritePos[0] > 0 && mainCharacter.spriteActualPos[0]
									- camera.spriteActualPos[0] <= window.getWidth() / 2) {
								camera.spriteActualPos[0] -= 3 * deltaTimeMS;
								camera.spritePos[0] = (int) camera.spriteActualPos[0];
							}
						}

					}
				// controlled jump based on gravity
				if (kbState[KeyEvent.VK_UP] && mainCharacter.spritePos[1] > 0 && mainCharacter.canJump) {
					// gunMan.setDef(gunManWalkingUp);
					// gunMan.update((deltaTimeMS / 1000));

					mainCharacter.jumpForce = (float) 2;
					mainCharacter.spriteActualPos[1] -= mainCharacter.jumpForce;
					mainCharacter.canJump = false;
				}

				if (kbState[KeyEvent.VK_LEFT] && mainCharacter.spritePos[0] > 0) {
					gunMan.setDef(gunManWalkingLeft);
					gunMan.update((deltaTimeMS / 1000), mainCharacter);
					mainCharacter.spriteActualPos[0] -= (deltaTimeMS) / 2;
					if (collidesWithBackground(mainCharacter, bground) == -1
							|| collidesWithBackground(mainCharacter, bground) == 1) {
						mainCharacter.spriteActualPos[0] += (deltaTimeMS) / 2;
					}

					mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
				}
				if (kbState[KeyEvent.VK_RIGHT]
						&& mainCharacter.spritePos[0] < tileSize[0] * bground.width - mainCharacter.spriteSize[0]) {
					gunMan.setDef(gunManWalkingRight);
					gunMan.update((deltaTimeMS / 1000), mainCharacter);
					mainCharacter.spriteActualPos[0] += (deltaTimeMS) / 2;

					if (collidesWithBackground(mainCharacter, bground) == 1
							|| collidesWithBackground(mainCharacter, bground) == -1) {
						mainCharacter.spriteActualPos[0] -= (deltaTimeMS) / 2;
					}

					mainCharacter.spritePos[0] = (int) mainCharacter.spriteActualPos[0];
				}
				// main player attacks
				if (kbState[KeyEvent.VK_SPACE] && (mainCharacter.currentTime > mainCharacter.shootDelay)) {
					mainCharacter.currentTime = 0;
					// System.out.println("current time" +
					// mainCharacter.currentTime);
					// System.out.println("shoot delay" +
					// mainCharacter.shootDelay);

					// add to bullets
					if (gunMan.getDef() == gunManWalkingUp) {
						bullets.add(new BulletSpriteDef(
								new int[] { mainCharacter.spritePos[0] + 40, mainCharacter.spritePos[1] + 40 },
								new float[] { mainCharacter.spritePos[0] + 40, mainCharacter.spritePos[1] + 40 },
								bullet.spriteTex, bullet.spriteSize, -1, 0, true, "player"));
					} else if (gunMan.getDef() == gunManWalkingDown) {
						bullets.add(new BulletSpriteDef(
								new int[] { mainCharacter.spritePos[0] + 16, mainCharacter.spritePos[1] + 80 },
								new float[] { mainCharacter.spritePos[0] + 16, mainCharacter.spritePos[1] + 80 },
								bullet.spriteTex, bullet.spriteSize, 1, 0, true, "player"));

					} else if (gunMan.getDef() == gunManWalkingLeft) {
						bullets.add(new BulletSpriteDef(
								new int[] { mainCharacter.spritePos[0] + 20, mainCharacter.spritePos[1] + 68 },
								new float[] { mainCharacter.spritePos[0] + 20, mainCharacter.spritePos[1] + 68 },
								bullet.spriteTex, bullet.spriteSize, 0, -1, true, "player"));

					} else if (gunMan.getDef() == gunManWalkingRight) {
						bullets.add(new BulletSpriteDef(
								new int[] { mainCharacter.spritePos[0] + 70, mainCharacter.spritePos[1] + 68 },
								new float[] { mainCharacter.spritePos[0] + 70, mainCharacter.spritePos[1] + 68 },
								bullet.spriteTex, bullet.spriteSize, 0, 1, true, "player"));

					}

				} else if (kbState[KeyEvent.VK_Z] && (mainCharacter.slashCurrentTime > mainCharacter.slashDelay)
						&& (mainCharacter.slashCurrentTime > mainCharacter.slashCooldown)) {
					mainCharacter.slashCurrentTime = 0;
					if (gunMan.getDef() == gunManWalkingLeft) {
						mainCharacter.slashing = -1;
					} else if (gunMan.getDef() == gunManWalkingRight) {
						mainCharacter.slashing = 1;

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

				if (!kbState[KeyEvent.VK_RIGHT] && !kbState[KeyEvent.VK_LEFT] && !kbState[KeyEvent.VK_DOWN]
						&& !kbState[KeyEvent.VK_UP]) {
					gunMan.idle();
				}
			}
			gl.glClearColor(0, 0, 0, 1);
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

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
							s.health--;
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

				mainCharacter.animation.draw(mainCharacter.spritePos[0] - camera.spritePos[0],
						mainCharacter.spritePos[1] - camera.spritePos[1], gl);
				// System.out.println("drawing main character");

			}
			// Update currentTime for main character sprite
			mainCharacter.currentTime += deltaTimeMS;
			mainCharacter.slashCurrentTime += deltaTimeMS;
			
	

//			// Draw slimes
			int slimesSize = slimes.size();
			for (int i = 0; i < slimesSize; i++) {
				if (intersects(slimes.get(i), camera) && slimes.get(i).health > 0) {
					slimes.get(i).animation.draw(slimes.get(i).spritePos[0] - camera.spritePos[0],
							slimes.get(i).spritePos[1] - camera.spritePos[1], gl);

				} else if (slimes.get(i).health <= 0) {
					slimes.remove(slimes.get(i));
					slimesSize--;
					if (slimes.size() == 0) {

					}
				}
			}

			if (slimes.size() == 0 && currentLevel == 0) {
				currentLevel = 1;
				changingLevel = true;
			}
			if (changingLevel == true && currentLevel == 1) {
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
				changingLevel = false;
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