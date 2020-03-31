package Projeto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Pacote {

    int VER;  // Tem 4 bits logo pode ser (0-15)
    //0000, 0001,0010,0011,0100,0101,0110,0111,1000,1001,1010,1011,1100,1101,1111
    int Hlen;
    byte Servico;            // Setar como tudo zero
    byte[] comprimentoTotal; // HLEN * 4
    int identificacao;       // 2Bytes
    int reservado;           // Compoem o flag 
    boolean canFragment;     // Compoem o flag -- pode ser 0 ou 1
    boolean isLast;          // Compoem o flag -- pode ser 0 ou 1
    int offSet;
    int tempoDeVida;         // Começa com um valor fixo
    int protocolo;           // Começa com um valor fixo (Pagina 588)
    byte[] checksum;         // 2 Bytes , precisamos calcular
    String ipv4Origem;       // Setamos no inicio
    String ipv4Destino;      // Setamos no inicio
    byte[] dados;
    byte[] RestanteDosDados;
    byte[] mensagemCompleta;
    String primeiraParteMensagem;
    
    public Pacote(int numMensagem, Object mensagem, String ipv4Origem, String ipv4Destino, int protocolo) {

        VER = 15; // 11111
        Hlen = 5;
        String message = (String) mensagem;
        primeiraParteMensagem = message;            // Inicialmente a mensagem nao é dividida;
        dados = message.getBytes(StandardCharsets.UTF_8); // Pega a mensagem e transforma em bytes
        byte hlen = (byte) (Hlen * 4);
        comprimentoTotal = new byte[Hlen * 4 + dados.length];        
        identificacao = numMensagem;
        reservado = 0;
        offSet = 0;
        canFragment = true; // 0
        isLast = true; // 0
        this.ipv4Origem = ipv4Origem;
        this.ipv4Destino = ipv4Destino;
        this.protocolo = protocolo;    //  1 == ICMP, 2== IGMP, 6== TCP, 17 == UDP, 89 == OSPF
                                       // pagina 588
        this.mensagemCompleta =  message.getBytes(StandardCharsets.UTF_8);
        
    } // FALTA COLOCAR O HLEN + OS DADOS DENTRO DA ARRAY COMPRIMENTO TOTAL

    public ArrayList<Pacote> Fragmentar() {

        boolean PrimeiroPacoteDaFragmentação = true;
        ArrayList<Pacote> p = new ArrayList<>();
        int TotalDeDados = 0;
        int comprimentoAtual = this.comprimentoTotal.length;
        Pacote pacote;

        if (!this.canFragment) { // Nao posso fragmentar o pacote
            p.add(this);
        } else if (this.comprimentoTotal.length <= 1500) {  // MTU == 1500 bytes
            p.add(this);
        } else {      // Preciso fragmentar
            int comprimentoInicial = comprimentoAtual;
            while (comprimentoAtual > 1500) {
                pacote = new Pacote(this.getIdentificacao(), primeiraParteMensagem,this.ipv4Origem,this.ipv4Destino,this.protocolo);
                
                if (PrimeiroPacoteDaFragmentação ) {
                                  
                    pacote.setOffSet(this.offSet);
                    pacote.setCanFragment(true);
                    pacote.setIsLast(false);
                    PrimeiroPacoteDaFragmentação = false;
                    pacote.setComprimentoTotal(1500, this.Hlen);
                    
                    
                    
                }else{ 
                    
                    pacote.setOffSet((TotalDeDados /8) ); // Divisão Inteira
                    pacote.setCanFragment(true);
                    pacote.setIsLast(false);                
                    pacote.setComprimentoTotal(1500, this.Hlen);
                    p.add(pacote);
                    TotalDeDados = TotalDeDados + (1500 - this.Hlen * 4);			                            
                    comprimentoAtual = comprimentoAtual - ((1500 - this.Hlen * 4) + (this.Hlen * 4)); 
                } 
            }    // Se saiu do while significa que é o último pacote da fragmentação
            
            pacote = new Pacote(this.getIdentificacao(), primeiraParteMensagem,this.ipv4Origem,this.ipv4Destino,this.protocolo);
            pacote.setComprimentoTotal(comprimentoAtual, this.Hlen );	
            pacote.setOffSet((TotalDeDados /8) ); // Divisão Inteira
            pacote.setCanFragment(true);
            pacote.setIsLast(true);
            p.add(pacote);
            System.out.println("Fragmentação deu certo: " + (TotalDeDados == this.mensagemCompleta.length));
        } // fora do else
        return p;
    }
    
    public void setComprimentoTotal(int MTU, int hlen) {
        
        int soDados = MTU - hlen*4;        
        
        RestanteDosDados = new byte[dados.length - soDados];
        int j=0;
        byte [] DadosPacoteAtual = new byte[MTU];
        byte [] comprimentoTotalAtual = new byte[MTU];
        
        for(int i=0; i < comprimentoTotal.length; i ++){
            
            if(i>= soDados){ // Dados para os proximo pacotes da fragmentação
                
                RestanteDosDados[j] = this.comprimentoTotal[i];                
                j++;
                
            }else if ( i >= hlen*4 && i < soDados){ // pacote para esse fragmentação
                
                DadosPacoteAtual[i] = this.comprimentoTotal[i];
                this.dados = DadosPacoteAtual;
            }
            if(i < soDados){
                
                 comprimentoTotalAtual[i] = this.comprimentoTotal[i];
            }
        }              
        this.primeiraParteMensagem = new String(DadosPacoteAtual,StandardCharsets.UTF_8);
        this.dados = DadosPacoteAtual;
        this.comprimentoTotal = comprimentoTotalAtual;
        
        
    }
    
    public void Checksum() {

    }



    public String getIpv4Origem() {
        return ipv4Origem;
    }

    public String getIpv4Destino() {
        return ipv4Destino;
    }

    public int getTempoDeVida() {
        return tempoDeVida;
    }

    public void setTempoDeVida(int tempoDeVida) {
        this.tempoDeVida = tempoDeVida;
    }

    public byte[] getChecksum() {
        return checksum;
    }

    public void setChecksum(byte[] checksum) {
        this.checksum = checksum;
    }

    public byte[] getComprimentoTotal() {
        return comprimentoTotal;
    }

    public boolean isCanFragment() {
        return canFragment;
    }

    public void setCanFragment(boolean canFragment) {
        this.canFragment = canFragment;
    }

    public int getIdentificacao() {
        return identificacao;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public boolean isIsLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public void setProtocolo(int protocolo) {
        this.protocolo = protocolo;
    }
    
    public int getProtocolo() {
        return protocolo;
    }
    
    public byte[] getMensagemCompleta() {
        return mensagemCompleta;
    }
     
}
