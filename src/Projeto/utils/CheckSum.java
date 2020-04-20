package Projeto.utils;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class CheckSum {

    private byte[] mensagem;

    public CheckSum(byte[] mensagem) {
        this.mensagem = mensagem;
    }

    public byte[] getMensagem() {
        return mensagem;
    }

    public void setMensagem(byte[] mensagem) {
        this.mensagem = mensagem;
    }

    public Long calculate() {
        Checksum checksum = new CRC32();
        checksum.update(this.mensagem, 0, this.mensagem.length);
        return checksum.getValue();
    }

}
