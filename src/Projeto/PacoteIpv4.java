package Projeto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PacoteIpv4  implements Comparable< PacoteIpv4 >{

    int VER;  // Tem 4 bits logo pode ser (0-15)
    //0000, 0001,0010,0011,0100,0101,0110,0111,1000,1001,1010,1011,1100,1101,1111
    int Hlen;
    byte servico;            // Setar como tudo zero
    byte[] comprimentoTotal; // HLEN * 4
    byte[] dados;
    byte[] RestanteDosDados; // Restante dos dados para os proximos pacotes
    byte[] mensagemCompleta; // Mensagem inicial (completa) 
    byte[] checksum;         // 2 Bytes , precisamos calcular
    int identificacao;       // 2 Bytes
    int reservado;           // Compoem o flag 
    boolean canFragment;     // Compoem o flag -- pode ser 0 ou 1
    boolean isLast;          // Compoem o flag -- pode ser 0 ou 1
    int offSet;              // Inicialmente é 0
    int tempoDeVida;         // Começa com um valor fixo
    int protocolo;           // Começa com um valor fixo (Pagina 588)
    String ipv4Origem;       // Setamos no inicio
    String ipv4Destino;      // Setamos no inicio
    String primeiraParteMensagem;
    String restanteDaMensagem;
    int mtu;
    
    public PacoteIpv4(int numMensagem, Object mensagem, String ipv4Origem, String ipv4Destino, int protocolo, int mtu) {
        
        this.VER = 15;  // 11111
        this.Hlen = 5;
        String message = (String) mensagem;        
        this.primeiraParteMensagem = message;            ;
        this.restanteDaMensagem = message;
        
        this.dados = message.getBytes(StandardCharsets.UTF_8); // Pega a mensagem e transforma em bytes        
        this.RestanteDosDados = dados;                         // Mensagem nao fragmentada logo, RestanteDosDados é mensagem original
        this.mensagemCompleta = dados;                         // Salva Mensagem Original
        this.comprimentoTotal = new byte[Hlen * 4 + dados.length];        
        
        this.servico = 0;
        this.identificacao = numMensagem;
        this.reservado = 0;
        this.offSet = 0;
        this.canFragment = true; // 0
        this.isLast = true; // 0
        this.tempoDeVida = 256;
        this.ipv4Origem = ipv4Origem;
        this.ipv4Destino = ipv4Destino;
        this.protocolo = protocolo;    //  1 == ICMP, 2== IGMP, 6== TCP, 17 == UDP, 89 == OSPF  -- pagina 588
        this.mtu = mtu;
        

    } // FALTA COLOCAR O HLEN + OS DADOS DENTRO DA ARRAY COMPRIMENTO TOTAL
    
    public void PreencherComprimentoTotal(){
        int j = 0;
        int i;
        
        for(i=0; i < this.comprimentoTotal.length; i ++){
            if(i < Hlen *4){
                
               comprimentoTotal[i] = 0;
                
            }else{
                comprimentoTotal[i] =  this.dados[j];                
                j++;
            }            
        }        
    }
    
    public ArrayList<PacoteIpv4> Fragmentar() {
        
                
        boolean PrimeiroPacoteDaFragmentação = true;
        ArrayList<PacoteIpv4> p = new ArrayList<>();
        int TotalDeDados = 0;
        int comprimentoAtual = this.comprimentoTotal.length;
        PacoteIpv4 pacote;

        if (!this.canFragment) { // Nao posso fragmentar o pacote
            p.add(this);
        } else if (this.comprimentoTotal.length <= mtu) {  // MTU == 1500 bytes
            p.add(this);
        } else {      // Preciso fragmentar
            
            int offSet = 0;  
            
            while (comprimentoAtual > mtu) {
                
                pacote = new PacoteIpv4(this.identificacao, this.restanteDaMensagem,this.ipv4Origem, this.ipv4Destino, this.protocolo,mtu);
                
                
                if (PrimeiroPacoteDaFragmentação) {
                    
                    pacote.setOffSet(this.offSet);
                    pacote.setCanFragment(true);
                    pacote.setIsLast(false);
                    PrimeiroPacoteDaFragmentação = false;
                    pacote.setMensagemCompleta(this.mensagemCompleta);
                    this.setDividirDados(22, this.Hlen,pacote);
                     
                    
                } else { 
                    
                    if (TotalDeDados % 8 == 0) {

                        offSet = TotalDeDados / 8;
                    
                    } else {
                        

                        offSet = TotalDeDados % 8;
                    }
                    pacote.setOffSet(offSet); // Divisão Inteira
                    pacote.setCanFragment(true);
                    pacote.setIsLast(false);
                    pacote.setMensagemCompleta(this.mensagemCompleta);
                    this.setDividirDados(22, this.Hlen,pacote);                   
                    
                }                
                p.add(pacote);
                TotalDeDados = TotalDeDados + (mtu - this.Hlen * 4);
                comprimentoAtual = (comprimentoAtual - (mtu - this.Hlen * 4) ) ;
                
            }    // Se saiu do while significa que é o último pacote da fragmentação
            if (TotalDeDados % 8 == 0) {

                offSet = TotalDeDados / 8;
            } else {

                offSet = TotalDeDados % 8;
            }
            
            pacote = new PacoteIpv4(this.identificacao, restanteDaMensagem,this.ipv4Origem, this.ipv4Destino, this.protocolo,mtu);
            pacote.setCanFragment(true);
            pacote.setIsLast(true);
            pacote.setRestanteDaMensagem("Fim da mensagem");
            pacote.setPrimeiraParteMensagem(this.restanteDaMensagem);
            pacote.setDados(this.RestanteDosDados);
            pacote.setComprimentoTotal(this.comprimentoTotal);
            pacote.setOffSet(offSet); // Divisão Inteira 
            pacote.setMensagemCompleta(this.mensagemCompleta);
            p.add(pacote);
           
            
        } // fora do else
        return p;
    }

    public void setDividirDados(int MTU, int hlen, PacoteIpv4 p) {

        if (!p.isLast) { // Se nao for o ultimo pacote, fragmentar as informações

            int soDados = MTU - hlen * 4; // QUANTOS DE DADOS POR PACOTE 
                        
            int j = 0;
            int k = 0;
            byte[] DadosPacoteAtual = new byte[soDados];
            byte[] comprimentoTotalAtual = new byte[MTU];
            byte[] dadosRestantes = new byte [this.RestanteDosDados.length - soDados];
            byte[] comprimentoTotalProximoPacote = new byte[this.RestanteDosDados.length - soDados + hlen * 4];
            String antes, depois;        
                    
           
            System.out.println("");
           
            
            for (int i = 0; i < comprimentoTotal.length; i++) {

                if (i >= hlen * 4  && i < hlen * 4 + soDados) { //  Cabeçalho < dados para pacote < dados restates  
                    
                    DadosPacoteAtual[k] = this.comprimentoTotal[i];                    
                    k++;
                    

                } else if (i >= (hlen * 4 + soDados) ) { // Restantes Dos dados
                    
                    
                    dadosRestantes[j] = this.comprimentoTotal[i];                   
                    comprimentoTotalProximoPacote[i-soDados] = this.comprimentoTotal[i];                   
                    j++;
                }
                
                if (i < hlen * 4 + soDados ) {  // Preencher o comprimento Total do pacote

                    comprimentoTotalAtual[i] = this.comprimentoTotal[i];
                    
                }
                if( i < hlen * 4){
                    
                    comprimentoTotalProximoPacote[i] = this.comprimentoTotal[i]; // Comprimento Total Restante Nos PacoteIpv4                    
                }                
            }
            
            
            antes = new String(DadosPacoteAtual, StandardCharsets.UTF_8);   // Mensagem que ta no pacote
            primeiraParteMensagem = antes;           
            p.setPrimeiraParteMensagem(primeiraParteMensagem);
            
                          // Mensagem para o proximo pacote
            
            depois = new String(dadosRestantes, StandardCharsets.UTF_8);
            restanteDaMensagem = depois;
            p.setRestanteDaMensagem(depois);
                        
            RestanteDosDados =  dadosRestantes;                                        // restante da mensagem em bytes    
            p.setRestanteDosDados(dadosRestantes);
                        
            dados = DadosPacoteAtual;                                                 // Mensagem do pacote atual em bytes
            p.setDados(DadosPacoteAtual );            
            comprimentoTotal = comprimentoTotalAtual;                                 // comprimento total em bytes 
            
            p.setComprimentoTotal(comprimentoTotalAtual);  
            
            comprimentoTotal = comprimentoTotalProximoPacote;                        // Comprimento Total Restante Nos PacoteIpv4
                        
        }
    }

    public void  CalcularChecksum() {

    }

    @Override
    public int compareTo(PacoteIpv4 p) {
       
        return ( this.offSet - p.getOffSet() ) ;
    }
    
    public byte getServico() {
        return servico;
    }

    public byte[] getMensagemCompleta() {
        return mensagemCompleta;
    }

    public void setMensagemCompleta(byte[] mensagemCompleta) {
        this.mensagemCompleta = mensagemCompleta;
    }
    
    public byte[] getComprimentoTotal() {
        return comprimentoTotal;
    }

    public void setComprimentoTotal(byte[] comprimentoTotal) {
        this.comprimentoTotal = comprimentoTotal;
    }

    public byte[] getDados() {
        return dados;
    }

    public void setDados(byte[] dados) {
        this.dados = dados;
    }

    public byte[] getRestanteDosDados() {
        return RestanteDosDados;
    }

    public void setRestanteDosDados(byte[] RestanteDosDados) {
        this.RestanteDosDados = RestanteDosDados;
    }

    public int getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(int identificacao) {
        this.identificacao = identificacao;
    }

    public boolean isCanFragment() {
        return canFragment;
    }

    public void setCanFragment(boolean canFragment) {
        this.canFragment = canFragment;
    }

    public boolean isIsLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
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

    public String getIpv4Origem() {
        return ipv4Origem;
    }

    public void setIpv4Origem(String ipv4Origem) {
        this.ipv4Origem = ipv4Origem;
    }

    public String getIpv4Destino() {
        return ipv4Destino;
    }

    public void setIpv4Destino(String ipv4Destino) {
        this.ipv4Destino = ipv4Destino;
    }

    public String getPrimeiraParteMensagem() {
        return primeiraParteMensagem;
    }

    public void setPrimeiraParteMensagem(String primeiraParteMensagem) {
        this.primeiraParteMensagem = primeiraParteMensagem;
    }

    public String getRestanteDaMensagem() {
        return restanteDaMensagem;
    }

    public void setRestanteDaMensagem(String restanteDaMensagem) {
        this.restanteDaMensagem = restanteDaMensagem;
    }

    
}
