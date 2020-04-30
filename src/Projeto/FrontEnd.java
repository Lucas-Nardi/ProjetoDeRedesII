package Projeto;

import java.util.ArrayList;
import java.util.Scanner;

public class FrontEnd {

    public static ArrayList<Computador> listaComputadores = new ArrayList<>();
    public static ArrayList<CamadaFisica> listaBarramentos = new ArrayList<>();
    public static ArrayList<Roteador> listaRoteadores = new ArrayList<>();

    
    public static void CriarPc() {
        Scanner in = new Scanner(System.in);
        System.out.println("Criação de pc:");
        System.out.println("id do barramento?");
        String barramentoId = in.nextLine();
        CamadaFisica barramentoExistente = null;

        boolean exists = false;
        for (CamadaFisica p : listaBarramentos) {
            if (p.getId().equals(barramentoId)) {
                System.out.println("este barramento já existe. deseja utiliza-lo?(s/n)");
                char resposta2 = in.nextLine().charAt(0);
                if (resposta2 == 's') {
                    barramentoExistente = p;
                    exists = true;
                    break;
                }else{
                    System.out.println("id do barramento?");
                    barramentoId = in.nextLine();
                }
                break;
            }
        };

        System.out.println("endereço IPV4 do pc?");
        String ipv4Address = in.nextLine();
        System.out.println("endereço MAC do pc?");
        String macAddress = in.nextLine();
        System.out.println("mascara da rede do pc?(somente inteiro)");
        int mascaraRede = in.nextInt();
        System.out.println("MTU do pc?");
        int mtu = in.nextInt();

        in.nextLine(); //clean input

        System.out.println("tabela de roteamento:");
        char escolha = 'n';
        ArrayList<ItensDaTabela> tabelaTemp = new ArrayList<>();
        do {
            System.out.println("mascara da linha");
            String linhaMask = in.nextLine();
            System.out.println("endereço da linha");
            String linhaAddress = in.nextLine();
            System.out.println("next hope da linha");
            String linhaNextHope = in.nextLine();
            if (linhaNextHope.equals("null") || linhaNextHope.equals("")) {
                linhaNextHope = null;
            }
            System.out.println("id do barramento da linha");
            String linhaBarramentoId = in.nextLine();

            ItensDaTabela itensTemp = new ItensDaTabela();
            itensTemp.setMascara(linhaMask);
            itensTemp.setEnderecoDeRede(linhaAddress);
            itensTemp.setProximoSalto(linhaNextHope);
            itensTemp.setInterFace(linhaBarramentoId);
            tabelaTemp.add(itensTemp);

            System.out.println("\n" + linhaMask + " | " + linhaAddress + " | " + linhaNextHope + " | " + linhaBarramentoId);

            System.out.println("deseja adicionar outra linha à tabela? (s/n)");
            escolha = in.nextLine().charAt(0);
        } while (escolha != 'n' && escolha != 'N');

        System.out.println("----");
        System.out.println("pc criado:");
        System.out.println("barramento: " + barramentoId);
        System.out.println("IPV4: " + ipv4Address);
        System.out.println("MAC: " + macAddress);
        System.out.println("mascara de rede: " + mascaraRede);
        System.out.println("MTU: " + mtu);

        if(!exists){
            barramentoExistente = new CamadaFisica(barramentoId);
        }
        listaBarramentos.add(barramentoExistente);
        Computador pctemp = new Computador(barramentoExistente, ipv4Address, macAddress, mascaraRede, mtu);
        listaComputadores.add(pctemp);
        
        barramentoExistente.attach(pctemp);

        pctemp.getCamadaRedes().setTabelaDeRoteamento(tabelaTemp);
    }

