package Projeto;

public class FrontEnd {
    
    public static void main (String[]args){
        
        CamadaFisica barramento = new CamadaFisica();
        Computador comp1 = new Computador(barramento, "192.168.25.2", "MAC ORIGEM");
        Computador comp2 = new Computador(barramento, "192.168.25.1", "MAC DESTINO");
        barramento.attach(comp1);
        barramento.attach(comp2);        
        Mensagem mensagem  = new Mensagem("Abacaxi","192.168.25.1");
        comp1.Send(mensagem);

    }
}
