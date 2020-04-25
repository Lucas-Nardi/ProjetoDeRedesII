package Projeto;

import java.util.ArrayList;

public class FrontEnd {
    
    public static void main (String[]args){
        
        
        ArrayList<ItensDaTabela> tabelaDeRoteamento = new ArrayList<>();
        ItensDaTabela linha1 = new ItensDaTabela();
        linha1.setMascara("255.255.255.127");
        linha1.setEnderecoDeRede("192.168.95.1");
        linha1.setProximoSalto(null);
        linha1.setInterFace("m2");
        tabelaDeRoteamento.add(linha1);
        
        CamadaFisica barramento = new CamadaFisica("m2");
        
        
        
        Computador comp1 = new Computador(barramento, "192.168.25.2", "E0:D5:5E:89:16:10",tabelaDeRoteamento);
        Computador comp2 = new Computador(barramento, "192.168.25.1", "E0:D5:5E:89:16:10",tabelaDeRoteamento);
        barramento.attach(comp1);
        barramento.attach(comp2);        
        Roteador roteador = new Roteador("192.168.0.1","E0:D5:5E:89:16:10",tabelaDeRoteamento);
        roteador.adicionarBarramento(barramento);
        
        Mensagem mensagem  = new Mensagem("Abacaxi","192.168.25.1");
        comp1.Send(mensagem);
       
    }
}
