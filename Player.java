package src.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import src.main.GamePanel;

public class Player extends Entity{

	GamePanel gp;
	public int movingState;
	public int gameState;
	public final int MOVE = 0;
	public final int BATTLE = 1;
	public boolean forcedMove;

	public int level;
	public int experience;
	public int experienceToNextLevel;
	public int strength;
	public int dexterity;
	public int vitality;

	public int intelligence;
	public int mana;
	public int maxMana;

	private int animationTick = 0;
	private final int ANIMATION_SPEED_WALK = 12;
	private final int ANIMATION_SPEED_IDLE = 15;


	public Player(GamePanel gp) {
		this.gp = gp;
		setDefaultValues();
		getPlayerImage();
	}

	@Override
	public void setDefaultValues() {
		super.setDefaultValues();
		x = 1 * gp.tileSize;
		y = 12 * gp.tileSize;
		speed = 1;
		steps = 0;
		facing = true;
		direction = DOWN;
		symbol = HERO;
		gameState = MOVE;
		movingState = 0;
		forcedMove = false;
		spriteNum = 0;
		animationTick = 0;

		level = 1;
		experience = 0;
		experienceToNextLevel = 100;
		strength = 5;
		dexterity = 5;
		vitality = 95;
		intelligence = 5;
		maxMana = 20 + (intelligence * 5);
		mana = maxMana;
		max_hit_point = 50 + (vitality * 10);
		hit_point = max_hit_point;
		critChance = 0.05 + (dexterity * 0.005);
		critDamageMultiplier = 1.5;
	}

	public void gainExperience(int amount) {
		if (amount <= 0) return;
		experience += amount;
		System.out.println("Player gained " + amount + " XP. Total XP: " + experience + "/" + experienceToNextLevel);
		while (experience >= experienceToNextLevel) {
			levelUp();
		}
	}

	private void levelUp() {
		level++;
		experience -= experienceToNextLevel;
		experienceToNextLevel = (int) (experienceToNextLevel * 1.5) + 50;
		strength += 1;
		dexterity += 1;
		vitality += 2;
		intelligence += 1;
		max_hit_point = 50 + (vitality * 10);
		hit_point = max_hit_point;
		maxMana = 20 + (intelligence * 5);
		mana = maxMana;
		critChance = 0.05 + (dexterity * 0.005);
		System.out.println("LEVEL UP! Player is now level " + level + "!");
		System.out.println("HP: " + hit_point + "/" + max_hit_point + " MP: " + mana + "/" + maxMana +
				" Str: " + strength + " Dex: " + dexterity + " Vit: " + vitality + " Int: " + intelligence);
		if(gp != null) gp.playSE(2);
	}

	public boolean canCastHeal(int manaCost) {
		return mana >= manaCost;
	}

	public void castHeal(int manaCost, int healAmountBase) {
		if (canCastHeal(manaCost)) {
			mana -= manaCost;
			int actualHeal = healAmountBase + intelligence * 2;
			hit_point += actualHeal;
			if (hit_point > max_hit_point) {
				hit_point = max_hit_point;
			}
			System.out.println("Player casts Heal! Restored " + actualHeal + " HP. Mana left: " + mana);

		} else {
			System.out.println("Not enough mana to cast Heal!");

		}
	}

	public boolean canCastStrongAttack(int manaCost) {
		return mana >= manaCost;
	}

	public void consumeManaForStrongAttack(int manaCost) {
		if (canCastStrongAttack(manaCost)) {
			mana -= manaCost;
			System.out.println("Player prepares Strong Attack! Mana left: " + mana);
		}
	}

