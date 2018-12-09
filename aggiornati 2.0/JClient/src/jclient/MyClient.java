/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jclient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Stefano
 */
public class MyClient implements Runnable{
    
    //key Words
    public static final String KW_S= "SERVER", KW_C="CLIENT", KW_L= "local", KW_G= "global";
    //Messaggi: dopo il prefisso seguono i vari campi
    public static final String M_M= "message:" /*writer, recipient, time, text*/, M_Cl= "client:"/*name, address, state*/, M_Pass= "password:", M_Host= "hostname:";
    /*//States vari stati che può assumere un messaggio
    public static final String S_Send= "sended", S_Rec= "recived", S_Disp= "displayed";*/
    //States vari stati che può assumere un client
    public static final String S_Conn= "connected", S_Dis= "disconnected", S_Ban= "banned";
    //Requests inviati dal Server al Client
    public static final String R_Host= "request_hostname", R_Pass= "request_password", R_Acc= "request_accepted"/*, R_Deny= "request_denied"*/;
    //Closed: messaggi di chiusura
    public static final String C_Client= "JClient:closed_connection:", C_Server= "JServer:closed_connection:";
    //Separator: carattere che l'utente non può inserire: utilizzato per separare i campi dei messaggi
    public static final String Sep= String.valueOf((char)0);
    
    //la connessione
    private Socket socket;
    private BufferedReader in, fIn;
    private PrintStream out, fOut;
    //interfaccia grafica
    private MainInterface interf;
    //array dei messaggi
    private ArrayList<String[]> messaggi;
    //array dei clients
    private ArrayList<String[]> clients;
    //attributi della connessione
    private String address, hostname;
    private int port;

    /**
     * Inizializza gli attributi
     */
    public MyClient(){
        messaggi= new ArrayList<>();
        clients= new ArrayList<>();
        address= null;
        port= 0;
        interf= new MainInterface(this);
    }
    
    /**
     * Rimane in ascolto per il server
     */
    @Override
    public void run(){
        String stringa;
        try{
            do{
                stringa= in.readLine();
                decryptString(stringa);
            }while(!stringa.startsWith(C_Server));
        }catch(IOException ex){}catch(Exception ex){}
        addMessage(cryptMessage(parseMessage(KW_G, KW_L, "TI SEI DISCONNESSO")));
        disconnettiti();
    }
    
    /**
     * Apre la connessione 
     */
    public void connettiti(){
        loadFile();
        try{
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            new Thread(this).start();
        }catch(IOException ex){}
    }
    
    /**
     * Chiude la connessione
     */
    public void disconnettiti(){
        for(String[] client : clients) if(client[2].equals(S_Conn)) client[2]= S_Dis;
        if(socket != null){
            try{
                saveFile();
                send(C_Client);
                socket.close();
                out.close();
                in.close();
            }catch(IOException ex){}
        }
        hostname= KW_C;
        interf.disconnesso();
        interf.reloadClients();
        try{Thread.sleep(200); //aspetta che i thread concorrenti finiscano di utilizzare le risorse
        }catch(InterruptedException ex){}
    }
    
    /**
     * Carica il file di questa connessione,
     * se non esiste non carica niente
     */
    public void loadFile(){
        File file= new File("serverFiles/dataServer-" + address + "-" + port);
        String stringa;
        messaggi.clear();
        clients.clear();
        try{
            fIn= new BufferedReader(new FileReader(file));
            while(fIn.ready()){
                stringa= fIn.readLine();
                decryptString(stringa);
            }
            fIn.close();
        }catch(FileNotFoundException ex){}catch(IOException ex){}
    }
    
    
    /**
     * Salva i vari dati su file 
     */
    private void saveFile(){
        File file= new File("serverFiles");
        file.mkdir();
        file= new File("serverFiles/dataServer-" + address + "-" + port);
        try{
            file.createNewFile();
            fOut= new PrintStream(file);
            for(String[] client : clients){
                fOut.println(cryptClient(client));
            }
            for(String[] message : messaggi){
                fOut.println(cryptMessage(message));
            }
            fOut.close();
        }catch(FileNotFoundException ex){}catch(IOException ex){}
    }
    
    /**
     * In base ai parametri passati ritorna il messaggio
     */
    private String[] parseMessage(String writer, String recipient, String text){
        long time= System.currentTimeMillis();
        return new String[]{writer, recipient, String.valueOf(time), text};
    }
    
    /**
     * Ritorna il messaggio della stringa cifrata
     */
    private String[] parseMessage(String message){
        message= message.substring(M_M.length());
        return message.split(Sep);
    }
    
    /**
     * Ritorna la stringa cifrata del messaggio
     */
    private String cryptMessage(String[] message){
        return M_M + message[0] + Sep + message[1] + Sep + message[2] + Sep + message[3];
    }
    
    /**
     * Ritorna il client della stringa cifrata
     */
    private String[] parseClient(String client){
        client= client.substring(M_Cl.length());
        return client.split(Sep);
    }
    
