/**
 * Copyright 2016-2017 Maduka Attamah
 *
 * This class does the following: 
 * 
 * (a.) Loads the peptides from the given mzIdentML
 * file into a hash map. The key of the hash map is the Peptide Ref, which can 
 * be used (as seen in the mzIdentML files) to refer to same
 * peptides found in different "spectra". For the case where the same peptides are
 * loaded from more than one "spectrum", only the peptide with the highest score
 * is maintained in the entry corresponding to the peptide evidence ref. 
 * 
 * (b.) Loads the database proteins together with their associated peptides
 * (identified by the peptide evidence ref) into a hash map. This hash map is
 * used in tandem with the one from (a.)
 *
 * @author Maduka Attamah
 */

package proteomics.proteingrouping;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class MzIdentMLLoader {

    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document document;

    /**
     * For the peptideMap, the key is the peptide reference, whereas the value
     * is the peptide object constructed Likewise for dbProteinMap, the key is
     * the dbSequence ref, and the value is the protein object constructed
     */
    private HashMap<String, Peptide> peptideMap;
    private HashMap<String, Protein> dbProteinMap;

    /**
     * I am using the DOM Parser approach to XML parsing because I expect the
     * sample data set to be relatively small. This may not be the optimal
     * approach for very large XML files. See SAX Parser, JDOM Parser, StAX
     * Parser, XPath Parser, DOM4J Parser etc.
     *
     * @param file
     */
    public MzIdentMLLoader(File file) {
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.parse(file);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MzIdentMLLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MzIdentMLLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MzIdentMLLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Loads up peptide from given mzIdentML file. Note: I ignore peptides with
     * "passThreshold=false"
     *
     * @return
     */
    private HashMap loadPeptides() {
        NodeList spectrumNodes = document.getElementsByTagName("SpectrumIdentificationResult");
        for (int c = 0; c < spectrumNodes.getLength(); c++) {
            Node spectrumNode = spectrumNodes.item(c);
            if (spectrumNode.getNodeType() == Node.ELEMENT_NODE) {
                Element sElement = (Element) spectrumNode;
                String spectrumId = sElement.getAttribute("spectrumID");

                NodeList peptideNodes = sElement.getElementsByTagName("SpectrumIdentificationItem");
                for (int d = 0; d < peptideNodes.getLength(); d++) {
                    Node peptideNode = peptideNodes.item(d);
                    if (spectrumNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element pElement = (Element) peptideNode;
                        //Now I can get needed attributes of the Spectrum Identification Item
                        //Skip peptides that do not reach the "threshold"
                        if (pElement.getAttribute("passThreshold").equalsIgnoreCase("false")) {
                            continue;
                        }
                        //else
                        String peptideRef = pElement.getAttribute("peptide_ref");
                        String rank = pElement.getAttribute("rank");
                        String psmId = pElement.getAttribute("id");
                        String peptideScore = "";
                        boolean uniqueToAProtein = true;
                        NodeList cvParamNodes = pElement.getElementsByTagName("cvParam");
                        for (int e = 0; e < cvParamNodes.getLength(); e++) {
                            Node cvParamNode = cvParamNodes.item(e);
                            if (cvParamNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element cvpElement = (Element) cvParamNode;
                                //Look for the one that has score and peptide uniqueness
                                if (cvpElement.getAttribute("name").equalsIgnoreCase("Mascot:score")) {
                                    peptideScore = cvpElement.getAttribute("value");
                                } else if (cvpElement.getAttribute("name").equalsIgnoreCase("peptide shared in multiple proteins")) {
                                    uniqueToAProtein = false;
                                }
                            }
                        }
                        //Now create the new peptide
                        Peptide peptide = new Peptide(peptideRef, psmId, Integer.valueOf(spectrumId),
                                Integer.valueOf(rank), Float.valueOf(peptideScore), uniqueToAProtein);

                        //Use lambda expression to ensure that the where this peptide has already been found in 
                        // another spectrum, we keep the version that has the higher score
                        // I am here assumming that the same peptide cannot be found in the same spectrum otherwise
                        // the following condition will be more complex.
                        peptideMap.compute(peptideRef, (k, v) -> v == null ? peptide : (peptide.getScore() > v.getScore() ? peptide : v));
                    }
                }
            }
        }
        return peptideMap;
    }

    /**
     * Loads up identified proteins, together with associated peptides, from the
     * given mzIdentML file. Contrary to the case of peptides, I do not ignore
     * the cases where "passThreshold=false".
     *
     * @return
     */
    private HashMap loadIdentifiedProteins() {
        NodeList pagNodes = document.getElementsByTagName("ProteinAmbiguityGroup");
        for (int c = 0; c < pagNodes.getLength(); c++) {
            Node pagNode = pagNodes.item(c);
            if (pagNode.getNodeType() == Node.ELEMENT_NODE) {
                Element pagElement = (Element) pagNode;
                NodeList pdhNodes = pagElement.getElementsByTagName("ProteinDetectionHypothesis");
                for (int d = 0; d < pdhNodes.getLength(); d++) {
                    Node pdhNode = pdhNodes.item(d);
                    if (pdhNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element pdhElement = (Element) pdhNode;
                        String dbSequenceRef = pdhElement.getAttribute("dBSequence_ref");
                        Protein protein = new Protein(dbSequenceRef);

                        //Now get the peptides associated with this protein
                        NodeList assocPeptideNodes = pdhElement.getElementsByTagName("PeptideHypothesis");
                        for (int e = 0; e < assocPeptideNodes.getLength(); e++) {
                            Node assocPeptideNode = assocPeptideNodes.item(e);
                            if (assocPeptideNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element pepElement = (Element) assocPeptideNode;
                                String peptideRef = pepElement.getAttribute("peptideEvidence_ref");
                                //Reads the peptided evidence ref uptill the character before the second underscore (it doesn't read the second underscore)
                                //This value is used as the peptide reference number - same as the key of the peptideMap
                                //Note that this is application dependent i.e. the application that produced the mzIdentML file
                                //So keep an eye on it
                                peptideRef = peptideRef.substring(0, peptideRef.indexOf("-", peptideRef.indexOf("_")));
                                //Now use this ref to pull out the actual peptide from the peptideMap 
                                //Note that this assumes that the peptideMap has already been populated
                                Peptide peptide = peptideMap.get(peptideRef);
                                protein.addObservedPeptides(peptide);

                                //Check if the current peptide is unique to this protein
                                if (peptide.isUniqueToAProtein()) {
                                    protein.addUniquePeptides(peptide);
                                }

                            }
                        }
                        //Add the protein to the proteinMap, but do so only if this protein has at least one associated peptide
                        if (protein.getObservedPeptides().size() > 0) {
                            dbProteinMap.put(dbSequenceRef, protein);
                        }
                    }
                }
            }
        }
        return dbProteinMap;
    }

    /**
     * This method is then convenient to call inside the "run" method of a
     * runnable or callable for the purpose of multi-threading. The passed in
     * parameters will contain the results of the processing;
     *
     * @param peptideHashMap
     * @param proteinHashMap
     */
    public void processIdentMLFile(HashMap<String, Peptide> peptideHashMap, HashMap<String, Protein> proteinHashMap) {
        peptideHashMap = this.loadPeptides();
        proteinHashMap = this.loadIdentifiedProteins();
    }

}
