import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

class SRClient {
   public static void main(String args[]) throws Exception {
      
      //Variables
      boolean errorDetected = false;
      int dmgProb = 0;
      
      //Create input stream
      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      
      //Create client socket
      DatagramSocket clientSocket = new DatagramSocket();
      
      //Get IP Address? ---- Will be manually assigned
      InetAddress IPAddress = null;
      
      //Create packets
      byte[] sendData = new byte[256];
      byte[] receiveData = new byte[256];
      byte[] headerData = new byte[256];
      
      //Assign IP Address
      String IPInput;
      Scanner sc = new Scanner(System.in);
      do {
         //User Enters IP
         System.out.println("Please Enter the IP Address of the Server: ");
         IPInput = sc.nextLine();
         
         //Try to get IP by name
         try {
            IPAddress = InetAddress.getByName(IPInput);
         }
         //Throw error if IP could not be found
         catch (UnknownHostException e) {
            System.out.println("No IP Address for the host could be found");
            continue;
         }
         finally {
            System.out.println("Host with IP found...");
         }
         break;
         
      } while(true);
      
      //Assign Port Number
      int portNumber;
      System.out.println("Please enter the server port number: ");
      portNumber = sc.nextInt();
      
      //Enter packet damage probability
      System.out.println("Please enter the probability for damaged packets (Out of 100): ");
      dmgProb = sc.nextInt();
      sc.close();
      
      //Create datagram - Send a request packet
      DatagramPacket request = new DatagramPacket(new byte[256], 256, IPAddress, portNumber);
      clientSocket.send(request);
      System.out.println("HTTP request sent to server...");
      
      //Create a recieving packet
      DatagramPacket headerPacket;
      headerPacket = new DatagramPacket(headerData, headerData.length);
      
      //Recieve Header Information
      clientSocket.receive(headerPacket);
      System.out.println("HTTP response received");
      
      receiveData = headerPacket.getData();
      String headerInfo = new String(receiveData);
      
      receiveData = new byte[256];
      
      //Recieve Packet Information
      byte[] buffer = new byte[256];
      byte[] dataReceived = new byte[252];
      String textReceived = "";
      int packetNumber = 0;
      boolean eof = false;
      do {
         //create recieving packet and recieve from socket
         DatagramPacket receivingPacket = new DatagramPacket(receiveData, receiveData.length);
         
         clientSocket.receive(receivingPacket);
         System.out.println("Packet Recieved...");

         //populate buffer with bytes
         buffer = receivingPacket.getData();
         packetNumber++;
         
         for(int i = 4; i < 256; i++) {
            dataReceived[i - 4] = buffer[i];
         }
         
         //Pass data through gremlin function
         dataReceived = gremlinFct(dataReceived, dmgProb);
         
         for(int i = 4; i < 256; i++) {
            buffer[i] = dataReceived[i - 4];
         }
   	 
	 if(buffer[4] == 0) {
            System.out.println("End of Packet Stream...");
            eof = true;
	    break;
         }
      
         //Error Detection
         if(errorDetection(buffer)) {
            System.out.println("Error detected in packet #" + packetNumber);
         }
         
         //Cast to string, add to text recieved
         textReceived += new String(dataReceived);
         
         //Reset recieve data
         receiveData = new byte[receiveData.length];
         

         //If single NULL byte recieved - end loop

      } while(eof == false);
      
      System.out.println("\n\nResponse Header Info Received: ");
      System.out.println(headerInfo);
      System.out.println("\n\nText Written to File: ");
      System.out.println(textReceived);
      //Write text to file
      writeToFile(textReceived);
   } 
   
   public static void extractHeaderInformation() {
   }
   
   public static void writeToFile(String text) throws IOException{
      BufferedWriter out = new BufferedWriter(new FileWriter("TestText.txt"));
      text = text.replace("\u0000", "");
      try {
         out.write(text);
      }
      catch (IOException e) {
         System.out.println("Exception ");
      }
      finally
      {
         out.close();
      }
   } 
   
   public static boolean errorDetection(byte[] data) {
      byte[] serverChecksum = new byte[4];
      byte[] clientChecksum = new byte[4];
      int clientChecksumInt = 0;
      
      for (int i = 0; i < 4; i ++) {
         serverChecksum[i] = data[i];
      }
      
      for (int j = 4; j < 256; j++) {
         clientChecksumInt += data[j];
      }
      
      clientChecksum = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(clientChecksumInt).array();
      
      if (Arrays.equals(serverChecksum, clientChecksum)) {
         return false;
      }
      return true;
   }
   
   public static byte[] gremlinFct(byte[] readByte, int probability ) 
    {
        byte[] read = readByte;
        Random numGen = new Random(); 
        int packNum = numGen.nextInt(100);
        int readNum;
        readNum = probability;
        
        
        if (packNum <= (100 - readNum))
        {
            return read; 
        }
       
        else
        {
            Random nextGen = new Random();
            int nextNumGen = nextGen.nextInt(100);
            
            if (nextNumGen >= 50) 
            {
                Random rand = new Random();
                int byteChoice = rand.nextInt(252);
                read[byteChoice] = 100;
            }
            
            else if ((nextNumGen < 50) && (nextNumGen > 20))
            {
                Random rand = new Random();
                int byteChoice = rand.nextInt(252);
                read[byteChoice] = 100;
                
                if(byteChoice > 251) {
                  read[byteChoice] = 0;
                  read[byteChoice - 1] = 100;
                }
                else {
                  read[byteChoice] = 0;
                  read[byteChoice + 1] = 100;
                }
            }
            
            else 
            {
                Random rand = new Random();
                int byteChoice = rand.nextInt(252);
                read[byteChoice] = 100;
                
                if(byteChoice > 251) {
                  read[byteChoice] = 100;
                  read[byteChoice - 1] = 100;
                  read[byteChoice - 2] = 100;
                }
                else {
                  read[byteChoice] = 100;
                  read[byteChoice + 1] = 100;
                  read[byteChoice + 2] = 100;
                }
            }
            
        }
        return read;
    }
}
