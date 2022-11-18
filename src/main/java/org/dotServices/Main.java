package org.dotServices;

import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("/*******************************/\n" +
                "/*     DotServices Software    */\n" +
                "/*     --------------------    */\n" +
                "/*    Contacts extractor for   */\n" +
                "/*            PST Files        */\n" +
                "/*(From Inbox and outbox mails)*/\n" +
                "/*******************************/");
        Main main = new Main(args[0]);
    }

    public Main(String fileName) {
        try {
            PSTFile pstFile = new PSTFile(fileName);
            //Prints the display name of the PST file.
            System.out.println("File title: " + pstFile.getMessageStore().getDisplayName());
            proccessFolder(pstFile.getRootFolder());

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void proccessFolder(PSTFolder pstFolder) throws PSTException, IOException {
        Scanner keyboard = new Scanner(System.in);

        //Counts the number of folders in the PST file.
        int counter = 0;

        //Store the subfolders in a list.
        List<PSTFolder> folders = pstFolder.getSubFolders();

        //Creates an auxiliar folder object to select and explore the subfolder.
        PSTFolder selectedSubFolder;

        if(pstFolder.hasSubfolders()){
            System.out.println("Number of subfolders: " + pstFolder.getSubFolderCount());
            for(PSTFolder subFolder : folders){
                //Prints the name of the subfolders with their index.
                System.out.println(counter + " - " + subFolder.getDisplayName());
                counter++;
            }
        }
        System.out.print("Select a folder (by index number): ");
        selectedSubFolder = folders.get(keyboard.nextInt());
        if(selectedSubFolder.hasSubfolders()){
            proccessFolder(selectedSubFolder);
        }
        else{
            System.out.println("No subfolders found.");
        }




    }
}