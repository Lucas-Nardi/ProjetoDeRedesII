package Projeto;

import java.util.ArrayList;

public class CamadaFisica implements Observable{ // É o barramento

    ArrayList <Observer> observadores;
    //ArrayList <Pacote> pacotes;
    public CamadaFisica(){
        
        observadores = new ArrayList<>();
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
                        
                        comp.Receive(mensagem); // Recebe a mensagem do Barramento
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
}