    public static void CriarRoteador() {
        Scanner in = new Scanner(System.in);
        System.out.println("Criação de roteador:");
        System.out.println("endereço IPV4 do roteador?");
        String ipv4Address = in.nextLine();
        System.out.println("endereço MAC do roteador?");
        String macAddress = in.nextLine();

        System.out.println("tabela de roteamento:");
        char escolha = 'n';
        ArrayList<ItensDaTabela> tabelaTemp = new ArrayList<>();
        do {
            System.out.println("mascara da linha");
            String linhaMask = in.nextLine();
            System.out.println("endereço da linha");
            String linhaAddress = in.nextLine();
            System.out.println("next hope da linha");
            String linhaNextHope = in.nextLine();
            if (linhaNextHope.equals("null") || linhaNextHope.equals("")) {
                linhaNextHope = null;
            }
            System.out.println("id do barramento da linha");
            String linhaBarramentoId = in.nextLine();

            ItensDaTabela itensTemp = new ItensDaTabela();
            itensTemp.setMascara(linhaMask);
            itensTemp.setEnderecoDeRede(linhaAddress);
            itensTemp.setProximoSalto(linhaNextHope);
            itensTemp.setInterFace(linhaBarramentoId);
            tabelaTemp.add(itensTemp);

            System.out.println("\n" + linhaMask + " | " + linhaAddress + " | " + linhaNextHope + " | " + linhaBarramentoId);

            System.out.println("deseja adicionar outra linha à tabela? (s/n)");
            escolha = in.nextLine().charAt(0);
        } while (escolha != 'n' && escolha != 'N');

        System.out.println("lista de barramentos:");
        ArrayList<CamadaFisica> barramentosTemp = new ArrayList<>();
        escolha = 'n';
        System.out.println("deseja adicionar um barramento ao roteador? (s/n)");
        escolha = in.nextLine().charAt(0);
        while (escolha != 'n' && escolha != 'N') {
            System.out.println("id do barramento à adicionar");
            String linhaIdBarramento = in.nextLine();

            Boolean exists = false;
            for (CamadaFisica p : listaBarramentos) {
                if (p.getId().equals(linhaIdBarramento)) {
                    barramentosTemp.add(p);
                    exists = true;
                    break;
                }
            };
            if (exists) {
                System.out.println("barramento adicionado");
            } else {
                System.out.println("barramento não existe");
            }

            System.out.println("deseja adicionar outro barramento ao roteador? (s/n)");
            escolha = in.nextLine().charAt(0);
        };

        Roteador roteadortemp = new Roteador(ipv4Address, macAddress);
        listaRoteadores.add(roteadortemp);

        roteadortemp.setTabelaDeRoteamento(tabelaTemp);
        roteadortemp.setListaDeBarramentos(barramentosTemp);
        
        for(CamadaFisica p: barramentosTemp){
            p.attach(roteadortemp);
        }
    }
    
    public static void CriarBarramento(){
        Scanner in = new Scanner(System.in);
        System.out.println("Criação de barramento:");
        System.out.println("id do barramento?");
        String barramentoId = in.nextLine();
        CamadaFisica barramentoExistente = null;

        boolean exists = false;
        for (CamadaFisica p : listaBarramentos) {
            if (p.getId().equals(barramentoId)) {
                System.out.println("este barramento já existe. deseja utiliza-lo?(s/n)");
                char resposta2 = in.nextLine().charAt(0);
                if (resposta2 == 's') {
                    System.out.println("nenhum barramento criado");
                    exists = true; 
                    break;
                }else{
                    System.out.println("id do barramento?");
                    barramentoId = in.nextLine();
                    exists = false;
                }
                break;
            }
        };
        
        if(!exists){
            System.out.println("novo barramento criado");
            barramentoExistente = new CamadaFisica(barramentoId);
            listaBarramentos.add(barramentoExistente);
        }
        
    }
    
    public static void CriarMensagem(){
        Computador origem = null;
        Scanner in = new Scanner(System.in);
        System.out.println("Criação de mensagem:");
        System.out.println("qual a informação que deseja enviar?(string)");
        String inputData = in.nextLine();
        System.out.println("IPV4 origem?(string)");
        String ipv4Origem = in.nextLine();
        boolean originExists = false;
        while(!originExists){
            for(Computador cp : listaComputadores){
                if(cp.getCamadaRedes().getIpv4().equals(ipv4Origem)){
                    originExists = true;
                    origem = cp;
                    break;
                }
            }
            if(!originExists){
                System.out.println("IPV4 escolhido não existe, escolha novamente");
                System.out.println("IPV4 origem?(string)");
                ipv4Origem = in.nextLine();
            }
        }
        System.out.println("IPV4 destino?(string)");
        String ipv4Destino = in.nextLine();
        boolean destinoExists = false;
        while(!destinoExists){
            for(Computador cp : listaComputadores){
                if(cp.getCamadaRedes().getIpv4().equals(ipv4Destino)){
                    destinoExists = true;
                    break;
                }
            }
            if(!destinoExists){
                System.out.println("IPV4 escolhido não existe, escolha novamente");
                System.out.println("IPV4 destino?(string)");
                ipv4Destino = in.nextLine();
            }
        }
        System.out.println("enviando mensagem...");
        Mensagem mensagemtemp = new Mensagem(inputData, ipv4Destino);
        origem.Send(mensagemtemp);
        System.out.println("mensagem enviada.");
        
           
    }

