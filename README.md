## The package implements a concurrent algorithm for protein grouping.

The goal of protein grouping is to find the smallest set of proteins that accurately explains the set of peptides identified
from a shotgun LC-MS/MS study, following a matching of database proteins to each identified peptide.

#### The implemented rules for protein grouping are as follows.

*  Any two proteins PT<sub>1</sub> and PT<sub>2</sub> are assigned to the same group if and only if:

    * Proteins PT<sub>1</sub> and PT<sub>2</sub> share the same set of identified peptides; OR
    
    * The set of identified peptides associated with PT<sub>1</sub> is a proper subset of the set of identified peptides associated with PT<sub>2</sub>; 
      such set of peptides which are associated with PT<sub>1</sub> are called subset peptides; proteins that are assigned to a group by virtue of subset peptides may be 
      confidently discarded during reporting.
      
*  If a peptide is unique to a protein PT, then protein PT is definitely identified, and it is assigned to a singleton group.

*  A protein that is unassigned by any of the foregoing rules may be confidently discarded during reporting.

*  A higher level grouping may be achieved by clustering any pair of groups that have a set of peptides in common.

#### The work of developing this package is still on-going. 

#### The next steps will be towards a hierarchical visualisation of the formed protein groups.


## Bibliography
1. A.I. Nesvizhskii and R. Aebersold, Interpretation of Shotgun Proteomic Data: The Protein Inference Problem, *Mol. Cell Proteomics*, 2005, 4, 1419-1440.
2. A.R. Jones, Protein Inference and Grouping, In C. Bessant (Ed.), *New Developments in Mass Spectrometry*, 2017, 5, 93-115.
3. V.R. Koskinen, P.A. Emery, D.M. Creasy and J.S. Cottrell, Hierarchical Clustering of Shotgun Proteomics Data, *Mol. Cell Proteomics*, 2011, 10.
4. A.R. Jones, M. Eisenacher, G. Mayer, O. Kohlbacher, J. Siepen, S. Hubbard, J. Selley, B. Searle, J. Shofstahl, S. Seymour, R. Julian, P.A. Binz, 
E.W. Deutsch, H. Hermjakob, F. Reisinger, J. Griss, J.A. Vizcaino, M. Chambers, A. Pizarro and D. Creasy, The mzIdentML Data Standard for Mass Spectrometry-based 
Proteomics Results, *Mol. Cell Proteomics*, 2012, 11, M111.014381.
5. J. Griss, A.R. Jones, T. Sachsenberg, M. Walzer, L. Gatto, J. Hartler, G.G. Thallinger, R.M. Salek, C. Steinbeck, N. Neuhauser, J. Cox, S. Neumann,
J. Fan, F. Reisinger, Q.W. Xu, N. Del Toro, Y. Perez-Riverol, F. Ghali, N. Bandeira, I. Xenarios, O. Kohlbacher, J.A. Vizcaino and H. Hermjakob, The
mzTab Data Exchange Format: Communicating Mass Spectrometry-based Proteomics and Metabolomics Experimental Results to a Wider Audience, *Mol. Cell Proteomics*, 2014, 13, 2765-2775.

    
