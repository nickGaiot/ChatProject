/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Stefano
 */
public class MyServer implements Runnable{
    
    private ServerSocket Server;
    private ArrayList<Connection> connessioni;
    private MainInterface interf;

    public MyServer() throws Exception {
        Server = new ServerSocket(4000);
        connessioni= new ArrayList<>();
        interf= new MainInterface(this);
        sendMessages("messaggio:Il server e' in attesa sulla porta 4000");
        new Thread(this).start();
    }

    public void run() {
        while(true){
            try {
                Socket client = Server.accept();
                String hostname= client.getInetAddress().toString();
                Connection conn= getConnectionByAddress(hostname);
                if(conn != null) if(conn.hostname != null) hostname= conn.hostname;
                sendMessages("messaggio:Connessione accettata da: " + hostname);
                new Thread(new Connection(client)).start();
            } catch(IOException ex){}
        }
    }
    
    public void espelli(String hostname){
        Connection conn= getConnectionByHostname(hostname);
        conn.espulso= true;
        conn.closeAll("e' stato espulso");
    }
    
    private boolean isHostnameEsistente(String hostname){
        for(Connection conn : connessioni) if(hostname.equalsIgnoreCase(conn.hostname)) return true;
        return false;
    }
    
    private Connection getConnectionByAddress(String address){
        for(Connection conn : connessioni) if(address.equals(conn.address)) return conn;
        return null;
    }
    
    private Connection getConnectionByHostname(String hostname){
        for(Connection conn : connessioni) if(hostname.equals(conn.hostname)) return conn;
        return null;
    }
    
    public void reloadClientsList(){
        String clients= "";
        for(Connection conn : connessioni) if(conn.connesso) clients+= conn.hostname + " ";
        interf.reloadClientsList(clients.split(" "));
        sendMessages("clients:" + clients);
    }
    
    public void sendMessages(String message){
        if(message.startsWith("messaggio:")) interf.addMessage(message.substring(10));
        for(Connection conn : connessioni) if(conn.connesso) conn.sendMessage(message);
    }

    class Connection implements Runnable {

        private Socket client = null;
        private BufferedReader in = null;
        private PrintStream out = null;
        String address;
        String hostname;
        String password;
        boolean connesso, espulso;
        
        public Connection(Socket clienSocket){
            client = clienSocket;
            address= client.getInetAddress().toString();
            connesso= false;
            espulso= false;
        }

        private void initConnection() throws IOException{
            in= new BufferedReader(new InputStreamReader(client.getInputStream()));
            out= new PrintStream(client.getOutputStream(), true);
            Connection conn= getConnectionByAddress(address);
            
            if(conn == null) requestHostname();
            else{
                hostname= conn.hostname;
                password= conn.password;
                espulso= conn.espulso;
                if(espulso) closeAll("e' espulso");
                else{
                    if(hostname == null) requestHostname();
                    if(password != null) requestPassword();
                    if(conn.connesso) conn.closeAll("connesso da una nuova connessione");
                    connessioni.remove(conn);
                }
            }
            if(!espulso){
                connessioni.add(this);
                connesso= true;
                reloadClientsList();
                if(conn == null) sendMessages("messaggio:BENVENUTO " + hostname.toUpperCase() + "!");
                else sendMessages("messaggio:" + hostname.toUpperCase() + " SI E' RICONNESSO");
            }
        }
        
        public void run(){
            String messaggio;
            try{
                initConnection();
                do{
                    messaggio= in.readLine();
                    if(messaggio.startsWith("messaggio:")) sendMessages("messaggio:" + hostname + ": " + messaggio.substring(10));
                    else if(messaggio.startsWith("password:")) password= messaggio.substring(9);
                }while(!messaggio.equals("JClient:connessione chiusa"));
            }catch(IOException ex){}catch(Exception ex){}
            if(!client.isClosed()) closeAll("e' uscito");
        }
        
        private void requestHostname() throws IOException{
            String messaggio;
            boolean isValido= false;
            sendMessage("JServer:request hostname");
            do{
                messaggio= in.readLine();
                if(messaggio.startsWith("hostname:")) isValido= !isHostnameEsistente(messaggio.substring(9));
                if(isValido) sendMessage("JServer:hostname valido");
                else sendMessage("JServer:hostname non valido");
            }while(!isValido);
            hostname= messaggio.substring(9);
        }
        
        private void requestPassword() throws IOException{
            String messaggio;
            boolean isValido= false;
            sendMessage("JServer:request password");
            do{
                messaggio= in.readLine();
                if(messaggio.startsWith("password:")) isValido= (messaggio.substring(9).equals(password));
                if(isValido) sendMessage("JServer:password valida");
                else sendMessage("JServer:password non valida");
            }while(!isValido);
            password= messaggio.substring(9);
        }
        
        private void sendMessage(Object message){
            out.println(message);
            out.flush();
        }
        
        private void closeAll(String messaggio){
            try{
                connesso= false;
                sendMessage("JServer:connessione chiusa:" + messaggio);
                client.close();
                out.close();
                in.close();
            }catch(IOException ex){}
            reloadClientsList();
            if(hostname == null) sendMessages("messaggio:" + client.getInetAddress() + " SI E' DISCONNESSO: " + messaggio.toUpperCase());
            else sendMessages("messaggio:" + hostname.toUpperCase() + " SI E' DISCONNESSO: " + messaggio.toUpperCase());
        }

    }
}