    public static void MenuCriar() {
        Scanner in = new Scanner(System.in);
        do {
            System.out.println("criar:");
            System.out.println("1) pc");
            System.out.println("2) roteador");
            System.out.println("3) barramento");
            System.out.println("4) mensagem");
            System.out.println("9) voltar");
            System.out.print(">");
            int option = in.nextInt();
            System.out.println(option);

            switch (option) {
                case 1: {
                    CriarPc();
                    break;
                }
                case 2: {
                    CriarRoteador();
                    break;
                }
                case 3: {
                    CriarBarramento();
                    break;
                }
                case 4: {
                    CriarMensagem();
                    break;
                }
                case 9: {
                    return;
                }
                default: {
                    System.out.println("input não suportado");
                }
            }
        } while (true);
    }

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        do {
            System.out.println("Selecione a opção que deseja:");
            System.out.println("1) criar");
            System.out.println("9) sair");
            System.out.print(">");
            int option = in.nextInt();
            System.out.println(option);

            switch (option) {
                case 1: {
                    MenuCriar();
                    break;
                }
                case 9: {
                    System.out.println("bye bye");
                    System.exit(0);
                    break;
                }
                default: {
                    System.out.println("input não suportado");
                }
            }

        } while (true);

//        ArrayList<ItensDaTabela> tabelaDeRoteamentoRoteador = new ArrayList<>();
//        ArrayList<ItensDaTabela> tabelaDeRoteamentoSubRede1 = new ArrayList<>();        
//        ArrayList<ItensDaTabela> tabelaDeRoteamentoSubRede2 = new ArrayList<>();
//        
//        CamadaFisica barramento1 = new CamadaFisica("m2");
//        CamadaFisica barramento2 = new CamadaFisica("m3");
//        
//        
//        Computador comp1 = new Computador(barramento1, "140.24.7.2", "E0:D5:5E:89:16:10",26,22);
//        Computador comp2 = new Computador(barramento1, "140.24.7.4", "E0:D5:5E:89:16:11", 26,22);
//        
//        Computador comp3 = new Computador(barramento2, "192.168.25.2", "E0:D5:5E:89:16:10",27,22);
//        Computador comp4 = new Computador(barramento2, "192.168.25.1", "E0:D5:5E:89:16:10",27,22);
//        
//        
//        
//        
//        Roteador roteador = new Roteador("192.168.0.1","E0:D5:5E:89:16:23");
//        roteador.adicionarBarramento(barramento1);
//        roteador.adicionarBarramento(barramento2);
//        
//        
//        ItensDaTabela linha1 = new ItensDaTabela(); // Tabela comp 1 
//        ItensDaTabela linha2 = new ItensDaTabela();
//        
//        linha1.setMascara("255.255.255.224"); // De comp 1 ou comp 2 PARA comp 3 ou comp 4
//        linha1.setEnderecoDeRede("192.168.25.0");
//        linha1.setProximoSalto(null);
//        linha1.setInterFace("m3");        
//        tabelaDeRoteamentoRoteador.add(linha1);
//        
//        linha2.setMascara("255.255.255.192"); // De comp 3 ou comp 4 PARA comp 1 ou comp 2
//        linha2.setEnderecoDeRede("140.24.7.0");
//        linha2.setProximoSalto(null);
//        linha2.setInterFace("m2");     
//        tabelaDeRoteamentoRoteador.add(linha2);
//        
//        roteador.setTabelaDeRoteamento(tabelaDeRoteamentoRoteador);
//        // ------------------------------------------------------------------- COMPUTADOR
//        
//        
//        ItensDaTabela linha3 = new ItensDaTabela();
//        linha3.setMascara("255.255.255.224");
//        linha3.setEnderecoDeRede("192.168.25.0");
//        linha3.setProximoSalto(roteador.getIpv4());
//        linha3.setInterFace("m2");        
//        tabelaDeRoteamentoSubRede1.add(linha3);
//        
//        comp1.getCamadaRedes().setTabelaDeRoteamento(tabelaDeRoteamentoSubRede1);        
//        comp2.getCamadaRedes().setTabelaDeRoteamento(tabelaDeRoteamentoSubRede1);
//        
//        ItensDaTabela linha4 = new ItensDaTabela();
//        
//        linha4.setMascara("255.255.255.192");
//        linha4.setEnderecoDeRede("140.24.7.0");
//        linha4.setProximoSalto(roteador.getIpv4());
//        linha4.setInterFace("m3");     
//        
//        comp3.getCamadaRedes().setTabelaDeRoteamento(tabelaDeRoteamentoSubRede2);        
//        comp4.getCamadaRedes().setTabelaDeRoteamento(tabelaDeRoteamentoSubRede2);
//        
//        
//        barramento1.attach(comp1);
//        barramento1.attach(comp2);
//        barramento1.attach(roteador);
//        barramento2.attach(comp3);
//        barramento2.attach(comp4);
//        barramento2.attach(roteador);
//        
//        
//        
//        Mensagem mensagem  = new Mensagem("Abacaxi","192.168.25.2");
//        comp1.Send(mensagem);
    }
}
