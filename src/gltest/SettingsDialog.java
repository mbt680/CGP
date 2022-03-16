package gltest;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.GridLayout;
import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.joml.Vector3f;
import gltest.ModelViewer.Settings;

public class SettingsDialog extends JFrame {
	private static final long serialVersionUID = 1L;

	private JCheckBox chkCelShading;
	private JCheckBox chkLighting;
	private JCheckBox chkRimLighting;
	private JCheckBox chkContours;
	private JCheckBox chkSugContours;
	private JTextField materialSourceTxt;
	private FloatTextField xLightPos;
	private FloatTextField yLightPos;
	private FloatTextField zLightPos;
	private IntTextField colorLevelsVertex;
	private IntTextField colorLevelsFragment;

	public void confirmed() {
		if (new File(materialSourceTxt.getText()).exists()) {
			Settings.materialFileLoc = materialSourceTxt.getText();
		}
		Settings.lighting.position = new Vector3f(xLightPos.value, yLightPos.value, zLightPos.value);
		Settings.lighting.vertexLevels = colorLevelsVertex.value;
		Settings.lighting.fragLevels = colorLevelsFragment.value;
	}
		
	public SettingsDialog() {
		super("Model Effects");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		JPanel transformations = new JPanel(new GridBagLayout());
		transformations.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0; gbc.weighty = 1.0; ++gbc.gridy;

		transformations.add(effects, gbc); ++gbc.gridy;
		transformations.add(materialSelection, gbc); ++gbc.gridy;
		transformations.add(lightingEffects, gbc); ++gbc.gridy;
		transformations.add(celShadingEffects, gbc); ++gbc.gridy;

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder( BorderFactory.createLineBorder(Color.gray),BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		panel.add(transformations, BorderLayout.PAGE_START);
		panel.add(endPanel, BorderLayout.PAGE_END);
		
		add(panel);
		pack();
		setVisible(true); 		
	}

	private JPanel effects = new JPanel(new GridLayout(3,2)) { {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		chkCelShading = new JCheckBox("Cel Shading", Settings.hasCelShading);
		chkLighting = new JCheckBox("Diffuse Lighting", Settings.hasLighting);
		chkRimLighting = new JCheckBox("Rim Lighting", Settings.hasRimLighting);
		chkContours = new JCheckBox("Contours", Settings.hasContours);
		chkSugContours = new JCheckBox("Suggestive Contours", Settings.hasSugContours);

		chkCelShading.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { Settings.hasCelShading = chkCelShading.isSelected(); }	
		});
		chkLighting.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { Settings.hasLighting = chkLighting.isSelected(); }	
		});
		chkRimLighting.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { Settings.hasRimLighting = chkRimLighting.isSelected(); }	
		});
		chkContours.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { Settings.hasContours = chkContours.isSelected(); }	
		});
		chkSugContours.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { Settings.hasSugContours = chkSugContours.isSelected(); }	
		});

		add(chkLighting);
		add(chkRimLighting);	
		add(chkCelShading);	
		add(chkContours);		
		add(chkSugContours);
	} };

	private JPanel materialSelection = new JPanel(new FlowLayout(FlowLayout.LEFT)) { {
		setBorder(new CompoundBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createTitledBorder(new TitledBorder("Material"))));

		materialSourceTxt = new JTextField("", 16);
		JButton selectBtn = new JButton("Select");
		selectBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { 
				new JFileChooser() {{
					setCurrentDirectory(new File(materialSourceTxt.getText()));
					setFileFilter(new FileNameExtensionFilter("*.png", "png"));
					if (showOpenDialog(selectBtn.getParent()) == JFileChooser.APPROVE_OPTION) {
						String path = getSelectedFile().getAbsolutePath();
						materialSourceTxt.setText( path );
						Settings.materialFileLoc = path;
					}
				}};
			}
		});

		add(new JLabel("Image: "));
		add(materialSourceTxt);
		add(selectBtn);
	}};	

	private JPanel lightingEffects = new JPanel() { {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(new CompoundBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createTitledBorder(new TitledBorder("Lighting"))));
		xLightPos = new FloatTextField(Settings.lighting.position.x, 4);
		yLightPos = new FloatTextField(Settings.lighting.position.y, 4);
		zLightPos = new FloatTextField(Settings.lighting.position.z, 4);

		add(new JLabel("Position ( "));
		add(xLightPos); 	add(new JLabel(", "));		
		add(yLightPos); 	add(new JLabel(", "));			
		add(zLightPos); 	add(new JLabel(" )"));
	} };

	private JPanel celShadingEffects = new JPanel(new FlowLayout(FlowLayout.LEFT)) { {
		setBorder(new CompoundBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createTitledBorder(new TitledBorder("Cel Shading Levels"))));
		colorLevelsVertex = new IntTextField(Settings.lighting.vertexLevels, 3);
		colorLevelsFragment = new IntTextField(Settings.lighting.fragLevels, 3);

		//add(new JLabel("Vertex Shader: ")); 	add(colorLevelsVertex);
		add(new JLabel("Fragment Shader: "));	add(colorLevelsFragment);
	} };

	private JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING)) { {
		JButton applyBtn = new JButton("Apply");
		applyBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(java.awt.event.ActionEvent e) { confirmed(); }
		});
		add(applyBtn);	
	} };
	
	public class IntTextField extends JTextField {
		private static final long serialVersionUID = 1L;
		int value;
		
		IntTextField(int value, int length) {
			super(String.valueOf(value), length);
			this.value = value;
			setInputVerifier(new IntVerifier());
			setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
		public class IntVerifier extends InputVerifier {
			@Override
			public boolean verify(JComponent input) {
				JTextField tf = (JTextField) input;
				try {
					value = Integer.parseInt(tf.getText());
				} catch (Exception e) {
					tf.setBorder(BorderFactory.createLineBorder(Color.red));
					return false;
				}
				tf.setBorder(BorderFactory.createLineBorder(Color.gray));
				return true;
			}
		}
	}

	public class FloatTextField extends JTextField {
		private static final long serialVersionUID = 1L;
		float value;
		
		FloatTextField(float value, int length) {
			super(String.valueOf(value), length);
			this.value = value;
			setInputVerifier(new FloatVerifier());
			setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
		public class FloatVerifier extends InputVerifier {
			@Override
			public boolean verify(JComponent input) {
				JTextField tf = (JTextField) input;
				try {
					value = Float.parseFloat(tf.getText());
				} catch (Exception e) {
					tf.setBorder(BorderFactory.createLineBorder(Color.red));
					return false;
				}
				tf.setBorder(BorderFactory.createLineBorder(Color.gray));
				return true;
			}
		}
	}
}