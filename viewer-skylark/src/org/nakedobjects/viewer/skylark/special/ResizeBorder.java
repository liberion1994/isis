package org.nakedobjects.viewer.skylark.special;

import org.nakedobjects.viewer.skylark.Bounds;
import org.nakedobjects.viewer.skylark.Canvas;
import org.nakedobjects.viewer.skylark.Color;
import org.nakedobjects.viewer.skylark.InternalDrag;
import org.nakedobjects.viewer.skylark.Location;
import org.nakedobjects.viewer.skylark.Size;
import org.nakedobjects.viewer.skylark.Style;
import org.nakedobjects.viewer.skylark.View;
import org.nakedobjects.viewer.skylark.ViewAreaType;
import org.nakedobjects.viewer.skylark.core.AbstractBorder;

import org.apache.log4j.Logger;


public class ResizeBorder extends AbstractBorder {
    private static final int BORDER_WIDTH = 5;
    private Size resize = new Size(200, 100);
    private int direction;

    public ResizeBorder(View view) {
        super(view);
        bottom = right = BORDER_WIDTH;
    }

    protected void debugDetails(StringBuffer b) {
        super.debugDetails(b);
        b.append("\n           resized to " + resize);
    }

    public void draw(Canvas canvas) {
        Size size = getSize();
        int width = size.getWidth();
        int height = size.getHeight();
        Color color = isOnBorder() && getState().isViewIdentified() ? Style.INVALID : Style.PRIMARY3;
        for (int i = 0; i < BORDER_WIDTH; i++) {
            int y = height - BORDER_WIDTH + i;
            canvas.drawLine(0, y, width - 1, y, color);
            int x = width - BORDER_WIDTH + i;
            canvas.drawLine(x, 0, x, height - 1, color);
        }

        canvas.setClip(0,0, width - BORDER_WIDTH, height - BORDER_WIDTH);
       // super.draw(canvas);
        wrappedView.draw(canvas);
    }

    public ViewAreaType viewAreaType(Location mouseLocation) {
        if(isOnBorder()) {
            return ViewAreaType.INTERNAL;
        }
        return super.viewAreaType(mouseLocation);
    }

    public View dragFrom(InternalDrag drag) {
        if(isOnBorder()) {
	        direction = onBorder(drag.getLocation());
	        if (direction > 0) {
	            ViewResizeOutline outline = new ViewResizeOutline(drag, getView(), direction);
	            getViewManager().setOverlayView(outline);
	            outline.markDamaged();
	            return outline;
	        }
	        return null;
        } else {
            return super.dragFrom(drag);
        }
    }

    public void drag(InternalDrag drag) {
        ViewResizeOutline outline = ((ViewResizeOutline) drag.getDragOverlay());
        if(outline != null) {
                   outline.markDamaged();
                   outline.adjust(drag);
        } else {
            super.drag(drag);
        }
    }

    public void dragTo(InternalDrag drag) {
        getViewManager().showDefaultCursor();
        ViewResizeOutline outline = ((ViewResizeOutline) drag.getDragOverlay());
        if(outline != null) {
            getView().setRequiredSize(outline.getSize());
            Logger.getLogger(getClass()).debug("resizing view " + resize);
            invalidateLayout();
        } else {
            super.dragTo(drag);
        }
    }

    public Size getRequiredSize() {
        return new Size(resize);
    }
    
    public void setRequiredSize(Size size) {
        this.resize = size;
    }

    /**
     * Detects wheter the point is on the resize border, and if so changes
     * the cursor to show it can be resized.
     */
    public void mouseMoved(Location at) {
        switch (onBorder(at)) {
        case ViewResizeOutline.RIGHT:
            getViewManager().showResizeRightCursor();
            break;

        case ViewResizeOutline.BOTTOM:
            getViewManager().showResizeDownCursor();
            break;

        case ViewResizeOutline.BOTTOM_RIGHT:
            getViewManager().showResizeDownRightCursor();
            break;

        default:
            getViewManager().showDefaultCursor();
        	break;
        }
	        super.mouseMoved(at);
    }
    
    public void exited() {
        getViewManager().showDefaultCursor();
        super.exited();
    }

    private int onBorder(Location at) {
        Bounds area = contentArea();
 /*       Size size = getSize();
        int width = size.getWidth();
        int height = size.getHeight();
       boolean right = at.getX() >= width - BORDER_WIDTH;
        boolean bottom = at.getY() >= height - BORDER_WIDTH;
        
     */   
        boolean right = at.getX() >= area.getWidth();
        boolean bottom = at.getY() >= area.getHeight();
        
        final int status;
        if(right && bottom) {
            status = ViewResizeOutline.BOTTOM_RIGHT;
        } else if(right) {
            status = ViewResizeOutline.RIGHT;
        } else if(bottom) {
            status = ViewResizeOutline.BOTTOM;
        } else {
            status = 0;
        }
        
        return status;
    }
    

}

/*
 * Naked Objects - a framework that exposes behaviourally complete business
 * objects directly to the user. Copyright (C) 2000 - 2004 Naked Objects Group
 * Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address
 * of Naked Objects Group is Kingsway House, 123 Goldworth Road, Woking GU21
 * 1NR, UK).
 */