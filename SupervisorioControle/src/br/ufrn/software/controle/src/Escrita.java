package br.ufrn.software.controle.src;

import br.ufrn.dca.controle.QuanserClientException;
import br.ufrn.software.controle.screen.MainScreen;
import static java.lang.Math.abs;

public class Escrita implements Runnable {

    private MainScreen mainscreen;
    private long start, end;
    private double fullTime, sliceTime;

    //construtor
    public Escrita(MainScreen mainscreen) {
        this.mainscreen = mainscreen;
    }

    //travas de segurança
    public void Trava(double tensao) {

        if (mainscreen.getNivel_tank0() < 4.0 && tensao < 0) {
            mainscreen.setTensao_aplicada(0);
        } else if (mainscreen.getNivel_tank0() >= 28.0 && tensao > 3.15) {
            mainscreen.setTensao_aplicada(3.15);
        } else if (mainscreen.getNivel_tank0() >= 29.0) {
            mainscreen.setTensao_aplicada(0);
        } else {
            if (tensao > 4.0) {
                mainscreen.setTensao_aplicada(4.0);
            } else if (tensao < -4.0) {
                mainscreen.setTensao_aplicada(-4.0);
            } else {
                mainscreen.setTensao_aplicada(tensao);
            }
        }
        System.out.println("Tensao: " + tensao);
        System.out.println("Tensao1: " + mainscreen.getTensao_aplicada());
        System.out.println("Erro: " + mainscreen.getErro_Tq0());
        System.out.println("Nível Tank: " + mainscreen.getNivel_tank0());
    }

    public void MalhaAberta(){
    mainscreen.setTensao_calculada(mainscreen.getNivel_calculado());    
    }
    
    public void ControllerTanque0() {
        mainscreen.setErro_prev_Tq0(mainscreen.getErro_Tq0());
        mainscreen.setErro_Tq0((mainscreen.getNivel_calculado() - mainscreen.getNivel_tank0()));

        // calculo da tensao de controle (resultado do controle em nível convertido para tensão)
        switch (mainscreen.getTipo_Ctrl_Tq0()) {
            case 0:
                mainscreen.setCtrlP_Tq0(Controlador.proporcional(mainscreen.getErro_Tq0(), mainscreen.getKp_Tq0()));
                mainscreen.setCtrlI_Tq0(0);
                mainscreen.setCtrlD_Tq0(0);
                break;
            case 1:
                mainscreen.setCtrlP_Tq0(Controlador.proporcional(mainscreen.getErro_Tq0(), mainscreen.getKp_Tq0()));
                mainscreen.setCtrlI_Tq0(Controlador.integrador(mainscreen.getErro_Tq0(), mainscreen.getCtrlI_prev_Tq0(), sliceTime, mainscreen.getKi_Tq0()));
                mainscreen.setCtrlD_Tq0(0);
                break;
            case 2:
                mainscreen.setCtrlP_Tq0(Controlador.proporcional(mainscreen.getErro_Tq0(), mainscreen.getKp_Tq0()));
                mainscreen.setCtrlI_Tq0(0);
                mainscreen.setCtrlD_Tq0(Controlador.derivador(mainscreen.getErro_Tq0(), mainscreen.getErro_prev_Tq0(), sliceTime, mainscreen.getKd_Tq0()));
                break;
            case 3:
                mainscreen.setCtrlP_Tq0(Controlador.proporcional(mainscreen.getErro_Tq0(), mainscreen.getKp_Tq0()));
                mainscreen.setCtrlI_Tq0(Controlador.integrador(mainscreen.getErro_Tq0(), mainscreen.getCtrlI_prev_Tq0(), sliceTime, mainscreen.getKi_Tq0()));
                mainscreen.setCtrlD_Tq0(Controlador.derivador(mainscreen.getErro_Tq0(), mainscreen.getErro_prev_Tq0(), sliceTime, mainscreen.getKd_Tq0()));
                break;
            case 4:
                mainscreen.setCtrlP_Tq0(Controlador.proporcional(mainscreen.getErro_Tq0(), mainscreen.getKp_Tq0()));
                mainscreen.setCtrlI_Tq0(Controlador.integrador(mainscreen.getErro_Tq0(), mainscreen.getCtrlI_prev_Tq0(), sliceTime, mainscreen.getKi_Tq0()));
                mainscreen.setCtrlD_Tq0(Controlador.derivador(mainscreen.getNivel_tank0(), mainscreen.getNivel_tank0_ant(), sliceTime, mainscreen.getKd_Tq0()));
                break;
        }
        
        
        //condicional
        if (mainscreen.isFiltroConTq0()&& (mainscreen.getTensao_calculada() > 3.0 || mainscreen.getTensao_calculada() < -3.0)) { 
            double sat_erro = mainscreen.getTensao_aplicada() - mainscreen.getTensao_calculada();
            double sat_valor = 3;
            if (abs(sat_erro) > sat_valor) {
                mainscreen.setCtrlI_Tq0(mainscreen.getCtrlI_prev_Tq0());
            }
        }
        //back-calculation
//        if (mainscreen.isFiltroWTq0() && (mainscreen.getTensao_calculada() > 3.0 || mainscreen.getTensao_calculada() < -3.0)) {
//            mainscreen.setCtrlI_Tq0(mainscreen.getCtrlI_prev_Tq0());
//        }
        
        if (mainscreen.isFiltroBackTq0()&& (mainscreen.getTensao_calculada() > 3.0 || mainscreen.getTensao_calculada() < -3.0)) {
            double sat = mainscreen.getTensao_aplicada() - mainscreen.getTensao_calculada();
            mainscreen.setCtrlI_Tq0((((mainscreen.getKi_Tq0()*mainscreen.getErro_Tq0() + mainscreen.getKt_Tq0()*sat)*sliceTime) + mainscreen.getCtrlI_prev_Tq0()));
        }

        mainscreen.setCtrlD_prev_Tq0(mainscreen.getCtrlD_Tq0());

        mainscreen.setCtrlI_prev_Tq0(mainscreen.getCtrlI_Tq0());
        mainscreen.setTensao_calculada(mainscreen.getCtrlP_Tq0() + mainscreen.getCtrlI_Tq0() + mainscreen.getCtrlD_Tq0());
    }

