package Projeto;

import java.nio.charset.StandardCharsets;



class CamadaAplicacao {

    CamadaTransporte transporte;

    public CamadaAplicacao(){
        
        this.transporte = new CamadaTransporte();
    }

    void SendTransporte(Object mensagem) {
        
        this.transporte.ReceiveAplicacao(mensagem);
    }
    
    void ReceiveTransporte(Object mensagem) { // Tira a informação que esta em bytes e transforma em string
       
        byte [] message = (byte [] ) mensagem;        
        String informacao = new String(message,StandardCharsets.UTF_8);
        System.out.println(informacao);
    }

    public CamadaTransporte getTransporte() {
        return transporte;
    }

    public void setTransporte(CamadaTransporte transporte) {
        this.transporte = transporte;
    }
}
