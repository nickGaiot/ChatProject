/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Stefano
 */
public class MyServer implements Runnable{
    
    //key Words
    public static final String KW_S= "SERVER", KW_C="CLIENT", KW_L= "local", KW_G= "global";
    //Messaggi: dopo il prefisso seguono i vari campi
    public static final String M_M= "message:" /*writer, recipient, time, text*/, M_Cl= "client:"/*name, address, state*/, M_Pass= "password:", M_Host= "hostname:";
    //States vari stati che può assumere un client
    public static final String S_Conn= "connected", S_Dis= "disconnected", S_Ban= "banned";
    //Requests inviati dal Server al Client
    public static final String R_Host= "request_hostname", R_Pass= "request_password", R_Acc= "request_accepted"/*, R_Deny= "request_denied"*/;
    //Closed: messaggi di chiusura
    public static final String C_Client= "JClient:closed_connection:", C_Server= "JServer:closed_connection:";
    //Separator: carattere che l'utente non può inserire: utilizzato per separare i campi dei messaggi
    public static final String Sep= String.valueOf((char)0);
    
    //soket che sta in ascolto per le connessioni
    private ServerSocket Server;
    //array delle connessioni
    private ArrayList<Connection> connessioni;
    //array dei messaggi
    private ArrayList<String[]> messaggi;
    //interfaccia grafica
    private MainInterface interf;
    //indica se mostrare o nasconedere gli address ai client
    boolean shAddr, autoSave;

    /**
     * Inizializza gli attributi.
     */
    public MyServer() throws Exception {
        Server = new ServerSocket(4000);
        connessioni= new ArrayList();
        messaggi= new ArrayList();
        shAddr= true;
        autoSave= true;
        interf= new MainInterface(this);
        loadFiles();
        sendMessageAll(KW_G, "IL SERVER E' IN ATTESA SULLA PORTA '4000'");
        new Thread(this).start();
    }

    /**
     * Mette il server in ascolto alla porta.
     */
    @Override
    public void run() {
        while(true){
            try {
                Socket client = Server.accept();
                String hostname= client.getInetAddress().getHostAddress();
                Connection conn= getConnectionByAddress(hostname);
                if(conn != null) if(conn.hostname != null) hostname= conn.hostname;
                sendMessageAll(KW_G, "Connessione accettata da: " + hostname);
                new Thread(new Connection(client)).start();
            } catch(IOException ex){}
        }
    }
    
    /**
     * Carica i files delle connessioni,
     * se non esistono non carica niente.
     */
    public void loadFiles(){
        File directory= new File("clientFiles");
        BufferedReader fIn;
        Connection conn;
        String[] client;
        String stringa;
        
        
        try{
            File file= new File("globalChat/globalChat");
            fIn= new BufferedReader(new FileReader(file));
            while(fIn.ready()){
                stringa= fIn.readLine();
                messaggi.add(parseMessage(stringa));
            }
        }catch(FileNotFoundException ex){
        }catch(IOException ex){}
        
        for(File file : directory.listFiles()){
            try{
                fIn= new BufferedReader(new FileReader(file));
                conn= new Connection();
                
                stringa= fIn.readLine();
                client= parseClient(stringa);
                conn.hostname= client[0];
                conn.address= client[1];
                if(client[2].equals(S_Ban)) conn.bannato= true;
                
                stringa= fIn.readLine();
                conn.password= stringa.substring(M_Pass.length());
                
                while(fIn.ready()){
                    stringa= fIn.readLine();
                    conn.offlineMessages.add(stringa);
                }
                connessioni.add(conn);
            }catch(FileNotFoundException ex){
            }catch(IOException ex){}
        }
        interf.reloadMessages();
        interf.reloadClients();
    }
    
