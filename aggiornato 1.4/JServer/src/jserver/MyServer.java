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
    
    //key Words
    public static final String KW_S= "SERVER", KW_L= "local", KW_G= "global";
    //Messaggi: dopo il prefisso seguono i vari campi
    public static final String M_M= "message:" /*writer, recipient, time, text*/, M_Cl= "client:"/*name, address, state*/, M_Pass= "password:", M_Host= "hostname:";
    //States vari stati che può assumere un client
    public static final String S_Conn= "connected", S_Dis= "disconnected", S_Ban= "banned";
    //Requests inviati dal Server al Client
    public static final String R_Host= "request_hostname", R_Pass= "request_password", R_Acc= "request_accepted", R_Deny= "request_denied";
    //Closed: messaggi di chiusura
    public static final String C_Client= "JClient:closed_connection:", C_Server= "JServer:closed_connection:";
    //Separator: carattere che l'utente non può inserire: utilizzato per separare i campi dei messaggi
    public static final String Sep= String.valueOf((char)255);
    
    
    private ServerSocket Server;
    //array delle connessioni
    private ArrayList<Connection> connessioni;
    //array dei messaggi
    private ArrayList<String[]> messaggi;
    //interfaccia grafica
    private MainInterface interf;
    //indica se mostrare o nasconedere gli address ai client
    boolean shAddr;

    /**
     * Inizializza gli attributi
     */
    public MyServer() throws Exception {
        Server = new ServerSocket(4000);
        connessioni= new ArrayList();
        messaggi= new ArrayList();
        shAddr= true;
        interf= new MainInterface(this);
        sendMessageAll("", "IL SERVER E' IN ATTESA SULLA PORTA '4000'");
        new Thread(this).start();
    }

    /**
     * Mette il server in ascolto alla porta
     */
    @Override
    public void run() {
        while(true){
            try {
                Socket client = Server.accept();
                String hostname= client.getInetAddress().toString();
                Connection conn= getConnectionByAddress(hostname);
                if(conn != null) if(conn.hostname != null) hostname= conn.hostname;
                sendMessageAll("", "Connessione accettata da: " + hostname);
                new Thread(new Connection(client)).start();
            } catch(IOException ex){}
        }
    }
    
    /**
     * Banna la connessione in base all'hostname
     */
    public void banna(String hostname){
        Connection conn= getConnectionByHostname(hostname);
        if(!conn.bannato && conn.client.isClosed()){
            conn.bannato= true;
            sendMessageAll("", hostname.toUpperCase() + " E' STATO ORA BANNATO");
            sendClientAll(conn);
            interf.reloadClients();
        }else{
            conn.bannato= true;
            conn.closeAll("E' STATO ORA BANNATO");
        }
    }
    
    /**
     * @return 
     * -true se l'hostname esiste<br>
     * -false senno
     */
    private boolean isHostnameEsistente(String hostname){
        for(Connection conn : connessioni) if(hostname.equalsIgnoreCase(conn.hostname)) return true;
        return false;
    }
    
    /**
     * @return 
     * -Connection se esiste<br>
     * -null senno
     */
    private Connection getConnectionByAddress(String address){
        for(Connection conn : connessioni) if(address.equals(conn.address)) return conn;
        return null;
    }
    
    /**
     * @return 
     * -Connection se esiste<br>
     * -null senno
     */
    private Connection getConnectionByHostname(String hostname){
        for(Connection conn : connessioni) if(hostname.equals(conn.hostname)) return conn;
        return null;
    }
    
    /**
     * In base alla connessione passata ritorna un client con le sue caratteristiche
     */
    private String[] parseClient(Connection conn){
        String address= "", state= S_Conn;
        if(shAddr) address= conn.address;
        if(conn.bannato) state= S_Ban;
        else if(conn.client.isClosed()) state= S_Dis;
        return new String[]{conn.hostname, address, state};
    }
    /*funzione semplice che potrebbe essere usata in futuro
    private void sendAll(String message){
        for(Connection conn : connessioni) conn.send(message);
    }*/
    
    /**
     * Invia a tutte le connessioni il messaggio come destinatario 'global'
     */
    public void sendMessageAll(String writer, String text){
        long time= System.currentTimeMillis();
        String[] message= new String[]{writer, KW_G, String.valueOf(time), text};
        for(Connection conn : connessioni) conn.sendMessage(writer, KW_G, text);
        messaggi.add(message);
        interf.addMessage(message);
    }
    
    /**
     * Una connessione viene passata come client a tutte le connessioni
     */
    private void sendClientAll(Connection conn){
        for(Connection other : connessioni) other.sendClient(conn);
    }
    
    /**
     * Ritorna tutti i messaggi nel formato: writer, recipient, time, text
     */
    public String[][] getMessaggi() {
        String[][] messaggi= new String[this.messaggi.size()][];
        for(int i= 0; i < this.messaggi.size(); i++){
            messaggi[i]= this.messaggi.get(i);
        }
        return messaggi;
    }
    
    /**
     * Ritorna tutti i client nel formato: name, address, state
     */
    public String[][] getClients(){
        String[][] clients= new String[connessioni.size()][];
        for(int i= 0; i < connessioni.size(); i++){
            clients[i]= parseClient(connessioni.get(i));
        }
        return clients;
    }
    
    /**
     * @return 
     * -String[] se trova il client
     * -null senno
     */
    public String[] getClientByName(String hostname){
        Connection conn= getConnectionByHostname(hostname);
        if(conn != null) return parseClient(conn);
        return null;
    }

    /**
     * Setta la variabile shAddr e ad ogni connessione si ripassano i client
     */
    public void setShowAddress(boolean show) {
        this.shAddr = show;
        for(Connection conn : connessioni) conn.sendAllClient();
    }
    

    class Connection implements Runnable {

        private Socket client = null;
        private BufferedReader in = null;
        private PrintStream out = null;
        //attributi del client
        private String address, hostname, password;
        private boolean bannato;
        
        /**
         * Inizializza gli attributi
         */
        public Connection(Socket clienSocket){
            client = clienSocket;
            address= client.getInetAddress().toString();
            bannato= false;
        }

        /**
         * Inizializza la connessione:<br>
         * se il client si connette per la prima volta richiede l'hostname, e aggiunge la connessione all'array
         * senno controlla l'hostname, la password, e se è bannato chiude la connessione
         * senno chiude la vecchia connessione e aggiunge la connessione all'array
         */
        private void initConnection() throws IOException{
            in= new BufferedReader(new InputStreamReader(client.getInputStream()));
            out= new PrintStream(client.getOutputStream(), true);
            Connection conn= getConnectionByAddress(address);
            
            if(conn == null) requestHostname();
            else{
                hostname= conn.hostname;
                password= conn.password;
                bannato= conn.bannato;
                if(bannato) closeAll("E' BANNATO");
                else{
                    if(hostname == null) requestHostname();
                    else send(M_Host + hostname);
                    if(password != null) requestPassword();
                    conn.closeAll("CONNESSO DA UNA NUOVA CONNESSIONE");
                    connessioni.remove(conn);
                }
            }
            if(!bannato){
                connessioni.add(this);
                sendAllClient();
                sendClientAll(this);
                interf.reloadClients();
                if(conn == null) sendMessageAll("", "BENVENUTO " + hostname.toUpperCase() + "!");
                else sendMessageAll("", hostname.toUpperCase() + " SI E' RICONNESSO");
            }
        }
        
        /**
         * Inizializza e rimane in ascolto
         */
        public void run(){
            String messaggio;
            try{
                initConnection();
                do{
                    messaggio= in.readLine();
                    if(messaggio.startsWith(M_M)) writeMessage(messaggio);
                    else if(messaggio.startsWith(M_Pass)) password= messaggio.substring(M_Pass.length());
                }while(!messaggio.startsWith(C_Client));
            }catch(IOException ex){}catch(Exception ex){}
            closeAll("E' USCITO");
        }
        
        private void requestHostname() throws IOException{
            String messaggio;
            boolean isValido= false;
            send(R_Host);
            do{
                messaggio= in.readLine();
                if(messaggio.startsWith(M_Host)) isValido= !isHostnameEsistente(messaggio.substring(M_Host.length()));
                if(isValido) send(R_Acc);
                else send(R_Deny);
            }while(!isValido);
            hostname= messaggio.substring(M_Host.length());
        }
        
        private void requestPassword() throws IOException{
            String messaggio;
            boolean isValido= false;
            send(R_Pass);
            do{
                messaggio= in.readLine();
                if(messaggio.startsWith(M_Pass)) isValido= (messaggio.substring(M_Pass.length()).equals(password));
                if(isValido) send(R_Acc);
                else send(R_Deny);
            }while(!isValido);
            password= messaggio.substring(M_Pass.length());
        }
        
        private void writeMessage(String message){
            message= message.substring(M_M.length());
            String[] m= message.split(Sep);
            String recipient= m[0];
            String text= m[1];
            if(recipient.equals(KW_G)) sendMessageAll(hostname, text);
            else{
                getConnectionByHostname(recipient).sendMessage(hostname, recipient, text);
                this.sendMessage(hostname, recipient, text);
            }
        }
        
        private void send(String message){
            if(!client.isClosed()){
                out.println(message);
                out.flush();
            }
        }
        
        private void sendMessage(String writer, String recipient, String text){
            if(!client.isClosed()){
                long time= System.currentTimeMillis();
                send(M_M + writer + Sep + recipient + Sep + time + Sep + text);
            }
        }
        
        private void sendClient(Connection conn){
            if(!client.isClosed()){
                String[] client= parseClient(conn);
                send(M_Cl + client[0] + Sep + client[1] + Sep + client[2]);
            }
        }
        
        private void sendAllClient(){
            if(!client.isClosed()){
                for(Connection conn : connessioni) sendClient(conn);
            }
        }
        
        private void closeAll(String messaggio){
            if(!client.isClosed()){
                try{
                    sendMessage("", KW_L, messaggio);
                    out.print(C_Server + messaggio);
                    client.close();
                    out.close();
                    in.close();
                }catch(IOException ex){}
                
                String hostname= client.getInetAddress().toString();
                if(this.hostname != null) hostname= this.hostname;
                sendMessageAll("", hostname.toUpperCase() + " SI E' DISCONNESSO: " + messaggio.toUpperCase());
                sendClientAll(this);
                interf.reloadClients();
            }
        }

    }
}
