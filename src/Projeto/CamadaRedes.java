package Projeto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

class CamadaRedes {

    CamadaEnlace enlace;
    CamadaTransporte transporte;
    int numeroDaMensagem;
    String ipv4;
    String mascara;
    int protocolo;
    HashMap<String, ArrayList<ArrayList<Pacote>>> mapping;

    public CamadaRedes(String ipv4, int valor) {

        numeroDaMensagem = 0;
        this.ipv4 = ipv4;
        pegarMascara(valor);
        mapping = new HashMap<String, ArrayList<ArrayList<Pacote>>>();
    }

    void ReceiveTransporte(Object mensagem, int protocolo) { // Colocar essa mensagem em um pacote

        numeroDaMensagem++; // Recebi uma mensagem
        ArrayList<Pacote> p = new ArrayList<>();
        Pacote pacote = new Pacote(numeroDaMensagem, mensagem, this.ipv4, "185.255.148.24", 1, this.enlace.getMtu());
        p = pacote.Fragmentar();
        for (Pacote pack : p) {
            this.SendEnlace(pack);
        }

        this.protocolo = protocolo; // UDP, TCP/IP protocolos que vem da camda de transporte
        // E passar para o pacote
    }

    void SendTransporte(Object mensagem) {

        this.transporte.ReceiveRedes(mensagem);
    }

    void SendEnlace(Object pacote) { // Mandar o pacote ipv4 com fragmentacao
        // Criar O pacote

        this.enlace.ReceiveRedes(pacote);

    }

    void ReceiveEnlace(Object mensagem) throws InterruptedException { // Ler o Pacote Ipv4        
        
        
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

    byte[] reEstruturarMensagem(Pacote p) {

        int dadoFinal = p.getMensagemCompleta().length;  // Tem o tamanho do mensagem antes da fragmentação
        int dadoAtual = 0;                               // Tem o tamanho da mensagem com relaçao aos pacote que ja chegaram
        ArrayList<ArrayList<Pacote>> origemMensagens;
        ArrayList<Pacote> listaPacotes = null;
        Pacote pacote;
        int qualMensagem = 0;
        byte[] mensagem = new byte[dadoFinal];            // Criar Mensagem
        int j = 0;

        if (!mapping.isEmpty()) { // Segun

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
                    
                    listaPacotes.add(p);
                    Collections.sort(listaPacotes);
                    
                    for (int i = 0; i < listaPacotes.size(); i++) {
                      
                        pacote = listaPacotes.get(i);
                        byte[] valor = pacote.getDados();
                        
                        for (int k = 0; k < pacote.getDados().length; k++) { // Começar a construir a mensagem

                            mensagem[j] = valor[k];
                            j++;
                        }
                        listaPacotes.remove(i);
                    }
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
        return mensagem;
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
