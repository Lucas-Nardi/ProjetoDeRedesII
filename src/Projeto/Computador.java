package Projeto;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Computador  implements Observer{
    
    private CamadaRedes camadaRedes;
    private CamadaEnlace camadaEnlace;
    private CamadaTransporte camadaTransporte;
    private CamadaAplicacao camadaAplicacao;

    public Computador(CamadaFisica barramento, String iPv4, String macAddress, int mascara,int mtu) {
    
        this.camadaAplicacao = new CamadaAplicacao();
        this.camadaTransporte = new CamadaTransporte();
        this.camadaRedes = new CamadaRedes(iPv4,mascara);        
        this.camadaEnlace = new CamadaEnlace(barramento, macAddress,mtu);
        
        this.camadaAplicacao.setTransporte(camadaTransporte);
        
        this.camadaTransporte.setAplicacao(camadaAplicacao);
        this.camadaTransporte.setRedes(camadaRedes);
        
        this.camadaRedes.setTransporte(camadaTransporte);
        this.camadaRedes.setEnlace(camadaEnlace);
        
        this.camadaEnlace.setRedes(camadaRedes);
    }
    
    @Override
    public void Send(Object mensagem) { try {
        // Enviar pacote para o barramento
        
        camadaAplicacao.SendTransporte(mensagem);
        } catch (InterruptedException ex) {
            
        } catch (ExecutionException ex) {
            
        }
    }
    
    @Override
    public void Receive(Object mensagem)
    { 
        try {
            camadaEnlace.ReceiveFisica(mensagem);
            
        } catch (ExecutionException ex) {
           
        } catch (InterruptedException ex) {
            
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
