package Projeto;

import java.util.concurrent.ExecutionException;

class CamadaAplicacao {

    CamadaTransporte transporte;
    
    void SendTransporte(Object mensagem) throws InterruptedException, ExecutionException {
        
        this.transporte.ReceiveAplicacao(mensagem);
    }
    
    void ReceiveTransporte(Object mensagem) { // Tira a informação que esta em bytes e transforma em string
       
        
        System.out.println("APLICAÇÃO RECEBEU: " + mensagem);
        System.out.println("--------------------------------------------------------");
    }

    public CamadaTransporte getTransporte() {
        return transporte;
    }

    public void setTransporte(CamadaTransporte transporte) {
        this.transporte = transporte;
    }
}