    /**
     * Ritorna la stringa cifrata del messaggio
     */
    private String cryptClient(String[] client){
        return M_Cl + client[0] + Sep + client[1] + Sep + client[2];
    }
    
    private void decryptString(String crypted) throws IOException{
        if(crypted.startsWith(M_M)) addMessage(crypted);
        else if(crypted.startsWith(M_Cl)) updateClient(crypted);
        else if(crypted.startsWith(M_Host)) updateHostname(crypted);
        else if(crypted.startsWith(R_Host)) requestHostname();
        else if(crypted.startsWith(R_Pass)) requestPassword();
    }
    
    /**
     * Richiede all'interfaccia l'hostname finchè il server non lo accetta
     * @throws IOException 
     */
    private void requestHostname() throws IOException{
        String stringa, hostname= null;
        boolean show= false;
        do{
            hostname= interf.requestHostname(show);
            show= true;
            if(hostname == null) disconnettiti();
            sendHostname(hostname);
            stringa= in.readLine();
        }while(!stringa.startsWith(R_Acc));
    }
    
    /**
     * Richiede all'interfaccia la password finchè il server non l'accetta
     * @throws IOException 
     */
    private void requestPassword() throws IOException{
        String stringa, password= null;
        boolean show= false;
        do{
            password= interf.requestPassword(show);
            show= true;
            if(password == null) disconnettiti();
            sendPassword(password);
            stringa= in.readLine();
        }while(!stringa.startsWith(R_Acc));
    }
    
    /**
     * Imposta l'hostname e aggiorna l'interfaccia.
     * Questa funzione viene chiamata dal server quando ha validato la connessione
     * @param hostname 
     */
    private void updateHostname(String hostname){
        this.hostname= hostname.substring(M_Host.length());
        interf.connesso();
    }
    
    /**
     * Se il client è nuovo lo aggiunge,
     * altrimenti aggiorna quello vecchio,
     * aggiorna l'interfaccia
     * @param crypted 
     */
    private void updateClient(String crypted){
        String[] client, other;
        client= parseClient(crypted);
        other= getClientByName(client[0]);
        if(other == null) clients.add(client);
        else clients.set(clients.indexOf(other), client);
        interf.reloadClients();
        interf.reloadMessages();
    }
    
    /**
     * Aggiunge il messaggio e aggiorna l'interfaccia
     * @param crypted 
     */
    private void addMessage(String crypted){
        String[] message= parseMessage(crypted);
        messaggi.add(message);
        interf.addMessage(message);
    }
    
    /**
     * Invia la stringa cifrata se la connessione è aperta.
     * Se è chiusa salva la stringa nell'array offlineMessages
     * @param message 
     */
    private void send(String message){
        if(isOpen()){
            out.println(message);
            out.flush();
        }else{
            //da fare
        }
    }
    
    /**
     * Manda l'hostame
     * @param hostname 
     */
    public void sendHostname(String hostname){
        send(M_Host + hostname);
    }
    
    /**
     * Manda la password
     * @param password 
     */
    public void sendPassword(String password){
        send(M_Pass + password);
    }
    
    /**
     * Invia il messaggio
     * @param writer
     * @param recipient
     * @param text 
     */
    public void sendMessage(String writer, String recipient, String text){
        String crypted= cryptMessage(parseMessage(writer, recipient, text));
        send(crypted);
    }
    
    /**
     * Ritorna tutti i messaggi nel formato: writer, recipient, time, text
     * @return -String[][] i messaggi, es:<br>
     *  {<br>
     *      {writer, recipient, time, text},<br>
     *      {writer, recipient, time, text},<br>
     *      {writer, recipient, time, text}<br>
     *  };
     */
    public String[][] getMessaggi() {
        String[][] messages= new String[messaggi.size()][];
        for(int i= 0; i < messaggi.size(); i++){
            messages[i]= messaggi.get(i);
        }
        return messages;
    }
    
    /**
     * Ritorna tutti i client nel formato: name, address, state
     * @return -String[][] i clients, es:<br>
     *  {<br>
     *      {name, address, state},<br>
     *      {name, address, state},<br>
     *      {name, address, statet}<br>
     *  };
     */
    public String[][] getClients() {
        String[][] clients= new String[this.clients.size()][];
        for(int i= 0; i < this.clients.size(); i++){
            clients[i]= this.clients.get(i);
        }
        return clients;
    }

    /**
     * Ritorna il client nel formato: name, address, state.
     * @param hostname
     * @return 
     * -String[] se il client esiste<br>
     * -null altrimenti
     */
    public String[] getClientByName(String hostname){
        for(String[] client : clients){
            if(client[0].equals(hostname)) return client;
        }
        return null;
    }
    
    /**
     * Ritorna il client nel formato: name, address, state.
     * @param index
     * @return se il client esiste<br>
     * -null altrimenti
     */
    public String[] getClient(int index){
        return clients.get(index);
    }

    /**
     * @return -true se il soket non è chiuso<br>
     * -false altrimenti
     */
    public boolean isOpen(){
        if(socket == null) return false;
        return !socket.isClosed();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}
