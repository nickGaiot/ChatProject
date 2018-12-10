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
    
    //key Words: parole base
    public static final String KW_S= "SERVER", KW_C="CLIENT", KW_L= "local", KW_G= "global";
    //Accepted: messaggio di accettazione della autentificazione
    public static final String A_Conn= "JServer:accepted_connection";
    //Closed: messaggi di chiusura
    public static final String C_Client= "JClient:closed_connection:", C_Server= "JServer:closed_connection:";
    //Messaggi: dopo il prefisso seguono i vari campi
    public static final String M_M= "message:" /*id, writer, recipient, text, time, state*/, M_Cl= "client:"/*name, address, state*/, M_Pass= "password:", M_Host= "hostname:";
    //States vari stati che può assumere un messaggio
    public static final String S_Send= "sending", S_For= "forwarding", S_Rec= "recived";
    //States vari stati che può assumere un client
    public static final String S_Conn= "connected", S_Dis= "disconnected", S_Ban= "banned";
    //Requests inviati dal Server al Client
    public static final String R_Host= "request_hostname", R_Pass= "request_password", R_Acc= "request_accepted"/*, R_Deny= "request_denied"*/;
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
        hostname= null;
        address= null;
        port= 0;
        interf= new MainInterface(this);
    }
    
    /**
     * Rinvia gli 'offlineMessagges',
     * Rimane in ascolto per il server,
     * Si disconnette.
     */
    @Override
    public void run(){
        String stringa;
        sendOfflineMssages();
        try{
            do{
                stringa= in.readLine();
                decryptString(stringa);
            }while(!stringa.startsWith(C_Server));
        }catch(IOException ex){}catch(Exception ex){}
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
        try{
            if(hostname != null){
                saveFile();
            }
            if(isConnectionOpen()){
                updateMessage(cryptMessage(parseMessage(KW_G, KW_L, "TI SEI DISCONNESSO", S_Rec)));
            }
            send(C_Client);
            socket.close();
            out.close();
            in.close();
        }catch(Exception ex){}
        
        interf.disconnesso();
        try{ Thread.sleep(200); //aspetta che i thread concorrenti finiscano di utilizzare le risorse
        }catch(InterruptedException ex){}
    }
    
    /**
     * Carica il file di questa connessione,
     * se non esiste non carica niente
     */
    public void loadFile(){
        File file= new File("serverFiles/dataServer-" + address + "-" + port);
        String stringa;
        clearAll();
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
            fOut.println(M_Host + hostname);
            for(String[] client : clients){
                fOut.println(cryptClient(client));
            }
            for(String[] message : messaggi){
                fOut.println(cryptMessage(message));
            }
            fOut.close();
        }catch(FileNotFoundException ex){}catch(IOException ex){}
    }
    
    public void clearAll(){
        messaggi.clear();
        clients.clear();
        hostname= null;
    }
    
    /**
     * In base ai parametri passati ritorna il messaggio,
     * nota che l'id viene creato così:<br>
     * <i>hostname + messaggi.size()</i>.
     */
    private String[] parseMessage(String writer, String recipient, String text, String state){
        long time= System.currentTimeMillis();
        return new String[]{hostname + messaggi.size(), writer, recipient, text, String.valueOf(time), state};
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
        return M_M + message[0] + Sep + message[1] + Sep + message[2] + Sep + message[3] + Sep + message[4] + Sep + message[5];
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
        if(crypted.startsWith(M_M)) updateMessage(crypted);
        else if(crypted.startsWith(M_Cl)) updateClient(crypted);
        else if(crypted.startsWith(M_Host)) updateHostname(crypted);
        else if(crypted.startsWith(R_Host)) requestHostname();
        else if(crypted.startsWith(R_Pass)) requestPassword();
        else if(crypted.startsWith(A_Conn)) interf.connesso();
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
     * Setta l'hostname.
     * @param hostname deve essere cifrato
     */
    private void updateHostname(String crypted){
        hostname= crypted.substring(M_Host.length());
    }
    
    /**
     * Se il client è nuovo lo aggiunge,
     * altrimenti aggiorna quello vecchio e aggiorna i messaggi dell'interfaccia,
     * aggiorna i clients dell'interfaccia.
     * @param crypted 
     */
    private void updateClient(String crypted){
        String[] client, other;
        client= parseClient(crypted);
        other= getClientByName(client[0]);
        if(other == null) clients.add(client);
        else{
            clients.set(clients.indexOf(other), client);
            interf.reloadMessages();
        }
        interf.reloadClients();
    }
    
    /**
     * Se il mesaggio è nuovo lo aggiunge,
     * altrimenti aggiorna quello vecchio e aggiorna i messaggi dell'interfaccia.
     * @param crypted 
     */
    private void updateMessage(String crypted){
        String[] message, other;
        message= parseMessage(crypted);
        other= getMesasgeById(message[0]);
        if(other == null){
            messaggi.add(message);
            interf.addMessage(message);
        }
        else{
            messaggi.remove(other);
            messaggi.add(message);
            interf.reloadMessages();
        }
    }
    
    /**
     * Invia la stringa cifrata se la connessione è aperta.
     * @param message 
     */
    private void send(String message){
        if(isConnectionOpen()){
            out.println(message);
            out.flush();
        }
    }
    
    /**
     * Manda l'hostame.
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
     * Invia il messaggio se la connessione è aperta,
     * e anche se la connessione non è aperta aggiunge il messaggio tra i messaggi in stato 'spedito'.
     * @param writer
     * @param recipient
     * @param text 
     */
    public void sendMessage(String writer, String recipient, String text){
        String crypted= cryptMessage(parseMessage(writer, recipient, text, S_Send));
        updateMessage(crypted);
        send(crypted);
    }
    
    /**
     * Rinvia tutti i messaggi che sono in stato sending.
     */
    private void sendOfflineMssages(){
        String[] message;
        int index= 0;
        boolean a= true;
        while(a){
            if(index >= messaggi.size()) a= false;
            else{
                message= messaggi.get(index);
                if(message[5].equals(S_Send)) a= false;
                else index++;
            }
        }
        for(int i= index; i < messaggi.size(); i++){
            message= messaggi.get(i);
            send(cryptMessage(message));
        }
    }
    
    /**
     * Ritorna tutti i messaggi nel formato: id, writer, recipient, text, time, state.
     * @return -String[][] i messaggi, es:<br>
     *  {<br>
     *      {id, writer, recipient, text, time, state},<br>
     *      {id, writer, recipient, text, time, state},<br>
     *      {id, writer, recipient, text, time, state}<br>
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
     * Ritorna il messaggio nel formato: id, writer, recipient, text, time, state.
     * @param id
     * @return 
     * -String[] se il messaggio esiste<br>
     * -null altrimenti
     */
    public String[] getMesasgeById(String id){
        for(String[] message : messaggi){
            if(message[0].equals(id)) return message;
        }
        return null;
    }
    
    /**
     * Ritorna tutti i client nel formato: name, address, state.
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
    public boolean isConnectionOpen(){
        if(socket == null) return false;
        if(socket.isClosed()) return false;
        return true;
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
