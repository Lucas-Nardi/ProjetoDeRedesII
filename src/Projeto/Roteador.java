package Projeto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class Roteador implements Observer {

    String ipv4, macAddress;
    PacoteArp pacoteArp;
    boolean arpRequest = false;
    ArrayList<ItensDaTabela> tabelaDeRoteamento;
    ArrayList<CamadaFisica> listaDeBarramentos;
    PacoteIpv4 pacoteIpv4;
    String idBarramento;
    boolean fizArpReply = false;

    public Roteador(String ipv4, String macAddress) {

        this.ipv4 = ipv4;
        this.macAddress = macAddress;
        this.listaDeBarramentos = new ArrayList<>();
    }

    public void SendData(Object mensagem, String barramento) { // Manda pra outra maquina ou roteador

        PacoteArp pacoteArp;
        PacoteIpv4 pacoteIpv4;

        if (arpRequest) { // Preciso fazer arp Request

            pacoteIpv4 = (PacoteIpv4) mensagem;
            
            for (CamadaFisica b : listaDeBarramentos) {
                
                
                    
                if (b.id.equals(this.getIdBarramento())) {
                    System.out.println("--------------------------------------------------------");
                    System.out.println("CRIANDO UM PACOTE ARP");
                    this.fizArpReply  = false;
                    if (pacoteIpv4.getEndereçoRoteador() == null) { // Destino do arp é o destino do pacote

                        criarPacoteArp(1, this.getIpv4(), pacoteIpv4.getIpv4Destino(), this.getMacAddress(), "0");

                    } else { // Destino do arp é o destino do Endereço do Roteador que tem no pacote

                        criarPacoteArp(1, this.getIpv4(), pacoteIpv4.getEndereçoRoteador(), this.getMacAddress(), "0");
                    }
                    System.out.println("ENVIANDO ARP REQUEST");
                    System.out.println("ORIGEM DO PACOTE: " + this.pacoteArp.getIpV4Origem());
                    System.out.println("DESTINO DO PACOTE: " + this.pacoteArp.getIpV4Destino());
                    System.out.println("MAC ADDRESS DESTINO: " + this.pacoteArp.getMacDestino());
                    System.out.println("--------------------------------------------------------");
                    b.notifyObserver(this.pacoteArp); // Envia o arp para o barramento destino
                }
            }

        } else { // Nao precisa fazer arp ou já fiz arp e receb a resposta

            if (mensagem instanceof PacoteIpv4) {

                pacoteIpv4 = (PacoteIpv4) mensagem;

                for (CamadaFisica b : listaDeBarramentos) {

                    if (b.id.equals(this.getIdBarramento())) {
                        
                        System.out.println("ENVIANDO PACOTE IPV4 PARA O BARRAMENTO " + barramento);
                        System.out.println("--------------------------------------------------------");
                        b.notifyObserver(pacoteIpv4);
                    }
                }
            }
            if (mensagem instanceof PacoteArp) { // Enviando um arp Reply
                
                pacoteArp = (PacoteArp) mensagem;
                System.out.println("ENVIANDO O ARP REPLY");
                System.out.println("--------------------------------------------------------");
                
                for (CamadaFisica ba : listaDeBarramentos) {

                    if (ba.getId().equals(barramento)) {
                        
                        System.out.println("QUE BARRAMENTO O ROTEADOR ESTA ENVIANDO O ARP REPLY: " + ba.getId());
                        System.out.println("--------------------------------------------------------");
                        ba.notifyObserver(pacoteArp);
                    }
                }
            }
        }
    }
    
    public void ReceiveData(Object mensagem, String idBarramento){
        
        
        PacoteArp pacoteArp;
        PacoteIpv4 pacoteIpv4;

        if (mensagem instanceof PacoteArp) {

            pacoteArp = (PacoteArp) mensagem;

            if (pacoteArp.getOperacao() == 2 && pacoteArp.getIpV4Origem().equals(this.ipv4) && fizArpReply == false) {  // Esse pacote ja é o arp reply, logo posso enviar meu pacote
              
                System.out.println("ROTEADOR: " + this.getIpv4() + " RECEBEU UM PACOTE ARP DO ARP REPLY");
                System.out.println("ORIGEN DO PACOTE: " + pacoteArp.getIpV4Origem());
                System.out.println("DESTINO DO PACOTE: " + pacoteArp.getIpV4Destino());
                System.out.println("MAC ADDRESS DESTINO " + pacoteArp.getMacDestino());
                
                pacoteArp.setMacDestino(pacoteArp.getMacDestino());
                this.pacoteArp.setOperacao(2);
                arpRequest = false; // Isso é um arp reply               
                fizArpReply = true;
                this.SendData(this.pacoteIpv4, idBarramento); // Verificar A tabela de roteamento

            } else { // SOU O COMPUTADOR DE DESTINO E PRECISO COLOCAR O MEU MACADDRESS NO ARP RECEBIDO

                if (pacoteArp.getIpV4Destino().equals(this.ipv4)) {

                    
                    System.out.println("ROTEADOR: " + this.getIpv4() + " RECEBEU UM PACOTE ARP DO ARP REQUEST");
                    System.out.println("ORIGEN DO PACOTE: " + pacoteArp.getIpV4Origem());
                    System.out.println("DESTINO DO PACOTE: " + pacoteArp.getIpV4Destino());
                    System.out.println("MAC ADDRESS DESTINO " + pacoteArp.getMacDestino());                  
                    
                    pacoteArp.setMacDestino(this.getMacAddress());
                    pacoteArp.setOperacao(2);
                    this.SendData(pacoteArp, idBarramento); // Enviar a resposta para o mesmo barramento
                }
                if(fizArpReply == false){
                    System.out.println("ESSE PACOTE ARP NAO É PARA ESSTE ROTEADOR: " + this.getIpv4());
                }
            }

        } else if (mensagem instanceof PacoteIpv4) {// Recebi um pacote , primeiro verificar a tabela e depois fazer arp

            pacoteIpv4 = (PacoteIpv4) mensagem;
            String informacao[];
            String problema;
            int checksum;

            if (pacoteIpv4.getEndereçoRoteador() != null && pacoteIpv4.getEndereçoRoteador().equals(this.ipv4)) {
                
                checksum = pacoteIpv4.getChecksum();
                pacoteIpv4.CalcularChecksum();
                
                
                
                System.out.println("ROTEADOR: " + this.getIpv4() + " RECEBENDO UM PACOTE IPV4");
                System.out.println("ORIGEN DO PACOTE: " + pacoteIpv4.getIpv4Origem());
                System.out.println("DESTINO DO PACOTE: " + pacoteIpv4.getIpv4Destino());
                System.out.println("CALCULANDO O CHECKSUM ");
                
                if (pacoteIpv4.getChecksum() == 0) { // Pacote nao perdeu nenhum informação
                    
                    System.out.println("RESULTADO DO CHECKSUM: 0" );
                    pacoteIpv4.setChecksum(checksum); // Não perder o valor original do checksum
                    
                    informacao = encontrarDestino(pacoteIpv4.getIpv4Destino());
                    
                    if(informacao[0] == null) { //  PRECISO ENVIAR O PACOTE PARA UM BARRAMENTO
                        System.out.println("PRECISO FAZER UM ENTREGA INDIRETA");
                   
                        this.pacoteIpv4 = pacoteIpv4;        // Salva o pacote que devo enviar , pois preciso fazer o arp antes
                        this.idBarramento = informacao[1];   // Guarda o id do barramento que devo mandar o arp e o pacote
                        pacoteIpv4.setEndereçoRoteador(null); // atualizo o novo endereco de um roteador se tiver.
                        this.arpRequest = true;                   // seto que preciso fazer o arp;
                        this.SendData(pacoteIpv4, informacao[1]); 
                                       
                    } else if (informacao[0].equals("unreachable")) {

                        System.out.println("Host inalcançavel");
                        System.out.println("--------------------------------------------------------");

                    }else{ // PRECISO ENVIAR O PACOTE PARA OUTRO ROTEADOR
                        System.out.println("ENVIAR PACOTE PARA OUTRO ROTEADOR");
                        this.pacoteIpv4 = pacoteIpv4;        // Salva o pacote que devo enviar , pois preciso fazer o arp antes
                        this.idBarramento = informacao[1];   // Guarda o id do barramento que devo mandar o arp e o pacote
                        pacoteIpv4.setEndereçoRoteador(informacao[0]); // atualizo o novo endereco de um roteador se tiver.
                        this.arpRequest = true;                   // seto que preciso fazer o arp;
                        this.SendData(pacoteIpv4, informacao[1]); 
                    } 

                } else { // Pacote Perdeu informação
                    
                    problema = new String(pacoteIpv4.getDados(), StandardCharsets.UTF_8);
                    
                    System.out.println("O pacote que tinha " + problema + "essa informação perdeu algum dado");
                    System.out.println("--------------------------------------------------------");
                }
            }else{
                
                System.out.println("ESSE PACOTE IPV4 NÃO É PARA O ROTEADOR: " + this.getIpv4());
                System.out.println("--------------------------------------------------------");
            }
        }
    }

    public void adicionarBarramento(CamadaFisica barramento) {

        this.listaDeBarramentos.add(barramento);
    }

    String[] encontrarDestino(String ipv4Destino) {
        
        System.out.println("VERIFCANDO A TABELA DE ROTEAMENTO");
        String resultado;
        String retorno[] = new String[2];
        for (ItensDaTabela iten : tabelaDeRoteamento) {

            
            resultado = compararMascarasComDestino(iten.getMascara(), ipv4Destino);            
            
            if (iten.getEnderecoDeRede().compareTo(resultado) == 0) {
                retorno[0] = iten.getProximoSalto();
                retorno[1] = iten.getInterFace();
                return retorno;
            }
        }
        retorno[0] = "unreachable";
        retorno[1] = "null";
        return retorno;
    }

    String compararMascarasComDestino(String ipv4_1, String ipv4_2) {

        String ipv4_1_SemPonto[];
        String ipv4_2_SemPonto[];

        ipv4_1_SemPonto = ipv4_1.split("\\.");
        ipv4_2_SemPonto = ipv4_2.split("\\.");

        int ipv4_1_Decimal[] = new int[ipv4_1_SemPonto.length];
        int ipv4_2_Decimal[] = new int[ipv4_1_SemPonto.length];
        int resultado[] = new int[ipv4_1_SemPonto.length];

        for (int i = 0; i < ipv4_1_SemPonto.length; i++) {

            //System.out.println("-------------------------------------------------------------------");            
            ipv4_1_Decimal[i] = Integer.parseInt(ipv4_1_SemPonto[i]); // Transforma a cada parte do ipv4 em decimal            
            //System.out.println("Valor em Inteiro IPV4 1: " + ipv4_1_Decimal[i]);                        
            ipv4_2_Decimal[i] = Integer.parseInt(ipv4_2_SemPonto[i]);
            //System.out.println("Valor em Inteiro IPV4 2: " + ipv4_2_Decimal[i]);
            resultado[i] = ipv4_2_Decimal[i] & ipv4_1_Decimal[i];         // Faz Operaçao AND
            //System.out.println("Resultado em decimal : " + resultado[i]);                 
            //System.out.println();
        }
        String resultadoAND = "";

        for (int i = 0; i < resultado.length; i++) {

            if (i < 3) {
                resultadoAND = resultadoAND + resultado[i] + ".";
            } else {
                resultadoAND = resultadoAND + resultado[i];
            }
        }        

        return resultadoAND;
    }

    void criarPacoteArp(int operacao, String ipV4Origem, String ipV4Destino, String macOrigem, String macDestino) {

        pacoteArp = new PacoteArp(operacao, ipV4Origem, ipV4Destino, macOrigem, macDestino);
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public PacoteIpv4 getPacoteIpv4() {
        return pacoteIpv4;
    }

    public void setPacoteIpv4(PacoteIpv4 pacoteIpv4) {
        this.pacoteIpv4 = pacoteIpv4;
    }

    public String getIdBarramento() {
        return idBarramento;
    }

    public void setIdBarramento(String idBarramento) {
        this.idBarramento = idBarramento;
    }
    
    @Override
    public void Send(Object mensagem) {
    }
    
    @Override
    public void Receive(Object mensagem) { // Verifica o checksum e verifica a tabela de roteamento

    }

    public ArrayList<ItensDaTabela> getTabelaDeRoteamento() {
        return tabelaDeRoteamento;
    }

    public void setTabelaDeRoteamento(ArrayList<ItensDaTabela> tabelaDeRoteamento) {
        this.tabelaDeRoteamento = tabelaDeRoteamento;
    }

    public ArrayList<CamadaFisica> getListaDeBarramentos() {
        return listaDeBarramentos;
    }

    public void setListaDeBarramentos(ArrayList<CamadaFisica> listaDeBarramentos) {
        this.listaDeBarramentos = listaDeBarramentos;
    }

    public boolean isFizArpReply() {
        return fizArpReply;
    }

    public void setFizArpReply(boolean fizArpReply) {
        this.fizArpReply = fizArpReply;
    }
    
}
