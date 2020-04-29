package Projeto;

import java.util.ArrayList;

public class FrontEnd {
    
    
    public static String pegarNetid(String ipv4_1, String ipv4_2){
                
        String ipv4_1_SemPonto[];       
        String ipv4_2_SemPonto[];
        
        ipv4_1_SemPonto = ipv4_1.split("\\.");
        ipv4_2_SemPonto = ipv4_2.split("\\.");
        
        int ipv4_1_Decimal [] = new int[ipv4_1_SemPonto.length];
        int ipv4_2_Decimal [] = new int[ipv4_1_SemPonto.length];
        int resultado [] = new int[ipv4_1_SemPonto.length];
        
        for (int i = 0; i < ipv4_1_SemPonto.length; i++) {
            
            //System.out.println("-------------------------------------------------------------------");            
            ipv4_1_Decimal[i] = Integer.parseInt(ipv4_1_SemPonto[i]); // Transforma a cada parte do ipv4 em decimal            
            //System.out.println("Valor em Inteiro IPV4 1: " + ipv4_1_Decimal[i]);                        
            ipv4_2_Decimal[i] = Integer.parseInt(ipv4_2_SemPonto[i]);        
            //System.out.println("Valor em Inteiro IPV4 2: " + ipv4_2_Decimal[i]);
            resultado[i] =  ipv4_2_Decimal[i] & ipv4_1_Decimal[i];         // Faz Operaçao AND
            //System.out.println("Resultado em decimal : " + resultado[i]);                 
            //System.out.println();
        }
        String resultadoAND = "";
        
        for(int i = 0; i < resultado.length; i++){
            
            if(i < 3){
                resultadoAND = resultadoAND + resultado[i]+".";
            }else{
                resultadoAND = resultadoAND + resultado[i];
            }        
        }        
        
        return resultadoAND;
    }
    
    public static void main (String[]args){
        
        
        ArrayList<ItensDaTabela> tabelaDeRoteamentoRoteador = new ArrayList<>();
        ArrayList<ItensDaTabela> tabelaDeRoteamentoSubRede1 = new ArrayList<>();        
        ArrayList<ItensDaTabela> tabelaDeRoteamentoSubRede2 = new ArrayList<>();
        
        CamadaFisica barramento1 = new CamadaFisica("m2");
        CamadaFisica barramento2 = new CamadaFisica("m3");
        
        
        Computador comp1 = new Computador(barramento1, "140.24.7.2", "E0:D5:5E:89:16:10",26,22);
        Computador comp2 = new Computador(barramento1, "140.24.7.4", "E0:D5:5E:89:16:11", 26,22);
        
        Computador comp3 = new Computador(barramento2, "192.168.25.2", "E0:D5:5E:89:16:10",27,22);
        Computador comp4 = new Computador(barramento2, "192.168.25.1", "E0:D5:5E:89:16:10",27,22);
        
        
        
        
        Roteador roteador = new Roteador("192.168.0.1","E0:D5:5E:89:16:23");
        roteador.adicionarBarramento(barramento1);
        roteador.adicionarBarramento(barramento2);
        
        
        ItensDaTabela linha1 = new ItensDaTabela(); // Tabela comp 1 
        ItensDaTabela linha2 = new ItensDaTabela();
        
        linha1.setMascara("255.255.255.224"); // De comp 1 ou comp 2 PARA comp 3 ou comp 4
        linha1.setEnderecoDeRede("192.168.25.0");
        linha1.setProximoSalto(null);
        linha1.setInterFace("m3");        
        tabelaDeRoteamentoRoteador.add(linha1);
        
        linha2.setMascara("255.255.255.192"); // De comp 3 ou comp 4 PARA comp 1 ou comp 2
        linha2.setEnderecoDeRede("140.24.7.0");
        linha2.setProximoSalto(null);
        linha2.setInterFace("m2");     
        tabelaDeRoteamentoRoteador.add(linha2);
        
        roteador.setTabelaDeRoteamento(tabelaDeRoteamentoRoteador);
        // ------------------------------------------------------------------- COMPUTADOR
        
        
        ItensDaTabela linha3 = new ItensDaTabela();
        linha3.setMascara("255.255.255.224");
        linha3.setEnderecoDeRede("192.168.25.0");
        linha3.setProximoSalto(roteador.getIpv4());
        linha3.setInterFace("m2");        
        tabelaDeRoteamentoSubRede1.add(linha3);
        
        comp1.getCamadaRedes().setTabelaDeRoteamento(tabelaDeRoteamentoSubRede1);        
        comp2.getCamadaRedes().setTabelaDeRoteamento(tabelaDeRoteamentoSubRede1);
        
        ItensDaTabela linha4 = new ItensDaTabela();
        
        linha4.setMascara("255.255.255.192");
        linha4.setEnderecoDeRede("140.24.7.0");
        linha4.setProximoSalto(roteador.getIpv4());
        linha4.setInterFace("m3");     
        
        comp3.getCamadaRedes().setTabelaDeRoteamento(tabelaDeRoteamentoSubRede2);        
        comp4.getCamadaRedes().setTabelaDeRoteamento(tabelaDeRoteamentoSubRede2);
        
        
        barramento1.attach(comp1);
        barramento1.attach(comp2);
        barramento1.attach(roteador);
        barramento2.attach(comp3);
        barramento2.attach(comp4);
        barramento2.attach(roteador);
        
        
        
        Mensagem mensagem  = new Mensagem("Abacaxi","192.168.25.2");
        comp1.Send(mensagem);
       
    }
}
