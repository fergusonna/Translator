/******************************************************************************
 * Dictionary.jar
 * 
 *  This program translates a word given as an outside argument given when
 * Dictionary is called. It searches through a text file (currently hardcoded,
 * but an easy switch to make it able to use multiple text files) for the target
 * word,and returns the term given in the text file as the target's translation.
 * 
 ******************************************************************************
 * INPUT: A string containing the word to be translated.
 * 
 * OUTPUT: Prints to console the input word's translation.
 ******************************************************************************
 * Maintenance Log
 ******************************************************************************
 * FIRST VERSION    05/22/2014  Nicholas Ferguson
 *      Write the program.
 * SOCKET UPDATE    06/08/2014  Nicholas Ferguson
 *      Update Dictionary to act as a server, receiving input through a socket.
 * 
 ******************************************************************************/

import java.util.*;
import java.io.*;
import java.net.*;

/******************************************************************************
 * Dictionary
 * 
 * The main method
 ******************************************************************************
 * INPUT: A string containing the word to be translated.
 * 
 * OUTPUT: Prints to console the input word's translation.
 ******************************************************************************
 * 
 ******************************************************************************/
public class Dictionary {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
    
        
        String fileName = "german.txt"; 
        Scanner inFile = new Scanner(new FileReader(fileName));
        
        /***********************************
        * While loop looks through the file and counts the number of lines.
        * Each line should be a different word/translation pair, so this 
        * effectively gives the number of current words with translations.
        ************************************/
        int lineCount = 0;
        while(inFile.hasNextLine())
        {
            lineCount++;
            inFile.nextLine();
        }
        inFile.close();
        
        //Returns the Scanner to the beginning of the file, to now begin 
        //reading the words.
        inFile = new Scanner(new FileReader(fileName));
        
        /***********************************************************************
         * A 2 Dimensional Array where the first cell in the row is the English 
         *  word, and the second cell is the translation of the English word.
         * lineCount is used to vary the array size dependent on the number of 
         * words available in the translation text file list.
         **********************************************************************/
        String[][] wordList = new String[lineCount][2];
        int wordCount = 0;
        
        /***********************************************************************
         * While loop reads every line of the translation text. 
         * The first word of each line should be the English word, which is 
         *  saved as a cell in the word list array.
         * The second word of each line should be the translation, which is 
         *  saved as a second cell in the same row as the first word.
         * A new row in the 2D array is created for each word added. 
         **********************************************************************/
        while(inFile.hasNextLine())
        {
            wordList[wordCount][0] = inFile.next();
            wordList[wordCount][1] = inFile.next();
            wordCount++;
        }
        
        /***********************************************************************
         * The socket is constructed and launched here, as well as an input and
         * output stream to communicate back and forth.
         **********************************************************************/
        int listeningPort = 2014;
        ServerSocket ss = new ServerSocket(listeningPort);
        Socket socket = ss.accept();
        
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        
        
        /***********************************************************************
         * The while loop receives the word to translate from the client, 
         * translates it (if possible), and sends the translation to the client. 
         * The while loop is exited once the client sends a message denoting the 
         * user id finished. 
         **********************************************************************/
        boolean userDone = false; 
        while (!userDone)
        {
            boolean targetFound = false;
            String outputMsg = "";
            String target = (String) in.readObject(); //word to be translated
            
            
            for (int i = 0; i < wordList.length; i++)
            {
                /***************************************************************
                 * The if statement checks if the target is among the words
                 * translated in the file.
                 * If the word is found, it prints to console and includes it in
                 * an output message to be sent to client after the loop has ended.
                 **************************************************************/
                if (target.equalsIgnoreCase(wordList[i][0]) && !targetFound)
                {
                    targetFound = true;
                    System.out.println(target + " translates to: " + wordList[i][1]);
                    outputMsg = target + " translates to: " + wordList[i][1];
                    break; //Loop ends because word has been found.
                }
            }
            
            /*******************************************************************
             * If statement that checks if the word was translated. If not, 
             * a message saying the word was not found will print to the server 
             * window and be sent to the client.
             ******************************************************************/
            if (!targetFound)
            {
                System.out.println("\nThe word " + target + " is not in the dictionary.");
                outputMsg = "\nThe word " + target + " is not in the dictionary.";
            }
            
            //Sends the results of the attempted translation.
            // or a "word not found" message.
            out.writeObject(outputMsg); 
            out.flush();
            
            /*******************************************************************
             * The client asks the user if they are finished translating words. 
             * The user's response is received here, setting the userDone boolean
             * flag to true, to exit the loop.
             ******************************************************************/
            String donePrompt = "";
            donePrompt = (String) in.readObject();
            if (donePrompt.equalsIgnoreCase("y"))
            {
                userDone = true;
            }
        }
        
        //Translate is done, server shutting down. (Not the best idea, I know)
        socket.close();
        ss.close();
        System.exit(0);
    }
}
