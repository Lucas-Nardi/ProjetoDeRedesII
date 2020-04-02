package Projeto;

class CamadaEnlace {
    
    private String macAddress;
    CamadaRedes redes;
    CamadaFisica fisica;
    
    public CamadaEnlace (CamadaFisica fisica, String MacAddress){
        
        this.macAddress = MacAddress;
        this.fisica = fisica;
        
    }
   
    void ReceiveRedes(Object mensagem){
        
        this.SendFisica(mensagem);
    }
    
    void SendRedes(Object mensagem){
        
        this.redes.ReceiveEnlace(mensagem);
    }    
    
    void ReceiveFisica(Object mensagem){ // Recebi algo do barramento
        
        this.SendRedes(mensagem);
        
    }
    
    void SendFisica(Object mensagem){
        
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
    
    
}
