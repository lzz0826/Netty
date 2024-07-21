package com.example.nettypro.tcp;

public class TcpMessageProtocol {

    private int len;  //關鍵 用來確保每次讀包的長度 避免TCP拆包黏包問題

    public byte[] content;


    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
