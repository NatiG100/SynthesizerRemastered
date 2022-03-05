package com.g223.synth;

import com.g223.synth.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Oscillator extends SynthControlContainer {
    private static final int TONE_OFFSET_LIMIT=2000;
    private WaveTable waveTable = WaveTable.Sine;
    private double keyFrequency;
    private RefWrapper<Integer> toneOffset = new RefWrapper<>(0);
    private RefWrapper<Integer> volume = new RefWrapper<>(100);
    private int wavetableStepSize;
    private int getWavetableIndex;


    public Oscillator(SynthesizerRemastered synth){
        super(synth);
        JComboBox<WaveTable> comboBox = new JComboBox<>(WaveTable.values());
        comboBox.setSelectedItem(WaveTable.Sine);
        comboBox.setBounds(10,10,75,25);
        comboBox.addItemListener(l->{
            if(l.getStateChange()== ItemEvent.SELECTED){
                waveTable = (WaveTable) l.getItem();
            }
        });
        add(comboBox);
        JLabel toneParameter = new JLabel("x0.00");
        toneParameter.setBounds(165,65,50,25);
        toneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParameterMouseListeners(
                toneParameter,
                this,
                -TONE_OFFSET_LIMIT,
                TONE_OFFSET_LIMIT,
                1,
                toneOffset,
                ()->{
                    applyToneOffset();
                    toneParameter.setText(" x"+String.format(" %.3f",getToneOffset()));
                }
        );
        add(toneParameter);
        JLabel toneText = new JLabel("Tone");
        toneText.setBounds(172,47,75,25);
        add(toneText);
        JLabel volumeParameter = new JLabel(" 100%");
        volumeParameter.setBounds(222,65,50,25);
        volumeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
        Utils.ParameterHandling.addParameterMouseListeners(
                volumeParameter,
                this,
                0,
                100,
                1,
                volume,
                ()->{
                    volumeParameter.setText(" "+volume.val+"%");
                }
        );
        add(volumeParameter);
        JLabel volumeText = new JLabel("Volume");
        volumeText.setBounds(215,47,75,25);
        add(volumeText);
        setSize(279,100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }
    public double nextSample(){
        double sample = waveTable.getSamples()[getWavetableIndex]*getVolumeMultiplier();
        getWavetableIndex =  (getWavetableIndex+wavetableStepSize)%WaveTable.SIZE;
        return sample;
    }



    public void setKeyFrequency(double frequency){
        keyFrequency=frequency;
        applyToneOffset();
    }
    public double[] getSampleWaveForm (int numSamples){
        double[] samples = new double[numSamples];
        double frequency = 1/(numSamples/(double)SynthesizerRemastered.AudioInfo.SAMPLE_RATE);
        int index = 0;
        int stepSize = (int)(WaveTable.SIZE*);
    }
    private double getToneOffset(){
        return toneOffset.val/1000d;
    }
    private double getVolumeMultiplier(){
        return volume.val/100.0;
    }

    private void applyToneOffset(){
        wavetableStepSize = (int)(WaveTable.SIZE*(keyFrequency*Math.pow(2,getToneOffset()))/SynthesizerRemastered.AudioInfo.SAMPLE_RATE);
    }
}
