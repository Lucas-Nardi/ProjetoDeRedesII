package Projeto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FrontEnd {
    
    public static void main (String[]args){
        
//        CamadaFisica barramento = new CamadaFisica();
//        Computador comp = new Computador(barramento, "192.168.25.142", "MacAddress");
//        barramento.attach(comp);
//        comp.Send("Abacaxi");
        ArrayList <Pacote> p = new ArrayList <> ();
        Pacote pacote = new Pacote(1,"abacaxi","192.168.1.55","185.255.148.24",1);
        pacote.PreencherComprimentoTotal();
        String mensagem;
        p = pacote.Fragmentar();
        
        System.out.println("Mensagem: abacaxi" );
        
        int cont = 1;
        for(Pacote pa : p){
            
            System.out.println("Pacote " + cont + "---------------------------------------------------");
            System.out.println("");
            System.out.println("OffSet: " + pa.getOffSet());
            System.out.println("ComprimentoTotal: " + pa.getComprimentoTotal().length);
            mensagem = new String(pa.getDados(),StandardCharsets.UTF_8);
            System.out.println("Dados: " + mensagem);
            System.out.println("O Dado é verdadeiro: " + mensagem.equals(pa.getPrimeiraParteMensagem()));
            System.out.println("Restante da Mensagem: " + pa.getRestanteDaMensagem());
            System.out.println("");
            cont ++;
        }
    }
}
