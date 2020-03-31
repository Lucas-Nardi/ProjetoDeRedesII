package Projeto;

class CamadaTransporte {
    
    CamadaAplicacao aplicacao;
    CamadaRedes redes;

        
    void ReceiveAplicacao(Object mensagem){  // Recebe Algo da cadama de Aplicação
        
        this.redes.ReceiveTransporte(mensagem);
    }
    void SendAplicacao(Object mensagem){  // Envia Algo para a cadama de Aplicação
        
        this.aplicacao.ReceiveTransporte(mensagem);
    }
    
    void ReceiveRedes(Object mensagem){ // Recebe Algo Da camada de Redes e passo para aplicação
        
        this.SendAplicacao(mensagem);
    }
    
    void SendRedes(Object mensagem){ // Recebeu uma mensagem da camada de aplicacao e manda para a camada de redes
        
        this.redes.ReceiveTransporte(mensagem);
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
