package game;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;

public class Player {

	private double x;
	private double y;
	private double dx;
	private double dy;

	private int width;
	private int height;

	private boolean left;
	private boolean right;
	// private boolean jumping;
	// private boolean falling;
	private boolean up;
	private boolean down;

	private double moveSpeed;
	private double maxSpeed;
	private double maxFallingSpeed;
	private double stopSpeed;
	// private double jumpStart;
	private double gravity;

	private TileMap tileMap;

	private boolean topLeft;
	private boolean topRight;
	private boolean bottomLeft;
	private boolean bottomRight;

	private Animation animation;
	private BufferedImage[] idleSprites;
	private BufferedImage[] walkingSprites;
	private BufferedImage[] downSprites;
	private BufferedImage[] fallingSprites;

	private BufferedImage[] upSprites;

	private boolean facingLeft;

	public Player(TileMap tm) {

		tileMap = tm;

		width = 15;
		height = 33;

		moveSpeed = 9.6;
		maxSpeed = 9.6;
		//maxFallingSpeed = 12;
		stopSpeed = 9.60;

		try {

			idleSprites = new BufferedImage[8];
			upSprites = new BufferedImage[8];
			walkingSprites = new BufferedImage[8];
			downSprites = new BufferedImage[8];

			// idleSprites[8] =
			BufferedImage idle = ImageIO.read(new File("down_idle_small.png"));
			for (int i = 0; i < idleSprites.length; i++) {
				idleSprites[i] = idle.getSubimage(i * width, 0, width, height);
			}

			BufferedImage down = ImageIO.read(new File("walking_down_small.png"));
			for (int i = 0; i < downSprites.length; i++) {
				downSprites[i] = down.getSubimage(i * width, 0, width, height);
			}

			// jumpingSprites[0] = ImageIO.read(new File("kirbyjump.gif"));
			// fallingSprites[0] = ImageIO.read(new File("kirbyfall.gif"));

			// upSprites[0] = ImageIO.read(new File("walking_up.png"));

			BufferedImage up = ImageIO.read(new File("walking_up_small.png"));
			for (int i = 0; i < upSprites.length; i++) {
				upSprites[i] = up.getSubimage(i * width, 0, width, height);
			}

			BufferedImage walking_left = ImageIO.read(new File(
					"walking_left_small.png"));
			for (int i = 0; i < walkingSprites.length; i++) {
				walkingSprites[i] = walking_left.getSubimage(i * width, 0,
						width, height);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		animation = new Animation();
		facingLeft = false;

	}

	public void setx(int i) {
		x = i;
	}

	public void sety(int i) {
		y = i;
	}

	public void setLeft(boolean b) {
		left = b;
	}

	public void setRight(boolean b) {
		right = b;
	}

	public void setUp(boolean b) {
		up = b;
	}

	public void setDown(boolean b) {
		down = b;
	}

	private void calculateCorners(double x, double y) {
		int leftTile = tileMap.getColTile((int) (x - width / 2));
		int rightTile = tileMap.getColTile((int) (x + width / 2) - 1);
		int topTile = tileMap.getRowTile((int) (y - height / 2));
		int bottomTile = tileMap.getRowTile((int) (y + height / 2) - 1);
		topLeft = tileMap.isBlocked(topTile, leftTile);
		topRight = tileMap.isBlocked(topTile, rightTile);
		bottomLeft = tileMap.isBlocked(bottomTile, leftTile);
		bottomRight = tileMap.isBlocked(bottomTile, rightTile);
	}

	// ///////////////////////////////////////////////////////////////////

	public void update() {

		// determine next position
		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		} else if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		} else {
			if (dx > 0) {
				dx -= stopSpeed;
				if (dx < 0) {
					dx = 0;
				}
			} else if (dx < 0) {
				dx += stopSpeed;
				if (dx > 0) {
					dx = 0;
				}
			}
		}

		if (up) {
			dy -= moveSpeed;
			if (dy < -maxSpeed) {
				dy = -maxSpeed;
			}
		} else if (down) {
			dy += moveSpeed;
			if (dy > maxSpeed) {
				dy = maxSpeed;
			}
		} else {
			if (dy > 0) {
				dy -= stopSpeed;
				if (dy < 0) {
					dy = 0;
				}
			} else if (dy < 0) {
				dy += stopSpeed;
				if (dy > 0) {
					dy = 0;
				}
			}
		}

		// check collisions

		int currCol = tileMap.getColTile((int) x);
		int currRow = tileMap.getRowTile((int) y);

		double tox = x + dx;
		double toy = y + dy;

		double tempx = x;
		double tempy = y;

		calculateCorners(x, toy);
		if (dy < 0) {
			if (topLeft || topRight) {
				dy = 0;
				tempy = currRow * tileMap.getTileSize() + height / 2;
			} else {
				tempy += dy;
			}
		}
		if (dy > 0) {
			if (bottomLeft || bottomRight) {
				dy = 0;
				down = false;
				tempy = (currRow + 1) * tileMap.getTileSize() - height / 2;
			} else {
				tempy += dy;
			}
		}

		calculateCorners(tox, y);
		if (dx < 0) {
			if (topLeft || bottomLeft) {
				dx = 0;
				tempx = currCol * tileMap.getTileSize() + width / 2;
			} else {
				tempx += dx;
			}
		}
		if (dx > 0) {
			if (topRight || bottomRight) {
				dx = 0;
				tempx = (currCol + 1) * tileMap.getTileSize() - width / 2;
			} else {
				tempx += dx;
			}
		}

		x = tempx;
		y = tempy;

		// move the map
		
		tileMap.setx((int) (GamePanel.WIDTH / 2 - x));
		tileMap.sety((int) (GamePanel.HEIGHT / 2 - y));

		// sprite animation
		if (left || right) {
			animation.setFrames(walkingSprites);
			animation.setDelay(100);
		} else {
			animation.setFrames(idleSprites);
			animation.setDelay(100);
		}
		if (dy < 0) {
			animation.setFrames(upSprites);
			animation.setDelay(100);
		}
		if (dy > 0) {
			animation.setFrames(downSprites);
			animation.setDelay(100);
		}
		animation.update();

		if (dx < 0) {
			facingLeft = true;
		}
		if (dx > 0) {
			facingLeft = false;
		}

	}

	public void draw(Graphics2D g) {

		int tx = tileMap.getx();
		int ty = tileMap.gety();

		if (facingLeft) {
			g.drawImage(animation.getImage(), (int) (tx + x - width / 2),
					(int) (ty + y - height / 2), null);
		} else {
			g.drawImage(animation.getImage(),
					(int) (tx + x - width / 2 + width),
					(int) (ty + y - height / 2), -width, height, null);
		}

	}

}