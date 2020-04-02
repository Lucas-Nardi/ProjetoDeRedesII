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
        
        for(Observer o: observadores){
            
            if(o instanceof Computador){
                
                Computador comp = (Computador) o;
                comp.Receive(mensagem); // Recebe a mensagem do Barramento
            }
            
            if(o instanceof Roteador){
                
                Roteador router = (Roteador) o;
                router.Receive(mensagem); // Recebe a mensagem do barramento
            }
        
        }
    }
}
