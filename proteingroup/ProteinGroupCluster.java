/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proteomics.proteingroup;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Maduka Attamah
 */
public class ProteinGroupCluster {

    private static int id = -1;
    private final int clusterId;
    private final ArrayList<ProteinGroup> proteinGroups;
    private final HashSet<Peptide> clusterPeptideDomain;

    public ProteinGroupCluster() {
        clusterId = getNewClusterId();
        proteinGroups = new ArrayList<>();
        clusterPeptideDomain = new HashSet<>();
    }

    /**
     * This method is synchronised because many threads may be creating clusters
     * at the same time and the id have to be unique for each cluster created
     *
     * @return
     */
    public static synchronized int getNewClusterId() {
        id += 1;
        return id;
    }

    public ArrayList<ProteinGroup> getProteinGroups() {
        return this.proteinGroups;
    }

    public void addToCluster(ProteinGroup proteinGroup, ArrayList<Peptide> conflictedPeptides) {
        //Verify that there is basis for cluster membership
        if(conflictedPeptides.isEmpty()) return; //do nothing
        //else
        this.proteinGroups.add(proteinGroup);
        //Update the cluster peptide domain. The more groups we add to this cluster, the more even more groups can join it.
        this.clusterPeptideDomain.addAll(proteinGroup.getGroupPeptideDomain());
        //Update the cluster information of the given protein group
        proteinGroup.setProteinGroupCluster(this);
        //Update the protein groups conflicted peptides information
        proteinGroup.addAllToSharedClusterPeptides(conflictedPeptides);
    }

    public void removeFromCluster(ProteinGroup proteinGroup) {
        this.proteinGroups.remove(proteinGroup);
    }

    public int getClusterId() {
        return this.clusterId;
    }

    public void addToClusterPeptideDomain(Peptide peptide) {
        this.clusterPeptideDomain.add(peptide);
    }

    public void addAllToClusterPeptideDomain(ArrayList<Peptide> peptides) {
        this.clusterPeptideDomain.addAll(peptides);
    }

    /**
     * Checks whether the given protein group can belong to the cluster. 
     * @param proteinGroup  The protein group for which we wish to test cluster membership
     * @param conflictedPeptides  Will contain the conflicted peptides if the given protein group can belong to this cluster.
     * @return 
     */
    public boolean canBelongToCluster(ProteinGroup proteinGroup, ArrayList<Peptide> conflictedPeptides) {
        ArrayList<Peptide> peptides = new ArrayList<>(proteinGroup.getGroupPeptideDomain());
        peptides.retainAll(clusterPeptideDomain);
        conflictedPeptides = peptides;
        if (peptides.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * A utility method that will be used during visualisation to know what to
     * print as children of an instance of this node
     *
     * @return
     */
    public ArrayList<ProteinGroup> getChildren() {
        return this.proteinGroups;
    }

}
