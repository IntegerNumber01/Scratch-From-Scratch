import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import backend.Interpreter;
import backend.Parser;
import backend.Program;
import backend.Sprite;
import backend.World;

public class BackendMain {

    /**
     * Traverses the SBGame directory and maps each sprite folder
     * to an ArrayList of its files, excluding script.scratch
     *
     * @param sbGamePath path to the SBGame directory
     * @return Map of folder name -> ArrayList of files in that folder
     */
    private static Map<String, ArrayList<File>> getSBGameFolderFiles(String sbGamePath) {
        Map<String, ArrayList<File>> folderFilesMap = new HashMap<>();
        File sbGameDir = new File(sbGamePath);

        if (!sbGameDir.exists() || !sbGameDir.isDirectory()) {
            System.err.println("SBGame directory not found: " + sbGamePath);
            return folderFilesMap;
        }

        File[] contents = sbGameDir.listFiles();
        if (contents == null) return folderFilesMap;

        ArrayList<File> rootFiles = new ArrayList<>();
        for (File item : contents) {
            if (item.isFile() && !item.getName().equals("script.scratch")) {
                rootFiles.add(item);
            } else if (item.isDirectory()) {
                ArrayList<File> spriteFiles = new ArrayList<>();
                File[] spriteContents = item.listFiles();

                if (spriteContents != null) {
                    for (File spriteFile : spriteContents) {
                        if (spriteFile.isFile() && !spriteFile.getName().equals("script.scratch")) {
                            spriteFiles.add(spriteFile);
                        }
                    }
                }
                folderFilesMap.put(item.getName(), spriteFiles);
            }
        }

        folderFilesMap.put(sbGameDir.getName(), rootFiles);
        return folderFilesMap;
    }

    public static void main(String[] args) {
        World world = new World();
        Parser parser = new Parser();
        Program program = null;

        // Load SBGame folder structure (excluding script.scratch files)
        Map<String, ArrayList<File>> sbGameFiles = getSBGameFolderFiles("SBGame");
        System.out.println("[SBGAME FOLDERS] " + sbGameFiles);

        try {
            System.out.println("[START] PROGRAM BUILDER OUTPUT --------------");
            program = parser.buildProgram(new File("SBGame/Cat/script.scratch"));
            System.out.println("[END] PROGRAM BUILDER OUTPUT --------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Sprite cat = new Sprite("cat", sbGameFiles.get("Cat"));
        System.out.println(cat.toString());
        System.out.println();
        Interpreter interpreter = new Interpreter(world);
        interpreter.addProgram(cat, program);

        interpreter.execute();
        System.out.println(cat.toString());
    }
}