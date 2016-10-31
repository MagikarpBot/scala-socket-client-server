package Dictionary;

import java.io._;
import java.net._;
import resource._;
import scala.concurrent.ExecutionContext;

/**
 * @author 12077236
 */
object DictionaryServer extends App {
  val ownExecutor = ExecutionContext.fromExecutor(new java.util.concurrent.ForkJoinPool(3));
  
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
          println(Thread.currentThread().getName());
          
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
                  case "guru" => dOs.writeUTF(
                      "an influential teacher or popular expert");
                  case "love" => dOs.writeUTF(
                      "the feeling of affection");
                  case _=> dOs.writeUTF(
                      "meaning not known");
                  
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
      val s2 = managed(new DatagramSocket(udpPort));
      println(Thread.currentThread().getName());
      
      for (aSocket <- s2) {
        while (true) {
          val buffer: Array[Byte] = Array.ofDim[Byte](99999);
          val request: DatagramPacket = new DatagramPacket(buffer , buffer.length);
          aSocket.receive(request);
          
          execute(request) {
            request => {
              var requestDefinition = new String(request.getData());
        
              requestDefinition.trim().toLowerCase() match {
                case "guru" => 
                  val message = new String("an influential teacher or popular expert").getBytes();
                  val reply: DatagramPacket = new DatagramPacket(
                      message ,
                      message.length ,
                      request.getAddress() ,
                      request.getPort());
                  aSocket.send(reply);
                  
                case "love" => 
                  val message = new String("the feeling of affection").getBytes();
                  val reply: DatagramPacket = new DatagramPacket(
                      message ,
                      message.length ,
                      request.getAddress() ,
                      request.getPort());
                  aSocket.send(reply);
              
                case _=> 
                  val message = new String("meaning not known").getBytes();
                val reply: DatagramPacket = new DatagramPacket(
                      message ,
                      message.length ,
                      request.getAddress() ,
                      request.getPort());
                  aSocket.send(reply);
          
              }  
            }
          }
        }
      }
    }
    
    catch {
      case e: SocketException => println("Socket: " + e.getMessage());
      case e: IOException     => println("IO: " + e.getMessage());
      
    }
  }

  while (true) {
    Thread.sleep(4000);
 
  }
}