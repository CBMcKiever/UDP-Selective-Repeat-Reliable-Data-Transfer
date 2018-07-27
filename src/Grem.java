
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author swe00
 */
public class Grem {
     public static byte[] gremlinFct(byte[] readByte, int probability, int lostProb) 
    {
        byte[] read = readByte;
        Random numGen = new Random(); 
        int packNum = numGen.nextInt(100);
        int readNum;
        readNum = probability;
        int packLost = lostProb;
        // Add system.out.println("Insert lostProb: ") somewhere in the program
        
        
        if (packLost < (100 - readNum))
        {
            // drop packet and selective repeat would not send back an ack to server 
            // something like this may work read = null;
            boolean lost = true;
            if (lost == true)
            {
               read = new byte[0]; 
               return read;
            }
        }
        
        else if (packNum <= (100 - readNum))
        {
            return read; 
        }
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
            
        return read;
    }
}
