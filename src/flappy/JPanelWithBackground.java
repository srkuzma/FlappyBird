package flappy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;


import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class JPanelWithBackground extends JPanel {
	
	Image flappyImage;
	Image backgroundImage;
	int FLAPPY_WIDTH;
	int FLAPPY_HEIGHT;
	int BOOST = 4;
	int OFFSET = 200;
	int EMPTY_SIZE = 110;
	int PILLAR_WIDTH = 40;
	Flappy flappy = null;
	Timer fps;
	boolean gameOver = false;
	boolean started = false;
	ScoreBoard sb = new ScoreBoard();
	int pos = 0;
	boolean animation = false;
	boolean spaceDisabled = false;
	int opacityChange = 0;
	
	ArrayList<Pillar> pillars = new ArrayList<>();
	
	public JPanelWithBackground(String flp, String bck) {
		flappyImage = new ImageIcon(getClass().getClassLoader().getResource(flp)).getImage();
		backgroundImage = new ImageIcon(getClass().getClassLoader().getResource(bck)).getImage();
		FLAPPY_HEIGHT = flappyImage.getHeight(null);
		FLAPPY_WIDTH = flappyImage.getWidth(null);
		
		
		fps = new Timer(10, (ae) -> {
			if (flappy != null && (!gameOver || (animation && spaceDisabled)) && started) {
				if (!animation) {
					for(Pillar p:pillars) {
						p.x -= 1;
					}
					if (pillars.size()> 0 && flappy.x > pillars.get(pos).x + PILLAR_WIDTH) {
						pos++;
						sb.score++;
						if (sb.score > sb.highScore)
							sb.highScore = sb.score;
					}
					if (pillars.isEmpty() || pillars.get(pillars.size()-1).x < getWidth()) {
						pillars.add(new Pillar(getWidth() + OFFSET, 40 + (int)(Math.random()*(getHeight() - EMPTY_SIZE - 80)), PILLAR_WIDTH, EMPTY_SIZE));
					}
				}
				flappy.v = flappy.v - flappy.g;
				flappy.y = (int)(flappy.y - flappy.v);
				if (flappy.y + FLAPPY_HEIGHT > getHeight()) {
					if (animation) {
						spaceDisabled = false;
					}
					else {
						gameOver = true;
						
					}
					//flappy.y = (int)(flappy.y + flappy.v);
					flappy.y = getHeight() - FLAPPY_HEIGHT;
				}
				else {
					checkCollisions();
				}
				repaint();
			}
		}
		);
		fps.start();
		
		setFocusable(true);
		addListeners();
	}
	
	private void checkCollisions() {
		for(Pillar p:pillars) {
			if (!animation && (p.x < flappy.x + FLAPPY_WIDTH && p.x + PILLAR_WIDTH > flappy.x) && !(flappy.y > p.y && flappy.y + FLAPPY_HEIGHT < p.y + EMPTY_SIZE)) {
				animation = true;
				spaceDisabled = true;
				gameOver = true;
				opacityChange = 5;
			}
		}
	}
	
	private void addListeners() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (spaceDisabled)
					return;
				switch(e.getKeyCode()) {
				case KeyEvent.VK_SPACE:
					if (gameOver) {
						restartGame();
						break;
					}
					started = true;
					flappy.v = BOOST;
					break;
				}
			}
		});
	}
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		
		
		if (flappy == null) {
			flappy = new Flappy(flappyImage, getWidth()/3, getHeight()/2, FLAPPY_WIDTH, FLAPPY_HEIGHT);
		}
		Graphics2D g2D = (Graphics2D) g;
		if (opacityChange > 0) {
			opacityChange--;
			g2D.setComposite(AlphaComposite.getInstance(
	                AlphaComposite.SRC_OVER, (float)0.5));
		}
		g2D.drawImage(backgroundImage, 0, 0, null);
		
		
		for(Pillar p:pillars) {
			g2D.setColor(Color.GREEN);
			g2D.fillRect(p.x, 0, PILLAR_WIDTH, p.y);
			g2D.fillRect(p.x, p.y + EMPTY_SIZE, PILLAR_WIDTH, getHeight() - p.y - EMPTY_SIZE);
		}
		
		if (animation ) {
			//System.out.println(missile.x + " " + missile.y);
			if (FLAPPY_WIDTH > FLAPPY_HEIGHT)
				changeDimensions();
			double locationX = FLAPPY_WIDTH ;
			double locationY = FLAPPY_HEIGHT/ 2;
			AffineTransform tx = new AffineTransform();
			tx.translate(flappy.x + locationX, flappy.y);
			//AffineTransform tx = AffineTransform.getRotateInstance(missile.rotation, locationX, locationY);
			//
			double FACTOR = -0.1;
			
			tx.rotate(Math.toRadians(90));
			/*else {
				double rotation = FACTOR * (flappy.v + 5);
				if (rotation > 0) {
					rotation *= 3;
				}
				rotation = Math.min(Math.toRadians(90), rotation);
				rotation = Math.max(Math.toRadians(-15), rotation);
				System.out.println(rotation);
				tx.rotate(rotation);
			}*/
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			//g2D.drawImage(op.filter((BufferedImage) missileImage, null), missile.x, missile.y, null);
			g2D.drawImage(flappyImage, tx,  null);
		}
		else {
			g2D.drawImage(flappyImage, flappy.x, flappy.y, null);
		}
		
		
		sb.draw(g2D, getWidth(), getHeight(), gameOver);
			
		
	}
	private void changeDimensions() {
		int tmp = FLAPPY_WIDTH;
		FLAPPY_WIDTH = FLAPPY_HEIGHT;
		FLAPPY_HEIGHT = tmp;
	}
	private void restartGame() {
		pillars = new ArrayList<>();
		sb.score = 0;
		pos = 0;
		started = false;
		gameOver = false;
		animation = false;
		spaceDisabled = false;
		flappy = null;
		if (FLAPPY_WIDTH < FLAPPY_HEIGHT)
			changeDimensions();
		repaint();

	}
}
