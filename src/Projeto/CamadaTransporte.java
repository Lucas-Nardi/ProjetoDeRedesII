package Projeto;

import java.util.concurrent.ExecutionException;

class CamadaTransporte {
    
    CamadaAplicacao aplicacao;
    CamadaRedes redes;

        
    void ReceiveAplicacao(Object mensagem) throws InterruptedException, ExecutionException{  // Recebe Algo da cadama de Aplicação
        
        this.SendRedes(mensagem); // Manda para a camda de redes;
    }
    void SendAplicacao(Object mensagem){  // Envia Algo para a cadama de Aplicação
        
        this.aplicacao.ReceiveTransporte(mensagem);
    }
    
    void ReceiveRedes(Object mensagem){ // Recebe Algo Da camada de Redes e passo para aplicação
        
        this.SendAplicacao(mensagem);
    }
    
    void SendRedes(Object mensagem) throws InterruptedException, ExecutionException{ // Recebeu uma mensagem da camada de aplicacao e manda para a camada de redes
        
        this.redes.ReceiveTransporte(mensagem,1); // Mandar o Protocolo para a camda de redes
    }

    public CamadaAplicacao getAplicacao() {
        return aplicacao;
    }

    public void setAplicacao(CamadaAplicacao aplicacao) {
        this.aplicacao = aplicacao;
    }

    public CamadaRedes getRedes() {
        return redes;
    }

    public void setRedes(CamadaRedes redes) {
        this.redes = redes;
    }    
}
