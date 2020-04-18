
package Projeto;


public class PacoteArp {
    
    int operacao;
    String ipV4Origem;
    String ipV4Destino;
    String macOrigem;
    String macDestino;

    public PacoteArp(int operacao, String ipV4Origem, String ipV4Destino, String macOrigem, String macDestino) {
        this.operacao = operacao;
        this.ipV4Origem = ipV4Origem;
        this.ipV4Destino = ipV4Destino;
        this.macOrigem = macOrigem;
        this.macDestino = macDestino;
    }

    public int getOperacao() {
        return operacao;
    }

    public void setOperacao(int operacao) {
        this.operacao = operacao;
    }

    public String getIpV4Origem() {
        return ipV4Origem;
    }

    public void setIpV4Origem(String ipV4Origem) {
        this.ipV4Origem = ipV4Origem;
    }

    public String getIpV4Destino() {
        return ipV4Destino;
    }

    public void setIpV4Destino(String ipV4Destino) {
        this.ipV4Destino = ipV4Destino;
    }

    public String getMacOrigem() {
        return macOrigem;
    }

    public void setMacOrigem(String macOrigem) {
        this.macOrigem = macOrigem;
    }

    public String getMacDestino() {
        return macDestino;
    }

    public void setMacDestino(String macDestino) {
        this.macDestino = macDestino;
    }
    
}
