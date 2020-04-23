package Projeto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PacoteIpv4  implements Comparable< PacoteIpv4 >{

    int VER;  // Tem 4 bits logo pode ser (0-15)
    //0000, 0001,0010,0011,0100,0101,0110,0111,1000,1001,1010,1011,1100,1101,1111
    int Hlen;
    byte servico;            // Setar como tudo zero
    byte[] comprimentoTotal; // HLEN * 4
    byte[] dados;
    byte[] RestanteDosDados; // Restante dos dados para os proximos pacotes
    byte[] mensagemCompleta; // Mensagem inicial (completa) 
    int checksum;         // 2 Bytes , precisamos calcular
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
        this.tempoDeVida = 255;
        this.ipv4Origem = ipv4Origem;
        this.ipv4Destino = ipv4Destino;
        this.protocolo = protocolo;    //  1 == ICMP, 2== IGMP, 6== TCP, 17 == UDP, 89 == OSPF  -- pagina 588
        this.mtu = mtu;
        this.checksum = 0;             //   inicialmente possui valor zero
        

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

        int linhas = 10;
        int colunas = 4;
        char [][] matStr = new char [linhas][colunas];
        int [][] matInt;
        int [] somaDec = new int[colunas];
        int [] copiaSomaDec = new int[colunas];
        char [] somaHex;

        //preenche matriz de char's
        //primeira linha da matriz
        matStr[0][0] = Integer.toHexString(VER).charAt(0);
        matStr[0][1] = Integer.toHexString(Hlen).charAt(0);
       if(Integer.toHexString(servico).length() == 2){
           matStr[0][2] = Integer.toHexString(servico).charAt(0);
           matStr[0][3] = Integer.toHexString(servico).charAt(1);
       } else {
           matStr[0][2] = '0';
           matStr[0][3] = Integer.toHexString(servico).charAt(0);
       }

       //segunda linha da matriz
        int tamComprimento = Integer.toHexString(comprimentoTotal.length).length()-1;
        for(int i = colunas-1; i >= 0; i--){
            if(tamComprimento >=0){
                matStr[1][i] = Integer.toHexString(comprimentoTotal.length).charAt(tamComprimento);
                tamComprimento--;
            } else {
                matStr[1][i] = '0';
            }
        }

        //terceira linha da matriz
        int tamIdentificacao = Integer.toHexString(identificacao).length()-1;
        for(int i = colunas-1; i >= 0; i--){
            if(tamIdentificacao >=0){
                matStr[2][i] = Integer.toHexString(identificacao).charAt(tamIdentificacao);
                tamIdentificacao--;
            } else {
                matStr[2][i] = '0';
            }
        }

        //quarta linha da matriz
        char [] flags =  new char [4];           // 3 bits de flags e 1 bit do offset
        flags[0] = Integer.toString(reservado).charAt(0);
        if(canFragment == true){
            flags[1] = '0';
        } else {
            flags[1] = '1';
        }
        if(isLast == true){
            flags[2] = '0';
        } else {
            flags[2] = '1';
        }
        if(Integer.toHexString(offSet).length() == 4){
            flags[3] = Integer.toHexString(offSet).charAt(0);
        } else {
            flags[3] = '0';
        }
        String flagString = new String(flags);
        matStr[3][0] = Integer.toHexString(Integer.parseInt(flagString,2)).charAt(0);

        int tamOffset = Integer.toHexString(offSet).length()-1;
        for(int i = colunas-1; i > 0; i--){
            if(tamOffset >=0){
                matStr[3][i] = Integer.toHexString(offSet).charAt(tamOffset);
                tamOffset--;
            } else {
                matStr[3][i] = '0';
            }
        }

        //quinta linha da matriz
        if(Integer.toHexString(tempoDeVida).length() == 2){
            matStr[4][0] = Integer.toHexString(tempoDeVida).charAt(0);
            matStr[4][1] = Integer.toHexString(tempoDeVida).charAt(1);
        } else {
            matStr[4][0] = '0';
            matStr[4][1] = Integer.toHexString(tempoDeVida).charAt(0);
        }
        if(Integer.toHexString(protocolo).length() == 2){
            matStr[4][2] = Integer.toHexString(protocolo).charAt(0);
            matStr[4][3] = Integer.toHexString(protocolo).charAt(1);
        } else {
            matStr[4][2] = '0';
            matStr[4][3] = Integer.toHexString(protocolo).charAt(0);
        }

        //sexta linha da matriz
        Integer [] numerosIpOrigem = convertIpToArrayOfInteger(ipv4Origem);
        //primera parte do ipv4Origem
        if(Integer.toHexString(numerosIpOrigem[0]).length() == 2){
            matStr[5][0] = Integer.toHexString(numerosIpOrigem[0]).charAt(0);
            matStr[5][1] = Integer.toHexString(numerosIpOrigem[0]).charAt(1);
        } else {
            matStr[5][0] = '0';
            matStr[5][1] = Integer.toHexString(numerosIpOrigem[0]).charAt(0);
        }
        //segunda parte do ipv4Origem
        if(Integer.toHexString(numerosIpOrigem[1]).length() == 2){
            matStr[5][2] = Integer.toHexString(numerosIpOrigem[1]).charAt(0);
            matStr[5][3] = Integer.toHexString(numerosIpOrigem[1]).charAt(1);
        } else {
            matStr[5][2] = '0';
            matStr[5][3] = Integer.toHexString(numerosIpOrigem[1]).charAt(0);
        }

        //setima linha da matriz
        //terceira parte do ipv4Origem
        if(Integer.toHexString(numerosIpOrigem[2]).length() == 2){
            matStr[6][0] = Integer.toHexString(numerosIpOrigem[2]).charAt(0);
            matStr[6][1] = Integer.toHexString(numerosIpOrigem[2]).charAt(1);
        } else {
            matStr[6][0] = '0';
            matStr[6][1] = Integer.toHexString(numerosIpOrigem[2]).charAt(0);
        }
        //quarta parte do ipv4Origem
        if(Integer.toHexString(numerosIpOrigem[1]).length() == 2){
            matStr[6][2] = Integer.toHexString(numerosIpOrigem[3]).charAt(0);
            matStr[6][3] = Integer.toHexString(numerosIpOrigem[3]).charAt(1);
        } else {
            matStr[6][2] = '0';
            matStr[6][3] = Integer.toHexString(numerosIpOrigem[3]).charAt(0);
        }

        //oitava linha da matriz
        Integer [] numerosIpDestino = convertIpToArrayOfInteger(ipv4Destino);
        //primera parte do ipv4Destino
        if(Integer.toHexString(numerosIpDestino[0]).length() == 2){
            matStr[7][0] = Integer.toHexString(numerosIpDestino[0]).charAt(0);
            matStr[7][1] = Integer.toHexString(numerosIpDestino[0]).charAt(1);
        } else {
            matStr[7][0] = '0';
            matStr[7][1] = Integer.toHexString(numerosIpDestino[0]).charAt(0);
        }
        //segunda parte do ipv4Origem
        if(Integer.toHexString(numerosIpDestino[1]).length() == 2){
            matStr[7][2] = Integer.toHexString(numerosIpDestino[1]).charAt(0);
            matStr[7][3] = Integer.toHexString(numerosIpDestino[1]).charAt(1);
        } else {
            matStr[7][2] = '0';
            matStr[7][3] = Integer.toHexString(numerosIpDestino[1]).charAt(0);
        }

        //nona linha da matriz
        //terceira parte do ipv4Destino
        if(Integer.toHexString(numerosIpDestino[2]).length() == 2){
            matStr[8][0] = Integer.toHexString(numerosIpDestino[2]).charAt(0);
            matStr[8][1] = Integer.toHexString(numerosIpDestino[2]).charAt(1);
        } else {
            matStr[8][0] = '0';
            matStr[8][1] = Integer.toHexString(numerosIpDestino[2]).charAt(0);
        }
        //quarta parte do ipv4Destino
        if(Integer.toHexString(numerosIpDestino[1]).length() == 2){
            matStr[8][2] = Integer.toHexString(numerosIpDestino[3]).charAt(0);
            matStr[8][3] = Integer.toHexString(numerosIpDestino[3]).charAt(1);
        } else {
            matStr[8][2] = '0';
            matStr[8][3] = Integer.toHexString(numerosIpDestino[3]).charAt(0);
        }

        //decima linha da matriz (checksum)
        int tamChecksum = Integer.toHexString(checksum).length()-1;
        for(int i = colunas-1; i >= 0; i--){
            if(tamChecksum >=0){
                matStr[9][i] = Integer.toHexString(checksum).charAt(tamChecksum);
                tamChecksum--;
            } else {
                matStr[9][i] = '0';
            }
        }


        //criar matriz decimal a partir da matriz de caracteres
        matInt = criarMatrizNumerica(matStr, linhas, colunas);

        //somar colunas da matriz decimal e armazenar em vetor decimal
        for(int i = colunas-1; i >= 0; i--){
            for(int j = 0; j < linhas; j++){
                somaDec[i] = somaDec[i] + matInt[j][i];
            }
        }

        for(int i = 0; i < somaDec.length; i++){                //guardando soma original das colunas
            copiaSomaDec[i] = somaDec[i];
        }


        //realiza o calculo do que sobe e  do que fica no vetor somaDec e retorna no vetor somaHex o resultado em hexa
        somaHex = calculaSomahexa(somaDec, colunas);

        //realizar o complemento de um no vetor somaHex
        calculaComplementoDeUm(somaHex);

        //transforma somaHex em uma string
        String somaHexStr = new String(somaHex);

//        converte o string em inteiro e adiciona ao checksum
        checksum = Integer.parseInt(somaHexStr,16);



//        System.out.println(checksumBinStr.substring(0,8));
//        System.out.println(checksumBinStr.substring(8,16));
//
//        System.out.println(Integer.parseInt(checksumBinStr.substring(0,8),2));
//        System.out.println(Integer.parseInt(checksumBinStr.substring(8,16),2));
        //checksum[0] = Byte.parseByte(checksumBinStr.substring(0,5),2);


        for(int i = 0; i < linhas; i++){
            for(int j = 0; j < colunas; j++){
                System.out.print(matStr[i][j] + ", ");

            }
            System.out.println();
        }
        System.out.println("================================");

        for(int i = 0; i < linhas; i++){
            for(int j = 0; j < colunas; j++){
                System.out.print(matInt[i][j] + ", ");

            }
            System.out.println();
        }
        System.out.println();
        for(int i = 0; i < copiaSomaDec.length; i++){
            System.out.print(copiaSomaDec[i] + ", ");
        }
        System.out.println();
        for(int i = 0; i < somaHex.length; i++){
            System.out.print(somaHex[i] + ", ");
        }
        System.out.println(somaHex);
        System.out.println();

       // System.out.println(Integer.parseInt(somaHexStr,16));
        System.out.println(checksum);
        //System.out.println(Integer.toBinaryString(checksum));

    }

    public static Integer[] convertIpToArrayOfInteger(String ip) {
        List<String> splittedIp = Arrays.asList(ip.split("[.]"));
        List<Integer> splittedIpIntegers = splittedIp.stream().map(Integer::valueOf).collect(Collectors.toList());
        return splittedIpIntegers.toArray(new Integer[0]);
    }

    public char [] calculaSomahexa(int [] somaDec, int colunas){
        char [] somaHex = new char[colunas];
        int sobe = 0;
        int [] copiaSomaDec = new int[colunas];

        for(int i = 0; i < somaDec.length; i++){                //guardando soma original das colunas
            copiaSomaDec[i] = somaDec[i];
        }

        for(int i = colunas-1; i >= 0; i--){
          if(Integer.toHexString(somaDec[i]).length() == 1){                //se a string hexadecimal tiver um so caracter
              somaHex[i] = Integer.toHexString(somaDec[i]).charAt(0);
              sobe = 0;
          } else{                                                           //se a string hexadecimal tiver dois caracteres
              somaHex[i] = Integer.toHexString(somaDec[i]).charAt(1);
              sobe = encontraValorChar(Integer.toHexString(somaDec[i]).charAt(0));
          }
          if(i > 0){                                                    //enquanto n chega na ultima coluna sobe eh adicionado na coluna seguinte
              somaDec[i-1] = somaDec[i-1] + sobe;
          } else {                                                       //quando chegamos na ultima coluna sobe tem q ser adicionado a primeira coluna(a mais a direita)
              somaDec[colunas-1] = somaDec[colunas-1] + sobe;
              if(Integer.toHexString(somaDec[colunas-1]).length() == 1){                //se a string hexadecimal tiver um so caracter
                  somaHex[colunas-1] = Integer.toHexString(somaDec[colunas-1]).charAt(0);
              } else{                                                           //se a string hexadecimal tiver dois caracteres
                  copiaSomaDec[colunas-1] = copiaSomaDec[colunas-1] + sobe;      //adiciono o sobra a ultima coluna dda copia do vetor decimal e faco um novo preenchimento do vetor hexa
                  return verificacaoCalculoSomaHexa(copiaSomaDec, colunas);
              }
          }
        }
        return somaHex;
    }

    public char [] verificacaoCalculoSomaHexa(int [] somaDec, int colunas){
        char [] somaHex = new char[colunas];
        int sobe = 0;

        for(int i = colunas-1; i >= 0; i--){
            if(Integer.toHexString(somaDec[i]).length() == 1){                //se a string hexadecimal tiver um so caracter
                somaHex[i] = Integer.toHexString(somaDec[i]).charAt(0);
                sobe = 0;
            } else{                                                           //se a string hexadecimal tiver dois caracteres
                somaHex[i] = Integer.toHexString(somaDec[i]).charAt(1);
                sobe = encontraValorChar(Integer.toHexString(somaDec[i]).charAt(0));
            }
            if(i > 0){                                                    //enquanto n chega na ultima coluna sobe eh adicionado na coluna seguinte
                somaDec[i-1] = somaDec[i-1] + sobe;
            }
        }
        return somaHex;
    }

    public int[][] criarMatrizNumerica( char  [][] matStr, int linhas, int colunas){
        int [][] matInt = new int [linhas][colunas];

        for(int i = 0; i < linhas; i++){
            for(int j = 0; j < colunas; j++){
                matInt[i][j] = encontraValorChar(matStr [i][j]);
            }
        }

        return matInt;
    }

    public void calculaComplementoDeUm(char [] somaHex){
        for(int i = 0; i < somaHex.length; i++){
            if(somaHex[i] == 'f'){
                somaHex[i] = '0';
            } else if(somaHex[i] == 'e'){
                somaHex[i] = '1';
            } else if(somaHex[i] == 'd'){
                somaHex[i] = '2';
            } else if(somaHex[i] == 'c'){
                somaHex[i] = '3';
            } else if(somaHex[i] == 'b'){
                somaHex[i] = '4';
            } else if(somaHex[i] == 'a'){
                somaHex[i] = '5';
            } else if(somaHex[i] == '9'){
                somaHex[i] = '6';
            } else if(somaHex[i] == '8'){
                somaHex[i] = '7';
            } else if(somaHex[i] == '7'){
                somaHex[i] = '8';
            } else if(somaHex[i] == '6'){
                somaHex[i] = '9';
            } else if(somaHex[i] == '5'){
                somaHex[i] = 'a';
            } else if(somaHex[i] == '4'){
                somaHex[i] = 'b';
            } else if(somaHex[i] == '3'){
                somaHex[i] = 'c';
            } else if(somaHex[i] == '2'){
                somaHex[i] = 'd';
            } else if(somaHex[i] == '1'){
                somaHex[i] = 'e';
            } else if(somaHex[i] == '0'){
                somaHex[i] = 'f';
            }
        }
    }

    public int encontraValorChar(char caracter){
        switch (caracter){
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
                return 10;
            case 'b':
                return 11;
            case 'c':
                return 12;
            case 'd':
                return 13;
            case 'e':
                return 14;
            case 'f':
                return 15;
            default:
                return -1;

        }
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

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
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
