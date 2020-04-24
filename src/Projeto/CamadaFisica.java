package Projeto;

import java.util.ArrayList;

public class CamadaFisica implements Observable{ // É o barramento

    ArrayList <Observer> observadores;
    String id;
    
    
    public CamadaFisica(String id){
        
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
        
        for(Observer o: observadores){
            
            if(o instanceof Computador){
                
                Computador comp = (Computador) o;
                
                if(mensagem instanceof PacoteArp){                
                    
                    arp = (PacoteArp) mensagem;
                    
                    if(!arp.getMacDestino().equals("0")){ // MEU ARP É O REPLY
                        
                        if(comp.getCamadaRedes().getIpv4().equals(arp.getIpV4Origem())){ // SO ENVIAR O ARP REPLY PARA O COMPUTADOR DE ORIGEM
                            
                            comp.Receive(mensagem); // Recebe a mensagem do Barramento
                        }
                        
                    }else{                        
                        
                        comp.Receive(mensagem); // Manda o pacote arp para todos os computadores que estão no barramento
                    }                
                }
                if(mensagem instanceof PacoteIpv4){                    
                    
                    comp.Receive(mensagem);
                }
            }
            if(o instanceof Roteador){
                
                Roteador router = (Roteador) o;
                router.Receive(mensagem); // Recebe a mensagem do barramento
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
