package Projeto;

import java.util.ArrayList;

public class CamadaFisica implements Observable { // É o barramento

    ArrayList<Observer> observadores;
    String id;

    public CamadaFisica(String id) {

        observadores = new ArrayList<>();
        this.id = id;
    }

    @Override
    public void attach(Observer o) {

        observadores.add(o);
    }

    @Override
    public void dettach(Observer o) {
        observadores.remove(o);
    }

    @Override
    public void notifyObserver(Object mensagem) {

        PacoteArp arp;
        PacoteIpv4 ipv4;
        
        System.out.println("QUE BARRAMENTO OU ESTOU: " + this.getId());
        System.out.println("--------------------------------------------------------"); 
        for (Observer o : observadores) {
            
            if (mensagem instanceof PacoteArp) {

                arp = (PacoteArp) mensagem;                
                
                if (o instanceof Computador) {

                    Computador comp = (Computador) o;
                    if (arp.getOperacao() == 2) { // MEU ARP É O REPLY

                        if (comp.getCamadaRedes().getIpv4().equals(arp.getIpV4Origem())) { // SO ENVIAR O ARP REPLY PARA O COMPUTADOR DE ORIGEM                            
                            
                            comp.Receive(mensagem); // Recebe a mensagem do Barramento
                        }

                    } else {
                        
                        //if(!arp.getIpV4Origem().equals(comp.getCamadaRedes().getIpv4())){
                            
                            comp.Receive(mensagem); // Manda o pacote arp para todos os computadores que estão no barramento
                        //}
                    }
                }
                if (o instanceof Roteador) {
                    
                    Roteador router = (Roteador) o;
                    
                    if (arp.getOperacao() == 2) { // MEU ARP É O REPLY

                        if (router.getIpv4().equals(arp.getIpV4Origem())) { // SO ENVIAR O ARP REPLY PARA O COMPUTADOR DE ORIGEM
                         
                            router.ReceiveData(mensagem,this.getId()); // Recebe a mensagem do Barramento
                        }

                    } else {
                        
                        //if(!arp.getIpV4Origem().equals(router.getIpv4())){
                            
                            router.ReceiveData(mensagem,this.getId());
                        //}                        
                    }                    
                }
            }
            if (mensagem instanceof PacoteIpv4) {

                if (o instanceof Computador) {

                    Computador comp = (Computador) o;
                    comp.Receive(mensagem);
                }
                if (o instanceof Roteador) {

                    Roteador router = (Roteador) o;
                    router.ReceiveData(mensagem,this.getId()); // Recebe a mensagem do barramento
                }
            }

        }
    }

    public ArrayList<Observer> getObservadores() {
        return observadores;
    }

    public void setObservadores(ArrayList<Observer> observadores) {
        this.observadores = observadores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
