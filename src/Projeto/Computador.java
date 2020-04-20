package Projeto;

import java.util.concurrent.ExecutionException;

public class Computador  implements Observer{
    
    private CamadaRedes camadaRedes;
    private CamadaEnlace camadaEnlace;
    private CamadaTransporte camadaTransporte;
    private CamadaAplicacao camadaAplicacao;

    public Computador(CamadaFisica barramento, String iPv4, String macAddress) {
    
        this.camadaAplicacao = new CamadaAplicacao();
        this.camadaTransporte = new CamadaTransporte();
        this.camadaRedes = new CamadaRedes(iPv4,26);        
        this.camadaEnlace = new CamadaEnlace(barramento, macAddress,22);
        
        this.camadaAplicacao.setTransporte(camadaTransporte);
        
        this.camadaTransporte.setAplicacao(camadaAplicacao);
        this.camadaTransporte.setRedes(camadaRedes);
        
        this.camadaRedes.setTransporte(camadaTransporte);
        this.camadaRedes.setEnlace(camadaEnlace);
        
        this.camadaEnlace.setRedes(camadaRedes);
    }
    
    @Override
    public void Send(Object mensagem) { // Enviar pacote para o barramento
        
        camadaAplicacao.SendTransporte(mensagem);
    }
    
    @Override
    public void Receive(Object mensagem) { // Receber pacote do barramento
        try {
            camadaEnlace.ReceiveFisica(mensagem);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CamadaRedes getCamadaRedes() {
        return camadaRedes;
    }

    public CamadaEnlace getCamadaEnlace() {
        return camadaEnlace;
    }

    public CamadaTransporte getCamadaTransporte() {
        return camadaTransporte;
    }

    public CamadaAplicacao getCamadaAplicacao() {
        return camadaAplicacao;
    }
    
    
    
}
