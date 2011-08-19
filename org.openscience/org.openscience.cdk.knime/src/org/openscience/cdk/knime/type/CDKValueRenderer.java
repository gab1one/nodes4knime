/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2011
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * -------------------------------------------------------------------
 *
 */
package org.openscience.cdk.knime.type;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;

import org.knime.core.data.renderer.AbstractPainterDataValueRenderer;
import org.knime.core.node.NodeLogger;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.AtomContainerRenderer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.font.AWTFontManager;
import org.openscience.jchempaint.renderer.generators.BasicAtomGenerator;
import org.openscience.jchempaint.renderer.generators.BasicBondGenerator;
import org.openscience.jchempaint.renderer.visitor.AWTDrawVisitor;

/**
 * Renderer for {@link CDKValue}s. It will use CDK classes to render a 2D
 * structure of a molecule.
 *
 * @author Bernd Wiswedel, University of Konstanz
 * @author Andreas Truszkowski, EMBL-EBI
 * @author Mark Reijnberg, EMBL-EBI
 * @author Christoph Steinbeck, EMBL-EBI
 */
public class CDKValueRenderer extends AbstractPainterDataValueRenderer {

	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(CDKValueRenderer.class);

	private static final AtomContainerRenderer RENDERER;
	private static final double SCALE = 0.9;

	static {
		AtomContainerRenderer renderer = null;
		try {
//			List<IGenerator<IAtomContainer>> generators =
//				new ArrayList<IGenerator<IAtomContainer>>();
//			generators.add(new BasicSceneGenerator());
//			generators.add(new BasicBondGenerator());
//			generators.add(new BasicAtomGenerator());
//	        generators.add(new RingGenerator());
//	        generators.add(new LonePairGenerator());
//	        generators.add(new RadicalGenerator());
//	        generators.add(new ExtendedAtomGenerator());
//	        generators.add(new AtomNumberGenerator());
//	        generators.add(new ExternalHighlightAtomGenerator());
//	        generators.add(new ExternalHighlightBondGenerator());
//	        generators.add(new HighlightAtomGenerator());
//	        generators.add(new HighlightBondGenerator());
//	        generators.add(new SelectAtomGenerator());
//	        generators.add(new SelectBondGenerator());
//	        generators.add(new MergeAtomsGenerator());
//	        generators.add(new PhantomBondGenerator());
//	        generators.add(new BasicSceneGenerator());

			renderer = new AtomContainerRenderer(Arrays.asList(
	        		new BasicBondGenerator(), new BasicAtomGenerator()),
	        		new AWTFontManager(), true);

			RendererModel renderer2dModel = renderer.getRenderer2DModel();
			renderer2dModel.setUseAntiAliasing(true);
			renderer2dModel.setShowExplicitHydrogens(true);
			renderer2dModel.setShowAtomAtomMapping(false);
			renderer2dModel.setShowAtomTypeNames(false);

		} catch (Exception e) {
			LOGGER.error("Error during renderer initialization!", e);
		}
		RENDERER = renderer;

	}

	private IAtomContainer m_mol;

	private static final Font NO_2D_FONT = new Font(Font.SANS_SERIF,
			Font.ITALIC, 12);


	/**
	 * Sets a new object to be rendered.
	 *
	 * @param con
	 *            the new molecule to be rendered (<code>null</code> is ok)
	 */
	protected void setAtomContainer(final IAtomContainer con) {
		m_mol = con;
	}

	/**
	 * Get the currently set molecule for rendering (may be <code>null</code>).
	 *
	 * @return the current molecule
	 */
	protected IAtomContainer getAtomContainer() {
		return m_mol;
	}

	/**
	 * Sets the string object for the cell being rendered.
	 *
	 * @param value
	 *            the string value for this cell; if value is <code>null</code>
	 *            it sets the text value to an empty string
	 * @see javax.swing.JLabel#setText
	 *
	 */
	@Override
	protected void setValue(final Object value) {
		if (value instanceof CDKValue) { // when used directly on CDKCell
			setAtomContainer(((CDKValue) value).getAtomContainer());
			return;
		} else {
			setAtomContainer(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (m_mol == null) {
			g.setFont(NO_2D_FONT);
			g.drawString("?", 2, 14);
			return;
		}
		if (GeometryTools.has2DCoordinatesNew(m_mol) == 0) {
			g.setFont(NO_2D_FONT);
			g.drawString("No 2D coordinates", 2, 14);
			return;
		}
		int x = 0;
		int y = 0;
		int width = getWidth();
		int height = getHeight();

		// not needed with updated JChemPaint version
//		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		GraphicsConfiguration gc =
//			genv.getDefaultScreenDevice().getDefaultConfiguration();
//		BufferedImage image = gc.createCompatibleImage(width, height);
//		Graphics2D g2 = (Graphics2D) image.getGraphics().create();
//		g2.setColor(Color.WHITE);
//		g2.fillRect(0, 0, width, height);
//		if (m_image != null) {
//			try {
//				m_image = drawMolecule(m_mol, getWidth(), getHeight(), 0.9);
//				((Graphics2D)g).drawImage(m_image, 0, 0, null);
//			} catch (Exception e) {
//				m_mol = null;
//			}
//		}
//		g2.dispose();

		Graphics2D g2 = (Graphics2D)g;

		if (SCALE < 1.0) {
			x = (int) ((width * (1.0 - SCALE)) / 2);
			y = (int) ((height * (1.0 - SCALE)) / 2);
			width = (int) (width * SCALE);
			height = (int) (height * SCALE);
		}
//		try {
			Dimension aPrefferedSize = new Dimension(width, height);
			IAtomContainer cont = m_mol;
//			if (!ConnectivityChecker.isConnected(m_mol)) {
//				IMoleculeSet fragments = ConnectivityChecker.partitionIntoMolecules(m_mol);
//				int biggest = 0;
//				for (int i = 1; i < fragments.getAtomContainerCount(); i++) {
//					if (fragments.getAtomContainer(i).getAtomCount() >
//						fragments.getAtomContainer(biggest).getAtomCount()) {
//						biggest = i;
//					}
//				}
//				cont = fragments.getAtomContainer(biggest);
				// TODO render rest too
//			}
			GeometryTools.translateAllPositive(cont);
			GeometryTools.scaleMolecule(cont, aPrefferedSize, 0.8f);
			GeometryTools.center(cont, aPrefferedSize);
			RENDERER.paintMolecule(cont, new AWTDrawVisitor(g2),
					new Rectangle(x, y, width, height), true);
//		} catch (Exception e) {
//			LOGGER.warn("Error rendering molecule!", e);
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return "CDK Molecule";
	}

	/**
	 * @return new pref dimension object
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 100);
	}
}