package Projeto;

import java.nio.charset.StandardCharsets;


class CamadaRedes {
        
    CamadaEnlace enlace;
    CamadaTransporte transporte;        
    int numeroDaMensagem;
    String ipv4;
    String mascara;
    int protocolo;
    
    public CamadaRedes(String ipv4, int valor){
        
        numeroDaMensagem = 0;
        this.ipv4 = ipv4;
        pegarMascara(valor);        
    }
    
    void ReceiveTransporte(Object mensagem, int protocolo) { // Colocar essa mensagem em um pacote
        
        numeroDaMensagem ++; // Recebi uma mensagem
        String message = (String) mensagem;
        byte[] b = message.getBytes(StandardCharsets.UTF_8);
        this.SendEnlace(b);
        this.protocolo = protocolo; // UDP, TCP/IP protocolos que vem da camda de transporte
                                    // E passar para o pacote
    }
    
    void SendTransporte(Object mensagem){
        
        this.transporte.ReceiveRedes(mensagem);
    }
    
    void SendEnlace(Object mensagem){ // Mandar o pacote ipv4 com fragmentacao
                                      // Criar O pacote
        
        this.enlace.ReceiveRedes(mensagem);
        
    }
    
    void ReceiveEnlace(Object mensagem){ // Ler o Pacote Ipv4        
        
        
        this.SendTransporte(mensagem);
    }

    void pegarMascara(int mascara) {
        
        switch(mascara){
            
            case 0: this.mascara = "000.000.000.000";
                break;
            case 1: this.mascara = "128.000.000.000";
                break;
            case 2: this.mascara = "192.000.000.000";
                break;
            case 3: this.mascara = "224.000.000.000";
                break;
            case 4: this.mascara = "240.000.000.000";
                break;
            case 5: this.mascara = "248.000.000.000";
                break;    
            case 6: this.mascara = "252.000.000.000";
                break;
            case 7: this.mascara = "254.000.000.000";
                break;
            case 8: this.mascara = "255.000.000.000";
                break;
            case 9: this.mascara = "255.128.000.000";
                break;
            case 10: this.mascara = "255.192.000.000";
                break;
            case 11: this.mascara = "255.224.000.000";
                break;
            case 12: this.mascara = "255.240.000.000";
                break;
            case 13: this.mascara = "255.248.000.000";
                break;
            case 14: this.mascara = "255.252.000.000";
                break;
            case 15: this.mascara = "255.254.000.000";
                break;
            case 16: this.mascara = "255.255.000.000";
                break;
            case 17: this.mascara = "255.255.128.000";
                break;
            case 18: this.mascara = "255.255.192.000";
                break;
            case 19: this.mascara = "255.255.224.000";
                break;
            case 20: this.mascara = "255.255.240.000";
                break;
            case 21: this.mascara = "255.255.248.000";
                break;    
            case 22: this.mascara = "255.255.252.000";
                break;
            case 23: this.mascara = "255.255.254.000";
                break;
            case 24: this.mascara = "255.255.255.000";
                break;
            case 25: this.mascara = "255.255.255.128";
                break;
            case 26: this.mascara = "255.255.255.192";
                break;
            case 27: this.mascara = "255.255.255.224";
                break;
            case 28: this.mascara = "255.255.255.240";
                break;
            case 29: this.mascara = "255.255.255.248";
                break;
            case 30: this.mascara = "255.255.255.252";
                break;
            case 31: this.mascara = "255.255.255.254";
                break;
            case 32: this.mascara = "255.255.255.255";
                break;    
        }
    }        
    
    
    public CamadaEnlace getEnlace() {
        return enlace;
    }

    public void setEnlace(CamadaEnlace enlace) {
        this.enlace = enlace;
    }

    public CamadaTransporte getTransporte() {
        return transporte;
    }

    public void setTransporte(CamadaTransporte transporte) {
        this.transporte = transporte;
    }    
}