	public void getPlayerImage() {
		try {
			runningUp = new BufferedImage[4];
			runningUp[0] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			runningUp[1] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			runningUp[2] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			runningUp[3] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			runningDown = new BufferedImage[4];
			runningDown[0] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			runningDown[1] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			runningDown[2] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			runningDown[3] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			runningLeft = new BufferedImage[4];
			runningLeft[0] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			runningLeft[1] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			runningLeft[2] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			runningLeft[3] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			runningRight = new BufferedImage[4];
			runningRight[0] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			runningRight[1] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			runningRight[2] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			runningRight[3] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			standingUp = new BufferedImage[2];
			standingUp[0] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			standingUp[1] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			standingDown = new BufferedImage[2];
			standingDown[0] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			standingDown[1] = ImageIO.read(getClass().getResourceAsStream("/res/hero/hero.png"));
			standingLeft = new BufferedImage[6];
			standingLeft[0] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			standingLeft[1] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			standingLeft[2] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			standingLeft[3] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			standingLeft[4] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			standingLeft[5] = ImageIO.read(getClass().getResourceAsStream("/res/hero/herol.png"));
			standingRight = new BufferedImage[6];
			standingRight[0] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			standingRight[1] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			standingRight[2] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			standingRight[3] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			standingRight[4] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
			standingRight[5] = ImageIO.read(getClass().getResourceAsStream("/res/hero/heror.png"));
		}catch(IOException e) {
			e.printStackTrace();
		}catch(IllegalArgumentException e) {
			System.err.println("Error loading player images: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void update() {
		if(gp == null) return;
		if(movingCounter == 0) {
			moving = false;
		}
		if(forcedMove) {
			if (!moving) {
				moving = true;
				movingCounter = 0;
				spriteNum = 0;
			}
			forcedMove = false;
		}
		if(!moving) {
			if((gp.keyH.upPressed || gp.keyH.downPressed
					|| gp.keyH.leftPressed || gp.keyH.rightPressed) && movingState == 0 && gameState == MOVE) {
				if(gp.keyH.upPressed) direction = UP;
				else if(gp.keyH.downPressed) direction = DOWN;
				else if(gp.keyH.rightPressed) direction = RIGHT;
				else if(gp.keyH.leftPressed) direction = LEFT;
				gp.cChecker.checkTile();
				if(!collisionOn) {
					if(gp != null) gp.playSE(2);
					moving = true;
					movingCounter = 0;
					spriteNum = 0;
				}
			}
		}
		animationTick++;
		if(moving) {
			switch(direction) {
				case UP: y -= speed; break;
				case DOWN: y += speed; break;
				case RIGHT: x += speed; break;
				case LEFT: x -= speed; break;
			}
			movingCounter += speed;
			if(movingCounter >= gp.tileSize) {
				movingCounter = 0;
				steps++;
				gp.cChecker.checkVillages();
				gp.cChecker.checkMines();
				if(movingState == 0 && gameState == BATTLE)
					movingState = 1;
			}
			if (animationTick >= ANIMATION_SPEED_WALK) {
				spriteNum = (spriteNum + 1) % 4;
				animationTick = 0;
			}
		} else {
			if (animationTick >= ANIMATION_SPEED_IDLE) {
				if (direction.equals(LEFT) || direction.equals(RIGHT)) {
					spriteNum = (spriteNum + 1) % 6;
				} else {
					spriteNum = (spriteNum + 1) % 2;
				}
				animationTick = 0;
			}
		}
		if (hit_point > max_hit_point) {
			hit_point = max_hit_point;
		}
		if (mana > maxMana) {
			mana = maxMana;
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		BufferedImage image = null;
		int currentFrame = spriteNum;
		if(moving) {
			currentFrame = spriteNum % 4;
			if(facing) {
				switch(direction) {
					case UP:    image = (runningUp != null && runningUp.length > currentFrame) ? runningUp[currentFrame] : null;    break;
					case DOWN:  image = (runningDown != null && runningDown.length > currentFrame) ? runningDown[currentFrame] : null;  break;
					case LEFT:  image = (runningLeft != null && runningLeft.length > currentFrame) ? runningLeft[currentFrame] : null;  break;
					case RIGHT: image = (runningRight != null && runningRight.length > currentFrame) ? runningRight[currentFrame] : null; break;
				}
			} else {
				switch(direction) {
					case DOWN:  image = (runningUp != null && runningUp.length > currentFrame) ? runningUp[currentFrame] : null;    break;
					case UP:    image = (runningDown != null && runningDown.length > currentFrame) ? runningDown[currentFrame] : null;  break;
					case RIGHT: image = (runningLeft != null && runningLeft.length > currentFrame) ? runningLeft[currentFrame] : null;  break;
					case LEFT:  image = (runningRight != null && runningRight.length > currentFrame) ? runningRight[currentFrame] : null; break;
				}
			}
		} else { // Стояние
			if(facing) {
				switch(direction) {
					case UP:    currentFrame = spriteNum % 2; image = (standingUp != null && standingUp.length > currentFrame) ? standingUp[currentFrame] : null;    break;
					case DOWN:  currentFrame = spriteNum % 2; image = (standingDown != null && standingDown.length > currentFrame) ? standingDown[currentFrame] : null;  break;
					case LEFT:  currentFrame = spriteNum % 6; image = (standingLeft != null && standingLeft.length > currentFrame) ? standingLeft[currentFrame] : null;  break;
					case RIGHT: currentFrame = spriteNum % 6; image = (standingRight != null && standingRight.length > currentFrame) ? standingRight[currentFrame] : null; break;
				}
			} else { // facing == false
				switch(direction) {
					case DOWN:  currentFrame = spriteNum % 2; image = (standingUp != null && standingUp.length > currentFrame) ? standingUp[currentFrame] : null;    break;
					case UP:    currentFrame = spriteNum % 2; image = (standingDown != null && standingDown.length > currentFrame) ? standingDown[currentFrame] : null;  break;
					case RIGHT: currentFrame = spriteNum % 6; image = (standingLeft != null && standingLeft.length > currentFrame) ? standingLeft[currentFrame] : null;  break;
					case LEFT:  currentFrame = spriteNum % 6; image = (standingRight != null && standingRight.length > currentFrame) ? standingRight[currentFrame] : null; break;
				}
			}
		}
		if (image != null) {
			g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
		} else {

		}
	}

	public void changeFacing() {
		facing = !facing;
		switch (direction){
			case UP:    direction = Entity.DOWN;  break;
			case DOWN:  direction = Entity.UP;    break;
			case LEFT:  direction = Entity.RIGHT; break;
			case RIGHT: direction = Entity.LEFT;  break;
		}
		spriteNum = 0;
		animationTick = 0;
	}

	public void moveUp() {

	}
	private List<HealthObserver> observers = new ArrayList<>();

	public void addObserver(HealthObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(HealthObserver observer) {
		observers.remove(observer);
	}

	private void notifyHealthChanged() {
		for (HealthObserver o : observers) {
			o.onHealthChanged(hit_point);
		}
	}

	public void setHitPoint(int hp) {
		this.hit_point = hp;
		notifyHealthChanged();
	}

}