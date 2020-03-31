package Projeto;

public interface Observable {
    
    void attach(Observer o);
    void dettach(Observer o);
    void notifyObserver(Object mensagem);
}
