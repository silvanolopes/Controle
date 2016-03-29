package br.ufrn.software.controle.src;

public class Controlador {
	
    public static double derivador(double error, double previous_error, double sample_time, double kd) {
        return ((error - previous_error)/sample_time)*kd;
    }

    public static double integrador(double error, double previous_int, double sample_time, double ki) {
        return ((error*sample_time*ki) + previous_int);
    }
    
    public static double proporcional(double error, double kp) {
        return kp*error;
    }
}