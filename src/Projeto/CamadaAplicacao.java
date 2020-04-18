package Projeto;

import java.nio.charset.StandardCharsets;



class CamadaAplicacao {

    CamadaTransporte transporte;
    
    void SendTransporte(Object mensagem) {
        
        this.transporte.ReceiveAplicacao(mensagem);
    }
    
    void ReceiveTransporte(Object mensagem) { // Tira a informação que esta em bytes e transforma em string
       
        
        System.out.println("APLICAÇÃO RECEBEU: " + mensagem);
    }

    public CamadaTransporte getTransporte() {
        return transporte;
    }

    public void setTransporte(CamadaTransporte transporte) {
        this.transporte = transporte;
    }
}
