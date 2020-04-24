package Projeto;

public class ItensDaTabela {

    String mascara;       // Mascará de um grupo
    String enderecoDeRede; // Endereço de um grupo
    String proximoSalto; // Inda se vai ser um roteador ou um computador
    String interFace;   // Indica que barramento devo pesquisar o computador ou o roteador;

    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public String getEnderecoDeRede() {
        return enderecoDeRede;
    }

    public void setEnderecoDeRede(String enderecoDeRede) {
        this.enderecoDeRede = enderecoDeRede;
    }

    public String getProximoSalto() {
        return proximoSalto;
    }

    public void setProximoSalto(String proximoSalto) {
        this.proximoSalto = proximoSalto;
    }

    public String getInterFace() {
        return interFace;
    }

    public void setInterFace(String interFace) {
        this.interFace = interFace;
    }
}
