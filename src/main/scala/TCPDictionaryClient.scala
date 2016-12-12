package Dictionary;

import java.io._;
import java.net._;
import resource._;

/**
 * @author 12077236
 */
object  TCPDictionaryClient extends App {
  val tcpPort = 1234
  val address = InetAddress.getByName("localhost");
  println("TCP Client");
  
  while (true) {
    try {
      for (
          s1 <- managed(new Socket(address , tcpPort));
          s1Out <- managed(s1.getOutputStream);
          dOs <- managed(new DataOutputStream(s1Out));
          s1In <- managed(s1.getInputStream);
          dIn <- managed(new DataInputStream(s1In))) {
            val word = readLine("Enter word : ");
            dOs.writeUTF(word);
            val meaning = dIn.readUTF();
            println(s"The meaning of $word : $meaning");
            
          }
    }
    
    catch {
      case e: Exception => e.printStackTrace();
      
    }
  }
}