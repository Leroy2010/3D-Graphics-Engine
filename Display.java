package renderer;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
/*
 * 
 * !!! com.renderer !!!
 * 
 */

@SuppressWarnings("resource")

public class Display extends Canvas implements Runnable {
	
	private static final long serialVersionUID = 1L;
	
	private Thread thread;
	private JFrame frame;
	private static String title = "3D Graphics Engine";
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	private static boolean running = false;
	
	private Tetrahedron tetra;
	private int rotationCase = 0;
	
	public Display() {
		
		this.frame = new JFrame();
		
		Dimension size = new Dimension(WIDTH, HEIGHT);
		this.setPreferredSize(size);
		
	}
	
	public static void main(String[] args) {
		
		Display display = new Display();
		display.frame.setTitle(title);
		display.frame.add(display);
		display.frame.pack();
		display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		display.frame.setLocationRelativeTo(null);
		display.frame.setResizable(false);
		display.frame.setVisible(true);
		
		display.start();
		
	}
	
	public synchronized void start() {
		
		running = true;
		this.thread = new Thread(this, "Display");
		this.thread.start();
		
	}
	
	public synchronized void stop() {
		
		running = false;
		
		try {
			
			this.thread.join();
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			
		}
		
	}

	@Override
	public void run() {
		
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60;
		double delta = 0;
		int frames = 0;
		
		init();
		
		while (running) {
			
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			while (delta >= 1) {
				
				update();
				delta--;
				render();
				frames++;
				
			}
			
			if (System.currentTimeMillis() - timer > 1000) {
				
				timer += 1000;
				this.frame.setTitle(title + " | " + frames + " fps");
				frames = 0;
				
			}
			
		}
		
		stop();
		
	}
	
	private void init() {
		
		Scanner in = new Scanner(System.in);
		System.out.println("which shape (1 = octagonal prism)(2 = simple diamond)(3 = square-based pyramid)(4 = square)(5 = house) : ");
		
		switch (in.nextInt()) {
		
		case 1 :
			octagonalPrism instance1 = new octagonalPrism();
			this.tetra = instance1.instance;
			rotationCase = 1;
			break;
		
		case 2 :
			simpleDiamond instance2 = new simpleDiamond();
			this.tetra = instance2.instance;
			rotationCase = 2;
			break;
		
		case 3 :
			squarePyramid instance3 = new squarePyramid();
			this.tetra = instance3.instance;
			rotationCase = 3;
			break;
		
		case 4 :
			square instance4 = new square();
			this.tetra = instance4.instance;
			rotationCase = 4;
			break;
		
		case 5 : 
			house instance5 = new house();
			this.tetra = instance5.instance;
			this.tetra.rotate(true, 180, 0, 0);
			rotationCase = 5;
			break;
		
		default :
			initialError();
		
		}
		
	}
	
	private void render() {
		
		BufferStrategy bs = this.getBufferStrategy();
		
		if (bs == null) {
			
			this.createBufferStrategy(3);
			return;
			
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.WHITE);
		g.fillRect(WIDTH / 2, HEIGHT / 2, 10, 10);
		
		tetra.render(g);
		
		g.dispose();
		bs.show();
		
	}
	
	private void update() {
		
		switch (rotationCase) {
		
		case 1 :
			this.tetra.rotate(true, 1, 1, 1);
			break;
		
		case 2 :
			this.tetra.rotate(true, 1, 1, 1);
			break;
		
		case 3 :
			this.tetra.rotate(true, 1, 1, 1);
			break;
		
		case 4 :
			this.tetra.rotate(true, 1, 1, 1);
			break;
		
		case 5 :
			this.tetra.rotate(true, 0, 0, 1);
			break;
		
		default : 
			initialError();
		
		}
		
	}
	
	private void initialError() {
		
		System.out.println("ERROR : CLOSING PROGRAM");
		System.exit(0);
		
	}

}

/*
 * 
 * !!! com.renderer.point
 * 
 */

class MyPoint {
	
	public double x, y, z;
	
	public MyPoint(double x, double y, double z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
}

class PointConverter {
	
	private static double scale = 1;
	
