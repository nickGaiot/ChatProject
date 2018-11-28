/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Stefano
 */
public class MyClient implements Runnable{
    
    private MainInterface interf;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintStream out = null;
    private String address;
    private int port;

    public MyClient(){
        interf= new MainInterface(this);
    }
    
    @Override
    public void run(){
        String messaggio;
        try{
            do{
                messaggio= in.readLine();
                if(messaggio.startsWith("messaggio:")) interf.addMessage(messaggio.substring(10));
                else if(messaggio.startsWith("clients:")) interf.setClientsList(messaggio.substring(8).split(" "));
                else if(messaggio.startsWith("JServer:request hostname")) sendHostname();
                else if(messaggio.startsWith("JServer:request password")) sendPassword();
                else if(messaggio.startsWith("JServer:connessione chiusa:")) interf.addMessage(messaggio.substring(8).replaceAll(":", ": "));
            }while(!messaggio.startsWith("JServer:connessione chiusa:"));
        }catch(IOException ex){}catch(Exception ex){}
        disconnettiti();
    }
    
    public void connettiti(){
        try{
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            new Thread(this).start();
        }catch(IOException ex){}
    }
    
    public void disconnettiti(){
        if(socket != null) if(!socket.isClosed()) try{
            sendMessage("JClient:connessione chiusa");
            interf.disconnesso();
            socket.close();
            in.close();
            out.close();
        }catch(IOException ex){}
    }
    
    private void sendHostname() throws IOException{
        String message, hostname= null;
        do{
            hostname= interf.requestHostname(hostname == null);
            if(hostname == null) disconnettiti();
            out.println("hostname:" + hostname);
            out.flush();
            message= in.readLine();
        }while(message.equals("JServer:hostname non valido"));
    }
    
    private void sendPassword() throws IOException{
        String message, password= null;
        do{
            password= interf.requestPassword(password == null);
            if(password == null) disconnettiti();
            out.println("password:" + password);
            out.flush();
            message= in.readLine();
        }while(message.equals("JServer:password non valida"));
    }
    
    public void sendMessage(String message){
        out.println(message);
        out.flush();
    }
    
    public boolean isConnected(){
        if(socket != null) return (socket.isConnected() && !socket.isClosed());
        return false;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}
