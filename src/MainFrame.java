import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import javax.swing.JProgressBar;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.SpringLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	private ArrayList<JTextField> inputFields;
	private JTextField rangeFromField;
	private JTextField rangeValueField;
	private JTextField rangeToField;
	private GraphPanel graphPanel;
	private JComboBox<Color> colorBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
					frame.setTitle("GraphBuilder [nonme]");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		initVariables();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuFile.setMinimumSize(new Dimension(50,20));
		menuBar.add(menuFile);
		
		JMenuItem menuSave = new JMenuItem("Save");
		menuSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.this.saveImage();
			}
		});
		menuFile.add(menuSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as");
		mntmSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogTitle("Saving graph");
				int r = fileChooser.showSaveDialog(MainFrame.this);
				if(r == JFileChooser.APPROVE_OPTION) {
					MainFrame.this.saveImage(fileChooser.getSelectedFile().getAbsolutePath());
					JOptionPane.showMessageDialog(MainFrame.this,
							"Graph saved as " + fileChooser.getSelectedFile() + ".");
				}
			}
		});
		menuFile.add(mntmSaveAs);
		
		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);
		
		JMenuItem mntmHowToUse = new JMenuItem("How to use");
		menuHelp.add(mntmHowToUse);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		menuHelp.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		//Set up the main panel of the frame
		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setDividerLocation(0.2);
		mainSplitPane.setResizeWeight(0.2);		
		contentPane.add(mainSplitPane);
		//Add graphPanel to right and another split panel to left
		graphPanel = new GraphPanel();
		mainSplitPane.setRightComponent(graphPanel);
		
		JSplitPane subSplitPane = new JSplitPane();
		subSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		subSplitPane.setDividerLocation(0.7);
		subSplitPane.setResizeWeight(0.7);
		mainSplitPane.setLeftComponent(subSplitPane);
		
		//Add scrollPane to subSplitPanel and JPanel with buttons
		JScrollPane scrollPane = new JScrollPane();
		JPanel inputFieldsPanel = new JPanel();
		inputFieldsPanel.setLayout(new GridLayout(6, 0, 0, 0));
		scrollPane.setViewportView(inputFieldsPanel);
		subSplitPane.setLeftComponent(scrollPane);
		final Font CAMBRIA_MATH = new Font("Cambria Math", Font.PLAIN, 20);
		for(int i = 0; i < 6; ++i) {
			JTextField inputField = new JTextField();
			inputField.setFont(CAMBRIA_MATH);
			Border border = new CustomBorder(new Color(202, 216, 255), 2);
			Border margin = new EmptyBorder(0, 15, 0, 0);
			inputField.setBorder(new CompoundBorder(border, margin));
			inputField.setMargin(new Insets(0, 15, 0, 0));
			inputFields.add(inputField);
			inputFieldsPanel.add(inputField);
		}
		/*
		String math = "y = cos(x)*x^2";
		TeXFormula formula = new TeXFormula(math);
		TeXIcon ti = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 40);
		BufferedImage b = new BufferedImage(ti.getIconWidth(), ti.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		ti.paintIcon(new JLabel(), b.getGraphics(), 0, 0);
		JPanel formulaPanel = new JPanel();
		JLabel flabel = new JLabel();
		flabel.setIcon(ti);
		formulaPanel.add(flabel);
		subSplitPane.setRightComponent(formulaPanel);
		*/
		JPanel drawButtonsPane = new JPanel();
		subSplitPane.setRightComponent(drawButtonsPane);
		
		GridBagLayout gbl_drawButtonsPane = new GridBagLayout();
		gbl_drawButtonsPane.columnWeights = new double[]{0.0, 0.0, 1.0};
		drawButtonsPane.setLayout(gbl_drawButtonsPane);
		rangeFromField = new JTextField();
		rangeFromField.setFont(new Font("Cambria", Font.PLAIN, 20));
		rangeFromField.setColumns(3);
		rangeFromField.setHorizontalAlignment(JTextField.CENTER);
		GridBagConstraints gbc_rangeFromField = new GridBagConstraints();
		gbc_rangeFromField.insets = new Insets(0, 0, 5, 0);
		gbc_rangeFromField.weightx = 1.0;
		gbc_rangeFromField.weighty = 0.4;
		gbc_rangeFromField.gridx = 0;
		gbc_rangeFromField.gridy = 0;
		drawButtonsPane.add(rangeFromField, gbc_rangeFromField);
		JLabel label = new JLabel("\u2264");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.weightx = 1.0;
		gbc_label.weighty = 0.4;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.fill = GridBagConstraints.HORIZONTAL;
		gbc_label.gridx = 1;
		gbc_label.gridy = 0;
		drawButtonsPane.add(label, gbc_label);
		
		rangeValueField = new JTextField();
		rangeValueField.setFont(new Font("Cambria", Font.PLAIN, 20));
		rangeValueField.setHorizontalAlignment(JTextField.CENTER);
		GridBagConstraints gbc_rangeValueField = new GridBagConstraints();
		gbc_rangeValueField.weighty = 0.4;
		gbc_rangeValueField.insets = new Insets(0, 0, 5, 0);
		gbc_rangeValueField.weightx = 0.4;
		gbc_rangeValueField.gridx = 1;
		gbc_rangeValueField.gridy = 0;
		drawButtonsPane.add(rangeValueField, gbc_rangeValueField);
		rangeValueField.setColumns(3);
		
		JLabel firstLessLabel = new JLabel("\u2264");
		GridBagConstraints gbc_firstLessLabel = new GridBagConstraints();
		gbc_firstLessLabel.weighty = 0.4;
		gbc_firstLessLabel.insets = new Insets(0, 0, 5, 5);
		gbc_firstLessLabel.anchor = GridBagConstraints.EAST;
		gbc_firstLessLabel.weightx = 0.4;
		gbc_firstLessLabel.gridx = 1;
		gbc_firstLessLabel.gridy = 0;
		drawButtonsPane.add(firstLessLabel, gbc_firstLessLabel);
		
		rangeToField = new JTextField();
		rangeToField.setFont(new Font("Cambria", Font.PLAIN, 20));
		rangeToField.setHorizontalAlignment(JTextField.CENTER);
		GridBagConstraints gbc_rangeToField = new GridBagConstraints();
		gbc_rangeToField.weighty = 0.4;
		gbc_rangeToField.insets = new Insets(0, 0, 5, 0);
		gbc_rangeToField.weightx = 1.0;
		gbc_rangeToField.gridx = 2;
		gbc_rangeToField.gridy = 0;
		drawButtonsPane.add(rangeToField, gbc_rangeToField);
		rangeToField.setColumns(3);
		
		JButton btnDraw = new JButton("Draw");
		btnDraw.setMinimumSize(new Dimension(100, 40));
		btnDraw.setPreferredSize(new Dimension(100, 40));
		btnDraw.setMaximumSize(new Dimension(100, 40));
		btnDraw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> equations = new ArrayList<String>();
				for(int i = 0; i < inputFields.size(); ++i) {
					if(!inputFields.get(i).getText().isEmpty())
						equations.add(inputFields.get(i).getText());
				}
				String rangeFrom = rangeFromField.getText(),
						rangeTo = rangeToField.getText();
				rangeFrom = rangeFrom.replaceFirst("PI", "3.1415926");
				rangeTo = rangeTo.replaceFirst("PI", "3.1415926");
				//CHECK FOR ERROR
				double from = new Parser().calculate(rangeFrom),
						to = new Parser().calculate(rangeTo);
				MainFrame.this.graphPanel.setColor((Color)colorBox.getSelectedItem());
				MainFrame.this.graphPanel.setPoints(new Parser().solveEquations(
						rangeValueField.getText().charAt(0),
						0.1,
						from,
						to,
						equations));
			}
		});
		
		JLabel lblFrom = new JLabel("From");
		lblFrom.setFont(new Font("Cambria", Font.PLAIN, 11));
		GridBagConstraints gbc_lblFrom = new GridBagConstraints();
		gbc_lblFrom.insets = new Insets(0, 0, 0, 5);
		gbc_lblFrom.weighty = 0.4;
		gbc_lblFrom.anchor = GridBagConstraints.SOUTH;
		gbc_lblFrom.gridx = 0;
		gbc_lblFrom.gridy = 0;
		drawButtonsPane.add(lblFrom, gbc_lblFrom);
		
		JLabel lblRangingValue = new JLabel("Parameter");
		lblRangingValue.setFont(new Font("Cambria", Font.PLAIN, 11));
		GridBagConstraints gbc_lblRangingValue = new GridBagConstraints();
		gbc_lblRangingValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblRangingValue.weightx = 0.4;
		gbc_lblRangingValue.weighty = 0.4;
		gbc_lblRangingValue.anchor = GridBagConstraints.SOUTH;
		gbc_lblRangingValue.gridx = 1;
		gbc_lblRangingValue.gridy = 0;
		drawButtonsPane.add(lblRangingValue, gbc_lblRangingValue);
		GridBagConstraints gbc_btnDraw = new GridBagConstraints();
		gbc_btnDraw.anchor = GridBagConstraints.EAST;
		gbc_btnDraw.gridwidth = 2;
		gbc_btnDraw.weightx = 0.7;
		gbc_btnDraw.weighty = 0.3;
		gbc_btnDraw.gridx = 0;
		gbc_btnDraw.gridy = 1;
		drawButtonsPane.add(btnDraw, gbc_btnDraw);
		
		JLabel lblNewLabel = new JLabel("To");
		lblNewLabel.setFont(new Font("Cambria", Font.PLAIN, 11));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.SOUTH;
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		drawButtonsPane.add(lblNewLabel, gbc_lblNewLabel);
		
		JButton btnDrawOver = new JButton("Draw over");
		btnDrawOver.setMinimumSize(new Dimension(100, 40));
		btnDrawOver.setPreferredSize(new Dimension(100, 40));
		btnDraw.setMaximumSize(new Dimension(100, 40));
		btnDrawOver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<String> equations = new ArrayList<String>();
				for(int i = 0; i < inputFields.size(); ++i) {
					if(!inputFields.get(i).getText().isEmpty())
						equations.add(inputFields.get(i).getText());
				}
				String rangeFrom = rangeFromField.getText(),
						rangeTo = rangeToField.getText();
				rangeFrom = rangeFrom.replaceFirst("PI", "3.1415926");
				rangeTo = rangeTo.replaceFirst("PI", "3.1415926");
				//CHECK FOR ERROR
				double from = new Parser().calculate(rangeFrom),
						to = new Parser().calculate(rangeTo);
				MainFrame.this.graphPanel.setColor((Color)colorBox.getSelectedItem());
				MainFrame.this.graphPanel.addPoints(new Parser().solveEquations(
						rangeValueField.getText().charAt(0),
						0.1,
						from,
						to,
						equations));
			}
		});
		GridBagConstraints gbc_btnDrawOver = new GridBagConstraints();
		gbc_btnDrawOver.anchor = GridBagConstraints.EAST;
		gbc_btnDrawOver.gridwidth = 2;
		gbc_btnDrawOver.weighty = 0.3;
		gbc_btnDrawOver.gridx = 0;
		gbc_btnDrawOver.gridy = 2;
		drawButtonsPane.add(btnDrawOver, gbc_btnDrawOver);
		
		Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW,
						Color.GREEN, Color.BLUE, Color.MAGENTA,
						Color.BLACK };
		colorBox = new JComboBox(colors);
		colorBox.setRenderer(new ColorBoxRenderer());
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.weightx = 0.3;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 1;
		drawButtonsPane.add(colorBox, gbc_comboBox);
		
		/*
		JProgressBar progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setMinimumSize(new Dimension(170, 20));
		progressBar.setPreferredSize(new Dimension(170, 20));
		progressBar.setMaximumSize(new Dimension(170, 20));

		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.anchor = GridBagConstraints.PAGE_END;
		gbc_progressBar.gridwidth = 5;
		gbc_progressBar.weightx = 1.0;
		gbc_progressBar.weighty = 0.2;
		//gbc_progressBar.anchor = GridBagConstraints.WEST;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 2;
		drawButtonsPane.add(progressBar, gbc_progressBar);
		*/
	}
	protected void saveImage() {
		BufferedImage buffImage = new BufferedImage(
				graphPanel.getWidth(), graphPanel.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		graphPanel.paint(buffImage.createGraphics());
		try {
			//imageBuff = new Robot().createScreenCapture(graphPanel.getBounds());
			ImageIO.write(buffImage, "png", new File("graph.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected void saveImage(String path) {
		if(!path.contains(".png") || !path.contains(".jpg"))
			path+=".png";
		BufferedImage buffImage = new BufferedImage(
				graphPanel.getWidth(), graphPanel.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		graphPanel.paint(buffImage.createGraphics());
		try {
			//imageBuff = new Robot().createScreenCapture(graphPanel.getBounds());
			ImageIO.write(buffImage, "png", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initVariables() {
		inputFields = new ArrayList<JTextField>();
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

}
