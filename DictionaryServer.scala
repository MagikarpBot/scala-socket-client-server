package Dictionary;

import java.io._;
import java.net._;
import resource._;
import scala.concurrent.ExecutionContext;

/**
 * @author MagikarpBot
 */
object DictionaryServer extends App {
  val ownExecutor = ExecutionContext.fromExecutor(new java.util.concurrent.ForkJoinPool(4));
  
  def executeMain(body: => Unit) = ownExecutor.execute(new Runnable() {
    def run = body;
    
  })
  
  def execute[E](a: E)(body: (E) => Unit) = ExecutionContext.global.execute(new Runnable() {
    def run = body(a);
    
  })
  
  executeMain {
    // TCP server code
    val tcpPort = 1234;
       
    for (s <- managed(new ServerSocket(tcpPort))) {
      while(true) {
        try {
          val s1: Socket = s.accept();
          
          execute(s1) ({
            (client: Socket) =>
              for (
                  clientSocket <- managed(s1);
                  s1In <- managed(clientSocket.getInputStream);
                  dIn <- managed(new DataInputStream(s1In));
                  s1Out <- managed(clientSocket.getOutputStream);
                  dOs <- managed(new DataOutputStream(s1Out))) {
                
                // Send string
                val word: String = dIn.readUTF();
                word.trim().toLowerCase() match {
                  case "guru" => dOs.writeUTF("an influential teacher or popular expert");
                  case "love" => dOs.writeUTF("the feeling of affection");
                  case _=> dOs.writeUTF("meaning not known");
                  
                }
            }
          })
        }
        
        catch {
          case e: IOException => e.printStackTrace;
        
        }
      }
    }
  }
  
  executeMain {
    // UDP server code
    var s2: DatagramSocket = null;
    val udpPort = 4321;
    
    try {
      s2 = new DatagramSocket(udpPort);
      
      val buffer: Array[Byte] = Array.ofDim[Byte](99999);
      
      while (true) {
        val request: DatagramPacket = new DatagramPacket(buffer , buffer.length);
        s2.receive(request);
        
        var requestDefinition = new String(
            request.getData() ,
            0 ,
            request.getLength());
        
        val sendRequestDefinition = requestDefinition.trim().toLowerCase() match {
          case "guru" => "an influential teacher or popular expert".getBytes();
          case "love" => "the feeling of affection".getBytes();
          case _=> "meaning not known".getBytes();
          
        };
        
        val reply: DatagramPacket = new DatagramPacket(
          sendRequestDefinition ,
          sendRequestDefinition.length ,
          request.getAddress() ,
          request.getPort());
        s2.send(reply);
        
      }
    }
    
    catch {
      case e: SocketException => println("Socket: " + e.getMessage());
      case e: IOException     => println("IO: " + e.getMessage());
      
    }
    
    finally {
     if (s2 != null) s2.close();
      
    }
  }
  
  while (true) {
    Thread.sleep(4000);
 
  }
}