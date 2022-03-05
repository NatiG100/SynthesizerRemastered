package com.g223.synth;

import com.g223.synth.utils.Utils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class SynthesizerRemastered {
    private static final HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();

    private final JFrame frame = new JFrame("Synthesizer Remastered");
    private  boolean shouldGenerate = false;
    private final Oscillator[] oscillators = new Oscillator[3];
    private WaveViewer waveViewer = new WaveViewer(oscillators);

    private final AudioThread audioThread = new AudioThread(()->{
        if(!shouldGenerate){
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for(int i=0; i<AudioThread.BUFFER_SIZE; i++){
           double d = 0;
           for(Oscillator o: oscillators){
               d += o.nextSample()/oscillators.length;
           }
           s[i] = (short)(Short.MAX_VALUE*d);
        }
        return s;
    });

    private final KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if(!KEY_FREQUENCIES.containsKey(e.getKeyChar())){
                return;
            }
            if(!audioThread.isRunning()){
                for(Oscillator o: oscillators){
                    o.setKeyFrequency(KEY_FREQUENCIES.get(e.getKeyChar()));
                }
                shouldGenerate = true;
                audioThread.triggerPlayback();
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            shouldGenerate = false;
        }
    };
    static {
        final int STARTING_KEY = 16;
        final int KEY_FREQUENCY_INCREMENT = 2;
        final char[] KEYS = "zxcvbnm,./asdfghjkl;'qwertyuiop[]".toCharArray();
        for (int i=STARTING_KEY,key=0;i<KEYS.length*KEY_FREQUENCY_INCREMENT+STARTING_KEY;i+=KEY_FREQUENCY_INCREMENT,++key){
            KEY_FREQUENCIES.put(KEYS[key], Utils.Math.getKeyFrequency(i));
        }
    }
    SynthesizerRemastered(){
        int y = 0;
        for(int i=0;i<oscillators.length;i++){
            oscillators[i]=new Oscillator(this);
            oscillators[i].setLocation(5,y);
            frame.add(oscillators[i]);
            y += 105;
        }
        Oscillator o = new Oscillator(this);
        o.setLocation(5,0);
        frame.add(o);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                audioThread.close();
            }
        });
        waveViewer.setBounds(290,0,310,340);
        frame.add(waveViewer);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(613,345);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addKeyListener(keyAdapter);
    }
    public KeyAdapter getKeyAdapter(){
        return keyAdapter;
    }
    public static class AudioInfo{
        public static final int SAMPLE_RATE = 44100;
    }
}
