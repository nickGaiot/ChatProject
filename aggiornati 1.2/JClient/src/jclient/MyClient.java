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
import java.util.ArrayList;

/**
 *
 * @author Stefano
 */
public class MyClient implements Runnable{
    
    public static final String KW_S= "SERVER", KW_L= "local", KW_G= "global"; //key Words
    public static final String M_M= "message:" /*writer, recipient, time, text*/, M_Cl= "client:"/*name, address, state*/, M_Pass= "password:", M_Host= "hostname:"; //Messages
    public static final String S_Conn= "connected", S_Dis= "disconnected", S_Ban= "banned"; //States
    public static final String R_Host= "request_hostname", R_Pass= "request_password", R_Acc= "request_accepted", R_Deny= "request_denied"; //Requests
    public static final String C_Client= "JClient:closed_connection:", C_Server= "JServer:closed_connection:"; //Closed
    public static final String Sep= "&"; //Separator: carattere che l'utente non può inserire (da trovare)
    
    private MainInterface interf;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintStream out = null;
    private ArrayList<String[]> messaggi;
    private ArrayList<String[]> clients;
    private String address;
    private int port;

    public MyClient(){
        messaggi= new ArrayList<>();
        clients= new ArrayList<>();
        interf= new MainInterface(this);
    }
    
    @Override
    public void run(){
        String messaggio;
        try{
            do{
                messaggio= in.readLine();
                if(messaggio.startsWith(M_M)) addMessage(messaggio);
                else if(messaggio.startsWith(M_Cl)) updateClient(messaggio);
                else if(messaggio.startsWith(R_Host)) requestHostname();
                else if(messaggio.startsWith(R_Pass)) requestPassword();
            }while(!messaggio.startsWith(C_Server));
        }catch(IOException ex){}catch(Exception ex){}
        disconnettiti();
        addMessage(M_M + "" + Sep + KW_L + Sep + System.currentTimeMillis() + Sep + "TI SEI DISCONNESSO");
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
        clients.clear();
        interf.disconnesso();
        if(socket != null) if(!socket.isClosed()) try{
            send(C_Client);
            socket.close();
            in.close();
            out.close();
        }catch(IOException ex){}
    }
    
    private void requestHostname() throws IOException{
        String message, hostname= null;
        do{
            hostname= interf.requestHostname(hostname == null);
            if(hostname == null) disconnettiti();
            sendHostname(hostname);
            message= in.readLine();
        }while(message.equals(R_Deny));
    }
    
    private void requestPassword() throws IOException{
        String message, password= null;
        do{
            password= interf.requestPassword(password == null);
            if(password == null) disconnettiti();
            sendPassword(password);
            message= in.readLine();
        }while(message.equals(R_Deny));
    }
    
    private void updateClient(String client){
        String[] cl, other;
        client= client.substring(M_Cl.length());
        cl= client.split(Sep);
        other= getClientByName(cl[0]);
        if(other == null) clients.add(cl);
        else clients.set(clients.indexOf(other), cl);
        interf.reloadClients();
        interf.reloadMessages();
    }
    
    private void addMessage(String message){
        message= message.substring(M_M.length());
        messaggi.add(message.split(Sep));
        interf.addMessage(message.split(Sep));
    }
    
    private void send(String message){
        out.println(message);
        out.flush();
    }
    
    public void sendMessage(String recipient, String text){
        out.println(M_M + recipient + Sep + text);
        out.flush();
    }
    
    public void sendPassword(String password){
        out.println(M_Pass + password);
        out.flush();
    }
    
    public void sendHostname(String hostname){
        out.println(M_Host + hostname);
        out.flush();
    }
    
    public boolean isConnected(){
        if(socket != null) return (socket.isConnected() && !socket.isClosed());
        return false;
    }
    
    public String[] getClientByName(String hostname){
        for(String[] client : clients){
            if(client[0].equals(hostname)) return client;
        }
        return null;
    }

    public String[][] getClients() {
        String[][] clients= new String[this.clients.size()][];
        for(int i= 0; i < this.clients.size(); i++){
            clients[i]= this.clients.get(i);
        }
        return clients;
    }

    public String[][] getMessaggi() {
        String[][] messaggi= new String[this.messaggi.size()][];
        for(int i= 0; i < this.messaggi.size(); i++){
            messaggi[i]= this.messaggi.get(i);
        }
        return messaggi;
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