	public static Point convertPoint(MyPoint point3D) {
		
		double x3d = point3D.y * scale;
		double y3d = point3D.z * scale;
		double depth = point3D.x * scale;
		double[] newVal = scale(x3d, y3d, depth);
		int x2d = (int) (Display.WIDTH / 2 + newVal[0]);
		int y2d = (int) (Display.HEIGHT / 2 + newVal[1]);
		
		Point point2D = new Point(x2d, y2d);
		return point2D;
		
	}
	
	private static double[] scale(double x3d, double y3d, double depth) {
		
		double dist = Math.sqrt(x3d*x3d + y3d*y3d);
		double theta = Math.atan2(y3d, x3d);
		double depth2 = 15 - depth;
		double localScale = Math.abs(1400 / (depth2 + 1400));
		dist *= localScale;
		double[] newVal = new double[2];
		newVal[0] = dist * Math.cos(theta);
		newVal[1] = dist * Math.sin(theta);
		return newVal;
		
	}
	
	public static void rotateAxisX(MyPoint p, boolean CW, double degrees) {
		
		double radius = Math.sqrt(p.y*p.y + p.z*p.z);
		double theta = Math.atan2(p.z, p.y);
		theta += 2*Math.PI / 360 * degrees * (CW?-1:1);
		p.y = radius * Math.cos(theta);
		p.z = radius * Math.sin(theta);
		
	}
	
	public static void rotateAxisY(MyPoint p, boolean CW, double degrees) {
		
		double radius = Math.sqrt(p.x*p.x + p.z*p.z);
		double theta = Math.atan2(p.x, p.z);
		theta += 2*Math.PI / 360 * degrees * (CW?1:-1);
		p.x = radius * Math.sin(theta);
		p.z = radius * Math.cos(theta);
		
	}

	public static void rotateAxisZ(MyPoint p, boolean CW, double degrees) {
		
		double radius = Math.sqrt(p.x*p.x + p.y*p.y);
		double theta = Math.atan2(p.y, p.x);
		theta += 2*Math.PI / 360 * degrees * (CW?-1:1);
		p.y = radius * Math.sin(theta);
		p.x = radius * Math.cos(theta);
		
	}
	
}

/*
 *
 * !!! com.renderer.shapes !!!
 *
 */

class MyPolygon {
	
	private Color color;
	private MyPoint[] points;
	
	public MyPolygon(Color color, MyPoint... points) {
		
		this.points = new MyPoint[points.length];
		this.color = color;
		
		for (int x = 0; x < points.length; x++) {
			
			MyPoint p = points[x];
			this.points[x] = new MyPoint(p.x, p.y, p.z);
			
		}
		
	}
	
	public MyPolygon(MyPoint... points) {
		
		this.points = new MyPoint[points.length];
		this.color = Color.WHITE;
		
		for (int x = 0; x < points.length; x++) {
			
			MyPoint p = points[x];
			this.points[x] = new MyPoint(p.x, p.y, p.z);
			
		}
		
	}
	
	public void render(Graphics g) {
		
		Polygon poly = new Polygon();
		
		for (int x = 0; x < points.length; x++) {
			
			Point p = PointConverter.convertPoint(points[x]);
			poly.addPoint(p.x, p.y);
			
		}
		
		g.setColor(this.color);
		g.fillPolygon(poly);
		
	}
	
	public void rotate(boolean CW, double xDegrees, double yDegrees, double zDegrees) {
		
		for (MyPoint p : points) {
			
			PointConverter.rotateAxisX(p, CW, xDegrees);
			PointConverter.rotateAxisY(p, CW, yDegrees);
			PointConverter.rotateAxisZ(p, CW, zDegrees);
			
		}
		
	}
	
	public double getAverageX() {
		
		double sum = 0;
		
		for (MyPoint p : this.points) {
			
			sum += p.x;
			
		}
		
		return sum / this.points.length;
		
	}
	
	public void setColor(Color color) {
		
		this.color = color;
		
	}
	
	public static MyPolygon[] sortPolygons(MyPolygon[] polys) {
		
		List<MyPolygon> polyList = new ArrayList<MyPolygon>();
		
		for (MyPolygon poly : polys) {
			
			polyList.add(poly);
			
		}
		
		Collections.sort(polyList, new Comparator<MyPolygon>() {

			@Override
			public int compare(MyPolygon p1, MyPolygon p2) {
				
				return p2.getAverageX() - p1.getAverageX() < 0 ? 1 : -1;
				
			}
			
		});
		
		for (int i = 0; i < polys.length; i++) {
			
			polys[i] = polyList.get(i);
			
		}
		
		return polys;
		
	}
	
}

@SuppressWarnings("unused")
class Tetrahedron {
	
