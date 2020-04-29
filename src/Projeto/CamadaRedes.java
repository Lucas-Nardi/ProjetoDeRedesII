package Projeto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class CamadaRedes {

    CamadaEnlace enlace;
    CamadaTransporte transporte;
    int numeroDaMensagem;
    int protocolo;
    boolean arpRequest;
    String ipv4;
    String mascara;
    String mensagemOriginal;
    String netID;
    String ipv4Roteador;
    String ipv4Destino;
    HashMap<String, ArrayList<ArrayList<PacoteIpv4>>> mapping;
    PacoteArp pacoteArp;
    ArrayList<ItensDaTabela> tabelaDeRoteamento;

    public CamadaRedes(String ipv4, int valor) {

        numeroDaMensagem = 0;
        this.ipv4 = ipv4;
        pegarMascara(valor);
        mapping = new HashMap<String, ArrayList<ArrayList<PacoteIpv4>>>();
        arpRequest = false;
        this.tabelaDeRoteamento = tabelaDeRoteamento;
        this.netID = pegarNetid(this.mascara, this.ipv4);
    }

    void pegarMascara(int mascara) {

        switch (mascara) {

            case 0:
                this.mascara = "000.000.000.000";
                break;
            case 1:
                this.mascara = "128.000.000.000";
                break;
            case 2:
                this.mascara = "192.000.000.000";
                break;
            case 3:
                this.mascara = "224.000.000.000";
                break;
            case 4:
                this.mascara = "240.000.000.000";
                break;
            case 5:
                this.mascara = "248.000.000.000";
                break;
            case 6:
                this.mascara = "252.000.000.000";
                break;
            case 7:
                this.mascara = "254.000.000.000";
                break;
            case 8:
                this.mascara = "255.000.000.000";
                break;
            case 9:
                this.mascara = "255.128.000.000";
                break;
            case 10:
                this.mascara = "255.192.000.000";
                break;
            case 11:
                this.mascara = "255.224.000.000";
                break;
            case 12:
                this.mascara = "255.240.000.000";
                break;
            case 13:
                this.mascara = "255.248.000.000";
                break;
            case 14:
                this.mascara = "255.252.000.000";
                break;
            case 15:
                this.mascara = "255.254.000.000";
                break;
            case 16:
                this.mascara = "255.255.000.000";
                break;
            case 17:
                this.mascara = "255.255.128.000";
                break;
            case 18:
                this.mascara = "255.255.192.000";
                break;
            case 19:
                this.mascara = "255.255.224.000";
                break;
            case 20:
                this.mascara = "255.255.240.000";
                break;
            case 21:
                this.mascara = "255.255.248.000";
                break;
            case 22:
                this.mascara = "255.255.252.000";
                break;
            case 23:
                this.mascara = "255.255.254.000";
                break;
            case 24:
                this.mascara = "255.255.255.000";
                break;
            case 25:
                this.mascara = "255.255.255.128";
                break;
            case 26:
                this.mascara = "255.255.255.192";
                break;
            case 27:
                this.mascara = "255.255.255.224";
                break;
            case 28:
                this.mascara = "255.255.255.240";
                break;
            case 29:
                this.mascara = "255.255.255.248";
                break;
            case 30:
                this.mascara = "255.255.255.252";
                break;
            case 31:
                this.mascara = "255.255.255.254";
                break;
            case 32:
                this.mascara = "255.255.255.255";
                break;
        }
    }

    String pegarNetid(String ipv4_1, String ipv4_2) {

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

    byte[] restruturarMensagem(PacoteIpv4 p) {

        int dadoFinal = p.getMensagemCompleta().length;  // Tem o tamanho do mensagem antes da fragmentação
        int dadoAtual = 0;                               // Tem o tamanho da mensagem com relaçao aos pacote que ja chegaram
        int j = 0;
        ArrayList<ArrayList<PacoteIpv4>> origemMensagens;
        ArrayList<PacoteIpv4> listaPacotes = null;
        PacoteIpv4 pacote;
        int qualMensagem = 0;
        byte[] mensagem = new byte[dadoFinal];            // Criar Mensagem

        if (!mapping.isEmpty()) { // Segun

            if (p.getOffSet() == 0 && p.getDados().length == dadoFinal) { // Pacote nao foi fragmentado

                return p.getDados();
            }
            if (mapping.containsKey(p.getIpv4Origem())) { // Ja tenho uma mensagem desse computador

                origemMensagens = mapping.get(p.getIpv4Origem());  // Pegar a arrayList que tem todas as mensagens de um computador

                for (int i = 0; i < origemMensagens.size(); i++) {  // Pegar Qual Mensagem esse pacote faz parte

                    listaPacotes = origemMensagens.get(i);
                    qualMensagem = listaPacotes.get(0).getIdentificacao();

                    if (qualMensagem == p.getIdentificacao()) { // Descobri qual é a mensagem que o pacote faz 
                        qualMensagem = i;
                        break;
                    }
                }

                for (int i = 0; i < listaPacotes.size(); i++) { // Ve se a quantidade de dados nos pacote guardados + o atual formam 
                    // a mensagem completa (inicial)
                    pacote = listaPacotes.get(i);
                    dadoAtual = dadoAtual + pacote.getDados().length;
                }
                dadoAtual = dadoAtual + p.getDados().length;

                if (dadoFinal == dadoAtual) {  // Todos os Pacotes Chegaram preciso remover eles da lista

                    System.out.println("CHEGOU TODOS OS PACOTES");
                    System.out.println("ORGANIZANDO OS PACOTES");

                    listaPacotes.add(p);

                    Collections.sort(listaPacotes);

                    for (int i = 0; i < listaPacotes.size(); i++) {

                        pacote = listaPacotes.get(i);
                        byte[] valor = pacote.getDados();

                        for (int k = 0; k < pacote.getDados().length; k++) { // Começar a construir a mensagem

                            mensagem[j] = valor[k];
                            j++;
                        }

                    }
                    origemMensagens.remove(qualMensagem);
                    return mensagem;

                } else { // Adicionar pacote na lista de pacote

                    listaPacotes.add(p);
                    Collections.sort(listaPacotes);
                }

            } else { // Não tenho mensagem desse pacote ainda
                origemMensagens = new ArrayList<>();
                listaPacotes = new ArrayList<>();
                listaPacotes.add(p);
                origemMensagens.add(listaPacotes);
                mapping.put(p.getIpv4Origem(), origemMensagens);
            }
        } else { // Primeira mensagem que esse computador recebeu

            origemMensagens = new ArrayList<>();
            listaPacotes = new ArrayList<>();
            listaPacotes.add(p);
            origemMensagens.add(listaPacotes);
            mapping.put(p.getIpv4Origem(), origemMensagens);
        }
        return null;
    }

    void criarPacoteArp(int operacao, String ipV4Origem, String ipV4Destino, String macOrigem, String macDestino) {

        pacoteArp = new PacoteArp(operacao, ipV4Origem, ipV4Destino, macOrigem, macDestino);
    }

    void ReceiveTransporte(Object mensagem, int protocolo) throws InterruptedException, ExecutionException { // Colocar essa mensagem em um pacote

        numeroDaMensagem++; // Recebi uma mensagem
        String resultado;
        int i = 1;

        if (mensagem instanceof Mensagem) {
            Mensagem m = (Mensagem) mensagem;
            this.arpRequest = m.isFazerArp();
        }

        if (arpRequest) { // Se arpResquest == true preciso fazer arp 

            this.protocolo = protocolo;
            Mensagem m = (Mensagem) mensagem;
            this.mensagemOriginal = m.getMensagem();
            this.ipv4Destino = m.getIpv4Destino();

            resultado = pegarNetid(this.mascara, m.getIpv4Destino());
            System.out.println("--------------------------------------------------------");
            System.out.println("RECEBI UM DADO DA CAMADA DE TRANSPORTE");
            System.out.println("SABER QUE TIPO DE ENTREGA O COMPUTADOR FARÁ (DIRETA OU INDIRETA)");
            if (resultado.compareTo(this.netID) == 0) { // Mesma rede

                System.out.println("ENTRA DIRETA");
                System.out.println("CRIANDO PACOTE ARP: ");
                this.criarPacoteArp(1, ipv4, m.getIpv4Destino(), this.enlace.getMacAddress(), "0");

            } else { // Esta em outra rede (Verificar a tabela de roteamento) para pegar o ipv4 do roteador

                System.out.println("ENTREGA INDIRETA");
                System.out.println("CRIANDO PACOTE ARP: ");

                for (ItensDaTabela item : tabelaDeRoteamento) {

                    resultado = pegarNetid(item.getMascara(), m.getIpv4Destino());

                    if (resultado.compareTo(item.getEnderecoDeRede()) == 0) {

                        this.ipv4Roteador = item.getProximoSalto();
                        this.criarPacoteArp(1, ipv4, ipv4Roteador, this.enlace.getMacAddress(), "0");
                    }
                }
            }
            System.out.println("ENVIANDO ARP REQUEST");
            System.out.println("ORIGEM DO PACOTE: " + this.pacoteArp.getIpV4Origem());
            System.out.println("DESTINO DO PACOTE: " + this.pacoteArp.getIpV4Destino());
            System.out.println("MAC ADDRESS DESTINO: " + this.pacoteArp.getMacDestino());
            System.out.println("--------------------------------------------------------");
            this.SendEnlace(pacoteArp);

        } else {       // Se a operacao for diferente de request == true , ela é repy tenho ja o macAddress do destino

            if (this.pacoteArp.getIpV4Origem().equals(this.ipv4)) {

                System.out.println("CRIANDO O PACOTE IPV4");
                ArrayList<PacoteIpv4> p = new ArrayList<>();
                PacoteIpv4 pacote = new PacoteIpv4(numeroDaMensagem, mensagemOriginal, this.ipv4, ipv4Destino, 1, this.enlace.getMtu());
                pacote.PreencherComprimentoTotal();
                pacote.setEndereçoRoteador(this.ipv4Roteador);

                p = pacote.Fragmentar();
   
                for (PacoteIpv4 pack : p) {
                    if (i == 0) {

                        System.out.println("");
                        System.out.println("ENVIANDO O PACOTE IPV4 " + i + " PARA O BARRAMENTO ");
                        System.out.println("--------------------------------------------------------");
                    } else {

                        System.out.println("");
                        System.out.println("");
                        System.out.println("ENVIANDO O PACOTE IPV4 " + i + " PARA O BARRAMENTO ");
                        System.out.println("--------------------------------------------------------");
                    }

                    this.SendEnlace(pack);
                    i++;
                }
            }
        }
        // UDP, TCP/IP protocolos que vem da camda de transporte
        // E passar para o pacote
    }

    void ReceiveEnlace(Object mensagem) throws InterruptedException, ExecutionException { // Ler o PacoteIpv4 Ipv4

        if (mensagem instanceof PacoteArp) {

            PacoteArp pacote = (PacoteArp) mensagem;

            if (pacote.getOperacao() == 2 && pacote.getIpV4Origem().equals(this.ipv4)) {  // Esse pacote ja é o arp reply, logo posso enviar meu pacote

                this.pacoteArp.setMacDestino(pacote.getMacDestino());
                this.pacoteArp.setOperacao(2);
                arpRequest = false; // Isso é um arp reply

                System.out.println("COMPUTADOR: " + this.getIpv4() + " RECEBEU UM PACOTE ARP DO ARP REPLY");
                System.out.println("ORIGEN DO PACOTE: " + pacote.getIpV4Origem());
                System.out.println("DESTINO DO PACOTE: " + pacote.getIpV4Destino());
                System.out.println("MAC ADDRESS DESTINO " + pacote.getMacDestino());
                System.out.println("--------------------------------------------------------");
                this.ReceiveTransporte(mensagemOriginal, protocolo);

            } else { // SOU O COMPUTADOR DE DESTINO E PRECISO COLOCAR O MEU MACADDRESS NO ARP RECEBIDO

                if (pacote.getIpV4Destino().equals(this.ipv4)) {

                    System.out.println("COMPUTADOR: " + this.getIpv4() + " RECEBEU UM PACOTE ARP DO ARP REQUEST");
                    System.out.println("ORIGEN DO PACOTE: " + pacote.getIpV4Origem());
                    System.out.println("DESTINO DO PACOTE: " + pacote.getIpV4Destino());
                    System.out.println("MAC ADDRESS DESTINO " + pacote.getMacDestino());
                    pacote.setMacDestino(this.enlace.getMacAddress());
                    pacote.setOperacao(2);

                    System.out.println("ENVIANDO O ARP REPLY");
                    System.out.println("--------------------------------------------------------");

                    this.SendEnlace(pacote);

                } else {
                    System.out.println("ESSE PACOTE ARP NÃO Ë PARA ESSE COMPUTADOR");
                    System.out.println("--------------------------------------------------------");
                }
            }
        } else if (mensagem instanceof PacoteIpv4) {

            PacoteIpv4 p = (PacoteIpv4) mensagem;
            String informacao, data;
            int checksum;

            if (p.getIpv4Destino().equals(this.ipv4)) {

                System.out.println("COMPUTADOR: " + this.getIpv4() + " RECEBEU UM PACOTE IPV4");
                System.out.println("ORIGEN DO PACOTE: " + p.getIpv4Origem());
                System.out.println("DESTINO DO PACOTE: " + p.getIpv4Destino());
                System.out.println("CALCULANDO O CHECKSUM ");
                checksum = p.getChecksum();
                p.CalcularChecksum();

                if (p.getChecksum() == 0) { // Pacote nao perdeu nenhum informação

                    data = new String(p.getDados(), StandardCharsets.UTF_8);
                    System.out.println("RESULTADO DO CHECKSUM: 0");
                    System.out.println("DADO DO PACOTE: " + data);
                    p.setChecksum(checksum); // NAO PERDER O VALOR DO CHECKSUM CALCULADO NA SUA CRIAÇÃO
                    System.out.println("VERIFICANDO SE TODOS OS PACOTES CHEGARAM");
                    CompletableFuture< byte[]> completableFuture = CompletableFuture.supplyAsync(() -> restruturarMensagem(p));

                    while (!completableFuture.isDone()) { // ENQUANTO A RESTRUTURAÇÃO NAO ESTA PRONTO

                    }
                    byte[] message = completableFuture.get();

                    if (message != null) { // TENHO TODOS OS PACOTES
                        System.out.println("ENVIANDO PACOTE PARA A CAMADA DE TRANSPORTE");
                        informacao = new String(message, StandardCharsets.UTF_8);
                        this.SendTransporte(informacao);

                    } else {

                        System.out.println("NÃO CHEGOU TODOS OS PACOTES");
                        System.out.println("--------------------------------------------------------");
                    }

                } else { // Pacote Perdeu informação

                    informacao = new String(p.getDados(), StandardCharsets.UTF_8);
                    System.out.println("O pacote ipv4 que tinha está informação " + informacao + "   perdeu algum dado durante o caminho.");
                    System.out.println("--------------------------------------------------------");
                }
            } else {

                System.out.println("ESSE PACOTE IPV4 NÃO É PARA ESTE COMPUTADOR: " + this.ipv4);
                System.out.println("--------------------------------------------------------");
            }
        }
    }

    void SendTransporte(Object mensagem) {

        this.transporte.ReceiveRedes(mensagem);
    }

    void SendEnlace(Object pacote) { // Mandar o pacote ipv4 com fragmentacao

        // Criar O pacote
        this.enlace.ReceiveRedes(pacote);

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

    public String getIpv4() {
        return ipv4;
    }

    public String getMascara() {
        return mascara;
    }

    public String getMensagemOriginal() {
        return mensagemOriginal;
    }

    public ArrayList<ItensDaTabela> getTabelaDeRoteamento() {
        return tabelaDeRoteamento;
    }

    public void setTabelaDeRoteamento(ArrayList<ItensDaTabela> tabelaDeRoteamento) {
        this.tabelaDeRoteamento = tabelaDeRoteamento;
    }
}
