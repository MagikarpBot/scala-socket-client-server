package Dictionary

import java.net._;
import java.io._;

/**
 * @author MagikarpBot
 */
object UDPDictionaryClient extends App {
  var s2: DatagramSocket = null;
  val udpPort = 4321;
  val address: InetAddress = InetAddress.getByName("localhost");
  println("UDP Client");
  
  while (true) {
        try {
          s2 = new DatagramSocket();
          var userWord: String = readLine("Enter word : ");
          val m: Array[Byte] = userWord.getBytes();
          var request: DatagramPacket = new DatagramPacket(
            m ,
            m.length ,
            address ,
            udpPort);
          s2.send(request);
    
          val buffer: Array[Byte] = Array.ofDim(99999);
          val reply: DatagramPacket = new DatagramPacket(buffer, buffer.length);
          s2.receive(reply);
          println(s"The meaning of $userWord : " + new String(reply.getData()).trim() + "\n");
          // println(Thread.currentThread().getId());
          
        }
        
        catch {
          case e: SocketException => println("Socket: " + e.getMessage());
          case e: IOException => println("IO: " + e.getMessage());
          
        }
        
        finally {
          if (s2 != null) s2.close();
          
        }
      }
}