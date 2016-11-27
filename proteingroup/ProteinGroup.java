/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proteomics.proteingroup;

import java.util.ArrayList;
/**
 *
 * @author Maduka Attamah
 */
public class ProteinGroup {
    
    private static int id = -1;
    private final int groupId;
   
    private ProteinGroupCluster cluster;
    private final ArrayList<Protein> proteins;
    private ArrayList<Peptide> groupPeptideDomain;
    private ArrayList<Peptide> sharedClusterPeptides;
    //In use for a singleton group
    // To make this cleaner we may have to refactor the classes using abstract classes and subclasses
    private final ArrayList<Peptide> uniquePeptides; 
    
    //To check whether this group is a singleton group due to a unique peptide
    private boolean singleton;
    
    public ProteinGroup() {
        groupId = getNewGroupId();
        proteins = new ArrayList<>();
        groupPeptideDomain = new ArrayList<>();
        sharedClusterPeptides = new ArrayList<>();
        uniquePeptides = new ArrayList<>();
        
        singleton = false; //by default
        
    }
    
    public static synchronized int getNewGroupId(){
        id += 1;
        return id;
    }
    
    public int getGroupId() {
        return this.groupId;
    }
    
    public void setProteinGroupCluster(ProteinGroupCluster cluster) {
        this.cluster = cluster;
    }
    
    public ProteinGroupCluster getProteinGroupCluster() {
        return this.cluster;
    }
    
    public ArrayList<Protein> getProteins() {
        return this.proteins;
    }
    
    public ArrayList<Peptide> getGroupPeptideDomain() {
        return this.groupPeptideDomain;
    }
    
    public ArrayList<Peptide> getSharedClusterPeptides() {
        return this.sharedClusterPeptides;
    }
    
    public void addToProteins(Protein protein) {
        this.proteins.add(protein);
    }
    
    public void removeFromProteins(Protein protein) {
        this.proteins.remove(protein);
    }
    
    public void addToGroupPeptideDomain(Peptide peptide) {
        this.groupPeptideDomain.add(peptide);
    }
    
    public void addAllToGroupPeptideDomain(ArrayList<Peptide> peptides) {
        this.groupPeptideDomain.addAll(peptides);
    }
    
    public void setGroupPeptideDomain(ArrayList<Peptide> peptides) {
        this.groupPeptideDomain = peptides;
    }
    
    public void removeFromGroupPeptideDomain(Peptide peptide) {
        this.groupPeptideDomain.remove(peptide);
    }
    
    public void addToSharedClusterPeptides(Peptide peptide) {
        this.sharedClusterPeptides.add(peptide);
    }
    
    public void addAllToSharedClusterPeptides(ArrayList<Peptide> conflictedPeptides) {
        this.sharedClusterPeptides.addAll(conflictedPeptides);
    }
    
    public void removeFromSharedClusterPeptides(Peptide peptide) {
        this.sharedClusterPeptides.remove(peptide);
    }
    
    public void addToUniquePeptides(Peptide peptide) {
        this.uniquePeptides.add(peptide);
    }

    public void addAllToUniquePeptides(ArrayList<Peptide> peptides) {
        this.uniquePeptides.addAll(peptides);
    }
    
    public void setSingleton(boolean value) {
        this.singleton = value;
    }
    
    public boolean isSingletonGroup() {
        return this.singleton;
    }
    
    /**
     * Used by the visualisation module to know what to print as children of this node
     * @return ArrayList of proteins in this group
     * TODO: This is probably better abstracted as an interface so both ProteinGroup and ProteinGroupCluster can
     * Implement it.
     */
    public ArrayList<Protein> getChildren() {
        return this.proteins;
    }
}
