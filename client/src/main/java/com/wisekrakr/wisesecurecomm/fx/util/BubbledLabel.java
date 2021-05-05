package com.wisekrakr.wisesecurecomm.fx.util;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class BubbledLabel extends Label{
    /*Copyright {2015} {Terah Laweh}

	   Licensed under the Apache License, Version 2.0 (the "License");
	   you may not use this file except in compliance with the License.
	   You may obtain a copy of the License at

	       http://www.apache.org/licenses/LICENSE-2.0

	   Unless required by applicable law or agreed to in writing, software
	   distributed under the License is distributed on an "AS IS" BASIS,
	   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	   See the License for the specific language governing permissions and
	   limitations under the License.
	*/

    private BubbleSpec bs = BubbleSpec.FACE_LEFT_CENTER;
    private double pading = 5.0;
    private boolean systemCall = false;

    public BubbledLabel() {
        super();

        init();
    }

    public BubbledLabel(String arg0, Node arg1) {
        super(arg0, arg1);
        init();
    }

    public BubbledLabel(String arg0) {
        super(arg0);
        init();
    }

    public BubbledLabel(BubbleSpec bubbleSpec) {
        super();
        this.bs = bubbleSpec;
        init();
    }

    public BubbledLabel(String arg0, Node arg1,BubbleSpec bubbleSpec) {
        super(arg0, arg1);
        this.bs = bubbleSpec;
        init();
    }

    public BubbledLabel(String arg0,BubbleSpec bubbleSpec) {
        super(arg0);
        this.bs = bubbleSpec;
        init();
    }

    private void init(){
//        setTextFill(Color.LIGHTGRAY);

        DropShadow ds = new DropShadow();
        ds.setOffsetX(1.3);
        ds.setOffsetY(1.3);
        ds.setColor(Color.DARKGRAY);
        setPrefSize(Label.USE_COMPUTED_SIZE, Label.USE_COMPUTED_SIZE);
        setWrapText(true);
        shapeProperty().addListener((arg0, arg1, arg2) -> {
            if(systemCall){
                systemCall = false;
            }else{
                shapeIt();
            }
        });

        heightProperty().addListener(arg0 -> {
            if(!systemCall)
                setPrefHeight(Label.USE_COMPUTED_SIZE);
        });

        widthProperty().addListener(observable -> {
            if(!systemCall)
                setPrefHeight(Label.USE_COMPUTED_SIZE);
        });

        shapeIt();
    }

    @Override
    protected void updateBounds() {
        super.updateBounds();
        //top right  bottom  left
        switch (bs) {
            case FACE_LEFT_BOTTOM:
                setPadding(new Insets(pading,pading,
                        (this.getBoundsInLocal().getWidth()*((Bubble)getShape()).drawRectBubbleIndicatorRule)/2
                                +pading,
                        pading));
                break;
            case FACE_LEFT_CENTER:
                setPadding(new Insets(pading,pading,pading,
                        (this.getBoundsInLocal().getWidth()*((Bubble)getShape()).drawRectBubbleIndicatorRule)/2
                                +pading
                ));
                break;
            case FACE_RIGHT_BOTTOM:
            case FACE_RIGHT_CENTER:
                setPadding(new Insets(pading,
                        (this.getBoundsInLocal().getWidth()*((Bubble)getShape()).drawRectBubbleIndicatorRule)/2
                                +pading
                        ,pading,pading));
                break;
            case FACE_TOP:
                setPadding(new Insets(
                        (this.getBoundsInLocal().getWidth()*((Bubble)getShape()).drawRectBubbleIndicatorRule)/2
                                +pading,
                        pading,pading,pading));
                break;

            default:
                break;
        }
    }

    public final double getPading() {
        return pading;
    }

    public void setPading(double pading) {
        if(pading > 25.0)
            return;
        this.pading = pading;
    }

    public BubbleSpec getBubbleSpec() {
        return bs;
    }

    public void setBubbleSpec(BubbleSpec bubbleSpec) {
        this.bs = bubbleSpec;
        shapeIt();
    }

    private void shapeIt(){
        systemCall = true;
        setShape(new Bubble(bs));
        System.gc();
    }
}
