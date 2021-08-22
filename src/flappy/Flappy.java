package flappy;

import java.awt.Image;

public class Flappy {
	int x, y, w,h;
	Image i;
	double g = 0.2;
	double v;
	public Flappy(Image i,int x,int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.i = i;
		
	}
	
}
