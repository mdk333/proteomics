/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proteomics.proteingroup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.ArrayList;

/**
 *
 * @author Maduka Attamah
 */
public class ProteinGrouper {

    public static final int PARALLELISM_THRESHOLD = 1; //Must execute in parallel

    public ConcurrentHashMap<Integer, ProteinGroup> groupProteins(HashMap<String, Protein> proteinHashMap) {
        ConcurrentHashMap<Integer, ProteinGroup> groupHashMap = new ConcurrentHashMap<>();
        for (Protein protein : proteinHashMap.values()) {
            //Check if protein has a unique peptide
            if (protein.hasUniquePeptides()) {
                //Assign it to a singleton group
                ProteinGroup proteinGroup = new ProteinGroup();
                proteinGroup.addToProteins(protein);
                proteinGroup.addAllToGroupPeptideDomain(protein.getObservedPeptides());
                proteinGroup.setSingleton(true);
                proteinGroup.addAllToUniquePeptides(protein.getUniquePeptides());

                protein.setProteinGroup(proteinGroup);
                //Include the new protein group in the hashmap
                groupHashMap.put(proteinGroup.getGroupId(), proteinGroup);
                //We are done with this protein
                continue;
            }
            //Otherwise

            //(Parallel) Search for an already group (not singleton ones though) for which the peptides of this protein is a subset
            // or a group for which its peptide domain is a subset of the peptides of this protein. Singleton groups must retain only one protein in the group
            Integer key = groupHashMap.search(PARALLELISM_THRESHOLD, (k, v)
                    -> ((v.getGroupPeptideDomain().containsAll(protein.getObservedPeptides()) && !v.isSingletonGroup())
                    || (protein.getObservedPeptides().containsAll(v.getGroupPeptideDomain()) && !v.isSingletonGroup())) ? k : null);
            if (key == null) {
                //No groups exists yet to which this protein can be assigned
                //So create a new group
                ProteinGroup proteinGroup = new ProteinGroup();
                proteinGroup.addToProteins(protein);
                proteinGroup.addAllToGroupPeptideDomain(protein.getObservedPeptides());
                proteinGroup.setSingleton(false);

                protein.setProteinGroup(proteinGroup);
                //Include the new protein group in the hashmap
                groupHashMap.put(proteinGroup.getGroupId(), proteinGroup);
            } else {
                //key is not null, so there is already a group that this protein can be assigned to
                ProteinGroup pg = groupHashMap.get(key);
                if (pg.getGroupPeptideDomain().size() >= protein.getObservedPeptides().size()) {
                    //The group peptides then is a superset of this protein's peptides set
                    // So we add this protein to the group
                    pg.addToProteins(protein);
                    protein.setProteinGroup(pg);
                    if(pg.getGroupPeptideDomain().size() > protein.getObservedPeptides().size()) {
                        //the protein is a subset protein so we flag it as such
                        protein.subsetProtein(true);
                    }
                } else {
                    //The proteins peptide set is a superset of the peptides of this group
                    //So we make this proteins peptides set to be the group peptide domain, and inlude
                    // this protein to the group
                    pg.setGroupPeptideDomain(protein.getObservedPeptides());
                    pg.addToProteins(protein);
                    protein.setProteinGroup(pg);
                }
            }
        }
        updateAllPeptideStatus(groupHashMap);
        return groupHashMap;
    }
    
    private void updateAllPeptideStatus(ConcurrentHashMap<Integer, ProteinGroup> groupHashMap) {
        for(ProteinGroup proteinGroup : groupHashMap.values()) {
            for(ProteinGroup pGroup : groupHashMap.values()) {
                if(proteinGroup == pGroup) continue;
                //else
                //Check if the two groups have any peptides in common, and mark those peptides as conflicted
                ArrayList<Peptide> commonPeptides = new ArrayList<>(proteinGroup.getGroupPeptideDomain());
                commonPeptides.retainAll(pGroup.getGroupPeptideDomain());
                for(Peptide peptide : commonPeptides) {
                    peptide.peptideStatus = Peptide.Status.CONFLICTED;
                }
            }
        }
        
    }
    
/*
    public ConcurrentHashMap<Integer, ProteinGroupCluster> clusterProteinGroups(HashMap<Integer, ProteinGroup> proteinGroupHashMap) {

        ConcurrentHashMap<Integer, ProteinGroupCluster> proteinGroupCluster = new ConcurrentHashMap<>();
        ArrayList<Peptide> conflictedPeptides = new ArrayList<>();
        for (ProteinGroup proteinGroup : proteinGroupHashMap.values()) {

        }

    }
*/
}