	private MyPolygon[] polygons;
	private Color color;
	
	public Tetrahedron(Color color, MyPolygon... polygons) {
		
		this.color = color;
		this.polygons = polygons;
		
	}
	
	public Tetrahedron(MyPolygon... polygons) {
		
		this.color = Color.WHITE;
		this.polygons = polygons;
		
	}

	public void render (Graphics g) {
		
		for (MyPolygon poly : this.polygons) {
			
			poly.render(g);
			
		}
		
	}
	
	public void rotate(boolean CW, double xDegrees, double yDegrees, double zDegrees) {
		
		for (MyPolygon p : this.polygons) {
			
			p.rotate(CW, xDegrees, yDegrees, zDegrees);
			
		}
		
		this.sortPolygons();
		
	}
	
	private void sortPolygons() {
		
		MyPolygon.sortPolygons(this.polygons);
		
	}
	
}

/*
 *
 * !!! com.renderer.entities
 *
 */

class house {
	
	public static int s = 100;
	public static MyPoint p1 = new MyPoint(s, -s, s);         //corner-point
	public static MyPoint p2 = new MyPoint(s, -s, s/2);       //edge-point
	public static MyPoint p3 = new MyPoint(s, -s, -s/2);      //edge-point
	public static MyPoint p4 = new MyPoint(s, -s, -s);        //corner-point
	public static MyPoint p5 = new MyPoint(s, -s/2, -s);      //edge-point
	public static MyPoint p6 = new MyPoint(s, s/2, -s);       //edge-point
	public static MyPoint p7 = new MyPoint(s, s, -s);         //corner-point
	public static MyPoint p8 = new MyPoint(s, s, -s/2);       //edge-point
	public static MyPoint p9 = new MyPoint(s, s, s/2);        //edge-point
	public static MyPoint p10 = new MyPoint(s, s, s);         //corner-point
	public static MyPoint p11 = new MyPoint(s, s/2, s);       //edge-point
	public static MyPoint p12 = new MyPoint(s, -s/2, s);      //edge-point
	public static MyPoint p13 = new MyPoint(-s, -s, s);       //corner-point
	public static MyPoint p14 = new MyPoint(-s, -s, s/2);     //edge-point
	public static MyPoint p15 = new MyPoint(-s, -s, -s/2);    //edge-point
	public static MyPoint p16 = new MyPoint(-s, -s, -s);      //corner-point
	public static MyPoint p17 = new MyPoint(-s, -s/2, -s);    //edge-point
	public static MyPoint p18 = new MyPoint(-s, s/2, -s);     //edge-point
	public static MyPoint p19 = new MyPoint(-s, s, -s);       //corner-point
	public static MyPoint p20 = new MyPoint(-s, s, -s/2);     //edge-point
	public static MyPoint p21 = new MyPoint(-s, s, s/2);      //edge-point
	public static MyPoint p22 = new MyPoint(-s, s, s);        //corner-point
	public static MyPoint p23 = new MyPoint(-s, s/2, s);      //edge-point
	public static MyPoint p24 = new MyPoint(-s, -s/2, s);     //edge-point
	public static MyPoint p25 = new MyPoint(-s/2, -s, -s);    //edge-point
	public static MyPoint p26 = new MyPoint(s/2, -s, -s);     //edge-point
	public static MyPoint p27 = new MyPoint(-s/2, -s, s);     //edge-point
	public static MyPoint p28 = new MyPoint(s/2, -s, s);      //edge-point
	public static MyPoint p29 = new MyPoint(-s/2, s, s);      //edge-point
	public static MyPoint p30 = new MyPoint(s/2, s, s);       //edge-point
	public static MyPoint p31 = new MyPoint(-s/2, s, -s);     //edge-point
	public static MyPoint p32 = new MyPoint(s/2, s, -s);      //edge-point
	public static MyPoint p33 = new MyPoint(s, -s/2, s/2);    //mid-point
	public static MyPoint p34 = new MyPoint(s, s/2, s/2);     //mid-point
	public static MyPoint p35 = new MyPoint(s, -s/2, -s/2);   //mid-point
	public static MyPoint p36 = new MyPoint(s, s/2, -s/2);    //mid-point
	public static MyPoint p37 = new MyPoint(s/2, s, s/2);     //mid-point
	public static MyPoint p38 = new MyPoint(-s/2, s, s/2);    //mid-point
	public static MyPoint p39 = new MyPoint(s/2, s, -s/2);    //mid-point
	public static MyPoint p40 = new MyPoint(-s/2, s, -s/2);   //mid-point
	public static MyPoint p41 = new MyPoint(s/2, -s, s/2);    //mid-point
	public static MyPoint p42 = new MyPoint(-s/2, -s, s/2);   //mid-point
	public static MyPoint p43 = new MyPoint(s/2, -s, -s/2);   //mid-point
	public static MyPoint p44 = new MyPoint(-s/2, -s, -s/2);  //mid-point
	public static MyPoint p45 = new MyPoint(3*s/2, -(3*s/2), 3*s/4);
	public static MyPoint p46 = new MyPoint(-(3*s/2), -(3*s/2), 3*s/4);
	public static MyPoint p47 = new MyPoint(-(3*s/2), 3*s/2, 3*s/4);
	public static MyPoint p48 = new MyPoint(3*s/2, 3*s/2, 3*s/4);
	public static MyPoint p49 = new MyPoint(3*s/2, 0, 2*s);
	public static MyPoint p50 = new MyPoint(-(3*s/2), 0, 2*s);
	
