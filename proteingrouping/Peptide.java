/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proteomics.proteingrouping;


/**
 * The Peptide class. Will be used to encapsulate data loaded from source files 
 * @author Maduka Attamah
 */
public class Peptide {
    private final String peptideRefSequence;
    private final String psmId;
    private final int spectrumId;
    private final int rank;
    private final float score;
    private final boolean uniqueToAProtein;
    
    public static enum Status {RESOLVED, CONFLICTED};
    
    public Status peptideStatus;
    
    /**
     * Forcing the creator of a peptide to initialise these fields first. There are
     * several versions of the constructor cater for various missing fields and purposes.
     * @param peptideRefSeq is called peptide_ref in mzIdentML but called Sequence in mzTab
     *   I use this to primarily identify a peptide.
     * @psmId peptide spectrum match id.
     * @param spectrumId
     * @param rank
     * @param score 
     * @param uniqueToAProtein 
     */
    public Peptide(String peptideRefSeq, String psmId, int spectrumId, 
            int rank, float score, boolean uniqueToAProtein) {
        this.peptideRefSequence = peptideRefSeq;
        this.psmId = psmId;
        this.spectrumId = spectrumId;
        this.rank = rank;
        this.score = score;
        this.uniqueToAProtein = uniqueToAProtein;
        this.peptideStatus = Status.RESOLVED; //by default
    }
    
    public Peptide(String peptideRef, String psmId, int spectrumId, 
            int rank, float score) {
        this.peptideRefSequence = peptideRef;
        this.psmId = psmId;
        this.spectrumId = spectrumId;
        this.rank = rank;
        this.score = score;
        this.uniqueToAProtein = false;  //by default
        this.peptideStatus = Status.RESOLVED; //by default
    }
    
    public float getScore() {
        return this.score;
    }
    
    public String getPeptideRef() {
        return this.peptideRefSequence;
    }
    
    public boolean isUniqueToAProtein() {
        return this.uniqueToAProtein;
    }
    
    @Override
    public String toString() {
        return this.peptideRefSequence;
    }
    
}
