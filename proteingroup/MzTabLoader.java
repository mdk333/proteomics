package proteomics.proteingroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads data from file into our working table
 *
 * @author Maduka Attamah
 */
public class MzTabLoader {

    File file;
    //Some of mzTab fields - I use only a few - just experimenting 
    public static final String PSM_ID = "PSM_ID";
    public static final String RANK = "RANK";
    public static final String SCORE = "X\\!TANDEM:HYPERSCORE";
    public static final String SPECTRUM_ID = "SPECTRUM ID";
    public static final String SEQUENCE = "SEQUENCE";
    public static final String PROTEINS = "PROTEINS";

    public MzTabLoader(File inputFile) {
        this.file = inputFile;
    }

    public HashMap<String, Protein> loadProteinData() {
        //Use this hash map to keep the proteins spotted 
        HashMap<String, Protein> proteinHashMap = new HashMap<>(); //key is proteinseq

        try {
            Scanner scanner = new Scanner(this.file, "UTF-8");
            //Get the header row
            String[] headerRow = scanner.nextLine().toUpperCase().split(",");

            /*
             *  Find the ID of the columns we are interested in (I process only a 
             *  few columns here just to experiment with the grouping)
             */
            
            //1. Assume PSM_ID to be the peptide ref
            int SEQUENCE_COL = Arrays.binarySearch(headerRow, SEQUENCE);
            int PSM_ID_COL = Arrays.binarySearch(headerRow, PSM_ID);
            int RANK_COL  = Arrays.binarySearch(headerRow, RANK);
            int SCORE_COL = Arrays.binarySearch(headerRow, SCORE);
            int SPECTRUM_ID_COL = Arrays.binarySearch(headerRow, SPECTRUM_ID);
            int PROTEINS_COL = Arrays.binarySearch(headerRow, PROTEINS);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] columns = line.split(",");

                //Create a peptide object from the current row of data
                Peptide peptide = new Peptide(columns[SEQUENCE_COL], columns[PSM_ID_COL],
                        Integer.parseInt(columns[SPECTRUM_ID_COL]),
                        Integer.parseInt(columns[RANK_COL]),
                        Float.parseFloat(columns[SCORE_COL]));

                //Create the protein objects, from the proteins associated with this peptide;
                //Associate the protein objects with the current peptide object
                String[] proteins = columns[PROTEINS_COL].split(";"); 
                for (String proteinSeq : proteins) {
                    //check if this protein is already in hashmap
                    if (proteinHashMap.containsKey(proteinSeq)) {
                        Protein aProtein = proteinHashMap.get(proteinSeq);
                        aProtein.addObservedPeptides(peptide);
                    } else {
                        //Create a new protein object
                        Protein protein = new Protein(proteinSeq);
                        //Add this peptide to it's list of peptides
                        protein.addObservedPeptides(peptide);
                        //Update the hasmap
                        proteinHashMap.put(proteinSeq, protein);
                    }
                }

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MzTabLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Now we have a hash map of all proteins, and the proteins encapsulate the peptides identified with them
        return proteinHashMap;
    }

}
