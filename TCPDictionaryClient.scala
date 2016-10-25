package Dictionary;

import java.io._;
import java.net._;
import resource._;

/**
 * @author MagikarpBot
 */
object  TCPDictionaryClient extends App {
  val tcpPort = 1234
  val address: InetAddress = InetAddress.getByName("localhost");
  println("TCP Client");
  
  while (true) {
    try {
      for (
          s1 <- managed(new Socket(address , tcpPort));
          s1Out <- managed(s1.getOutputStream);
          dOs <- managed(new DataOutputStream(s1Out));
          s1In <- managed(s1.getInputStream);
          dIn <- managed(new DataInputStream(s1In))) {
            val userWord = readLine("Enter word : ");
            dOs.writeUTF(userWord);
            val meaning = dIn.readUTF();
            println(s"The meaning of $userWord : $meaning");
            println(Thread.currentThread().getId());

          }
    }
    
    catch {
      case e: Exception => e.printStackTrace();
      
    }
  }
}