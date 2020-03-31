package Projeto;


public interface Observer {
    
    void Send(Object mensagem);
    void Receive(Object mensagem);
}
