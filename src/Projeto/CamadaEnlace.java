package Projeto;

import java.util.concurrent.ExecutionException;

class CamadaEnlace {

    private String macAddress;
    CamadaRedes redes;
    CamadaFisica fisica;
    int mtu;

    public CamadaEnlace(CamadaFisica fisica, String MacAddress, int mtu) {

        this.macAddress = MacAddress;
        this.fisica = fisica;
        this.mtu = mtu;
    }

    void ReceiveRedes(Object mensagem) {
        
        this.SendFisica(mensagem);
    }

    void SendRedes(Object mensagem) throws InterruptedException, ExecutionException {

        this.redes.ReceiveEnlace(mensagem);
    }

    void ReceiveFisica(Object mensagem) throws ExecutionException, InterruptedException { // Recebi algo do barramento
        
        this.SendRedes(mensagem);
    }

    void SendFisica(Object mensagem) {

        fisica.notifyObserver(mensagem);
    }

    public String getMacAddress() {        
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public CamadaRedes getRedes() {
        return redes;
    }

    public void setRedes(CamadaRedes redes) {
        this.redes = redes;
    }

    public CamadaFisica getFisica() {
        return fisica;
    }

    public void setFisica(CamadaFisica fisica) {
        this.fisica = fisica;
    }

    public int getMtu() {
        return mtu;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }
}