	public Tetrahedron instance = new Tetrahedron(
			
			new MyPolygon(new Color(200, 200, 200), p1, p4, p5, p33, p34, p6, p7, p10),     //front
			new MyPolygon(new Color(50, 50, 50), p5, p33, p34, p6),                         //door
			new MyPolygon(Color.WHITE, p9, p38, p40, p8, p7, p19, p22, p10),                //left-1
			new MyPolygon(Color.WHITE, p9, p37, p39, p8),                                   //left-2
			new MyPolygon(new Color(150, 150, 150), p37, p38, p40, p39),                    //left-window
			new MyPolygon(Color.WHITE, p14, p42, p44, p15, p16, p4, p1, p13),               //right-1
			new MyPolygon(Color.WHITE, p14, p41, p43, p15),                                 //right-2
			new MyPolygon(new Color(150, 150, 150), p41, p42, p44, p43),                    //right-window
			new MyPolygon(Color.WHITE, p1, p10, p22, p13),                                  //roof
			new MyPolygon(Color.WHITE, p22, p13, p16, p19),                                 //back
			new MyPolygon(Color.WHITE, p4, p7, p19, p16),                                   //base
			new MyPolygon(new Color(100, 100, 100), p47, p48, p49, p50),                    //roof
			new MyPolygon(new Color(100, 100, 100), p46, p45, p49, p50),                    //roof
			new MyPolygon(new Color(100, 100, 100), p45, p46, p47, p48),                    //roof
			new MyPolygon(new Color(100, 100, 100), p45, p48, p49),                         //roof
			new MyPolygon(new Color(100, 100, 100), p46, p47, p50)                          //roof
			
	);
	
}

class octagonalPrism {
	
	public static int s = 100;
	public static MyPoint p1 = new MyPoint(s, -s/4, s/2);
	public static MyPoint p2 = new MyPoint(s, s/4, s/2);
	public static MyPoint p3 = new MyPoint(s, s/2, s/4);
	public static MyPoint p4 = new MyPoint(s, s/2, -s/4);
	public static MyPoint p5 = new MyPoint(s, s/4, -s/2);
	public static MyPoint p6 = new MyPoint(s, -s/4, -s/2);
	public static MyPoint p7 = new MyPoint(s, -s/2, -s/4);
	public static MyPoint p8 = new MyPoint(s, -s/2, s/4);
	public static MyPoint p9 = new MyPoint(-s, -s/4, s/2);
	public static MyPoint p10 = new MyPoint(-s, s/4, s/2);
	public static MyPoint p11 = new MyPoint(-s, s/2, s/4);
	public static MyPoint p12 = new MyPoint(-s, s/2, -s/4);
	public static MyPoint p13 = new MyPoint(-s, s/4, -s/2);
	public static MyPoint p14 = new MyPoint(-s, -s/4, -s/2);
	public static MyPoint p15 = new MyPoint(-s, -s/2, -s/4);
	public static MyPoint p16 = new MyPoint(-s, -s/2, s/4);
	
