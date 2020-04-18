package Projeto;

public class Mensagem {

    String mensagem;
    String ipv4Destino;
    boolean fazerArp;
    
    public Mensagem(String mensagem, String ipv4Destino) {
        this.mensagem = mensagem;
        this.ipv4Destino = ipv4Destino;
        fazerArp = true;
    }
    
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getIpv4Destino() {
        return ipv4Destino;
    }

    public void setIpv4Destino(String ipv4Destino) {
        this.ipv4Destino = ipv4Destino;
    }

    public boolean isFazerArp() {
        return fazerArp;
    }
}
