package com.nuix.evmanager.controls;

import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/***
 * A see through panel which can be used to sink interactions against underlying controls, providing an easy way to disable a large swatch of controls
 * from accepting user input.
 * @author Jason Wells
 *
 */
@SuppressWarnings("serial")
public class DisablingGlassPaneWrapper extends JLayeredPane{
	private JPanel glassPanel = new JPanel();

	   public DisablingGlassPaneWrapper(JComponent myPanel) {
	      glassPanel.setOpaque(false);
	      glassPanel.setVisible(false);
	      glassPanel.addMouseListener(new MouseAdapter() {});
	      glassPanel.setFocusable(true);

	      myPanel.setSize(myPanel.getPreferredSize());
	      add(myPanel, JLayeredPane.DEFAULT_LAYER);
	      add(glassPanel, JLayeredPane.PALETTE_LAYER);

	      glassPanel.setPreferredSize(myPanel.getPreferredSize());
	      glassPanel.setSize(myPanel.getPreferredSize());
	      setPreferredSize(myPanel.getPreferredSize());
	   }

	   public void activateGlassPane(boolean activate) {
	      glassPanel.setVisible(activate);
	      if (activate) {
	         glassPanel.requestFocusInWindow();
	         glassPanel.setFocusTraversalKeysEnabled(false);
	      } 
	   }
}