    public void run() {
        // pega o momento de inicio da thread
        start = System.currentTimeMillis();
        fullTime = 0.0;
        while (true) {
            if (mainscreen.isFinalizeThread()) {
                try {
                    mainscreen.getQuanserClient().write(0, 0);
                    break;
                } catch (QuanserClientException ex) {
                    System.out.println(ex);
                }
                // reinicia o tempo de calculo
                start = System.currentTimeMillis();

            } else {
                //            start = System.currentTimeMillis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException erro) {
                    System.out.println(erro);
                }
                mainscreen.setNivel_tank0_ant(mainscreen.getNivel_tank0());
                try {
                    mainscreen.setNivel_tank0(mainscreen.getQuanserClient().read(0) * 6.25);
                    mainscreen.setNivel_tank1(mainscreen.getQuanserClient().read(1) * 6.25);
                    /*mainscreen.setNivel_tank2(mainscreen.getQuanserClient().read(2) * 6.25);
                    mainscreen.setNivel_tank3(mainscreen.getQuanserClient().read(3) * 6.25);
                    mainscreen.setNivel_tank4(mainscreen.getQuanserClient().read(4) * 6.25);
                    mainscreen.setNivel_tank5(mainscreen.getQuanserClient().read(5) * 6.25);
                    mainscreen.setNivel_tank6(mainscreen.getQuanserClient().read(6) * 6.25);
                    mainscreen.setNivel_tank7(mainscreen.getQuanserClient().read(7) * 6.25);*/
                } catch (QuanserClientException ex) {
                    System.out.println(ex);
                }
                //pega o momento atual da thread e calcula o periodo de tempo passado desde o inicio
                sliceTime = fullTime;
                end = System.currentTimeMillis();
                fullTime = (double) (end - start) / 1000.0;
                sliceTime = fullTime - sliceTime;
                //Calculo da tensao aplicada
                // calculo do nivel desejado de acordo com cada onda
                switch (mainscreen.getSelectedWave()) {
                    case 0:
                        mainscreen.setNivel_calculado(Sinais.waveStep(mainscreen.getSetpoint()));
                        break;
                    case 1:
                        mainscreen.setNivel_calculado(Sinais.waveSine(mainscreen.getSetpoint(), mainscreen.getPeriod(), mainscreen.getOffset(), fullTime));
                        break;
                    case 2:
                        mainscreen.setNivel_calculado(Sinais.waveSquare(mainscreen.getSetpoint(), mainscreen.getPeriod(), mainscreen.getOffset(), fullTime));
                        break;
                    case 3:
                        mainscreen.setNivel_calculado(Sinais.waveSawtooth(mainscreen.getSetpoint(), mainscreen.getPeriod(), mainscreen.getOffset(), fullTime));
                        break;
                    case 4:
                        mainscreen.setNivel_calculado(Sinais.waveRandom(mainscreen.getSetmax(), mainscreen.getPermax(), mainscreen.getSetmin(), mainscreen.getPermin(), mainscreen.getOffset(), fullTime));
                        break;
                }

                if(mainscreen.getTipoMalha()== 0){
                MalhaAberta();}
                else{
                ControllerTanque0();}

                Trava(mainscreen.getTensao_calculada());
                //fim calculo da tensao aplicada

                try {
                    mainscreen.getQuanserClient().write(0, mainscreen.getTensao_aplicada());
                } catch (QuanserClientException ex) {
                    System.out.println(ex);
                }

                while (mainscreen.isThread1Pause() && mainscreen.isThread2Pause()) {
                    System.out.println("Thread 1 e 2 pausadas");
                    // ajusta start e end caso haja pausa nas threads
                    start += System.currentTimeMillis() - end;
                    end = System.currentTimeMillis();
                }
            }
        }
    }
}
