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
        Scanner keyboard = new Scanner(System.in);
        Map<String, String> mailDirections = new HashMap<>();
        char especifyExport = 0;
        try {
            PSTFile pstFile = new PSTFile(fileName);
            //Prints the display name of the PST file.
            System.out.println("File title: " + pstFile.getMessageStore().getDisplayName());
            while (especifyExport != '1' && especifyExport != '2') {
                System.out.print("Do you want to specify the folder(1) or extract all the email contacts(2)?: ");
                especifyExport = keyboard.next().charAt(0);
            }
            switch (especifyExport) {
                case '1':
                    proccessFolder(pstFile.getRootFolder());
                    break;
                case '2':
                    exportAllFolders(pstFile.getRootFolder(), mailDirections);
                    System.out.println("Enter a name for the file(without extension): ");
                    generateCSV(keyboard.next(), mailDirections);

            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void exportAllFolders(PSTFolder pstFolder,Map<String,String > mailDirections) throws IOException, PSTException {
        // the root folder doesn't have a display name
        if (pstFolder.getDisplayName() != null) {
            System.out.println("Processed folder: " + pstFolder.getDisplayName());
        }

        // go through the folders...
        if (pstFolder.hasSubfolders()) {
            List<PSTFolder> childFolders = pstFolder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
                exportAllFolders(childFolder,mailDirections);
            }
        }


        // and now the emails for this folder
        if (pstFolder.getContentCount() > 0) {

            PSTMessage message = (PSTMessage) pstFolder.getNextChild();

            while (message != null) {
                if (!mailDirections.containsKey(message.getSenderEmailAddress())) {
                    mailDirections.put(message.getSenderEmailAddress(), message.getSenderName());
                }
                if (!mailDirections.containsKey(message.getReceivedByAddress())) {
                    mailDirections.put(message.getReceivedByAddress(), message.getReceivedByName());
                }
                message = (PSTMessage) pstFolder.getNextChild();
            }
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
        Map<String, String> mailDirections = new HashMap<>();
        //creates the record if it doesn't exist
        PSTMessage message = (PSTMessage) pstFolder.getNextChild();


        while (message != null) {
            if (!mailDirections.containsKey(message.getSenderEmailAddress())) {
                mailDirections.put(message.getSenderEmailAddress(), message.getSenderName());
            }
            if (!mailDirections.containsKey(message.getReceivedByAddress())) {
                mailDirections.put(message.getReceivedByAddress(), message.getReceivedByName());
            }
            message = (PSTMessage) pstFolder.getNextChild();
        }
        System.out.print("Do you want to export the adresses? (y/n): ");
        switch (keyboard.next()) {
            case "y":
                System.out.println("Please enter a name for the file (without extension): ");
                generateCSV(keyboard.next(), mailDirections);

                break;
            case "n":
                System.out.println("Not exporting adresses...");
                break;
            default:
                System.out.println("Not valid option.");
                break;
        }
    }

    public static void generateCSV(String fileName, Map<String, String> mailDirections) throws  IOException {
        //creates the csv file utf-8
        CSVWriter writer = new CSVWriter(new FileWriter(fileName + ".csv"), ';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);


        //writes the records to file
        for (String key : mailDirections.keySet()) {
            String[] record = {key, mailDirections.get(key)};
            writer.writeNext(record);
        }
        writer.close();
        System.out.println("File generated successfully.");

    }
}