    /**
     * Salva le connessioni sui files e la globalChat.
     */
    public void saveFiles(){
        File file;
        PrintStream fOut;
        
        file= new File("globalChat/globalChat");
        try{
            file.createNewFile();
            fOut= new PrintStream(file);
            for(String[] message: messaggi) fOut.println(cryptMessage(message));
        }catch(FileNotFoundException ex){
        }catch(IOException ex){}
            
        for(Connection conn : connessioni){
            file= new File("clientFiles/dataClient-" + conn.address + "-" + conn.hostname);
            try{
                file.createNewFile();
                fOut= new PrintStream(file);
                fOut.println(cryptClient(parseClient(conn)));
                fOut.println(M_Pass + conn.password);
                for(String offlineMessage : conn.offlineMessages) fOut.println(offlineMessage);
            }catch(FileNotFoundException ex){
            }catch(IOException ex){}
        }
    }
    
    /**
     * Banna la connessione in base all'hostname.
     */
    public void banna(int index){
        String hostname= connessioni.get(index).hostname;
        Connection conn= getConnectionByHostname(hostname);
        if(!conn.bannato && conn.client.isClosed()){
            conn.bannato= true;
            sendMessageAll(KW_G, hostname.toUpperCase() + " E' STATO ORA BANNATO");
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
     * -false altrimenti
     */
    private boolean isHostnameEsistente(String hostname){
        for(Connection conn : connessioni) if(hostname.equalsIgnoreCase(conn.hostname)) return true;
        return false;
    }
    
    /**
     * @return 
     * -Connection se esiste<br>
     * -null altrimenti
     */
    private Connection getConnectionByAddress(String address){
        for(Connection conn : connessioni) if(address.equals(conn.address)) return conn;
        return null;
    }
    
    /**
     * @return 
     * -Connection se esiste<br>
     * -null altrimenti
     */
    private Connection getConnectionByHostname(String hostname){
        for(Connection conn : connessioni) if(hostname.equals(conn.hostname)) return conn;
        return null;
    }
    
    /**
     * In base ai parametri passati ritorna il messaggio.
     */
    private String[] parseMessage(String writer, String recipient, String text){
        long time= System.currentTimeMillis();
        return new String[]{writer, recipient, String.valueOf(time), text};
    }
    
    /**
     * Ritorna il messaggio della stringa cifrata.
     */
    private String[] parseMessage(String message){
        message= message.substring(M_M.length());
        return message.split(Sep);
    }
    
    /**
     * Ritorna la stringa cifrata del messaggio.
     */
    private String cryptMessage(String[] message){
        return M_M + message[0] + Sep + message[1] + Sep + message[2] + Sep + message[3];
    }
    
    /**
     * In base alla connessione passata ritorna il client.
     */
    private String[] parseClient(Connection conn){
        String address= "", state= S_Conn;
        if(shAddr) address= conn.address;
        if(conn.bannato) state= S_Ban;
        else if(conn.client.isClosed()) state= S_Dis;
        return new String[]{conn.hostname, address, state};
    }
    
    /**
     * Ritorna il client della stringa cifrata.
     */
    private String[] parseClient(String client){
        client= client.substring(M_Cl.length());
        return client.split(Sep);
    }
    
    /**
     * Ritorna la stringa cifrata del messaggio.
     */
    private String cryptClient(String[] client){
        return M_Cl + client[0] + Sep + client[1] + Sep + client[2];
    }
    
    /**
     * Invia a tutti la stringa cifrata.
     * @param crypted 
     */
    private void sendAll(String crypted){
        for(Connection conn : connessioni) conn.send(crypted);
    }
    
    /**
     * Invia a tutte le connessioni il messaggio come destinatario 'global'.
     * @param writer il mittente
     * @param text il testo
     */
    public void sendMessageAll(String writer, String text){
        String[] message= parseMessage(writer, KW_G, text);
        String crypted= cryptMessage(message);
        sendAll(crypted);
        messaggi.add(message);
        interf.addMessage(message);
    }
    
    /**
     * Invia il messaggio ai client interessati.
     */
    private void sendPrivateMessage(String writer, String recipient, String text){
        String crypted= cryptMessage(parseMessage(writer, recipient, text));
        getConnectionByHostname(writer).send(crypted);
        getConnectionByHostname(recipient).send(crypted);
    }
    
    /**
     * Una connessione viene inviato come client a tutti.
     */
    private void sendClientAll(Connection conn){
        sendAll(cryptClient(parseClient(conn)));
    }
    
    /**
     * tutte le connessioni vengono inviati come client alla connessione.
     */
    private void sendAllClientTo(Connection connessione){
        for(Connection conn : connessioni) connessione.send(cryptClient(parseClient(conn)));
    }
    
    /**
     * Ritorna tutti i messaggi nel formato: writer, recipient, time, text.
     * @return -String[][] i messaggi, es:<br>
     *  {<br>
     *      {writer, recipient, time, text},<br>
     *      {writer, recipient, time, text},<br>
     *      {writer, recipient, time, text}<br>
     *  };
     */
    public String[][] getMessages() {
        String[][] messages= new String[messaggi.size()][];
        for(int i= 0; i < messaggi.size(); i++){
            messages[i]= messaggi.get(i);
        }
        return messages;
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
    public String[][] getClients(){
        String[][] clients= new String[connessioni.size()][];
        for(int i= 0; i < connessioni.size(); i++){
            clients[i]= parseClient(connessioni.get(i));
        }
        return clients;
    }
    
    /**
     * @param hostname
     * Ritorna il client nel formato: name, address, state.
     * @return 
     * -String[] se il client esiste<br>
     * -null altrimenti
     */
    public String[] getClientByName(String hostname){
        Connection conn= getConnectionByHostname(hostname);
        if(conn != null) return parseClient(conn);
        return null;
    }

    /**
     * Setta la variabile shAddr e ogni connessione viene ripassato come client a tutti.
     */
    public void setShowAddress(boolean show) {
        this.shAddr = show;
        for(Connection conn : connessioni) sendClientAll(conn);
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }
    
    /**
     * Chiude il server dopo aver chiuso tutte le connessioni,
     * se è impostato l'autosave salva i dati.
     */
    public void closeServer(){
        try{
            sendMessageAll(KW_G, "IL SERVER E' STATO CHIUSO");
            for(Connection conn : connessioni) conn.closeAll("SERVER CHIUSO");
            if(autoSave) saveFiles();
            Server.close();
        }catch(IOException ex){}
    }

    class Connection implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintStream out;
        //attributi del client
        private ArrayList<String> offlineMessages;
        private String address, hostname, password;
        private boolean bannato;
        
        /**
         * Utilizzato per creare una connessione senza socket,
         * questa connection può essere solo utilizzato come client.
         */
        public Connection(){
            client= new Socket();
            try{
                client.close();
            }catch(IOException ex){};
            offlineMessages= new ArrayList();
        }
        
        /**
         * Inizializza gli attributi.
         */
        public Connection(Socket clienSocket){
            client = clienSocket;
            offlineMessages= new ArrayList();
            address= client.getInetAddress().getHostAddress();
            password= "";
            bannato= false;
        }

        /**
         * Inizializza la connessione:<br>
         * se il client si era già connesso copia i suoi vecchi parametri e dopo l'autentificazione rimuove la vecchia connessione e invia i gli offlineMessages,
         * se il client è bannato chiude la connessione,
         * se il client è già connesso chiude la vecchia connessione,
         * aggiorna l'interfaccia.
         */
        private void initConnection() throws IOException{
            in= new BufferedReader(new InputStreamReader(client.getInputStream()));
            out= new PrintStream(client.getOutputStream(), true);
            Connection conn= getConnectionByAddress(address);
            
            if(conn != null){
                hostname= conn.hostname;
                password= conn.password;
                bannato= conn.bannato;
                if(bannato) closeAll("E' BANNATO");
            }
            if(hostname == null) requestHostname();
            requestPassword();
            send(M_Host + hostname);
            connessioni.add(this);
            if(conn != null){
                conn.closeAll("CONNESSO DA UNA NUOVA CONNESSIONE");
                connessioni.remove(conn);
                for(String crypted : conn.offlineMessages) send(crypted);
                sendMessageAll(KW_G, hostname.toUpperCase() + " SI E' RICONNESSO");
            }else sendMessageAll(KW_G, "BENVENUTO " + hostname.toUpperCase() + "!");
            sendAllClientTo(this);
            sendClientAll(this);
            interf.reloadClients();
        }
        
        /**
         * Inizializza e rimane in ascolto per il client.
         */
        public void run(){
            String stringa;
            try{
                initConnection();
                do{
                    stringa= in.readLine();
                    if(stringa.startsWith(M_M)) addMessage(stringa);
                    else if(stringa.startsWith(M_Pass)) setPassword(stringa.substring(M_Pass.length()));
                }while(!stringa.startsWith(C_Client));
            }catch(IOException ex){
            }catch(Exception ex){}
            closeAll("E' USCITO");
        }
        
        /**
         * Aggiunge il messaggio,
         * se è globale lo manda a tutti i client,
         * altrimenti lo manda ai client interessati.
         * @param crypted 
         */
        private void addMessage(String crypted){
            String[] message= parseMessage(crypted);
            String recipient= message[1];
            if(recipient.equals(KW_G)) sendMessageAll(message[0], message[3]);
            else{
                sendPrivateMessage(message[0], recipient, message[3]);
            }
        }
        
        /**
         * Richiede un hostname finchè non è valido e lo assegna al client.
         * @throws IOException se la connessione si interrompe nella richiesta
         */
        private void requestHostname() throws IOException{
            String messaggio;
            boolean errato= true;
            do{
                send(R_Host);
                messaggio= in.readLine();
                if(messaggio.startsWith(M_Host)){
                    if(!isHostnameEsistente(messaggio.substring(M_Host.length()))) errato= false;
                }
            }while(errato);
            hostname= messaggio.substring(M_Host.length());
            send(R_Acc);
        }
        
        /**
         * Richiede la password finchè non è corretta.
         * @throws IOException se la connessione si interrompe nella richiesta
         */
        private void requestPassword() throws IOException{
            String messaggio;
            boolean errata= true;
            if(password.isEmpty()) errata= false;
            while(errata){
                send(R_Pass);
                messaggio= in.readLine();
                if(messaggio.startsWith(M_Pass)){
                    messaggio= messaggio.substring(M_Pass.length());
                    if(messaggio.equals(password)) errata= false;
                }
            }
            send(R_Acc);
        }
        
        /**
         * Imposta il valore della password.
         * @param password 
         */
        private void setPassword(String password){
            this.password= password;
        }
        
        /**
         * Invia la stringa cifrata se la connessione è aperta.
         * Se è chiusa salva la stringa nell'array offlineMessages.
         * @param crypted 
         */
        private void send(String crypted){
            if(client.isClosed()){
                offlineMessages.add(crypted);
            }else{
                out.println(crypted);
                out.flush();
            }
        }
        
        /**
         * Se la connessione è aperta la chiude e invia gli opportuni messaggi.
         * @param messaggio messaggio di chiusura
         */
        private void closeAll(String messaggio){
            if(!client.isClosed()){
                String hostname= address;
                if(this.hostname != null) hostname= this.hostname;
                sendMessageAll(KW_G, hostname.toUpperCase() + " SI E' DISCONNESSO: " + messaggio.toLowerCase());
                
                try{
                    send(cryptMessage(parseMessage(KW_G, hostname, messaggio)));
                    out.print(C_Server + messaggio);
                    client.close();
                    out.close();
                    in.close();
                }catch(IOException ex){}
                
                sendClientAll(this);
                interf.reloadClients();
            }
        }

    }
}
