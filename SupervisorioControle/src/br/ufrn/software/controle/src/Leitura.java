package br.ufrn.software.controle.src;

import br.ufrn.dca.controle.QuanserClientException;
import br.ufrn.software.controle.screen.MainScreen;

public class Leitura implements Runnable {
    
    private MainScreen mainscreen;
    private long start, end;
    private double time;

    //construtor
    public Leitura(MainScreen mainscreen) {
        this.mainscreen = mainscreen;
    }

    public void run() {
        start = System.currentTimeMillis();
        while(true){
            if (mainscreen.isFinalizeThread()){
                try {
                    mainscreen.getQuanserClient().write(0, 0);
                    break;
                } catch (QuanserClientException ex) {
                    System.out.println(ex);
                }
            start = System.currentTimeMillis();
            }
            else {
                /*
                try {
                    Thread.sleep(100);
                } catch (InterruptedException erro) {
                    System.out.println(erro);
                }            
                end = System.currentTimeMillis();
                */
                end = System.currentTimeMillis();
                time = (double) (end-start)/1000.0;
                mainscreen.getGrafico1().addPoint(time, mainscreen.getNivel_calculado(), 0);
                mainscreen.getGrafico1().addPoint(time, mainscreen.getNivel_tank0(), 1);
                mainscreen.getGrafico1().addPoint(time, mainscreen.getNivel_tank1(), 2);
                mainscreen.getGrafico1().addPoint(time, mainscreen.getErro_Tq0(), 3);
                mainscreen.getGrafico1().addPoint(time, mainscreen.getControleP(), 4);
                mainscreen.getGrafico1().addPoint(time, mainscreen.getControleI(), 5);                
                mainscreen.getGrafico1().addPoint(time, mainscreen.getControleD(), 6);

                mainscreen.getGrafico2().addPoint(time, mainscreen.getTensao_calculada(), 0);
                mainscreen.getGrafico2().addPoint(time, mainscreen.getTensao_aplicada(), 1);
                

                mainscreen.getGrafico1().drawChart(time);
                mainscreen.getGrafico2().drawChart(time);
                
                while (mainscreen.isThread1Pause() && mainscreen.isThread2Pause()) {
                    System.out.println("Thread 1 e 2 pausadas");
                    start += System.currentTimeMillis() - end;
                    end = System.currentTimeMillis();
                }

            }
        }
        
    }
    
    
    
    
    
}