	public Tetrahedron instance = new Tetrahedron(
			
			new MyPolygon(Color.RED, p1, p2, p10, p9),
			new MyPolygon(Color.ORANGE, p2, p3, p11, p10),
			new MyPolygon(Color.YELLOW, p3, p4, p12, p11),
			new MyPolygon(Color.PINK, p4, p5, p13, p12),
			new MyPolygon(Color.GREEN, p5, p6, p14, p13),
			new MyPolygon(Color.CYAN, p6, p7, p15, p14),
			new MyPolygon(Color.BLUE, p7, p8, p16, p15),
			new MyPolygon(Color.MAGENTA, p8, p1, p9, p16),
			new MyPolygon(p1, p2, p3, p4, p5, p6, p7, p8),
			new MyPolygon(p9, p10, p11, p12, p13, p14, p15, p16)
			
	);
	
}

class simpleDiamond {
	
	public static int s = 100;
	public static MyPoint p1 = new MyPoint(0, 0, s);
	public static MyPoint p2 = new MyPoint(-s/2, s/2, 0);
	public static MyPoint p3 = new MyPoint(s/2, s/2, 0);
	public static MyPoint p4 = new MyPoint(s/2, -s/2, 0);
	public static MyPoint p5 = new MyPoint(-s/2, -s/2, 0);
	public static MyPoint p6 = new MyPoint(0, 0, -s);
	
	public Tetrahedron instance = new Tetrahedron(
			
			new MyPolygon(Color.RED, p1, p2, p3),
			new MyPolygon(Color.ORANGE, p1, p3, p4),
			new MyPolygon(Color.YELLOW, p1, p4, p5),
			new MyPolygon(Color.GREEN, p1, p5, p2),
			new MyPolygon(Color.BLUE, p6, p2, p3),
			new MyPolygon(Color.PINK, p6, p3, p4),
			new MyPolygon(Color.GRAY, p6, p4, p5),
			new MyPolygon(Color.WHITE, p6, p5, p2)
			
	);
	
}

class square {
	
	public static int s = 50;
	public static MyPoint p1 = new MyPoint(s, s, s);
	public static MyPoint p2 = new MyPoint(s, -s, s);
	public static MyPoint p3 = new MyPoint(s, -s, -s);
	public static MyPoint p4 = new MyPoint(s, s, -s);
	public static MyPoint p5 = new MyPoint(-s, s, s);
	public static MyPoint p6 = new MyPoint(-s, -s, s);
	public static MyPoint p7 = new MyPoint(-s, -s, -s);
	public static MyPoint p8 = new MyPoint(-s, s, -s);
	
	public Tetrahedron instance = new Tetrahedron(
			
			new MyPolygon(Color.RED, p1, p2, p3, p4),
			new MyPolygon(Color.ORANGE, p5, p6, p7, p8),
			new MyPolygon(Color.GREEN, p1, p4, p8, p5),
			new MyPolygon(Color.WHITE, p3, p4, p8, p7),
			new MyPolygon(Color.BLUE, p2, p3, p7, p6),
			new MyPolygon(Color.YELLOW, p1, p2, p6, p5)
			
	);
	
}

class squarePyramid {
	
	public static int s = 100;
	public static MyPoint p1 = new MyPoint(0, 0, s);
	public static MyPoint p2 = new MyPoint(s/-2, s/2, 0);
	public static MyPoint p3 = new MyPoint(s/2, s/2, 0);
	public static MyPoint p4 = new MyPoint(s/2, s/-2, 0);
	public static MyPoint p5 = new MyPoint(s/-2, s/-2, 0);
	
	public Tetrahedron instance = new Tetrahedron(
			
			new MyPolygon(Color.BLUE, p1, p2, p3),
			new MyPolygon(Color.MAGENTA, p1, p3, p4),
			new MyPolygon(Color.YELLOW, p1, p4, p5),
			new MyPolygon(Color.ORANGE, p1, p5, p2),
			new MyPolygon(Color.RED, p2, p3, p4, p5)
			
	);
	
}
