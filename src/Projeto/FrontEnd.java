package Projeto;

public class FrontEnd {
    
    public static void main (String[]args){
        
        CamadaFisica barramento = new CamadaFisica();
        Computador comp = new Computador(barramento, "192.168.25.142", "MacAddress");
        barramento.attach(comp);
        comp.Send("Abacaxi");
    }
}
