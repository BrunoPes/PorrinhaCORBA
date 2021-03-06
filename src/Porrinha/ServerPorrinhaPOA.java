package Porrinha;


/**
* Porrinha/ServerPorrinhaPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Porrinha.idl
* Domingo, 25 de Junho de 2017 22h29min39s GFT
*/

public abstract class ServerPorrinhaPOA extends org.omg.PortableServer.Servant
 implements Porrinha.ServerPorrinhaOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("registerClient", new java.lang.Integer (0));
    _methods.put ("putNumberOfPicks", new java.lang.Integer (1));
    _methods.put ("putResultGuess", new java.lang.Integer (2));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // Porrinha/ServerPorrinha/registerClient
       {
         String clientName = in.read_string ();
         this.registerClient (clientName);
         out = $rh.createReply();
         break;
       }

       case 1:  // Porrinha/ServerPorrinha/putNumberOfPicks
       {
         String clientName = in.read_string ();
         int picks = in.read_long ();
         this.putNumberOfPicks (clientName, picks);
         out = $rh.createReply();
         break;
       }

       case 2:  // Porrinha/ServerPorrinha/putResultGuess
       {
         String clientName = in.read_string ();
         int guess = in.read_long ();
         this.putResultGuess (clientName, guess);
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:Porrinha/ServerPorrinha:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public ServerPorrinha _this() 
  {
    return ServerPorrinhaHelper.narrow(
    super._this_object());
  }

  public ServerPorrinha _this(org.omg.CORBA.ORB orb) 
  {
    return ServerPorrinhaHelper.narrow(
    super._this_object(orb));
  }


} // class ServerPorrinhaPOA
