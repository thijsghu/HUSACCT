package husacct.graphics.task.layout;

import husacct.graphics.presentation.Drawing;
import husacct.graphics.presentation.figures.BaseFigure;
import husacct.graphics.presentation.figures.RelationFigure;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;

public class BasicLayoutStrategy implements LayoutStrategy {

	private static final double VERT_ITEM_SPACING = 40.0;
	private static final double HORZ_ITEM_SPACING = 35.0;

	private Drawing drawing = null;

	public BasicLayoutStrategy(Drawing theDrawing) {

		drawing = theDrawing;
	}

	public void doLayout(int screenWidth, int screenHeight) {
		//System.out.println("Width: " + width + "; Height: " + height);
		double x = HORZ_ITEM_SPACING, y = VERT_ITEM_SPACING;
		double maxHeightOnLine = 0.0;
		int figuresOnLine = 0;

		ArrayList<Figure> figures = new ArrayList<Figure>();
		ArrayList<Figure> connectors = new ArrayList<Figure>();
		figures.addAll(drawing.getChildren());

		for (Figure f : figures) {
			if (!isConnector(f)) {
				
				Rectangle2D.Double bounds = f.getBounds();
	
				if (x + bounds.width > screenWidth && figuresOnLine > 0) {
					x = HORZ_ITEM_SPACING;
					y += maxHeightOnLine + VERT_ITEM_SPACING;
					figuresOnLine = 0;
					maxHeightOnLine = 0;
				}
				
				bounds.x = x;
				bounds.y = y;
				Point2D.Double anchor = new Point2D.Double(bounds.x, bounds.y);
				Point2D.Double lead = new Point2D.Double(bounds.x + bounds.width, bounds.y + bounds.height);
			
				f.willChange();
				f.setBounds(anchor, lead);
				f.changed();

				x += bounds.width + HORZ_ITEM_SPACING;
				maxHeightOnLine = Math.max(maxHeightOnLine, bounds.height);
				figuresOnLine++;
			} else {
				connectors.add(f);
			}
		}		
	}
	
	// TODO: Update doLayout() to take screen resolution into account and
	// attempt to make it all fit
	// on the screen without the use of scrollbars.
//	public void doLayout(int maxHorizontalItems) {
//		double x = HORZ_ITEM_SPACING, y = VERT_ITEM_SPACING;
//		double maxHeightOnLine = 0.0;
//		int itemsOnCurrentLine = 0;
//
//		ArrayList<Figure> figures = new ArrayList<Figure>();
//		ArrayList<Figure> connectors = new ArrayList<Figure>();
//		figures.addAll(drawing.getChildren());
//
//		for (Figure f : figures) {
//			Rectangle2D.Double bounds = f.getBounds();
//
//			bounds.x = x;
//			bounds.y = y;
//			Point2D.Double anchor = new Point2D.Double(bounds.x, bounds.y);
//			Point2D.Double lead = new Point2D.Double(bounds.x + bounds.width, bounds.y + bounds.height);
//
//			if (!isConnector(f)) {
//				f.willChange();
//				f.setBounds(anchor, lead);
//				f.changed();
//
//				itemsOnCurrentLine++;
//
//				x += bounds.width + HORZ_ITEM_SPACING;
//				maxHeightOnLine = Math.max(maxHeightOnLine, bounds.height);
//			} else {
//				connectors.add(f);
//			}
//
//			if (itemsOnCurrentLine >= maxHorizontalItems) {
//
//				y = y + maxHeightOnLine + VERT_ITEM_SPACING;
//				x = HORZ_ITEM_SPACING;
//				itemsOnCurrentLine = 0;
//			}
//		}
//	}

	// TODO: Patrick: I'm not quite sure if this code should be here. We really
	// need to discuss this kind of
	// code. It's ugly and unneccessary I think.
	private boolean isConnector(Figure figure) {
		if (figure instanceof BaseFigure) {
			if (figure instanceof RelationFigure) // TODO Call the isLine method?
				return true;
		} else if (figure instanceof ConnectionFigure) { //TODO extend it and implement the isLine method?
			return true;
		}
		return false;
	}
}