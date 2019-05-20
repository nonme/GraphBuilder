import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class GraphPanel extends JPanel {
	public ArrayList<Tuple<ArrayList<Point>, Color> > points;
	private BufferedImage paintImage;
	private int left_xrange, right_xrange, down_yrange, up_yrange;
	private Color drawColor;
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		setBackground(Color.WHITE, g2d);
		setAxis(g2d);
		if(points != null) {
			g2d.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(2));
			if(points.size() == 0)
				return;
			for(int i = 0; i < points.size(); ++i) {
				g2d.setColor(points.get(i).second);
				int dp = (points.get(i).first.get(0).x == points.get(i).first.get(1).x ? 2 : 1);
				double coef = 10.d, coefx = adjustTo(this.getWidth()/2, 20),
									coefy = adjustTo(this.getHeight()/2, 20);
				for(int j = dp; j < points.get(i).first.size(); ++j) {
					g2d.drawLine(
							(int) (points.get(i).first.get(j-dp).x*coef+coefx),
							(int) (-1*points.get(i).first.get(j-dp).y*coef+coefy),
							(int) (points.get(i).first.get(j).x*coef+coefx), 
							(int) (-1*points.get(i).first.get(j).y*coef+coefy));
				}
			}
		}
	}
	public void setPoints(Tuple<ArrayList<Point>, String> tuple) {
		points.clear();
		this.points.add(new Tuple(tuple.first, drawColor));
		repaint();
	}
	public void addPoints(Tuple<ArrayList<Point>, String> tuple) {
		this.points.add(new Tuple(tuple.first, drawColor));
		repaint();
	}
	private void setBackground(Color color, Graphics2D g) {
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(202, 216, 255));
		for(int i = 0; i < 100; ++i) {
			g.drawLine(0, i*20, getWidth(), i*20);
			g.drawLine(i*20, 0, i*20, getHeight());
		}
	}
	/**
	 * This method adjust value to be dividable to a specific number
	 * Example: value = 364, number = 10 => 370
	 * @param value - value to be adjusted
	 * @param number
	 */
	private int adjustTo(int value, int number) {
		return value+number-value%number;
	}
	private void setAxis(Graphics2D g) {
		int xmid = adjustTo(getWidth()/2, 20), ymid = adjustTo(getHeight()/2, 20);
		double xstep = 40, ystep = 40;
		g.setColor(Color.GRAY);
		g.drawLine(0, ymid, getWidth(), ymid);
		g.drawLine(xmid, 0, xmid, getHeight());
		for(double i = 0; i <= 10; ++i) {
			g.drawLine((int)(xmid+i*xstep), ymid-5, (int) (xmid+i*xstep), ymid+5);
			g.drawLine((int)(xmid-i*xstep), ymid-5, (int) (xmid-i*xstep), ymid+5);
			g.drawLine(xmid-5, (int) (ymid+i*ystep), xmid+5, (int) (ymid+i*ystep));
			g.drawLine(xmid-5, (int) (ymid-i*ystep), xmid+5, (int) (ymid-i*ystep));
		}
	}
	private void initVariables() {
		left_xrange = -10; right_xrange = 10;
		down_yrange = -10; up_yrange = 10;
		drawColor = Color.black;
	}
	public GraphPanel() {
		initVariables();
		points = new ArrayList<Tuple<ArrayList<Point>, Color> >();
	}
	public void setColor(Color color) {
		this.drawColor = color;
	}
	public void totalRepaint() {
		repaint();
	}
}
