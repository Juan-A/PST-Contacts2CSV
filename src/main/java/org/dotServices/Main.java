package org.dotServices;

import com.opencsv.CSVWriter;
import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

        if (pstFolder.hasSubfolders()) {
            System.out.println("Number of subfolders: " + pstFolder.getSubFolderCount());
            for (PSTFolder subFolder : folders) {
                //Prints the name of the subfolders with their index.
                System.out.println(counter + " - " + subFolder.getDisplayName());
                counter++;
            }
        }
        System.out.print("Select a folder (by index number): ");
        selectedSubFolder = folders.get(keyboard.nextInt());
        if (selectedSubFolder.hasSubfolders()) {
            proccessFolder(selectedSubFolder);
        } else {
            System.out.println("No subfolders found, do you want to export the records? (y/n)");
            switch (keyboard.next()) {
                case "y":
                    System.out.println("Contents of the folder " + selectedSubFolder.getDisplayName());
                    if (emailsNumber(selectedSubFolder) > 0) {
                        System.out.println("There are " + emailsNumber(selectedSubFolder) + " mails in this folder.");
                        System.out.println("There are " + selectedSubFolder.getContentCount() + " elements in this folder.");
                        exportAdresses(selectedSubFolder);

                    } else {
                        System.out.println("There are no mails in this folder.");
                    }
                    break;
                case "n":
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Not valid option.");
                    break;
            }
        }


    }

    public static int emailsNumber(PSTFolder pstFolder) throws PSTException, IOException {
        return pstFolder.getEmailCount();
    }

    public static void exportAdresses(PSTFolder pstFolder) throws PSTException, IOException {
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Do you want to export the adresses? (y/n): ");
        switch (keyboard.next()) {
            case "y":
                System.out.println("Please enter a name for the file (without extension): ");
                generateCSV(keyboard.next(), pstFolder);

                break;
            case "n":
                System.out.println("Not exporting adresses...");
                break;
            default:
                System.out.println("Not valid option.");
                break;
        }
    }

    public static void generateCSV(String fileName, PSTFolder pstFolder) throws PSTException, IOException {
        //creates the csv file utf-8
        CSVWriter writer = new CSVWriter(new FileWriter(fileName + ".csv"), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        Map<String, String> mailDirections = new HashMap<>();
        //creates the record if it doesn't exist
        PSTMessage message = (PSTMessage) pstFolder.getNextChild();


        while (message != null) {
            if (!mailDirections.containsKey(message.getSenderEmailAddress())) {
                mailDirections.put(message.getSenderEmailAddress(), message.getSenderName());
            }
            if (!mailDirections.containsKey(message.getEmailAddress())) {
                mailDirections.put(message.getEmailAddress(), message.getDisplayName());
            }
            message = (PSTMessage) pstFolder.getNextChild();
        }


        //writes the records to file
        for (String key : mailDirections.keySet()) {
            String[] record = {key, mailDirections.get(key)};
            writer.writeNext(record);
        }
        writer.close();
        System.out.println("File generated successfully.");

    }
}