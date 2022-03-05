package com.g223.synth;

import com.g223.synth.utils.Utils;

import javax.swing.*;
import java.awt.*;

public class WaveViewer extends JPanel {
    private Oscillator[] oscillators;
    public WaveViewer(Oscillator[] oscillators){
        this.oscillators = oscillators;
        setBorder(Utils.WindowDesign.LINE_BORDER);
    }
    @Override
    public void paintComponent(Graphics graphics){
        final int PAD = 25;
        super.paintComponent(graphics);
        int midY = getHeight()/2;
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.drawLine(PAD,midY,getWidth()-PAD,midY);
        graphics2D.drawLine(PAD,PAD,PAD,getHeight()-PAD);
    }
}
