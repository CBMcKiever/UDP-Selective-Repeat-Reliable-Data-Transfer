import java.io.*; 
import java.net.*;
import java.util.*;
import java.nio.*;
  
class SRServer { 
   public static void main(String args[]) throws Exception 
   {  
      String str = "";
      String responseString = "";
      byte[] byteData;
      byte[] receiveData = new byte[256]; 
      byte[] sendData  = new byte[256]; 
      byte[] headerData = new byte[256];
      int port = 10035;
      
      
      try{
         DatagramSocket serverSocket = new DatagramSocket(port);
         
         
         System.out.println("\nServer waiting for connection...");
          
         //Create space for received datagram
         //Receive datagram 
         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
         
         serverSocket.receive(receivePacket);
         

          
         //Get IP addr and port # of sender
         InetAddress IPAddress = receivePacket.getAddress(); 
         
         port = receivePacket.getPort(); 
         System.out.println("Connected to host.");
                  
         //read the file, store in string
         str = readFile();
	 System.out.println("\n\nText from input file: \n" + str + "\n\n");

         //Get bytes again to account for header
         byteData = str.getBytes();
         
         //Send a response
         responseString = addHeaderInfo(responseString, byteData);
         headerData = responseString.getBytes();
         DatagramPacket responseMessage = new DatagramPacket(headerData, headerData.length, IPAddress, port); 
         System.out.println("Sending Response Packet...");
         serverSocket.send(responseMessage);
         
         //Loop through buffer, sending 256 bytes at a time to client
         int startOfPacket = 0;
         int endOfPacket = 251;
         for(int i = 0; i < (double)byteData.length/251; i++) {
            int checksum = 0;
            byte[] buffer = new byte[256];
            
            //Populate buffer with correct data
            if (endOfPacket >= byteData.length) {
               int index1 = 4;
               for (int j = startOfPacket; j < byteData.length; j++) {
                  buffer[index1++] = byteData[j];
                  checksum += byteData[j];
               }
            }
            else {
               int index2 = 4;
               for (int j = startOfPacket; j < endOfPacket; j++) {
                  buffer[index2++] = byteData[j];
                  checksum += byteData[j];
               }
            }
            
            //Decalare and fill checksum array
            byte[] checksumArray = new byte[4];
            checksumArray = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(checksum).array();
            
            //Insert checksum header
            for(int k = 0; k < 4; k++) {
               buffer[k] = checksumArray[k];
            }
            System.out.println("Sending...");                     
            sendData = buffer;
            //create datagramPacket to send
            DatagramPacket sendPacket =  new DatagramPacket(sendData, sendData.length, IPAddress, port);
            //Send packet
            serverSocket.send(sendPacket);
            startOfPacket = endOfPacket;
            endOfPacket += 251;
         }
         
         
         //Send null packet to signal end of stream
         byte[] nullPacket = new byte[1];
         nullPacket[0] = 0;
         DatagramPacket sendPacket = new DatagramPacket(nullPacket, nullPacket.length, IPAddress, port);
         System.out.println("Sending null packet...");
         serverSocket.send(sendPacket);
         //Create datagram to send to client
 
         //End While loop
         
      }    
      catch (IOException e) {
         System.out.println(e);
      }
   
   }
   
   public static String readFile()
   {
      StringBuilder contentBuilder = new StringBuilder();
      try {
         BufferedReader in = new BufferedReader(new FileReader("TestText.html"));
         String str1;
         while ((str1 = in.readLine()) != null) {
            contentBuilder.append(str1);
         }
         in.close();
      } 
      catch (IOException e) {
      }
      String content = contentBuilder.toString();
      
      return content;
   }
   
 
 
   public static String addHeaderInfo(String stringArg, byte[] byteArg) {
      String header = "HTTP/1.0 200 Document Follows\r\nContent-Type: text/plain\r\nContent-Length: " + byteArg.length + "\r\n\r\n";
      
      return header;
   }
   
}
