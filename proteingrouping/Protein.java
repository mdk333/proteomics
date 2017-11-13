/**
 *
 * Copyright 2016-2017 Maduka Attamah
 *
 * @author Maduka Attamah
 *
 */

package proteomics.proteingrouping;

import java.util.ArrayList;


public class Protein {

    private final String dbSequenceRef;
    private ProteinGroup proteinGroup;  // The group to which this protein is assigned
    private final ArrayList<Peptide> observedPeptides;  //Peptides that are found on this protein and possibly in other proteins
    private ArrayList<Peptide> uniquePeptides;  //Peptides that are unique to this protein

    private boolean subsetProtein;

    public Protein(String dbSequenceRef) {
        this.dbSequenceRef = dbSequenceRef;
        observedPeptides = new ArrayList<>();
        uniquePeptides = new ArrayList<>();
        subsetProtein = false; //by default
    }

    public void setProteinGroup(ProteinGroup proteinGroup) {
        this.proteinGroup = proteinGroup;
    }

    public ProteinGroup getProteinGroup() {
        return this.proteinGroup;
    }

    public void addObservedPeptides(Peptide peptide) {
        this.observedPeptides.add(peptide);
    }

    public void removeFromObservedPeptides(Peptide peptide) {
        this.observedPeptides.remove(peptide);
    }

    public ArrayList<Peptide> getObservedPeptides() {
        return this.observedPeptides;
    }

    public void addUniquePeptides(Peptide peptide) {
        this.uniquePeptides.add(peptide);
    }

    public void addAllToUniquePeptides(ArrayList<Peptide> peptides) {
        this.uniquePeptides.addAll(peptides);
    }

    public ArrayList<Peptide> getUniquePeptides() {
        return this.uniquePeptides;
    }

    public void subsetProtein(boolean value) {
        this.subsetProtein = value;
    }
    
    public boolean isSubsetProtein() {
        return this.subsetProtein;
    }

    /**
     * This method will be used by the visualisation procedure to output the
     * peptides which correspond to this protein.
     *
     * @return the same as getObserved Peptides
     */
    public ArrayList<Peptide> getChildren() {
        return this.observedPeptides;
    }

    public boolean hasUniquePeptides() {
        if (this.uniquePeptides.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return this.dbSequenceRef;
    }

}
