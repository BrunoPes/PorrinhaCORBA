package Porrinha;


/**
* Porrinha/ServerPorrinhaOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Porrinha.idl
* Quarta-feira, 14 de Junho de 2017 23h13min21s GFT
*/

public interface ServerPorrinhaOperations 
{
  void registerClient (String clientName);
  void putNumberOfPicks (String clientName, int picks);
  void putResultGuess (String clientName, int guess);
} // interface ServerPorrinhaOperations