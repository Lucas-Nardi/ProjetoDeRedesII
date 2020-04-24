package Projeto;

import java.nio.charset.StandardCharsets;

class Roteador  implements Observer{
    
    String ipv4, macAddress;
    PacoteArp pacoteArp;
    boolean arpRequest = false;
            
    @Override
    public void Send(Object mensagem) { // Manda pra outra maquina ou roteador
    
        
        if(! arpRequest){
            
            
        }
        
        
    }

    @Override
    public void Receive(Object mensagem) { // Verifica o checksum e verifica a tabela de roteamento
        
        PacoteArp pacoteArp;
        PacoteIpv4 pacoteIpv4;
        
        if (mensagem instanceof PacoteArp) {

            pacoteArp = (PacoteArp) mensagem;

            if (pacoteArp.getOperacao() == 2 && pacoteArp.getIpV4Origem().equals(this.ipv4)) {  // Esse pacote ja é o arp reply, logo posso enviar meu pacote

                pacoteArp.setMacDestino(pacoteArp.getMacDestino());
                this.pacoteArp.setOperacao(2);
                arpRequest = true; // Isso é um arp reply
                this.Send(mensagem); // Verificar A tabela de roteamento

            } else { // SOU O COMPUTADOR DE DESTINO E PRECISO COLOCAR O MEU MACADDRESS NO ARP RECEBIDO

                if (pacoteArp.getIpV4Destino().equals(this.ipv4)) {

                    pacoteArp.setMacDestino(this.getMacAddress());
                    pacoteArp.setOperacao(2);
                    this.Send(pacoteArp);

                }
            }
        } else if (mensagem instanceof PacoteIpv4) {

            pacoteIpv4 = (PacoteIpv4) mensagem;
            String informacao;
            
            if (pacoteIpv4.getIpv4Destino().equals(this.ipv4)) {

                pacoteIpv4.CalcularChecksum();
            
                if (pacoteIpv4.getChecksum() == 0) { // Pacote nao perdeu nenhum informação

                    // Colocar o pacote na lista de pacote    

                } else { // Pacote Perdeu informação
                    
                    informacao = new String(pacoteIpv4.getDados(), StandardCharsets.UTF_8);
                    System.out.println("O pacote que tinha "+  informacao + "essa informação perdeu algum dado");
                }
            }
        }        
        
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
    
}
