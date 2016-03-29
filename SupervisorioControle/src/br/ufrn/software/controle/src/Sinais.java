package br.ufrn.software.controle.src;

import java.util.Random;

public class Sinais {
    
    private static boolean flag = false;
    private static double init_time = 0;
    private static double amplitude = 0;
    private static double periodo = 0;
    
    // onda degrau
    public static double waveStep(double amplitude) {
        return amplitude;
    }
    
    // onda senoidal
    public static double waveSine(double amplitude, double period, double offset, double time) {
        double phase = 0;
        double angle = ((2*Math.PI*time)/period) + phase;
        return amplitude*Math.sin(angle) + offset;
    }
    
    // onda quadrada
    public static double waveSquare(double amplitude, double period, double offset, double time) {
        double phase = 0;
        double angle = ((2*Math.PI*time)/period) + phase;
        return amplitude*Math.signum(Math.sin(angle)) + offset;
    }
    
    // onda dente de serra
    public static double waveSawtooth(double amplitude, double period, double offset, double time) {
        System.out.println(time%period);
        return amplitude*(time%period)/period + offset;
    }
    
    // onda aleatório
    public static double waveRandom(double amp_max, double per_max , double amp_min,double per_min,double offset, double time) {
        Random amp, per;
        if (!flag){
            init_time = time;
            amp = new Random();
            per = new Random();
            amplitude = amp.nextDouble()*2*amp_max - amp_min;
            periodo = per.nextDouble()*per_max-per_min;
            flag = true;
        }else if (time >= init_time+periodo) {
            flag = false;
        }
        return amplitude + offset;
    }

}
