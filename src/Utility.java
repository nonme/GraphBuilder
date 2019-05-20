import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.event.EventListenerList;

class Tuple<X,Y> {
	public X first;
	public Y second;
	public Tuple(X x, Y y) {
		this.first = x;
		this.second = y;
	}
}
class Point {
	public double x,y;
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
@SuppressWarnings("serial")
class SyntaxError extends Exception {
	public SyntaxError(String msg) {
		super(msg);
	}
}
@SuppressWarnings("serial")
class CustomBorder extends LineBorder {
	public CustomBorder(Color arg0, int arg1) {
		super(arg0, arg1);
	}
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) g; 
		g2d.setStroke(new BasicStroke(thickness));
		g2d.setColor(lineColor);
		g2d.drawLine(x, y, x+width, y);
		g2d.drawLine(x, y+height, x+width, y+height);
	}
}
class ColorBoxRenderer extends JPanel implements ListCellRenderer {
	public ColorBoxRenderer() {
		setOpaque(true);
	}
	boolean background = false;
	@Override
	public void setBackground(Color bg) {
		if(!background)
			return;
		super.setBackground(bg);
	}
	@Override
	public Component getListCellRendererComponent(JList arg0, Object arg1,
			int arg2, boolean isSelected, boolean hasFocus) {
		background = true;
		setBackground((Color) arg1);
		setPreferredSize(new Dimension(20,20));
		background = false;
		return this;
	}
}