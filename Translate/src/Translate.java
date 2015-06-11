/******************************************************************************
 * Translate.jar
 * 
 *  This program prompts the user for a word to translate, feeds the user's 
 * entered word into Dictionary.jar, prints to console the output from Dictionary.jar,
 * and, after prompting the user if he/she is finished, exiting. 
 * 
 * PENDING: A log needs to be made of the word before and after translation. 
 * 
 ******************************************************************************
 * INPUT: No outside parameters
 * 
 * OUTPUT: User prompts, Dictionary.jar output
 ******************************************************************************
 * Maintenance Log
 ******************************************************************************
 * FIRST VERSION    05/22/2014  Nicholas Ferguson
 *      Write the program.
 * SOCKET UPDATE    06/08/2014  Nicholas Ferguson
 *      Update Translate to act as a client, sending information and receiving
 *      input through a socket.
 * 
 ******************************************************************************/


import java.util.*;
import java.io.*;
import java.net.*;

/******************************************************************************
 * Translate
 * 
 * The main method
 ******************************************************************************
 * INPUT: User entered text
 * 
 * OUTPUT:  User prompts, Dictionary.jar output
 ******************************************************************************
 * 
 ******************************************************************************/
public class Translate {

    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // TODO code application logic here
        Scanner userInput = new Scanner(System.in);
        
        System.out.println("\nLet's translate some words to German.");
        boolean userDone = false; //Used to stop the while loop below.
        
        /***********************************************************************
         * Creation of the translation log file. 
         * The IF statement checks if the filename already exists. If not, a new
         *  file is created. 
         * 
         * NOTE: A file with the same filename will be overwritten.
         **********************************************************************/
        File translation = new File("translation.txt");
        if (!translation.exists())
        {
            translation.createNewFile();
        }
            
        //  Set up to write to the log file. Set outside the while loop to 
        //  allow multiple entries into the log.
        FileWriter fwriter = new FileWriter(translation.getAbsoluteFile());
        BufferedWriter bwriter = new BufferedWriter(fwriter);
        
        /***********************************************************************
         * Socket is created, and a connection is attempted.
         **********************************************************************/
        String serverAddress = "127.0.0.1";
        int serverPort = 2014;
        Socket socket = new Socket(serverAddress, serverPort);
        
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        String serverMsg = "";
        
        /***********************************************************************
         * This while loop uses a boolean flag userDone to know when the user
         * no longer wishes to translate words. Opportunity to change the flag 
         * is at the end, after a prompt asking the user.
         **********************************************************************/
        while(!userDone){
        
            System.out.println("\nPlease enter an English word: ");
            String engWord = userInput.next(); 
            out.writeObject(engWord);
            System.out.println("You typed: " + engWord); //A simple system check
            out.flush();
            
            /*******************************************************************
             *  Receives output from Dictionary.jar.
             ******************************************************************/
            serverMsg = (String)in.readObject();
            System.out.println(serverMsg); //Prints the message to client
            
            /*******************************************************************
             *   Prints the output from Dictionary to console and the 
             * translation.txt log. Creates a new line in the log after each 
             * entry.
             ******************************************************************/
            bwriter.write(serverMsg);
            bwriter.newLine();
            
            
            /*******************************************************************
             * Prompt to know if the user would like to continue. 
             * 
             * If User enters Y or y, the program ends, else, the loop starts 
             * again at the prompt for the next word.
             ******************************************************************/
            System.out.println("\nWould you like to exit? (Y/N)");
            if (userInput.next().equalsIgnoreCase("y"))
            {
                userDone = true;
                out.writeObject("y");
                out.flush();
            }    
            else
            {
                out.writeObject("No");
            }
        }
        
        bwriter.close(); //Closes the translation log as the User is finished. 
         
    }
}
