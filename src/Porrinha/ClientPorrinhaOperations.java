package Porrinha;


/**
* Porrinha/ClientPorrinhaOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Porrinha.idl
* Domingo, 25 de Junho de 2017 22h29min39s GFT
*/

public interface ClientPorrinhaOperations 
{
  void tellPlayersNames (String[] players, int length);
  void tellNumberOfPicks ();
  void tellResultGuess (int maxSum);
  void roundFinished (int result, int maxSum, String[] playersPicks, String[] winners);
  void waitForStart ();
} // interface ClientPorrinhaOperations
